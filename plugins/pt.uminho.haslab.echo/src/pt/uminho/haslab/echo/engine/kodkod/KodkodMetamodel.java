package pt.uminho.haslab.echo.engine.kodkod;

import kodkod.ast.*;
import kodkod.ast.operator.Multiplicity;

import org.eclipse.emf.ecore.*;
import org.eclipse.ocl.examples.pivot.ExpressionInOCL;
import org.eclipse.ocl.examples.pivot.OCL;
import org.eclipse.ocl.examples.pivot.ParserException;
import org.eclipse.ocl.examples.pivot.helper.OCLHelper;
import org.eclipse.ocl.examples.pivot.utilities.PivotEnvironmentFactory;

import pt.uminho.haslab.echo.*;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.engine.EchoHelper;
import pt.uminho.haslab.echo.engine.OCLTranslator;
import pt.uminho.haslab.echo.engine.ast.CoreMetamodel;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IExpression;
import pt.uminho.haslab.echo.util.Pair;
import pt.uminho.haslab.mde.model.EMetamodel;

import java.util.*;

class KodkodMetamodel extends CoreMetamodel {

    //TODO can classes have the same name as enums?


    //TODO verify facts that mention classes with children must be changed.
    //TODO Use MDE and check if MDE uses sf hierarchy properly.

	/** maps classes into respective Kodkod relations */
	private Map<String,Relation> mapClassRel;
	/** maps structural features into respective Kodkod relations */
	private Map<String,Relation> mapSfRel;
    /** maps relations into structural features */
    private Map<Relation,EStructuralFeature> mapRelSf;
    /**maps the hierarchy */
    private Map<String,Set<String>> mapParents;
    /**maps a eReference relation into its type relations*/
    private Map<Relation,Pair<Set<Relation>,Set<Relation>>> mapRefType;
    /**maps a attribute relation with type = int + string + enum*/
    private Map<Relation, Set<Relation>> mapType;
    /**maps a attribute relation with type = bool*/
    private Map<Relation, Set<Relation>> mapBoolType;
    /**maps EEnumLiterals names into EEnumLiterals*/
    private Map<String,EEnumLiteral> literals;

    /** maps containment references, key is the type name,value is the set of containment relations */
    private Map<String, Set<Relation>> containmentMap ;

    private Set<EClass> abstracts ;

    private Set<EEnum> enums;

    /**facts about the meta-model*/
	private Formula facts;
    /**disjoint relations*/
    private Formula disjoint = null;
    /** */
    private boolean processedContainers = false;

	@Override
    public KodkodFormula getConforms(String modelID) {
		// TODO ignoring model ID!
        if(disjoint == null){
            makeDisjointFact();
            facts = facts.and(disjoint);
        }if(!processedContainers){
            makeContainmentFact();
            processedContainers = true;
        }

        return new KodkodFormula(facts);
    }

    private void makeContainmentFact() {


        for (String current : containmentMap.keySet()) {
            Set<Relation> containers = getParentsContainers(current);
            Set<Pair<Relation,Relation>> typeGroups = getSameTypeContainers(current);
            makeContainmentFact(containers,typeGroups,getDomain(current));
        }
    }

    private Set<Pair<Relation,Relation>> getSameTypeContainers(String current) {
        Set<Pair<Relation,Relation>> res = new HashSet<>();

        Object[] containers = containmentMap.get(current).toArray();

        for(int i=0;i<containers.length;i++)
        {
            Relation rel =(Relation) containers[i];
            for(int j = i+1 ;j<containers.length;j++)
            {
                Relation rel2 =(Relation) containers[j];
                if(mapRefType.get(rel2).left.containsAll(mapRefType.get(rel).left)
                        || mapRefType.get(rel).left.containsAll(mapRefType.get(rel2).left ))
                    res.add(new Pair<>(rel,rel2));
            }
        }

        return res;
    }

    Set<Relation> getParentsContainers(String className)
    {
        Set<Relation> res = new HashSet<>();
        if(containmentMap.containsKey(className))
            res.addAll(containmentMap.get(className));
        if(mapParents.containsKey(className)){
            Set<String> parents = mapParents.get(className);
            for(String s: parents){
                Set<Relation> aux = getParentsContainers(s);
                res.addAll(aux);
            }
        }
        return res;
    }

    private void makeContainmentFact(Set<Relation> all,Set<Pair<Relation,Relation>> sameType, Expression type){
        Variable x = Variable.unary("x");
        Decl d = x.declare(Multiplicity.ONE,type);
        facts = facts.and( Expression.union(all).join(x).one().forAll(d));

        for(Pair<Relation,Relation> pair: sameType)
                facts = facts.and(pair.left.intersection(pair.right).join(x).no().forAll(d));
    }

    public KodkodMetamodel(EMetamodel metaModel) throws EError{
		super(metaModel);
		mapClassRel = new HashMap<>();
		mapSfRel = new HashMap<>();
		mapRelSf = new HashMap<>();
        mapParents = new HashMap<>();
        mapRefType = new HashMap<>();
        mapType = new HashMap<>();
        mapBoolType = new HashMap<>();
        abstracts = new HashSet<>();
        enums = new HashSet<>();
        literals = new HashMap<>();
        containmentMap = new HashMap<>();
		facts = Formula.TRUE;
	}
	
	protected void processOperations(List<EOperation> eOperations) {
		// TODO Auto-generated method stub
	}

	protected void processAnnotations(List<EAnnotation> eAnnotations) throws EError {
        OCL ocl = OCL.newInstance(new PivotEnvironmentFactory());
        for (EAnnotation annotation : eAnnotations) {
        
            OCLHelper helper = ocl.createOCLHelper(annotation.eContainer());

            KodkodContext context = new KodkodContext();

            IExpression cl = new KodkodExpression(getRelation((EClass)annotation.eContainer()));
            IDecl self= cl.oneOf("self");
            context.addVar(self);

            OCLTranslator converter = new OCLTranslator(context);

            if (annotation.getSource() != null) {
                if (annotation.getSource().equals(
                        "http://www.eclipse.org/emf/2002/Ecore/OCL")
                        || annotation.getSource().equals("Echo/Gen"))
                    try {
                        for (String sExpr : annotation.getDetails().values()) {
                            ExpressionInOCL invariant = helper
                                    .createInvariant(sExpr);
                            KodkodFormula oclExpr = (KodkodFormula) converter.translateFormula(
                                    invariant.getBodyExpression());

//                            System.out.println(PrettyPrinter.print(oclExpr.formula, 2));
//                            System.out.println(invariant.getBodyExpression());

                            Formula oclKodkod = oclExpr.formula.forAll(((KodkodDecl)self).decl);

                            //TODO kodkod optimizations?
                            /*AlloyOptimizations opt = new AlloyOptimizations();
                            if (EchoOptionsSetup.getInstance().isOptimize()) {
                                oclalloy = opt.trading(oclalloy);
                                oclalloy = opt.onePoint(oclalloy);
                            }   */
                            if (annotation
                                    .getSource()
                                    .equals("http://www.eclipse.org/emf/2002/Ecore/OCL"))
                                facts = facts.and(oclKodkod);
                            /*TODO : generate
                            else
                                constraint_generate = constraint_generate
                                        .and(oclalloy);*/
                        }
                    } catch (ParserException e) {
                        throw new EErrorParser(EErrorParser.OCL,
                                "Failed to parse OCL annotation.",
                                e.getMessage(), EchoRunner.Task.TRANSLATE_METAMODEL);
                    }
            }
        }
		
	}

	protected void processReferences(List<EReference> eReferences) throws EErrorTransform {
		for(EReference eReference : eReferences) {
			String className = eReference.getEContainingClass().getName();
			String refName = EchoHelper.featureLabel(metamodel,eReference);
			Expression classRel = getDomain(className);
			EReference eOpposite = eReference.getEOpposite();
			
			if((eOpposite != null &&
					eOpposite.isContainment() &&
					EchoOptionsSetup.getInstance().isOptimize())) {} //do notting
			else if((eOpposite != null &&
					!eReference.isContainment() && 
					eOpposite.getLowerBound() == 1 &&
					eOpposite.getUpperBound() == 1 &&
					EchoOptionsSetup.getInstance().isOptimize())) {} //do notting
			else if((eOpposite != null
					&& getRelation(eOpposite) != null &&
					EchoOptionsSetup.getInstance().isOptimize())) {}
			else {
                String coDomainName = eReference.getEReferenceType().getName();
				Expression coDomain = getDomain(coDomainName);
				Relation refRel = Relation.binary(refName);


                mapRefType.put(refRel,
                        new Pair<>(
                                getRelDomain(className),
                                getRelDomain(coDomainName)));

                facts = facts.and(refRel.in(classRel.product(coDomain)));
				mapSfRel.put(eReference.getEContainingClass().getName()+"::"+eReference.getName(),refRel);
                mapRelSf.put(refRel,eReference);
				
				if(eOpposite!= null){
					Relation opRel = getRelation(eOpposite);
					if(opRel!=null)
						facts = facts.and(refRel.eq(opRel.transpose()));
				}
				
				

				Integer bitwidth = EchoOptionsSetup.getInstance().getBitwidth();
				Integer max = (int) (Math.pow(2, bitwidth) / 2);
				if (eReference.getLowerBound() >= max
						|| eReference.getLowerBound() < -max)
					throw new EErrorTransform(EErrorTransform.BITWIDTH,
							"Bitwidth not enough to represent lower bound: "
									+ eReference.getLowerBound() + ".", "",
							Task.TRANSLATE_METAMODEL);
				if (eReference.getUpperBound() >= max
						|| eReference.getUpperBound() < -max)
					throw new EErrorTransform(EErrorTransform.BITWIDTH,
							"Bitwidth not enough to represent upper bound: "
									+ eReference.getUpperBound() + ".", "",
							Task.TRANSLATE_METAMODEL);
				
				Variable x = Variable.unary("x");
				Decl d = x.declare(Multiplicity.ONE, classRel);
			
				
				if (eReference.getLowerBound() == 1 && eReference.getUpperBound() == 1) {					
					facts = facts.and(
							x.join(refRel).one().forAll(d));							
				} else if (eReference.getLowerBound() == 0 && eReference.getUpperBound() == 1) {
					facts = facts.and(
							x.join(refRel).lone().forAll(d));
				} else if (eReference.getLowerBound() == 1 && eReference.getUpperBound() == -1) {
					facts = facts.and(
							x.join(refRel).some().forAll(d));
				} else if (eReference.getUpperBound() == 0) {
					facts = facts.and(x.join(refRel).no().forAll(d));
				} else if (eReference.getLowerBound() == 0 && eReference.getUpperBound() == -1) {}
				else {
					if(eReference.getLowerBound() > 1) {
						facts = facts.and(
								x.join(refRel).count().gte(IntConstant.constant(eReference.getLowerBound())).forAll(d));
					}
					if(eReference.getUpperBound() > 1){
						facts = facts.and(
								x.join(refRel).count().lte(IntConstant.constant(eReference.getUpperBound())).forAll(d));
					}
				}
				
				if(eReference.isContainment()){
					d = x.declare(Multiplicity.ONE, coDomain);
					facts = facts.and(
							refRel.join(x).lone().forAll(d));

                    if(containmentMap.containsKey(coDomainName))
                        containmentMap.get(coDomainName).add(refRel);
                    else{
                        Set<Relation> newContainers = new HashSet<>();
                        newContainers.add(refRel);
                        containmentMap.put(coDomainName,newContainers);
                    }
				}
				
			}
		}
		
	}

	protected void processAttributes(List<EAttribute> eAttributes) throws EError {

		Relation attribute;
		
		for(EAttribute attr : eAttributes) {
			String className = attr.getEContainingClass().getName();
			Expression domain = getDomain(className);
			String attrName = EchoHelper.featureLabel(metamodel,attr);
			if(attr.getEType().getName().equals("EBoolean")) {					
				attribute  = Relation.unary(attrName);
				facts = facts.and(attribute.in(domain));
                mapBoolType.put(attribute,getRelDomain(className));
			} else if(attr.getEType().getName().equals("EString")) {
				attribute = Relation.binary(attrName);
				facts = facts.and(attribute.function(domain, KodkodUtil.stringRel));
                mapType.put(attribute,getRelDomain(className));
			} else if(attr.getEType().getName().equals("EInt")) {
				attribute = Relation.binary(attrName);
				facts = facts.and(attribute.function(domain, Expression.INTS));
                mapType.put(attribute, getRelDomain(className));
			} 
			else if (attr.getEType() instanceof EEnum) {
                attribute = Relation.binary(attrName);
                facts = facts.and(attribute.function(domain,mapClassRel.get(attr.getEType().getName())));
                mapType.put(attribute, getRelDomain(className));
			} 
			else
				throw new EErrorUnsupported(
					EErrorUnsupported.PRIMITIVE_TYPE,
					"Primitive type of attribute not supported: "
							+ attr.getName() + ".", "",
					Task.TRANSLATE_METAMODEL);
			
            mapSfRel.put(className+"::"+attr.getName(),attribute);
            mapRelSf.put(attribute,attr);
		}
		
	}

	
	/**
	 * Translates an {@link EClass}
	 * @param ec the EClass to translate
	 * @throws EErrorTransform
	 */
	protected void processClass(EClass ec) throws EErrorUnsupported {
		Relation eCRel;
		if (mapClassRel.get(ec.getName()) != null) return;
		List<EClass> superTypes = ec.getESuperTypes();
		if(superTypes.size() > 1) 
			throw new EErrorUnsupported(EErrorUnsupported.MULTIPLE_INHERITANCE,
					"Multiple inheritance not allowed: " + ec.getName() + ".",
					"", Task.TRANSLATE_METAMODEL);

        if(!superTypes.isEmpty()) {
            EClass pEC = superTypes.get(0);
            String parentName = pEC.getName();

            if(mapClassRel.get(parentName) == null)
            	processClass(superTypes.get(0));

            Set<String> sons = mapParents.get(parentName);

            if(sons == null){
                sons = new HashSet<>();
                mapParents.put(parentName,sons);
            }

            sons.add(ec.getName());
		}

        if(!ec.isAbstract()){
            String relName = EchoHelper.classifierLabel(metamodel,ec);
            eCRel = Relation.unary(relName);
            mapClassRel.put(ec.getName(), eCRel);
        }else
            abstracts.add(ec);
    }



    Expression getDomain(String className){
        Expression result = mapClassRel.get(className);
        if(result == null)
            result = Expression.NONE;
        Set<String> sons = mapParents.get(className);        
        if(sons!=null){
//        	for(String son : sons)
//        		System.out.println(son);
            for(String son : sons)
        		result = result.union(getDomain(son));
        }
        return result;
    }

    Set<Relation> getRelDomain(String className){
        Set<Relation> res = new HashSet<>();
        Relation rel = mapClassRel.get(className);
        if(rel!=null)
            res.add(rel);
        Set<String> sons = mapParents.get(className);
        if(sons!=null){
        	for(String s : sons)
        		res.addAll(getRelDomain(s));
        }


        return res;
    }

    private Set<Relation> classNamesToRelation(Collection<String> names){
        Set<Relation> res = new HashSet<>();

        for(String s : names){
            res.add(mapClassRel.get(s));
        }

        return res;
    }

    protected void processEnums(List<EEnum> enumList) {
        for(EEnum enu : enumList) {
            enums.add(enu);
            Relation rel = Relation.unary(enu.getName());
            mapClassRel.put(enu.getName(),rel);
            for(EEnumLiteral el : enu.getELiterals())
                literals.put(enu.getName() + "@" + el.getName(), el);
        }
	}

    Set<EEnum> getEEnums(){
        return enums;
    }

    Collection<EEnumLiteral> getAllLiterals(){
//        for(EEnumLiteral el : literals.values())
//            System.out.println(el);

        return literals.values();
    }

	Collection<Relation> getClassRelations(){
		return mapClassRel.values();
	}



    Collection<Relation> getSfRelations(){
        return mapSfRel.values();
    }


    Collection<Relation> getAllRelations(){
        Collection<Relation> res = new HashSet<>();
        res.addAll(mapSfRel.values());
        res.addAll(mapClassRel.values());
        return res;
    }

    Relation getRelation(EClassifier cc)
    {
        return  mapClassRel.get(cc.getName());
    }

	Relation getRelation(EStructuralFeature f) {
		return mapSfRel.get(f.getEContainingClass().getName()+"::"+f.getName());
	}

	EClassifier getEClass(Relation classRelation) {
		//TODO: not sure of equals.
        //TODO: safer to have an extra map, also faster.
		for (String cla : mapClassRel.keySet())
			if (mapClassRel.get(cla).equals(classRelation)) return metamodel.getEObject().getEClassifier(cla);
		return null;
	}
    Pair<Set<Relation>,Set<Relation>> getRefTypes(Relation sf){
        return mapRefType.get(sf);
    }

    Set<Relation> getType(Relation at)
    {
               return mapType.get(at);
    }

    Set<Relation> getBoolType(Relation at)
    {
        return mapBoolType.get(at);
    }

    private void makeDisjointFact() {
        disjoint = Expression.intersection(getClassRelations()).no();
    }

    Set<EClass> getAbstracts() {
        return abstracts;
    }

    EStructuralFeature getSf(Relation rel){
        return mapRelSf.get(rel);
    }

    EEnumLiteral getProperLiteral(EEnumLiteral el){
        return literals.get(el.getEEnum().getName() + "@" + el.getName());
    }
}
