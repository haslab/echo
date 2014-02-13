package pt.uminho.haslab.echo.transform.alloy;

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
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import pt.uminho.haslab.echo.*;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.transform.EchoHelper;
import pt.uminho.haslab.echo.transform.EchoTranslator;
import pt.uminho.haslab.echo.transform.ast.IFormula;
import pt.uminho.haslab.echo.transform.ast.IIntExpression;
import pt.uminho.haslab.mde.model.EElement;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.model.EModel;
import pt.uminho.haslab.mde.transformation.EDependency;
import pt.uminho.haslab.mde.transformation.EModelDomain;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.ETransformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class AlloyEchoTranslator extends EchoTranslator {

    public AlloyEchoTranslator() {}

    //private static AlloyEchoTranslator instance = new AlloyEchoTranslator();

    public static AlloyEchoTranslator getInstance() {
        return (AlloyEchoTranslator) EchoTranslator.getInstance();
    }
    
	/** maps metamodel IDs to the respective Alloy translator*/
	private Map<String,EAlloyMetamodel> metamodelalloys = new HashMap<String,EAlloyMetamodel>();
	
	/** maps instance IDs to the respective Alloy translator*/
	private Map<String,EAlloyModel> modelalloys = new HashMap<String,EAlloyModel>();
	
	/** maps QVT-R IDs to the respective Alloy translator*/
	private Map<String,EAlloyTransformation> qvtalloys = new HashMap<String,EAlloyTransformation>();
	
	/** the initial command scopes of the target instance 
	 * only these need be increased in enforce mode, null if not enforce mode */
	private ConstList<CommandScope> scopes;
	
	/** the scope increment for each Sig, if in operation-based distance 
	 * null is GED */
	private Map<PrimSig,Integer> scopesincrement = new HashMap<PrimSig,Integer>();

		/** the abstract top level state sig */
    static final PrimSig STATE;
    static{
    	PrimSig s = null;
    	try {s = new PrimSig(EchoHelper.STATESIGNAME,Attr.ABSTRACT);}
    	catch (Err a){}
    	STATE = s;
    }

	/** Translates EObject models to the respective Alloy specs.
	 * @param model the model to translate
	 */
	public void translateModel(EModel model) throws EchoError {
		EAlloyMetamodel mmtrans = metamodelalloys.get(model.getMetamodel().ID);	
		EAlloyModel modeltrans = new EAlloyModel(model,mmtrans);
		modelalloys.put(model.ID,modeltrans);
	}
	
	public EAlloyModel getModel(String modelID) {
		return modelalloys.get(modelID);
	}
    
	@Override
    public boolean hasModel(String modelID) {
        return modelalloys.containsKey(modelID);
    }

	public void remModel(String modelID) {
		modelalloys.remove(modelID);
	}
	
	/** Translates ECore meta-models to the respective Alloy specs.
	 * @param metaModel the meta-model to translate
	 */
	public void translateMetaModel(EMetamodel metaModel) throws EchoError {
		EAlloyMetamodel alloymm = new EAlloyMetamodel(metaModel);
		metamodelalloys.put(metaModel.ID,alloymm);
		alloymm.translate();
	}
    
    @Override
    public boolean hasMetaModel(String metamodelID) {
        return metamodelalloys.containsKey(metamodelID);
    }
    
	public EAlloyMetamodel getMetamodel(String metamodelID) {
		EAlloyMetamodel metamodel = metamodelalloys.get(metamodelID);
		if (metamodelID == null)
			EchoReporter.getInstance().warning("Looking for null metamodel ID.", Task.TRANSLATE_METAMODEL);
		if (metamodel == null)
			EchoReporter.getInstance().warning("Looking for non-existent metamodel: "+metamodelID, Task.TRANSLATE_METAMODEL);
		return metamodel;
	}
	
	public void remMetaModel(String metamodelID) {
		metamodelalloys.remove(metamodelID);
	}
    
    /** Translates the QVT transformation to the respective Alloy specs
	 * Assumes default model dependencies
	 * @throws EchoError */
	public void translateTransformation(ETransformation constraint) throws EchoError {
		Map<String,List<EDependency>> deps = new HashMap<String,List<EDependency>>();
		for (ERelation r : constraint.getRelations()) {
			List<EDependency> aux2 = new ArrayList<EDependency>();
			for (EModelDomain dom : r.getDomains()) {
				List<EModelDomain> aux = new ArrayList<EModelDomain>(r.getDomains());
				aux.remove(dom);
				aux2.add(new EDependency(dom,aux,null));
			}
			deps.put(r.getName(),aux2);
		}	
		translateTransformation(constraint,deps);
	}
	
	public void translateTransformation(ETransformation constraint, Map<String,List<EDependency>> deps) throws EchoError {
		EAlloyTransformation qvtrans = new EAlloyTransformation(constraint,deps);	
		qvtalloys.put(constraint.ID, qvtrans);
	}
	
	public void remTransformation(String transformationID)  {
		qvtalloys.remove(transformationID);
	}
	
	public boolean hasTransformation(String transformationID)
	{
		return qvtalloys.containsKey(transformationID);
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
    public void writeAllInstances(EchoSolution solution, String metamodelID, String modelURI) throws EchoError {
        writeAllInstances(((AlloyTuple) solution.getContents()).getSolution(),metamodelID,modelURI,
                ((AlloyTuple)solution.getContents()).getState(modelURI));
    }

    @Override
    public void writeInstance(EchoSolution solution, String modelID) throws EchoError {
    	PrimSig statesig = ((AlloyTuple)solution.getContents()).getState(modelID);
        writeInstance(((AlloyTuple) solution.getContents()).getSolution(), modelID, statesig);
    }

    //TODO
    @Override
    public IIntExpression makeNumber(int n) {
        return null;
    }


    public void createScopesFromSizes(int overall, Map<Entry<String,String>,Integer> scopesmap) throws ErrorAlloy {
		Map<PrimSig,Integer> sc = new HashMap<PrimSig,Integer>();
		sc.put(Sig.STRING, overall);
		EchoReporter.getInstance().debug(scopesmap+"");
		for (Entry<String,String> cla : scopesmap.keySet()) {
			if (cla.getKey().equals("") && cla.getValue().equals("String"))
				sc.put(PrimSig.STRING, scopesmap.get(cla));
			else {
				//EchoReporter.getInstance().debug("got here2");
				EAlloyMetamodel e2a = metamodelalloys.get(cla.getKey());
				EClassifier eclass = e2a.metamodel.getEObject().getEClassifier(cla.getValue());
				PrimSig sig = e2a.getSigFromEClassifier(eclass);
				sc.put(sig, scopesmap.get(cla));
			}
		}
		//EchoReporter.getInstance().debug("got here3");
		scopes = AlloyUtil.createScope(new HashMap<PrimSig,Integer>(),sc);
	}
	
	public void createScopesFromOps(List<String> uris) throws ErrorAlloy {
		Map<PrimSig,Integer> scopesmap = new HashMap<PrimSig,Integer>();
		Map<PrimSig,Integer> scopesexact = new HashMap<PrimSig, Integer>();
		
		for (String uri : uris) {
			EAlloyModel x2a = modelalloys.get(uri);
			EAlloyMetamodel e2a = x2a.metamodel;
	
			scopesincrement = new HashMap<Sig.PrimSig, Integer>();
			for (String cl : e2a.getCreationCount().keySet()) {
				EClassifier eclass = e2a.metamodel.getEObject().getEClassifier(cl);
				PrimSig sig = e2a.getSigFromEClassifier(eclass);
				scopesincrement.put(sig,e2a.getCreationCount().get(cl));
			}
			
			for (PrimSig sig : scopesincrement.keySet()) {
				int count = x2a.getClassSigs(sig)==null?0:x2a.getClassSigs(sig).size();
				if (scopesmap.get(sig) == null) scopesmap.put(sig, count);
				else scopesmap.put(sig, scopesmap.get(sig) + count);
				PrimSig up = sig.parent;
				while (up != Sig.UNIV && up != null){
					if (scopesmap.get(up) == null) scopesmap.put(up, count);
					else scopesmap.put(up, scopesmap.get(up) + count);
					up = up.parent;
				}
			}
	
			scopesincrement.put(e2a.sig_metamodel,1);
			//scopesincrement.put(PrimSig.STRING,1);
			
			Integer s = scopesexact.get(e2a.sig_metamodel);
			s = (s==null)?1:s+1;
			scopesexact.put(e2a.sig_metamodel,s);
		}

		scopes = AlloyUtil.createScope(scopesmap,scopesexact);
	}	
	
	public void createScopesFromID(List<String> IDs) throws ErrorAlloy {
		Map<PrimSig,Integer> scopesmap = new HashMap<PrimSig,Integer>();
		Map<PrimSig,Integer> exact = new HashMap<PrimSig,Integer>();
		for (String ID : IDs) {
			EAlloyModel x2a = modelalloys.get(ID);
			EAlloyMetamodel e2a = x2a.metamodel;
			EchoReporter.getInstance().debug("*** Creating scopes for "+e2a.getAllSigs());
			for (PrimSig sig : e2a.getCAllSigs()) {
				int count = x2a.getClassSigs(sig)==null?0:x2a.getClassSigs(sig).size();
				if (scopesmap.get(sig) == null) scopesmap.put(sig, count);
				else scopesmap.put(sig, scopesmap.get(sig) + count);
				PrimSig up = sig.parent;
				while (up != Sig.UNIV && up != null){
					if (scopesmap.get(up) == null) scopesmap.put(up, count);
					else scopesmap.put(up, scopesmap.get(up) + count);
					up = up.parent;
				}
			}
		}
		scopes = AlloyUtil.createScope(scopesmap,exact);
		//System.out.println(this.scopes);
	}	
	
	ConstList<CommandScope> incrementScopes (List<CommandScope> scopes) throws ErrorSyntax  {
		List<CommandScope> list = new ArrayList<CommandScope>();
		
		//System.out.println("incs: "+scopesincrement);
		System.out.println("scps: "+scopes);
		if (!EchoOptionsSetup.getInstance().isOperationBased())
			for (CommandScope scope : scopes) 
				list.add(new CommandScope(scope.sig, scope.isExact, scope.startingScope+1));
		else
			for (CommandScope scope : scopes) {				
				Integer i = scopesincrement.get(scope.sig);
				if (i == null) i = 0;
				list.add(new CommandScope(scope.sig, scope.isExact, scope.startingScope+i));
				// need to manage inheritance
			}		
		return ConstList.make(list);
	}
	
	/** Writes an Alloy solution in the target instance file 
	 * @throws ErrorAlloy 
	 * @throws ErrorTransform */
	private void writeInstance(A4Solution sol,String targetID, PrimSig targetstate) throws EchoError {
		EAlloyModel model = modelalloys.get(targetID);
		List<PrimSig> instsigs = model.getAllSigs();
    	EElement rootobj = model.emodel.getRootEElement();
		PrimSig rootsig = model.getSigFromEObject(rootobj);
		writeXMIAlloy(sol,model.emodel.getURI(),rootsig,targetstate,model.metamodel,instsigs);
	}

	
	private void writeAllInstances(A4Solution sol, String metamodelID, String modelURI, PrimSig state) throws EchoError {
		EAlloyMetamodel e2a = metamodelalloys.get(metamodelID);
		List<EClass> rootclasses = e2a.getRootClass();
		if (rootclasses.size() != 1) throw new ErrorUnsupported("Could not resolve root class: "+rootclasses);
		PrimSig sig = e2a.getSigFromEClassifier(rootclasses.get(0));
		writeXMIAlloy(sol,modelURI,sig,state,e2a,null);
	}
	
	private void writeXMIAlloy(A4Solution sol, String targetURI, PrimSig rootatom, PrimSig state, EAlloyMetamodel trad,List<PrimSig> instsigs) throws EchoError {
		Alloy2XMI a2x = new Alloy2XMI(sol,rootatom,trad,state,instsigs);
		
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
		    "*", new  XMIResourceFactoryImpl());

		Resource resource = resourceSet.createResource(URI.createURI(targetURI));
		resource.getContents().add(a2x.getModel());
		EchoReporter.getInstance().debug("Should write "+a2x);

		/*
		* Save the resource using OPTION_SCHEMA_LOCATION save option toproduce 
		* xsi:schemaLocation attribute in the document
		*/
		Map<Object,Object> options = new HashMap<Object,Object>();
		options.put(XMIResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
		try{
		    resource.save(options);
	    }catch (Exception e) {
	    	throw new ErrorTransform(e.getMessage());
	    }
		
	}


	EAlloyTransformation getQVTTransformation(String qvtID) {
		EAlloyTransformation t = qvtalloys.get(qvtID);
		if (t == null) {
			EchoReporter.getInstance().warning("Looking for non-existing QVT-R: "+qvtID, Task.TRANSLATE_TRANSFORMATION);
		}
		return t;
	}
	

	ConstList<CommandScope> getScopes(){
		return scopes;
	}

	ConstList<CommandScope> getScopes(int strings) throws ErrorAlloy{
		List<CommandScope> aux = new ArrayList<CommandScope>();
		if (scopes != null)
			aux = new ArrayList<CommandScope>(scopes);
		try {
			aux.add(new CommandScope(PrimSig.STRING, true, strings));
		} catch (ErrorSyntax e) {
			throw new ErrorAlloy(e.getMessage());
		}
		scopes = ConstList.make(aux);
		return scopes;
	}



	public List<PrimSig> getEnumSigs(String metamodeluri){
		EAlloyMetamodel e2a = metamodelalloys.get(metamodeluri);
		List<PrimSig> aux = new ArrayList<PrimSig>(e2a.getEnumSigs());
		return aux;
	}	


	PrimSig getClassifierFromSig(EClassifier c) {
		if (c.getName().equals("EString")) return Sig.STRING;
		else if (c.getName().equals("EBoolean")) return Sig.NONE;
		else {
			EAlloyMetamodel e2a = metamodelalloys.get(c.getEPackage().eResource().getURI().path());
			return e2a.getSigFromEClassifier(c);
		}
	}

	PrimSig getSigFromClass(String metamodelID, EClassifier eclass) {
		EAlloyMetamodel e2a = metamodelalloys.get(metamodelID);
		return e2a.getSigFromEClassifier(eclass);
	}
	
	Field getStateFieldFromClass(String metamodelID, EClass eclass) {
		EAlloyMetamodel e2a = metamodelalloys.get(metamodelID);
		Field f =  e2a.getStateFieldFromClass(eclass);
		if (f == null)
			EchoReporter.getInstance().warning("Looking for non-existing state field: "+eclass, Task.TRANSLATE_METAMODEL);
		return f;
	}
	
	Field getFieldFromFeature(String metamodeluri, EStructuralFeature f) {
		EAlloyMetamodel e2a = metamodelalloys.get(metamodeluri);
		return e2a.getFieldFromSFeature(f);
	}

	
	public EStructuralFeature getESFeatureFromName(String pck, String cla, String fie) {
		EAlloyMetamodel e2a = metamodelalloys.get(pck);
		if (e2a == null) return null;
		EClass eclass = (EClass) e2a.metamodel.getEObject().getEClassifier(cla);
		return eclass.getEStructuralFeature(fie);
	}

	public EClassifier getEClassifierFromName(String metamodelID, String cla) {
		EAlloyMetamodel e2a = metamodelalloys.get(metamodelID);
		if (e2a == null) return null;
		return e2a.metamodel.getEObject().getEClassifier(cla);
	}

	/**
	 * returns true is able to determine determinism;
	 * false otherwise
	 * @param exp
	 * @return true if able to determine determinism, false otherwise
	 * @throws ErrorUnsupported 
	 */
	Boolean isFunctional(Expr exp) throws EchoError {
		IsFunctionalQuery q = new IsFunctionalQuery();
		try {
			return q.visitThis(exp);
		} catch (Err e1) { throw new ErrorUnsupported(e1.getMessage()); }
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
        	String metamodeluri = AlloyUtil.getMetamodelIDfromExpr(x);
        	EAlloyMetamodel e2a = metamodelalloys.get(metamodeluri);
        	if (e2a == null) return false;
        	EStructuralFeature sf = e2a.getSFeatureFromField(x);
        	if (sf == null) return false;
        
        	if (sf instanceof EAttribute && !sf.getEType().getName().equals("EBoolean")) return true;
        	if (sf.getLowerBound() == 1 && sf.getUpperBound() == 1) return true;
        	return false;
       }
    }


}
