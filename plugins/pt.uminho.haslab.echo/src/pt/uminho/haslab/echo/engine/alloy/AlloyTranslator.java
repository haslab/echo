package pt.uminho.haslab.echo.engine.alloy;

import edu.mit.csail.sdg.alloy4.ConstList;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorSyntax;
import edu.mit.csail.sdg.alloy4compiler.ast.*;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import pt.uminho.haslab.echo.*;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.engine.EchoHelper;
import pt.uminho.haslab.echo.engine.CoreTranslator;
import pt.uminho.haslab.echo.engine.ITContext;
import pt.uminho.haslab.mde.model.EElement;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.model.EModel;
import pt.uminho.haslab.mde.transformation.EDependency;
import pt.uminho.haslab.mde.transformation.EModelDomain;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.ETransformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manages the translation of Echo artifacts into Alloy.
 * 
 * @author nmm,tmg
 * @version 0.4 20/02/2014
 */
class AlloyTranslator extends CoreTranslator {

    public static AlloyTranslator getInstance() {
        return (AlloyTranslator) CoreTranslator.getInstance();
    }
    
	/** Maps meta-model IDs to the respective Alloy translators. */
	private Map<String,AlloyMetamodel> metamodelalloys = new HashMap<String,AlloyMetamodel>();
	
	/** Maps model instance IDs to the respective Alloy translators. */
	private Map<String,AlloyModel> modelalloys = new HashMap<String,AlloyModel>();
	
	/** Maps transformation IDs to the respective Alloy translators. */
	private Map<String,AlloyTransformation> transalloys = new HashMap<String,AlloyTransformation>();
	
	/** The initial command scopes of the target instance.
	 * Only these need be increased in enforce mode, null if not enforce mode */
	private ConstList<CommandScope> scopes;
	
	/** The scope increment for each Sig, if under OBD. 
	 * Null under GED. */
	private Map<PrimSig,Integer> scopesincrement = new HashMap<PrimSig,Integer>();

	/** The abstract top level state sig, */
    public static final PrimSig STATE;
    static{
    	PrimSig s = null;
    	try {s = new PrimSig(EchoHelper.STATESIGNAME,Attr.ABSTRACT);}
    	catch (Err a){}
    	STATE = s;
    }

	/** 
	 * Translates meta-models to the respective Alloy representation.
	 * @param metamodel the meta-model to be translated.
	 * @return the Alloy representation.
	 */
	@Override
	public AlloyMetamodel translateMetamodel(EMetamodel metamodel) throws EError {
		EchoReporter.getInstance().start(Task.TRANSLATE_METAMODEL,
				metamodel.ID);

		AlloyMetamodel alloymm = new AlloyMetamodel(metamodel);
		// must be registred prior to translation
		metamodelalloys.put(metamodel.ID,alloymm);
		try {
			alloymm.translate();
		} catch (Exception e) {
			metamodelalloys.remove(metamodel.ID);
			throw e;
		}
		
		EchoReporter.getInstance().result(Task.TRANSLATE_METAMODEL, metamodel.getEObject().getName(), true);
		return alloymm;
	}
    
	/** 
	 * Checks whether a meta-model has already been processed into Alloy.
	 * @param metamodelID the ID of the meta-model.
	 * @return whether the meta-model has been processed.
	 */
    @Override
    public boolean hasMetamodel(String metamodelID) {
        return metamodelalloys.containsKey(metamodelID);
    }
    
    /**
	 * Retrieves a meta-model from its ID.
	 * Should have been already processed.
	 * @param metamodelID the ID of the meta-model.
	 * @return the processed meta-model.
     */
	@Override
	public AlloyMetamodel getMetamodel(String metamodelID) {
		if (metamodelID == null)
			EchoReporter.getInstance().warning("Looking for null metamodel ID.", Task.TRANSLATE_METAMODEL);
		AlloyMetamodel metamodel = metamodelalloys.get(metamodelID);
		if (metamodel == null)
			EchoReporter.getInstance().warning("Looking for non-existent metamodel: "+metamodelID, Task.TRANSLATE_METAMODEL);
		return metamodel;
	}
	
    /**
	 * Removes a processed meta-model from the manager.
	 * @param metamodelID the ID of the meta-model.
	 * @return whether the meta-model was removed.
     */
	@Override
	public boolean remMetamodel(String metamodelID) {
		return metamodelalloys.remove(metamodelID) != null;
	}
    
	
	/** Translates model instances to the respective Alloy representation.
	 * @param model the model instance to translated.
	 * @return the Alloy representation.
	 */
	@Override
	public AlloyModel translateModel(EModel model) throws EError {
		EchoReporter.getInstance().start(Task.TRANSLATE_MODEL, model.ID);

		AlloyMetamodel mmtrans = metamodelalloys.get(model.getMetamodel().ID);	
		AlloyModel modeltrans = new AlloyModel(model,mmtrans);
		modelalloys.put(model.ID,modeltrans);

		EchoReporter.getInstance().result(Task.TRANSLATE_MODEL, model.ID, true);

		return modeltrans;
	}
	
	@Override
	public AlloyModel getModel(String modelID) {
		return modelalloys.get(modelID);
	}
    
	@Override
    public boolean hasModel(String modelID) {
        return modelalloys.containsKey(modelID);
    }

	@Override
	public void remModel(String modelID) {
		modelalloys.remove(modelID);
	}
	
    /** Translates the transformation to the respective Alloy specs.
	 * Assumes default model dependencies.
	 * @throws EError */
	@Override
	public AlloyTransformation translateTransformation(ETransformation transformation) throws EError {
		Map<String,List<EDependency>> deps = new HashMap<String,List<EDependency>>();
//		if (constraint instanceof EATLTransformation)
//			for (ERelation r : constraint.getRelations()) {
//				List<EDependency> aux2 = new ArrayList<>();
//				List<EModelDomain> aux = new ArrayList<>();
//				aux.add(r.getDomains().get(0));
//				aux2.add(new EDependency(r.getDomains().get(1),aux,null));
//				deps.put(r.getName(),aux2);
//			}	
//		else
			for (ERelation r : transformation.getRelations()) {
				List<EDependency> aux2 = new ArrayList<EDependency>();
				for (EModelDomain dom : r.getDomains()) {
					List<EModelDomain> aux = new ArrayList<EModelDomain>(r.getDomains());
					aux.remove(dom);
					aux2.add(new EDependency(dom,aux,null));
				}
				deps.put(r.getName(),aux2);
			}	
		return translateTransformation(transformation,deps);
	}
	
	public AlloyTransformation translateTransformation(ETransformation transformation, Map<String,List<EDependency>> deps) throws EError {
		EchoReporter.getInstance().start(Task.TRANSLATE_TRANSFORMATION,
				transformation.ID);
		AlloyTransformation alloytrans = new AlloyTransformation(transformation,deps);	
		transalloys.put(transformation.ID, alloytrans);
		EchoReporter.getInstance().result(Task.TRANSLATE_TRANSFORMATION,
				transformation.ID, true);
		return alloytrans;
	}
	
	@Override
	public void remTransformation(String transformationID)  {
		transalloys.remove(transformationID);
	}
	
	@Override
	public boolean hasTransformation(String transformationID) {
		return transalloys.containsKey(transformationID);
	}
    
    @Override
    public AlloyFormula getTrueFormula() {
        return new AlloyFormula(Sig.NONE.no());
    }

    @Override
    public AlloyFormula getFalseFormula() {
        return new AlloyFormula(Sig.NONE.some());
    }

    @Override
    public void writeInstance(EchoSolution solution, String modelID) throws EError {
    	PrimSig statesig = ((AlloySolution)solution.getContents()).getState(modelID);
        writeInstance(((AlloySolution) solution.getContents()).getSolution(), modelID, statesig);
    }

    @Override
    public AlloyIntExpression makeNumber(int n) {
    	return new AlloyIntExpression(ExprConstant.makeNUMBER(n));
    }

	/**
	 * Initializes the scope of a set of models. 
	 * If operation-based, retrieves the creation count from operations and 
	 * considers only sigs involved in creations. 
	 * Otherwise, considers every element.
	 * Strings always incremented by 1.
	 * 
	 * @param modelIDs
	 *            the models' IDs
	 * @return 
	 * @throws EErrorAlloy
	 * @throws EErrorParser 
	 */
	ConstList<CommandScope> createScopesFromID(List<String> modelIDs, Map<String,Map<String,Integer>> additional, int strings) throws EErrorAlloy, EErrorParser {
		return createScopesFromID(modelIDs,new ArrayList<String>(modelIDs), additional, strings);
	}	

	ConstList<CommandScope> createScopesFromID(List<String> modelIDs, int strings) throws EErrorAlloy {
		try {
			return createScopesFromID(modelIDs,new ArrayList<String>(modelIDs), new HashMap<String,Map<String,Integer>>(), strings);
		} catch (EErrorParser e) {
			// should not occur for empty additional scopes
			e.printStackTrace();
			return null;
		}
	}

	ConstList<CommandScope> createScopesFromID(List<String> modelIDs, List<String> targetIDs, int strings) throws EErrorAlloy {
		try {
			return createScopesFromID(modelIDs, targetIDs, new HashMap<String,Map<String,Integer>>(), strings);
		} catch (EErrorParser e) {
			// should not occur for empty additional scopes
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Calculates the initial scope and increment steps from a set of models and target models.
	 * Increments are only defined over target models.
	 * Additionally provided scopes override those of the models.
	 * @param modelIDs
	 * @param targetIDs
	 * @param additional
	 * @param strings
	 * @return
	 * @throws EErrorAlloy
	 * @throws EErrorParser
	 */
	ConstList<CommandScope> createScopesFromID(List<String> modelIDs, List<String> targetIDs,
			Map<String, Map<String, Integer>> additional, int strings) throws EErrorAlloy, EErrorParser {
		Map<PrimSig, Integer> scopesmap = new HashMap<PrimSig, Integer>();
		Map<PrimSig, Integer> scopesexact = new HashMap<PrimSig, Integer>();

		for (String modelID : modelIDs) {
			AlloyModel model = modelalloys.get(modelID);
			AlloyMetamodel metamodel = model.metamodel;
			if (targetIDs.contains(modelID)) {
				if (EchoOptionsSetup.getInstance().isOperationBased()) {
					// gets creation count from operations
					scopesincrement = metamodel.getCreationCount();
					// gets initial scope for sigs with creations
					for (PrimSig p : metamodel.getCreationCount().keySet())
						scopesmap.put(p, model.getClassSigs(p).size());

					// also increments state sig
					scopesincrement.put(metamodel.SIG, 1);
					scopesexact.put(metamodel.SIG, 1);
				} else {
					scopesexact.put(metamodel.SIG, 0);
					// scope is the number of all elements
					for (PrimSig sig : metamodel.getCAllSigs()) {
						if (!(metamodel.getEClassifierFromSig(sig) instanceof EEnum)) {
							int count = model.getClassSigs(sig) == null ? 0 : model.getClassSigs(sig).size();
							scopesmap.put(sig, count);
							scopesincrement.put(sig, 1);
						}
					}
					EClass rootc = model.emodel.getRootEElement().type;
					scopesincrement.put(model.metamodel.getSigFromEClassifier(rootc), 0);
				}
			} else {
				scopesexact.put(metamodel.SIG, 0);
			}
		}

		scopesexact.put(Sig.STRING, strings);
		scopesincrement.put(Sig.STRING, 1);

		// additional scopes override those calculated from the models
		for (String mm : additional.keySet()) {
			if (mm.equals("") && additional.get(mm).get("String") != null)
				scopesexact.put(Sig.STRING, additional.get(mm).get("String"));
			else {
				AlloyMetamodel e2a = metamodelalloys.get(mm);
				for (String c : additional.get(mm).keySet()) {
					EClassifier eclass = e2a.metamodel.getEObject().getEClassifier(c);
					if (eclass == null)
						throw new EErrorParser(EErrorParser.SCOPE, "Invalid additional scopes: " + c,
								Task.GENERATE_TASK);
					PrimSig sig = e2a.getSigFromEClassifier(eclass);
					scopesexact.put(sig, additional.get(mm).get(c));
					scopesmap.remove(sig);
					scopesincrement.put(sig, 0);
				}
			}
		}

		scopes = AlloyHelper.createScope(scopesmap, scopesexact);

		StringBuilder sb = new StringBuilder("Scope: ");
		for (CommandScope e : scopes) {
			String l = EchoHelper.getClassifierName(e.sig.label);
			if (l == null)
				l = e.sig.label;
			sb.append(l + "=(" + e.endingScope + (e.isExact ? "*" : "") + "," + scopesincrement.get(e.sig) + "), ");
		}
		EchoReporter.getInstance().debug(sb.toString().substring(0, sb.length() - 2));

		return scopes;
	}

	
	/**
	 * Increments a collection of scopes by the defined increment.
	 * If operation-based, increments only by the defined creation count 
	 * (which should include inheritance).
	 * Otherwise, increments everything by 1.
	 * @param scopes the original scope
	 * @return <code>scopes</code> incremented
	 * @throws EErrorAlloy 
	 */
	ConstList<CommandScope> incrementScopes(List<CommandScope> scopes) throws EErrorAlloy {
		List<CommandScope> list = new ArrayList<CommandScope>();
		for (CommandScope scope : scopes) {
			Integer i = scopesincrement.get(scope.sig);
			if (i == null)
				i = 0;
			try {
				list.add(new CommandScope(scope.sig, scope.isExact,
						scope.startingScope + i));
			} catch (ErrorSyntax e) {
				throw new EErrorAlloy(EErrorAlloy.FAIL_SCOPE, e.getMessage(), e, Task.CORE_RUN);
			}
		}
		return ConstList.make(list);
	}
	
	/** Writes an Alloy solution in the target instance file 
	 * @throws EErrorAlloy 
	 * @throws EErrorTransform */
	private void writeInstance(A4Solution sol,String targetID, PrimSig targetstate) throws EError {
		AlloyModel model = modelalloys.get(targetID);
		List<PrimSig> instsigs = model.getAllSigs();
    	EElement rootobj = model.emodel.getRootEElement();
		PrimSig rootsig = model.getSigFromEObject(rootobj);
		writeXMIAlloy(sol,model.emodel.getURI(),rootsig,targetstate,model.metamodel,instsigs);
	}
	
	private void writeAllInstances(A4Solution sol, String metamodelID, String modelURI, PrimSig state) throws EError {
		AlloyMetamodel e2a = metamodelalloys.get(metamodelID);
		List<EClass> rootclasses = e2a.metamodel.getRootClass();
		if (rootclasses.size() != 1) throw new EErrorUnsupported(EErrorUnsupported.MULTIPLE_ROOT,"Could not resolve root class: "+rootclasses,Task.TRANSLATE_MODEL);
		PrimSig sig = e2a.getSigFromEClassifier(rootclasses.get(0));
		writeXMIAlloy(sol,modelURI,sig,state,e2a,null);
	}
	
	private void writeXMIAlloy(A4Solution sol, String targetURI, PrimSig rootatom, PrimSig state, AlloyMetamodel trad,List<PrimSig> instsigs) throws EError {
		Alloy2XMI a2x = new Alloy2XMI(sol,rootatom,trad,state,instsigs);
		
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
		    "*", new  XMIResourceFactoryImpl());
		
		Resource resource = resourceSet.createResource(URI.createURI(targetURI));
		resource.getContents().add(a2x.getModel());
		
		/*
		* Save the resource using OPTION_SCHEMA_LOCATION save option toproduce 
		* xsi:schemaLocation attribute in the document
		*/
		Map<Object,Object> options = new HashMap<Object,Object>();
		options.put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
		try{
		    resource.save(options);
	    }catch (Exception e) {
	    	throw new EErrorTransform(EErrorParser.MODEL,e.getMessage(),Task.TRANSLATE_MODEL);
	    }
		
	}

	@Override
	public AlloyTransformation getTransformation(String transformationID) {
		AlloyTransformation t = transalloys.get(transformationID);
		if (t == null) {
			EchoReporter.getInstance().warning("Looking for non-existing transformation: "+transformationID, Task.TRANSLATE_TRANSFORMATION);
		}
		return t;
	}
	
	List<PrimSig> getEnumSigs(String metamodeluri){
		AlloyMetamodel e2a = metamodelalloys.get(metamodeluri);
		List<PrimSig> aux = new ArrayList<PrimSig>(e2a.getEnumSigs());
		return aux;
	}	


	PrimSig getClassifierFromSig(EClassifier c) {
		if (c.getName().equals("EString")) return Sig.STRING;
		else if (c.getName().equals("EBoolean")) return Sig.NONE;
		else {
			AlloyMetamodel e2a = metamodelalloys.get(c.getEPackage().eResource().getURI().path());
			return e2a.getSigFromEClassifier(c);
		}
	}

	PrimSig getSigFromClass(String metamodelID, EClassifier eclass) {
		AlloyMetamodel e2a = metamodelalloys.get(metamodelID);
		return e2a.getSigFromEClassifier(eclass);
	}
	
	Field getStateFieldFromClass(String metamodelID, EClass eclass) {
		AlloyMetamodel e2a = metamodelalloys.get(metamodelID);
		Field f =  e2a.getStateFieldFromClass(eclass);
		if (f == null)
			EchoReporter.getInstance().warning("Looking for non-existing state field: "+eclass, Task.TRANSLATE_METAMODEL);
		return f;
	}
	
	Field getFieldFromFeature(String metamodeluri, EStructuralFeature f) {
		AlloyMetamodel e2a = metamodelalloys.get(metamodeluri);
		return e2a.getFieldFromSFeature(f);
	}

	/**
	 * returns true is able to determine determinism;
	 * false otherwise
	 * @param exp
	 * @return true if able to determine determinism, false otherwise
	 * @throws EErrorUnsupported 
	 */
	Boolean isFunctional(Expr exp) throws EError {
		IsFunctionalQuery q = new IsFunctionalQuery();
		try {
			return q.visitThis(exp);
		} catch (Err e1) { throw new EErrorUnsupported(EErrorUnsupported.ALLOY,e1.getMessage(),Task.CORE_RUN); }
	}
	
	private final class IsFunctionalQuery extends VisitQuery<Boolean> {

		IsFunctionalQuery() {}
		@Override public final Boolean visit(ExprQt x) { return false; }

        @Override public final Boolean visit(ExprBinary x) throws Err {
			switch (x.op) {
				case JOIN : 
					//System.out.println("DEBUG FUNC JOIN: " + x.right + " is "+visitThis(x.right)+", "+x.left + " is "+visitThis(x.left));
					return (visitThis(x.right) && visitThis(x.left));
				default : return false;
			}
		}

        @Override public final Boolean visit(ExprCall x) { return false; }

        @Override public final Boolean visit(ExprList x) { return false; }

        @Override public final Boolean visit(ExprConstant x) { return false; }

        @Override public final Boolean visit(ExprITE x) { return false; }

        @Override public final Boolean visit(ExprLet x) { return false; }

        @Override public final Boolean visit(ExprUnary x) { return false; }

        @Override public final Boolean visit(ExprVar x) { return true; }

        @Override public final Boolean visit(Sig x) {
        	return x.attributes.contains(Attr.ONE);
        }

        @Override public final Boolean visit(Sig.Field x) {
        	String metamodeluri = EchoHelper.getMetamodelIDfromLabel(x.label);
        	AlloyMetamodel e2a = metamodelalloys.get(metamodeluri);
        	if (e2a == null) return false;
        	EStructuralFeature sf = e2a.getSFeatureFromField(x);
        	if (sf == null) return false;
        
        	if (sf instanceof EAttribute && !sf.getEType().getName().equals("EBoolean")) return true;
        	if (sf.getLowerBound() == 1 && sf.getUpperBound() == 1) return true;
        	return false;
       }
    }

	@Override
	public AlloyExpression getEmptyExpression() {
		return new AlloyExpression(Sig.NONE);
	}

	@Override
	public ITContext newContext() {
		return new AlloyContext();
	}

	@Override
	public Set<String> strings() {
		Set<String> s = new HashSet<String>();
		for (AlloyModel m : modelalloys.values())
			s.addAll(m.strings());
		return s;
	}
}
