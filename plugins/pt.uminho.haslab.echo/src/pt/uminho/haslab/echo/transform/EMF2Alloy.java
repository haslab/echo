package pt.uminho.haslab.echo.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;

import pt.uminho.haslab.echo.EchoOptions;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.alloy.AlloyUtil;
import pt.uminho.haslab.echo.emf.URIUtil;
import edu.mit.csail.sdg.alloy4.ConstList;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorSyntax;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.CommandScope;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprBinary;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprCall;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprConstant;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprITE;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprLet;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprList;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprQt;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprUnary;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprVar;
import edu.mit.csail.sdg.alloy4compiler.ast.Func;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;
import edu.mit.csail.sdg.alloy4compiler.ast.VisitQuery;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;

public class EMF2Alloy {

	/** maps metamodels to the respective state signatures (should be "abstract")*/
	private Map<String,Expr> metamodelstatesigs = new HashMap<String,Expr>();
	/** maps instances to the respective state signatures (should be "one")*/
	private Map<String,PrimSig> modelstatesigs = new HashMap<String,PrimSig>();
	/** maps metamodel URIs to the respective Alloy translator*/
	private Map<String,ECore2Alloy> metamodelalloys = new HashMap<String,ECore2Alloy>();
	/** maps instance URIs to the respective Alloy translator*/
	private Map<String,XMI2Alloy> modelalloys = new HashMap<String,XMI2Alloy>();
	/** maps qvt-r URIs to the respective Alloy translator*/
	private Map<String,QVTTransformation2Alloy> qvtalloys = new HashMap<String,QVTTransformation2Alloy>();
	/** the echo options */
	public final EchoOptions options;
	/** the initial command scopes of the target instance 
	 * only these need be increased in enforce mode, null if not enforce mode */
	private ConstList<CommandScope> scopes;
	/** the scope increment for each Sig, if in operation-based distance 
	 * null is GED */
	private Map<PrimSig,Integer> scopesincrement = new HashMap<PrimSig,Integer>();

	private Map<String,String> modelmetamodel = new HashMap<String,String>();
	
	/** the abstract top level state sig */
    public static final PrimSig STATE;
    static{
    	PrimSig s = null;
    	try {s = new PrimSig(AlloyUtil.STATESIGNAME,Attr.ABSTRACT);}
    	catch (Err a){}
    	STATE = s;
    }

	/**
	 * Constructs a new EMF to Alloy translator
	 * @param options the Echo run options
	 */
	public EMF2Alloy(EchoOptions options) throws ErrorAlloy, ErrorTransform{
		this.options = options;
	}

	/** Translates ECore meta-models to the respective Alloy specs.
	 * @param metamodel the meta-model to translate
	 */
	public void translateMetaModel(EPackage metamodel) throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser {
		createModelStateSigs(metamodel);
		ECore2Alloy mmtrans = new ECore2Alloy(metamodel,(PrimSig) metamodelstatesigs.get(metamodel.eResource().getURI().path()),this);
		metamodelalloys.put(metamodel.eResource().getURI().path(),mmtrans);
		try {
			mmtrans.translate();
		} catch (Exception e) {
			metamodelalloys.remove(metamodel.eResource().getURI().path());
			throw e;
		}
	}

    /** Creates the metamodels abstract state signatures */
	private void createModelStateSigs(EPackage metamodel) throws ErrorAlloy, ErrorTransform {
		PrimSig s = null;
		try {
			if (options.isOperationBased())
				s = new PrimSig(metamodel.eResource().getURI().path(),STATE);
			else
				s = new PrimSig(metamodel.eResource().getURI().path(),STATE,Attr.ABSTRACT);
			metamodelstatesigs.put(metamodel.eResource().getURI().path(), s);
		} catch (Err a) {throw new ErrorAlloy (a.getMessage()); }
	}
	
	/** Translates EObject models to the respective Alloy specs.
	 * @param model the model to translate
	 */
	public void translateModel(EObject model) throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser {
		createInstanceStateSigs(model);
		String modeluri = model.eResource().getURI().path();
		String metamodeluri = model.eClass().getEPackage().eResource().getURI().path();
		PrimSig state = modelstatesigs.get(model.eResource().getURI().toString());
		ECore2Alloy mmtrans = metamodelalloys.get(metamodeluri);			
		XMI2Alloy insttrans = new XMI2Alloy(model,mmtrans,state);
		modelmetamodel.put(modeluri, metamodeluri);
		modelalloys.put(modeluri,insttrans);
	}
	
    /** Creates the instances singleton state signatures */
	private void createInstanceStateSigs(EObject model) throws ErrorAlloy, ErrorTransform {
		String modeluri = model.eResource().getURI().toString();
		String metamodeluri = model.eClass().getEPackage().eResource().getURI().path();
		//System.out.println("metamodeluri: "+metamodeluri);
		try {
			String name = modeluri;
			PrimSig s = new PrimSig(name,(PrimSig) metamodelstatesigs.get(metamodeluri),Attr.ONE);
			modelstatesigs.put(modeluri, s);
		} catch (Err a) {throw new ErrorAlloy (a.getMessage()); }
	}
	
	/** Translates the QVT transformation to the respective Alloy specs 
	 * @throws Err */
	public void translateQVT(RelationalTransformation qvt) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		QVTTransformation2Alloy qvtrans = new QVTTransformation2Alloy(this,qvt);	
		String qvturi = URIUtil.resolveURI(qvt.eResource());
		qvtalloys.put(qvturi, qvtrans);
	}
		
	public void createScopesFromSizes(int overall, Map<Entry<String,String>,Integer> scopes, String uri) throws ErrorAlloy {
		Map<PrimSig,Integer> sc = new HashMap<PrimSig,Integer>();
		sc.put(Sig.STRING, overall);
		for (Entry<String,String> cla : scopes.keySet()) {
			if (cla.getKey().equals("") && cla.getValue().equals("String"))
				sc.put(PrimSig.STRING, scopes.get(cla));
			else {
				ECore2Alloy e2a = metamodelalloys.get(cla.getKey());
				PrimSig sig = e2a.getSigFromEClass(e2a.getEClassFromName(cla.getValue()));
				sc.put(sig, scopes.get(cla));
			}
		}
		this.scopes = AlloyUtil.createScope(new HashMap<PrimSig,Integer>(),sc);
	}
	
	public void createScopesFromOps(String uri) throws ErrorAlloy {
		Map<PrimSig,Integer> scopes = new HashMap<PrimSig,Integer>();
		XMI2Alloy x2a = modelalloys.get(uri);
		ECore2Alloy e2a = x2a.translator;

		for (String cl : e2a.getOCLAreNews().keySet()) {
			PrimSig sig = e2a.getSigFromEClass(e2a.getEClassFromName(cl));
			scopesincrement.put(sig,e2a.getOCLAreNews().get(cl));
		}
		
		for (PrimSig sig : scopesincrement.keySet()) {
			int count = x2a.getSigMap().get(sig.label)==null?0:x2a.getSigMap().get(sig.label).size();
			if (scopes.get(sig) == null) scopes.put(sig, count);
			else scopes.put(sig, scopes.get(sig) + count);
			PrimSig up = sig.parent;
			while (up != Sig.UNIV && up != null){
				if (scopes.get(up) == null) scopes.put(up, count);
				else scopes.put(up, scopes.get(up) + count);
				up = up.parent;
			}
		}
		scopesincrement.put(e2a.statesig,1);
		scopesincrement.put(PrimSig.STRING,1);

		
		Map<PrimSig,Integer> aux = new HashMap<PrimSig, Integer>();
		aux.put(e2a.statesig,1);

		this.scopes = AlloyUtil.createScope(scopes,aux);
	}	
	
	public void createScopesFromURI(String uri) throws ErrorAlloy {
		XMI2Alloy x2a = modelalloys.get(uri);
		ECore2Alloy e2a = x2a.translator;
		Map<PrimSig,Integer> scopes = new HashMap<PrimSig,Integer>();
		Map<PrimSig,Integer> exact = new HashMap<PrimSig,Integer>();
		
		for (PrimSig sig : e2a.getAllSigs()) {
			//System.out.println("SigMap: "+x2a.getSigMap());
			int count = x2a.getSigMap().get(sig.label)==null?0:x2a.getSigMap().get(sig.label).size();
			if (scopes.get(sig) == null) scopes.put(sig, count);
			else scopes.put(sig, scopes.get(sig) + count);
			PrimSig up = sig.parent;
			while (up != Sig.UNIV && up != null){
				if (scopes.get(up) == null) scopes.put(up, count);
				else scopes.put(up, scopes.get(up) + count);
				up = up.parent;
			}
		}
		this.scopes = AlloyUtil.createScope(scopes,exact);
		//System.out.println(this.scopes);
	}	
	
	public ConstList<CommandScope> incrementScopes (List<CommandScope> scopes) throws ErrorSyntax  {
		List<CommandScope> list = new ArrayList<CommandScope>();
		
		//System.out.println("incs: "+scopesincrement);
		//System.out.println("scps: "+scopes);
		if (!options.isOperationBased())
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
	public void writeInstance(A4Solution sol,String trguri, PrimSig targetstate) throws ErrorAlloy, ErrorTransform{
		XMI2Alloy inst = modelalloys.get(trguri);
		List<PrimSig> instsigs = inst.getSigList();
		EObject rootobj = inst.getRootEObject();
		PrimSig rootsig = inst.getSigFromEObject(rootobj);
		writeXMIAlloy(sol,trguri,rootsig,targetstate,inst.translator,instsigs);
	}
	
	public void writeAllInstances(A4Solution sol, String metamodeluri, String modeluri) throws ErrorAlloy, ErrorTransform, ErrorUnsupported{
		ECore2Alloy e2a = metamodelalloys.get(metamodeluri);
		List<EClass> rootclasses = e2a.getRootClass();
		if (rootclasses.size() != 1) throw new ErrorUnsupported("Could not resolve root class: "+rootclasses);
		PrimSig sig = e2a.getSigFromEClass(rootclasses.get(0));
		writeXMIAlloy(sol,modeluri,sig,e2a.statesig,e2a,null);
	}
	
	private void writeXMIAlloy(A4Solution sol, String uri, PrimSig rootatom, PrimSig state, ECore2Alloy trad,List<PrimSig> instsigs) throws ErrorAlloy, ErrorTransform {
		Alloy2XMI a2x = new Alloy2XMI(sol,rootatom,trad,state,options,instsigs);
		
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
		    "*", new  XMIResourceFactoryImpl());

		Resource resource = resourceSet.createResource(URI.createURI(uri));
		resource.getContents().add(a2x.getModel());

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
	
	public Expr getModelFact(String uri){
		if (modelalloys.get(uri) == null) return null;
		return modelalloys.get(uri).getFact();
	}

	public Func getQVTFact(String uri) {
		System.out.println(uri + " over "+qvtalloys.keySet());
		if (qvtalloys.get(uri) == null) return null;
		return qvtalloys.get(uri).getFunc();
	}
	
	public RelationalTransformation getQVTTransformation(String uri) {
		if (qvtalloys.get(uri) == null) return null;
		return qvtalloys.get(uri).getQVTTransformation();
	}

	public ConstList<CommandScope> getScopes(){
		return scopes;
	}

	public ConstList<CommandScope> getScopes(int strings) throws ErrorAlloy{
		List<CommandScope> aux = new ArrayList<CommandScope>(scopes);
		try {
			aux.add(new CommandScope(PrimSig.STRING, true, strings));
		} catch (ErrorSyntax e) {
			throw new ErrorAlloy(e.getMessage());
		}
		scopes = ConstList.make(aux);
		return scopes;
	}



	public List<PrimSig> getEnumSigs(String metamodeluri){
		ECore2Alloy e2a = metamodelalloys.get(metamodeluri);
		List<PrimSig> aux = new ArrayList<PrimSig>(e2a.getEnumSigs());
		return aux;
	}	

	public Expr getMetamodelStateSig(String metamodeluri){
		return metamodelstatesigs.get(metamodeluri);
	}
	
	public PrimSig getModelStateSig (String modeluri){
		return modelstatesigs.get(modeluri);
	}

	public PrimSig getClassifierFromSig(EClassifier c) {
		if (c.getName().equals("EString")) return Sig.STRING;
		else if (c.getName().equals("EBoolean")) return Sig.NONE;
		else {
			ECore2Alloy e2a = metamodelalloys.get(c.getEPackage().eResource().getURI().path());
			return e2a.getSigFromEClass((EClass) c);
		}
	}

	public PrimSig getSigFromClassName(String metamodeluri, String classname) {
		ECore2Alloy e2a = metamodelalloys.get(metamodeluri);
		EClass ecl =  e2a.getEClassFromName(classname);
		return e2a.getSigFromEClass(ecl);
	}
	
	public Field getStateFieldFromClassName(String metamodeluri, String classname) {
		ECore2Alloy e2a = metamodelalloys.get(metamodeluri);
		EClass ecl =  e2a.getEClassFromName(classname);
		return e2a.getStateFieldFromSig(e2a.getSigFromEClass(ecl));
	}
	
	public Field getFieldFromClassName(String metamodeluri, String classname, String fieldname) {
		ECore2Alloy e2a = metamodelalloys.get(metamodeluri);
		EStructuralFeature sf = e2a.getSFeatureFromName(fieldname, classname);
		return e2a.getFieldFromSFeature(sf);
	}
	
	public List<PrimSig> getMetamodelSigs(String metamodeluri) throws ErrorAlloy{
		ECore2Alloy e2a = metamodelalloys.get(metamodeluri);
		
		List<PrimSig> aux = new ArrayList<PrimSig>(e2a.getAllSigs());
		
		return aux;
	}	
	
	public EStructuralFeature getESFeatureFromName(String pck, String cla, String fie) {
		ECore2Alloy e2a = metamodelalloys.get(pck);
		if (e2a == null) return null;
		else return e2a.getSFeatureFromName(fie, cla);
	}

	public Func getMetamodelDeltaExpr(String metamodeluri) throws ErrorAlloy {
		return metamodelalloys.get(metamodeluri).getDeltaExpr();
	}
	
	public Map<String, List<PrimSig>> getInstanceSigs(String uri) {
		return modelalloys.get(uri).getSigMap();
	}
	
	public String getModelMetamodel(String modeluri) {
		return modelmetamodel.get(modeluri);
	}
	
	public Expr getConformsInstance(String uri) throws ErrorAlloy {
		Func f = modelalloys.get(uri).translator.getConforms();
		return f.call(modelstatesigs.get(uri));
	}

	public Expr getConformsInstance(String uri, PrimSig sig) throws ErrorAlloy {
		Func f = modelalloys.get(uri).translator.getConforms();
		System.out.println("Conforms: "+f.getBody());
		return f.call(sig);
	}
	
	public Expr getGenerateInstance(String metamodeluri, PrimSig sig) throws ErrorAlloy {
		Func f = metamodelalloys.get(metamodeluri).getGenerate();
		return f.call(sig);
	}

	public Expr getConformsAllInstances(String metamodeluri) throws ErrorAlloy {
		Func f = metamodelalloys.get(metamodeluri).getConforms();
		return f.call(metamodelstatesigs.get(metamodeluri));
	}
	
	public Expr remMetamodel(String metamodeluri) {
		metamodelalloys.remove(metamodeluri);
		return metamodelstatesigs.remove(metamodeluri);
	}

	public PrimSig remModel(String modeluri) {
		modelalloys.remove(modeluri);
		return modelstatesigs.remove(modeluri);
	}

	public List<EClass> getRootClass(String metamodeluri) {
		return metamodelalloys.get(metamodeluri).getRootClass();
	}

	/**
	 * returns true is able to determine determinism;
	 * false otherwise
	 * @param exp
	 * @return
	 * @throws ErrorUnsupported 
	 */
	public Boolean isFunctional(Expr e) throws ErrorUnsupported {
		IsFunctionalQuery q = new IsFunctionalQuery();
		try {
			return q.visitThis(e);
		} catch (Err e1) { throw new ErrorUnsupported(e1.getMessage()); }
	}
	
	private final class IsFunctionalQuery extends VisitQuery<Boolean> {

		IsFunctionalQuery() {}
		@Override public final Boolean visit(ExprQt x) { return false; };
		@Override public final Boolean visit(ExprBinary x) throws Err { 
			switch (x.op) {
				case JOIN : 
					//System.out.println("DEBUG FUNC JOIN: " + x.right + " is "+visitThis(x.right)+", "+x.left + " is "+visitThis(x.left));
					return (visitThis(x.right) && visitThis(x.left));
				default : return false;
			}
		};
        @Override public final Boolean visit(ExprCall x) { return false; };
        @Override public final Boolean visit(ExprList x) { return false; };
        @Override public final Boolean visit(ExprConstant x) { return false; };
        @Override public final Boolean visit(ExprITE x) { return false; };
        @Override public final Boolean visit(ExprLet x) { return false; };
        @Override public final Boolean visit(ExprUnary x) { return false; };
        @Override public final Boolean visit(ExprVar x) { return true; };
        @Override public final Boolean visit(Sig x) { 
        	return x.attributes.contains(Attr.ONE);
        };
        @Override public final Boolean visit(Sig.Field x) { 
        	String metamodeluri = AlloyUtil.getMetamodelURIfromExpr(x);
        	ECore2Alloy e2a = metamodelalloys.get(metamodeluri);
        	if (e2a == null) return false;
        	EStructuralFeature sf = e2a.getSFeatureFromField(x);
        	if (sf == null) return false;
        
        	if (sf instanceof EAttribute && !sf.getEType().getName().equals("EBoolean")) return true;
        	if (sf.getLowerBound() == 1 && sf.getUpperBound() == 1) return true;
        	return false;
       };
	}
}
