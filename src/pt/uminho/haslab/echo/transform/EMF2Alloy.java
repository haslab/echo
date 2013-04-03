package pt.uminho.haslab.echo.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import edu.mit.csail.sdg.alloy4.ConstList;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.CommandScope;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import pt.uminho.haslab.echo.EchoOptions;
import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.alloy.AlloyUtil;
import pt.uminho.haslab.echo.emf.EMFParser;

public class EMF2Alloy {

	/** the parsed EMF resources */
	private EMFParser parser;
	/** the echo run options */
	private EchoOptions options;

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
	private ConstList<CommandScope> targetscopes;
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
		for (EPackage epck : parser.getPackages()) {
			ECore2Alloy mmtrans = new ECore2Alloy(epck,(PrimSig) modelstatesigs.get(epck.getName()),this);
			modeltrads.put(epck.getName(),mmtrans);
			mmtrans.translate();
		}	
	}

	/** Translates XMI instances to the respective Alloy specs */
	public void translateInstances() throws ErrorUnsupported, ErrorAlloy, ErrorTransform, ErrorParser {
		for (String name: options.getInstances()) {
			String mdl = parser.getObjectFromUri(name).eClass().getEPackage().getName();
			PrimSig state = inststatesigs.get(name);
			ECore2Alloy mmtrans = modeltrads.get(mdl);			
			EObject instmodel = parser.getObjectFromUri(name);
			XMI2Alloy insttrans = new XMI2Alloy(instmodel,mmtrans,state);
			insttrads.put(name,insttrans);
			instancefact = AlloyUtil.cleanAnd(instancefact,insttrans.getFact());
		}
	}
	
	/** Translates the QVT transformation to the respective Alloy specs */
	public void translateQVT() throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		String name = parser.getInstanceUri(options.getDirection());
		PrimSig back = inststatesigs.get(name);
		
		createTargetStateSig();
		ECore2Alloy mmtrans =  modeltrads.get(back.parent.label);
		deltaexpr = mmtrans.getDeltaExpr(targetstatesig,back);
		targetscopes = AlloyUtil.createScope(insttrads.get(name).getSigList(),mmtrans.getSigList());

		if (options.isEnforce()) {
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
		inststatesigs.put(name,back);
		modelstatesigs.put(targetstatesig.parent.label, targetstatesig.parent);

		Map<String,Expr> qvtfacts = qvtrans.getFact();
		for (String e : qvtfacts.keySet()){
			qvtfact = AlloyUtil.cleanAnd(qvtfact, qvtfacts.get(e));
		}	
	}
	
    /** Creates the metamodels abstract state signatures */
	private void createModelStateSigs() throws ErrorAlloy, ErrorTransform {
		PrimSig s = null;
		for (EPackage mdl : parser.getPackages()){
			try {
				s = new PrimSig(mdl.getName(),STATE,Attr.ABSTRACT);
				modelstatesigs.put(s.label, s);
			} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"AlloyUtil",s); }
		}
	}
	
    /** Creates the instances singleton state signatures */
	private void createInstanceStateSigs() throws ErrorAlloy, ErrorTransform {
		for (String uri : options.getInstances()){
			String pck = parser.getObjectFromUri(uri).eClass().getEPackage().getName();
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
	
	/** Writes an Alloy solution in the target instance file */
	public void writeTargetInstance(A4Solution sol) throws Err{
		String name = parser.getInstanceUri(options.getDirection());
		XMI2Alloy inst = insttrads.get(name);
		inst.writeXMIAlloy(sol,name,targetstatesig);
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

	public ConstList<CommandScope> getTargetScopes(){
		return targetscopes;
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
		
}
