package pt.uminho.haslab.echo.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.ocl.examples.pivot.Package;
import org.eclipse.ocl.examples.pivot.Property;
import org.eclipse.qvtd.pivot.qvtbase.TypedModel;
import org.eclipse.qvtd.pivot.qvtrelation.Relation;

import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorTransform;


import edu.mit.csail.sdg.alloy4.ConstList;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorSyntax;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.CommandScope;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprConstant;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;

public class AlloyUtil {

	// the top level state sig; extended by each metamodel
    public static final PrimSig STATE;
    static{
    	PrimSig s = null;
    	try {s = new PrimSig("State_",Attr.ABSTRACT);}
    	catch (Err a){}
    	STATE = s;
    }
	
	public static Map<String,PrimSig> createStateSig(List<TypedModel> mdls) throws ErrorAlloy, ErrorTransform {
		Map<String,PrimSig> sigs = new HashMap<String,PrimSig>();
		PrimSig s = null;
		for (TypedModel mdl : mdls){
			EList<Package> pcks = mdl.getUsedPackage();
			if (pcks.size() != 1) throw new ErrorTransform("Invalid model variables.","AlloyUtil",pcks);
			try {
				s = new PrimSig(pcks.get(0).getName(),STATE,Attr.ABSTRACT);
				sigs.put(s.label, s);
			} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"AlloyUtil",s); }
		}
		return sigs;
	}
	
	public static Map<String,PrimSig> createStateInstSig(Map<String,PrimSig> mdlsigs, List<TypedModel> mdls) throws ErrorAlloy, ErrorTransform {
		Map<String,PrimSig> sigs = new HashMap<String,PrimSig>();
		PrimSig s = null;
		for (TypedModel mdl : mdls){
			EList<Package> pcks = mdl.getUsedPackage();
			if (pcks.size() != 1) throw new ErrorTransform("Invalid model variables.","AlloyUtil",pcks);
			String mm = pcks.get(0).getName();
			try {
				s = new PrimSig(mdl.getName(),mdlsigs.get(mm),Attr.ONE);
				sigs.put(s.label, s);
			} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"AlloyUtil",s); }
		}
		return sigs;
	}
	
	public static PrimSig createTargetState(Map<String,PrimSig> sigs, String target) throws ErrorAlloy{
		PrimSig trg = null;
		PrimSig sig = sigs.get(target);
		try{
			trg = new PrimSig(targetName(target),sig.parent,Attr.ONE);
		} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"AlloyUtil",sig); }
		return trg;
	}
	
	public static void insertTargetState(Map<String,Expr> sigs, String target, PrimSig trg) throws ErrorAlloy{
		PrimSig source = (PrimSig) sigs.get(target);
		PrimSig sourcemdl = source.parent;
		Expr sourcemdlnosource = null;
		try{
		for (Sig s : sourcemdl.descendents())
			if (!s.label.equals(target)) 
				if (sourcemdlnosource != null) sourcemdlnosource = sourcemdlnosource.plus(s);
				else sourcemdlnosource = s;
		} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"AlloyUtil",trg); }
		sigs.put(target, trg);
		sigs.put(sourcemdl.label, sourcemdlnosource);
	}
	
	public static String targetName(String target) {
		return target+"_new_";
	}
	
	
	
	// composes an expression with the respective state variable
	public static Expr localStateAttribute(Property prop, Expr statesig, Map<String,List<Sig>> modelsigs) throws ErrorAlloy, ErrorTransform{
		Expr exp = OCL2Alloy.propertyToField(prop,modelsigs);
		return exp.join(statesig);
	}
	
	public static Expr localStateSig(Sig sig, Expr var) throws ErrorTransform, ErrorAlloy{
		Expr exp = null;
		
		for (Field field : sig.getFields()) {
			if (field.label.endsWith("_") && field.label.substring(0, field.label.length()-1).equals(sig.label) ){	
				exp = field;
			}
		}
		if (exp == null) throw new ErrorTransform ("State field not found.","AlloyUtil",sig);
		
		return exp.join(var);
	}
	

	
	// methods used to append prefixes to expressions
	public static String pckPrefix (String mdl, String str) {
		return (mdl + "_" + str);
	}

	public static String stateFieldName (EPackage pck, EClass cls) {
		return pck.getName() +"_"+ cls.getName() +"_";
	}
	
	public static String relationFieldName (Relation rel, TypedModel dir) {
		return rel.getName() +"_"+dir.getName()+"_";
	}
	
	
	// ignores first parameter if "no none" or "true"
	public static Expr cleanAnd (Expr e, Expr f) {
		if (e.isSame(Sig.NONE.no()) || e.isSame(ExprConstant.TRUE)) return f;
		else if (f.isSame(Sig.NONE.no()) || f.isSame(ExprConstant.TRUE)) return e;
		else return e.and(f);
	}
	
	public static ConstList<CommandScope> createScope (List<PrimSig> instsigs, List<Sig> modelsigs) throws ErrorAlloy {
		Map<String,CommandScope> scopes = new HashMap<String,CommandScope>();
		
		for (PrimSig sig : instsigs) {
			incrementScope(scopes,sig.parent);
			Sig up = sig.parent.parent;
			while (up != Sig.UNIV && up != null){
				incrementScope(scopes,up);
				up = (up instanceof PrimSig)?((PrimSig)up).parent:null;
			}
		}		
		for (Sig sig : modelsigs){
			if (scopes.get(sig.label)==null)
				try { scopes.put(sig.label,new CommandScope(sig, false, 0));}
				catch (Err e) { throw new ErrorAlloy(e.getMessage(),"AlloyUtil");}
		}
		return ConstList.make(scopes.values());
	}
	
	private static void incrementScope (Map<String,CommandScope> scopes, Sig sig) throws ErrorAlloy  {
		String type = sig.toString();
		CommandScope scope = scopes.get(type);
		if (scope == null)
			try { scope = new CommandScope(sig, false, 1);}
			catch (Err e) { throw new ErrorAlloy(e.getMessage(),"AlloyUtil");}
		else 
			try { scope = new CommandScope(sig, false, scope.startingScope+1);}
			catch (Err e) { throw new ErrorAlloy(e.getMessage(),"AlloyUtil");}
		scopes.put(type, scope);
	
	}
	
	public static ConstList<CommandScope> incrementScopes (List<CommandScope> scopes) throws ErrorSyntax  {
		List<CommandScope> list = new ArrayList<CommandScope>();
		
		for (CommandScope scope : scopes)
			list.add(new CommandScope(scope.sig, false, scope.startingScope+1));

		return ConstList.make(list);
	}
	
}
