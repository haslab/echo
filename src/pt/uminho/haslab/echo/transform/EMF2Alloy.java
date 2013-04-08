package pt.uminho.haslab.echo.transform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

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
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.VisitQuery;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;

public class EMF2Alloy {

	/** the parsed EMF resources */
	public final EMFParser parser;
	/** the echo run options */
	public final EchoOptions options;

	/** maps metamodels to the respective state signatures (should be "abstract")*/
	private Map<String,Expr> modelstatesigs = new HashMap<String,Expr>();
	/** maps instances to the respective state signatures (should be "one")*/
	private Map<String,PrimSig> inststatesigs = new HashMap<String,PrimSig>();
	/** maps metamodels to the respective Alloy translator*/
	private Map<String,ECore2Alloy> modeltrads = new HashMap<String,ECore2Alloy>();
	/** maps instances to the respective Alloy translator*/
	private Map<String,XMI2Alloy> insttrads = new HashMap<String,XMI2Alloy>();

	/** the abstract top level state sig */
    public static final PrimSig STATE;
    static{
    	PrimSig s = null;
    	try {s = new PrimSig("State_",Attr.ABSTRACT);}
    	catch (Err a){}
    	STATE = s;
    }
    
	/** the complete Alloy fact defining the instance models */
	private Expr instancefact = Sig.NONE.no();
	/** the Alloy fact denoting the QVT relations (true if not enforce mode) */
	private Expr qvtfact = Sig.NONE.no();
	/** the initial command scopes of the target instance 
	 * only these need be increased in enforce mode, null if not enforce mode */
	private ConstList<CommandScope> scopes;
	/** the Alloy expression denoting the delta function (true if not enforce mode) */
	private Expr deltaexpr = Sig.NONE.no();
	/** the target state signature (true if not enforce mode) */
	private PrimSig targetstatesig = null;

	/**
	 * Constructs a new EMF to Alloy translator
	 * @param parser the parsed EMF resources
	 * @param options the Echo run options
	 */
	public EMF2Alloy(EMFParser parser,EchoOptions options) throws ErrorAlloy, ErrorTransform{
		this.parser = parser;
		this.options = options;
		
		createModelStateSigs();
		createInstanceStateSigs();
	}
	
	/** Translates ECore metamodels to the respective Alloy specs */
	public void translateModels() throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser {
		for (EPackage epck : parser.getModels()) {
			ECore2Alloy mmtrans = new ECore2Alloy(epck,(PrimSig) modelstatesigs.get(epck.getName()),this);
			modeltrads.put(epck.getName(),mmtrans);
			mmtrans.translate();
		}	
	}

	/** Translates XMI instances to the respective Alloy specs */
	public void translateInstances() throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser {
		for (String name: options.getInstances()) {
			String mdl = parser.getInstanceFromUri(name).eClass().getEPackage().getName();
			PrimSig state = inststatesigs.get(name);
			ECore2Alloy mmtrans = modeltrads.get(mdl);			
			EObject instmodel = parser.getInstanceFromUri(name);
			XMI2Alloy insttrans = new XMI2Alloy(instmodel,mmtrans,state);
			insttrads.put(name,insttrans);
			instancefact = AlloyUtil.cleanAnd(instancefact,insttrans.getFact());
		}
	}
	
	/** Translates the QVT transformation to the respective Alloy specs */
	public void translateQVT() throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		String name = parser.getInstanceUri(options.getDirection());
		PrimSig back = inststatesigs.get(name);
		if (options.isEnforce()) {
			createTargetStateSig();
			ECore2Alloy mmtrans =  modeltrads.get(back.parent.label);
			deltaexpr = mmtrans.getDeltaExpr(targetstatesig,back);
			inststatesigs.put(name,targetstatesig);
			PrimSig mdl = targetstatesig.parent;
			
			Expr sourcemdlnosource = null;
			try{
				for (PrimSig s : mdl.descendents())
					if (!s.equals(back)) 
						if (sourcemdlnosource != null) sourcemdlnosource = sourcemdlnosource.plus(s);
						else sourcemdlnosource = s;
			} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"AlloyUtil",targetstatesig); }

			modelstatesigs.put(mdl.label, sourcemdlnosource);
			
		}
		QVTTransformation2Alloy qvtrans = new QVTTransformation2Alloy(this,parser.getTransformation());
		if (options.isEnforce()) {
			inststatesigs.put(name,back);
			modelstatesigs.put(targetstatesig.parent.label, targetstatesig.parent);
		}
		
		Map<String,Expr> qvtfacts = qvtrans.getFact();
		for (String e : qvtfacts.keySet()){
			qvtfact = AlloyUtil.cleanAnd(qvtfact, qvtfacts.get(e));
		}	
	}
	
    /** Creates the metamodels abstract state signatures */
	private void createModelStateSigs() throws ErrorAlloy, ErrorTransform {
		PrimSig s = null;
		for (EPackage mdl : parser.getModels()){
			try {
				s = new PrimSig(mdl.getName(),STATE,Attr.ABSTRACT);
				modelstatesigs.put(s.label, s);
			} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"AlloyUtil",s); }
		}
	}
	
    /** Creates the instances singleton state signatures */
	private void createInstanceStateSigs() throws ErrorAlloy, ErrorTransform {
		for (String uri : options.getInstances()){
			String pck = parser.getInstanceFromUri(uri).eClass().getEPackage().getName();
			try {
				String name = parser.getInstanceArgName(uri);
				PrimSig s = new PrimSig(name,(PrimSig) modelstatesigs.get(pck),Attr.ONE);
				inststatesigs.put(uri, s);
			} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"AlloyUtil"); }
		}
	}
	
    /** Creates the singleton target state */
	private void createTargetStateSig() throws ErrorAlloy{
		PrimSig sig = inststatesigs.get(parser.getInstanceUri(options.getDirection()));
		try{
			targetstatesig = new PrimSig(AlloyUtil.targetName(options.getDirection()),sig.parent,Attr.ONE);
		} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"AlloyUtil",sig); }
	}
	
	public void createScopes() throws ErrorAlloy {
		if (options.isQVT()) {
			String trg = parser.getInstanceUri(options.getDirection());
			scopes = AlloyUtil.createScopeFromSigs(insttrads.get(trg).getSigList());
		} else if (options.isGenerate()) {
			Map<PrimSig,Integer> sc = new HashMap<PrimSig,Integer>();
			for (Entry<String,String> cla : options.getScopes().keySet()) {
				ECore2Alloy e2a = modeltrads.get(cla.getKey());
				PrimSig sig = e2a.getSigFromEClass(e2a.getEClassFromName(cla.getValue()));
				sc.put(sig, options.getScopes().get(cla));
			}
			for (Expr sig : modelstatesigs.values())
				sc.put((PrimSig) sig,1);
			sc.put(Sig.STRING, options.getSize());
			System.out.println("SCOPE "+sc);
			scopes = AlloyUtil.createScope(sc,true);
		}

	}
	
	/** Writes an Alloy solution in the target instance file 
	 * @throws ErrorAlloy 
	 * @throws ErrorTransform */
	public void writeTargetInstance(A4Solution sol) throws Err, ErrorAlloy, ErrorTransform{
		String name = parser.getInstanceUri(options.getDirection());
		XMI2Alloy inst = insttrads.get(name);
		List<PrimSig> instsigs = inst.getSigList();
		EObject rootobj = inst.getRootEObject();
		PrimSig rootsig = inst.getSigFromEObject(rootobj);
		
		writeXMIAlloy(sol,name,rootsig,targetstatesig,inst.translator,instsigs);
	}
	
	public void writeInstances(A4Solution sol) throws Err, ErrorAlloy, ErrorTransform{
		for (String path : options.getModels()) {
			EPackage pck = parser.getModelsFromUri(path);
			String uri = path.replace(".ecore", ".xmi");
			ECore2Alloy e2a = modeltrads.get(pck.getName());
			List<EClass> topclass = parser.getTopObject(path);
			if (topclass.size() != 1) throw new ErrorTransform("Could not resolve top class.","");
			PrimSig sig = e2a.getSigFromEClass(topclass.get(0));
			writeXMIAlloy(sol,uri,sig, e2a.getState(),e2a,null);
		}
	}
	
	public void writeXMIAlloy(A4Solution sol, String uri, PrimSig rootatom, PrimSig state, ECore2Alloy trad,List<PrimSig> instsigs) throws ErrorAlloy, ErrorTransform {
		
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
	
	public Expr getInstanceFact(){
		return instancefact;
	}

	public Expr getQVTFact(){
		return qvtfact;
	}

	public Expr getDeltaFact(){
		return deltaexpr;
	}

	public ConstList<CommandScope> getScopes(){
		return scopes;
	}
	
	public PrimSig getTargetStateSig(){
		return targetstatesig;
	}
	
	public List<PrimSig> getModelSigs(){
		List<PrimSig> aux = new ArrayList<PrimSig>();
		for (ECore2Alloy x : modeltrads.values())
			aux.addAll(x.getSigList());
		return aux;
	}
	
	public List<PrimSig> getModelSigs(String s){
		List<PrimSig> aux = new ArrayList<PrimSig>(modeltrads.get(s).getSigList());
		return aux;
	}

	public Collection<PrimSig> getInstanceSigs(){
		List<PrimSig> aux = new ArrayList<PrimSig>();
		for (XMI2Alloy x : insttrads.values())
			aux.addAll(x.getSigList());
		return aux;
	}

	public List<Expr> getModelStateSigs(){
		return new ArrayList<Expr>(modelstatesigs.values());
	}

	public Expr getModelStateSig(String mm){
		return modelstatesigs.get(mm);
	}
	
	public List<PrimSig> getInstanceStateSigs(){
		List<PrimSig> aux = new ArrayList<PrimSig>(inststatesigs.values());
		return aux;
	}
	
	public PrimSig getInstanceStateSigFromURI (String uri){
		return inststatesigs.get(uri);
	}

	public PrimSig getInstanceStateSigFromArg (String arg){
		return inststatesigs.get(parser.getInstanceUri(arg));
	}

	public ECore2Alloy getModelTranslator (String mdl) {
		return modeltrads.get(mdl);
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
		} catch (Err e1) { throw new ErrorUnsupported("", ""); }
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
