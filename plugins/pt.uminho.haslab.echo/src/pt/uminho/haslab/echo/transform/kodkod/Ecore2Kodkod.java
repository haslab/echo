package pt.uminho.haslab.echo.transform.kodkod;

import kodkod.ast.*;
import kodkod.ast.operator.Multiplicity;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.*;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;

import java.util.*;




class Ecore2Kodkod {


    //TODO facts that mention classes with children must be changed.
    //TODO Use MDE

	EPackage ePackage;
	
	/** maps classes into respective Kodkod relations */
	private Map<String,Relation> mapClassRel;
	/** maps structural features into respective Kodkod relations */
	private Map<String,Relation> mapSfRel;
	/** maps signature names into respective classes */
	private Map<String,EClass> mapClassClass;


    /**facts about the meta-model*/
	private Formula facts;

    public Formula getFacts() {
        return facts;
    }

	public Ecore2Kodkod(EPackage metaModel){
		ePackage = metaModel;
		mapClassRel = new HashMap<String,Relation>();
		mapClassClass =  new HashMap<String,EClass>();
		mapSfRel = new HashMap<String,Relation>(); 
		facts = Formula.TRUE;
	}
	
	public void translate() throws ErrorTransform, ErrorUnsupported{
		List<EClass> classList = new LinkedList<EClass>();
		List<EDataType> dataList = new ArrayList<EDataType>();
		List<EEnum> enumList = new ArrayList<EEnum>();
		
		for(EClassifier e: ePackage.getEClassifiers()) {
			if (e instanceof EClass)
				classList.add((EClass)e);
			else if (e instanceof EEnum)
				enumList.add((EEnum) e);
			else if (e instanceof EDataType)
				dataList.add((EDataType) e);
		}
		
		
		processEnums(enumList);
		
		for (EClass c : classList)
			processClass(c);
		for (EClass c : classList)
			processAttributes(c.getEAttributes());
		for (EClass c : classList)
			processReferences(c.getEReferences());
		for (EClass c : classList)
			processAnnotations(c.getEAnnotations());
		for (EClass c : classList)
			processOperations(c.getEOperations());
		
		
	}
	
	
	
	private void processOperations(EList<EOperation> eOperations) {
		// TODO Auto-generated method stub
		
	}

	private void processAnnotations(EList<EAnnotation> eAnnotations) {
		// TODO Auto-generated method stub
		
	}

	private void processReferences(EList<EReference> eReferences) throws ErrorTransform {
		for(EReference eReference : eReferences) {
			String className = eReference.getEContainingClass().getName();
			String refName = KodkodUtil.pckPrefix(ePackage,className+"->"+eReference.getName());
			Relation classRel = mapClassRel.get(className);
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
				EClass cc = mapClassClass.get(eReference.getEReferenceType().getName());
				Relation trgRel = mapClassRel.get(cc.getName());
				Relation refRel = Relation.binary(refName);
				
				facts = facts.and(refRel.in(classRel.product(trgRel)));
				mapSfRel.put(eReference.getEContainingClass().getName()+"::"+eReference.getName(),refRel);
				
				if(eOpposite!= null){
					Relation opRel = getRelation(eOpposite);
					if(opRel!=null)
						facts = facts.and(refRel.eq(opRel.transpose()));
				}
				
				
				//TODO: check how relevant this bitwidth is in kodkod
				Integer bitwidth = EchoOptionsSetup.getInstance().getBitwidth();
				Integer max = (int) (Math.pow(2, bitwidth) / 2);
				if (eReference.getLowerBound() >= max || eReference.getLowerBound() < -max) throw new ErrorTransform("Bitwidth not enough to represent: "+eReference.getLowerBound()+".");
				if (eReference.getUpperBound() >= max || eReference.getUpperBound() < -max) throw new ErrorTransform("Bitwidth not enough to represent: "+eReference.getUpperBound()+".");
				
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
					d = x.declare(Multiplicity.ONE, trgRel);
					facts = facts.and(
							refRel.join(d.expression()).one().forAll(d));
				}
				
			}
		}
		
	}

	private void processAttributes(EList<EAttribute> eAttributes) throws ErrorUnsupported{

		
		Relation attribute;
		
		for(EAttribute attr : eAttributes) {
			String className = attr.getEContainingClass().getName();
			Relation classRel = mapClassRel.get(className);
			String attrName = KodkodUtil.pckPrefix(ePackage,className+"->"+attr.getName());
			if(attr.getEType().getName().equals("EBoolean")) {					
				attribute  = Relation.unary(attrName);
				facts = facts.and(attribute.in(classRel));
                mapSfRel.put(className+"::"+attr.getName(),attribute);
			} else if(attr.getEType().getName().equals("EString")) {
				attribute = Relation.binary(attrName);
				facts = facts.and(attribute.function(classRel, KodkodUtil.stringRel));
                mapSfRel.put(className+"::"+attr.getName(),attribute);
			} else if(attr.getEType().getName().equals("EInt")) {
				attribute = Relation.binary(attrName);
				facts = facts.and(attribute.function(classRel, Expression.INTS));
                mapSfRel.put(className+"::"+attr.getName(),attribute);
			} 
			else if (attr.getEType() instanceof EEnum) {
				//TODO
			} 
			else throw new ErrorUnsupported("Primitive type for attribute not supported: "+attr+".");
		}

		
	}

	
	/**
	 * Translates an {@link EClass}
	 * @param ec the EClass to translate
	 * @throws ErrorTransform
	 */
	private void processClass(EClass ec) throws ErrorTransform {
		Relation eCRel,parent = null;
		if (mapClassRel.get(ec.getName()) != null) return;
		List<EClass> superTypes = ec.getESuperTypes();
		if(superTypes.size() > 1) throw new ErrorTransform("Multiple inheritance not allowed: "+ec.getName()+".");
		if(!superTypes.isEmpty()) {
			parent = mapClassRel.get(superTypes.get(0).getName());
			if(parent == null) {
				processClass(superTypes.get(0));
				parent = mapClassRel.get(superTypes.get(0).getName());
			}
		}
		String relName = KodkodUtil.pckPrefix(ePackage,ec.getName());
		/*
		 * TODO:Think(and implement)  Abstract.
		 * facts and instance stuff.
		 * fact -> child1+child2+...+childN = abstractParent
		 * */
		eCRel = Relation.unary(relName);
		
		
		mapClassRel.put(ec.getName(), eCRel);
		mapClassClass.put(ec.getName(), ec);	
	}

	private void processEnums(List<EEnum> enumList) {
		// TODO Enums   -> save and then bind?
		
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

    Relation getRelation(EClass cc)
    {
        return  mapClassRel.get(cc.getName());
    }

	Relation getRelation(EStructuralFeature f) {
		return mapSfRel.get(f.getEContainingClass().getName()+"::"+f.getName());
	}
	
	/**
	 * Returns the {@link EClass} matching the class  name
	 * @param name the class name
	 * @return the matching class
	 */
	EClass getEClass(String name) {
		return mapClassClass.get(name);
	}
	
	EClassifier getEClass(Relation classRelation) {
		//TODO: not sure of equals.
		for (String cla : mapClassRel.keySet())
			if (mapClassRel.get(cla).equals(classRelation)) return ePackage.getEClassifier(cla);
		return null;
	}
	
	List<EClass> getRootClass() {
		Map<Integer,EClass> classes = new HashMap<Integer,EClass>();
		for (EClassifier obj : ePackage.getEClassifiers())
			if (obj instanceof EClass) classes.put(obj.getClassifierID(),(EClass) obj);
		Map<Integer,EClass> candidates = new HashMap<Integer,EClass>(classes);
			
		for (EClass obj : classes.values()) {
			for (EReference ref : obj.getEReferences())
				if (ref.isContainment()) 
					candidates.remove(ref.getEReferenceType().getClassifierID());
			List<EClass> sups = obj.getESuperTypes();
			if (sups != null && sups.size() != 0)
				if (!candidates.keySet().contains(sups.get(0).getClassifierID()))
					candidates.remove(obj.getClassifierID());				
		}			
		//System.out.println("Tops: "+candidates);
		return new ArrayList<EClass>(candidates.values());
	}
	
}
