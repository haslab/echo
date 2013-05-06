package pt.uminho.haslab.echo.transform;

import java.io.IOException;
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
import pt.uminho.haslab.echo.emf.EMFParser;
import edu.mit.csail.sdg.alloy4.ConstList;
import edu.mit.csail.sdg.alloy4.Err;
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

	/** the parsed EMF resources */
	public final EMFParser parser;

	/** maps metamodels to the respective state signatures (should be "abstract")*/
	private Map<String,Expr> modelstatesigs = new HashMap<String,Expr>();
	/** maps instances to the respective state signatures (should be "one")*/
	private Map<String,PrimSig> inststatesigs = new HashMap<String,PrimSig>();
	/** maps metamodels to the respective Alloy translator*/
	private Map<String,ECore2Alloy> modeltrads = new HashMap<String,ECore2Alloy>();
	/** maps instances to the respective Alloy translator*/
	private Map<String,XMI2Alloy> insttrads = new HashMap<String,XMI2Alloy>();

	private Map<String,QVTTransformation2Alloy> qvttrads = new HashMap<String,QVTTransformation2Alloy>();
	
	public final EchoOptions options;

	/** the abstract top level state sig */
    public static final PrimSig STATE;
    static{
    	PrimSig s = null;
    	try {s = new PrimSig("State_",Attr.ABSTRACT);}
    	catch (Err a){}
    	STATE = s;
    }
    
	/** the initial command scopes of the target instance 
	 * only these need be increased in enforce mode, null if not enforce mode */
	private ConstList<CommandScope> scopes;
	
	/**
	 * Constructs a new EMF to Alloy translator
	 * @param parser the parsed EMF resources
	 * @param options the Echo run options
	 */
	public EMF2Alloy(EMFParser parser, EchoOptions options) throws ErrorAlloy, ErrorTransform{
		this.parser = parser;
		this.options = options;
	}

	
	/** Translates ECore metamodels to the respective Alloy specs */
	public void translateModel(EPackage pck) throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser {
		ECore2Alloy mmtrans = new ECore2Alloy(pck,(PrimSig) modelstatesigs.get(pck.getName()),this);
		modeltrads.put(pck.getName(),mmtrans);
		mmtrans.translate();
	}

	/** Translates XMI instances to the respective Alloy specs */
	public void translateInstance(EObject obj) throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser {
		String mdl = obj.eClass().getEPackage().getName();
		PrimSig state = inststatesigs.get(obj.eResource().getURI().toString());
		ECore2Alloy mmtrans = modeltrads.get(mdl);			
		XMI2Alloy insttrans = new XMI2Alloy(obj,mmtrans,state);
		insttrads.put(obj.eResource().getURI().toString(),insttrans);
	}
	
	/** Translates the QVT transformation to the respective Alloy specs 
	 * @throws Err */
	public void translateQVT(String uri) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		QVTTransformation2Alloy qvtrans = new QVTTransformation2Alloy(this,parser.getTransformation(uri));		
		qvttrads.put(uri, qvtrans);
	}
	
    /** Creates the metamodels abstract state signatures */
	public void createModelStateSigs(EPackage mdl) throws ErrorAlloy, ErrorTransform {
		PrimSig s = null;
		try {
			s = new PrimSig(mdl.getName(),STATE,Attr.ABSTRACT);
			modelstatesigs.put(s.label, s);
		} catch (Err a) {throw new ErrorAlloy (a.getMessage()); }
	}
	
    /** Creates the instances singleton state signatures */
	public void createInstanceStateSigs(EObject inst) throws ErrorAlloy, ErrorTransform {
		String uri = inst.eResource().getURI().toString();
		String pck = inst.eClass().getEPackage().getName();
		try {
			String name = uri;
			PrimSig s = new PrimSig(name,(PrimSig) modelstatesigs.get(pck),Attr.ONE);
			inststatesigs.put(uri, s);
		} catch (Err a) {throw new ErrorAlloy (a.getMessage()); }
	}
		
	/*
	public void createScopesFromTargetInstance(String trguri) throws ErrorAlloy {
		XMI2Alloy i2a = insttrads.get(trguri);
		if (i2a != null) {
			List<PrimSig> modelsigs = i2a.translator.getSigList();
			scopes = AlloyUtil.createScopeFromSigs(modelsigs, i2a.getSigMap());
		} else {
			List<PrimSig> modelsigs = modeltrads.get(targetstatesig.parent.label).getSigList();
			scopes = AlloyUtil.createScopeFromSigs(modelsigs, new HashMap<String,List<PrimSig>>());	
		}
	}*/
	
	public void createScopesFromSizes(int overall, Map<Entry<String,String>,Integer> scopes) throws ErrorAlloy {
		Map<PrimSig,Integer> sc = new HashMap<PrimSig,Integer>();
		for (Entry<String,String> cla : scopes.keySet()) {
			ECore2Alloy e2a = modeltrads.get(cla.getKey());
			PrimSig sig = e2a.getSigFromEClass(e2a.getEClassFromName(cla.getValue()));
			sc.put(sig, scopes.get(cla));
		}
		for (Expr sig : modelstatesigs.values())
			sc.put((PrimSig) sig,1);
		sc.put(Sig.STRING, overall);
		this.scopes = AlloyUtil.createScope(sc,true);
	}
	
	
	/** Writes an Alloy solution in the target instance file 
	 * @throws ErrorAlloy 
	 * @throws ErrorTransform */
	public void writeInstance(A4Solution sol,String trguri, PrimSig targetstate) throws ErrorAlloy, ErrorTransform{
		XMI2Alloy inst = insttrads.get(trguri);
		List<PrimSig> instsigs = inst.getSigList();
		EObject rootobj = inst.getRootEObject();
		PrimSig rootsig = inst.getSigFromEObject(rootobj);
		writeXMIAlloy(sol,trguri,rootsig,targetstate,inst.translator,instsigs);
	}
	
	public void writeAllInstances(A4Solution sol, String mdluri) throws ErrorAlloy, ErrorTransform{
		EPackage pck = parser.getModelsFromUri(mdluri);
		String uri = mdluri.replace(".ecore", ".xmi");
		ECore2Alloy e2a = modeltrads.get(pck.getName());
		List<EClass> topclass = parser.getTopObject(mdluri);
		if (topclass.size() != 1) throw new ErrorTransform("Could not resolve top class.","");
		PrimSig sig = e2a.getSigFromEClass(topclass.get(0));
		writeXMIAlloy(sol,uri,sig, e2a.statesig,e2a,null);
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
		   }catch (IOException e) {
		     e.printStackTrace();
		   }
		
	}
	
	public Expr getInstanceFact(String uri){
		return insttrads.get(uri).getFact();
	}

	public Func getQVTFact(String uri) {
		return qvttrads.get(uri).getFunc();
	}
	
	public RelationalTransformation getQVTTransformation(String uri) {
		return qvttrads.get(uri).getQVTTransformation();
	}

	public ConstList<CommandScope> getScopes(){
		return scopes;
	}

	
	public List<PrimSig> getAllSigsFromName(String uri) throws ErrorAlloy{
		ECore2Alloy e2a = modeltrads.get(uri);
		List<PrimSig> aux = new ArrayList<PrimSig>(e2a.getAllSigs());
		return aux;
	}	

	public List<PrimSig> getEnumSigsFromName(String uri){
		ECore2Alloy e2a = modeltrads.get(uri);
		List<PrimSig> aux = new ArrayList<PrimSig>(e2a.getEnumSigs());
		return aux;
	}	

	public List<PrimSig> getAllSigsFromURI(String uri) throws ErrorAlloy{
		ECore2Alloy e2a = modeltrads.get(parser.getModelsFromUri(uri).getName());
		List<PrimSig> aux = new ArrayList<PrimSig>(e2a.getAllSigs());
		return aux;
	}	

	public Expr getModelStateSig(String mm){
		return modelstatesigs.get(mm);
	}
	
	public PrimSig getInstanceStateSigFromURI (String uri){
		return inststatesigs.get(uri);
	}

	public PrimSig getClassifierFromSig(EClassifier c) {
		if (c.getName().equals("EString")) return Sig.STRING;
		else if (c.getName().equals("EBoolean")) return Sig.NONE;
		else {
			ECore2Alloy e2a = modeltrads.get(c.getEPackage().getName());
			return e2a.getSigFromEClass((EClass) c);
		}
	}

	public PrimSig getSigFromName(String pck, String cla) {
		ECore2Alloy e2a = modeltrads.get(pck);
		EClass ecl =  e2a.getEClassFromName(cla);
		return e2a.getSigFromEClass(ecl);
	}
	
	public Field getStateFieldFromName(String pck, String cla) {
		ECore2Alloy e2a = modeltrads.get(pck);
		EClass ecl =  e2a.getEClassFromName(cla);
		return e2a.getStateFieldFromSig(e2a.getSigFromEClass(ecl));
	}
	
	public Field getFieldFromName(String pck, String cla, String fie) {
		ECore2Alloy e2a = modeltrads.get(pck);
		EStructuralFeature sf = e2a.getSFeatureFromName(fie, cla);
		return e2a.getFieldFromSFeature(sf);
	}
	
	public List<PrimSig> getModelSigs(String pck) {
		return modeltrads.get(pck).getClassSigs();
	}
	
	public EStructuralFeature getESFeatureFromName(String pck, String cla, String fie) {
		ECore2Alloy e2a = modeltrads.get(pck);
		if (e2a == null) return null;
		else return e2a.getSFeatureFromName(fie, cla);
	}

	public Expr getModelDeltaExpr(String pck, PrimSig m, PrimSig n) throws ErrorAlloy {
		return modeltrads.get(pck).getDeltaExpr(m, n);
	}
	
	public Map<String, List<PrimSig>> getInstanceSigs(String uri) {
		return insttrads.get(uri).getSigMap();
	}
	
	
	public Expr getConformsInstance(String uri) throws ErrorAlloy {
		Func f = insttrads.get(uri).translator.getConforms();
		return f.call(inststatesigs.get(uri));
	}

	public Expr getConformsInstance(String uri, PrimSig sig) throws ErrorAlloy {
		Func f = insttrads.get(uri).translator.getConforms();
		return f.call(sig);
	}

	public Expr getConformsAllInstances(String uri) throws ErrorAlloy {
		String name = parser.getModelsFromUri(uri).getName();
		Func f = modeltrads.get(name).getConforms();
		return f.call(modelstatesigs.get(name));
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
        	ECore2Alloy e2a = modeltrads.get(x.label.split("_")[0]);
        	if (e2a == null) return false;
        	EStructuralFeature sf = e2a.getSFeatureFromField(x);
        	if (sf == null) return false;
        
        	return (sf instanceof EAttribute || (sf.getLowerBound() == 1 && sf.getUpperBound() == 1));
       };
	}
}
