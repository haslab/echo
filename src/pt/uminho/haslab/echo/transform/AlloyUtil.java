package pt.uminho.haslab.echo.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ocl.examples.pivot.Property;

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
    	try {s = new PrimSig("State",Attr.ABSTRACT);}
    	catch (Err a){}
    	STATE = s;
    }
	
    // creates state variables for a particular metamodel, extensions of STATE
    // if metamodel is target, two instances are created
	public static List<PrimSig> createStateSig(String sig, int n, boolean target) throws ErrorAlloy {
		List<PrimSig> sigs = new ArrayList<PrimSig>();
		PrimSig s = null;
		try {
			s = new PrimSig(sig,STATE,Attr.ABSTRACT);
			sigs.add(s);
			for (int i = 1; i <= n; i++) {
				PrimSig s1 = new PrimSig(sig+i,s,Attr.ONE);
				sigs.add(s1);
			}
			if (target) {
				PrimSig s2 = new PrimSig(sig+"\'",s,Attr.ONE);
				sigs.add(s2);
			}
		} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"AlloyUtil",s); }
		return sigs;
	}
	
	
	
	// composes an expression with the respective state variable
	public static Expr localStateAttribute(Property prop, Map<String,List<PrimSig>> statesigs, Map<String,List<Sig>> modelsigs) throws ErrorAlloy, ErrorTransform{
		String mdl = prop.getOwningType().getPackage().getName();
		List<PrimSig> sigs = statesigs.get(mdl);
		PrimSig statesig = null;
		//ERROOO!!!
		for (Sig sig : sigs)
			if (sig.toString().equals(mdl+"\'") || (sig.toString().equals(mdl) && statesig == null))
				statesig = (PrimSig) sig;
		
		if (statesig == null) throw new ErrorTransform("State sig not found.","AlloyUtil",mdl);
		Expr exp = OCL2Alloy.propertyToField(prop,modelsigs);
		return exp.join(statesig);
	}
	
	public static Expr localStateSig(PrimSig sig, Expr var) throws ErrorTransform{
		Expr exp = null;
		for (Field field : sig.getFields()) {
			if ((field.label.toUpperCase()).equals(sig.label.toUpperCase()))
					exp = field;
		}
		if (exp == null) throw new ErrorTransform ("State field not found.","AlloyUtil",exp);
		
		return exp.join(var);
	}
	

	
	// methods used to append prefixes to expressions
	public static String pckPrefix (String mdl, String str) {
		return (mdl + "_" + str);
	}
	
	// ignores first parameter if "no none" or "true"
	public static Expr cleanAnd (Expr e, Expr f) {
		if (e.isSame(Sig.NONE.no()) || e.isSame(ExprConstant.TRUE)) return f;
		else if (f.isSame(Sig.NONE.no()) || f.isSame(ExprConstant.TRUE)) return e;
		else return e.and(f);
	}
	
	public static ConstList<CommandScope> createScope (List<PrimSig> sigs) throws ErrorAlloy {
		Map<String,CommandScope> scopes = new HashMap<String,CommandScope>();
		
		for (PrimSig sig : sigs) {
			incrementScope(scopes,sig.parent);
			Sig up = sig.parent.parent;
			while (up != Sig.UNIV && up != null){
				incrementScope(scopes,up);
				up = (up instanceof PrimSig)?((PrimSig)up).parent:null;
			}
		}		
		System.out.println(scopes.values());
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
