package pt.uminho.haslab.echo.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorTransform;

import net.sourceforge.qvtparser.model.emof.Property;
import net.sourceforge.qvtparser.model.essentialocl.Variable;

import edu.mit.csail.sdg.alloy4.ConstList;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorSyntax;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.CommandScope;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
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
	public static List<PrimSig> createStateSig(String sig, boolean target) throws ErrorAlloy {
		List<PrimSig> sigs = new ArrayList<PrimSig>();
		
		try {
			if (!target) {
				PrimSig s = new PrimSig(sig,STATE,Attr.ONE);
				sigs.add(s);
			} else {
				PrimSig s = new PrimSig(sig,STATE,Attr.ABSTRACT);
				PrimSig s1 = new PrimSig(sig+"1",s,Attr.ONE);
				PrimSig s2 = new PrimSig(sig+"2",s,Attr.ONE);
				sigs.add(s);
				sigs.add(s1);
				sigs.add(s2);
			}
		} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"AlloyUtil"); }
		return sigs;
	}
	
	// creates a list of Alloy declarations from a list of OCL variables
	public static List<Decl> variableListToExpr (List<Variable> ovars, List<Sig> sigs) throws ErrorTransform, ErrorAlloy {
		List<Decl> avars = new ArrayList<Decl>();
		for (Variable ovar : ovars) {
			Expr range = Sig.NONE;
			String type = ovar.getType().getName();
			if (type.equals("String")) range = Sig.STRING;
			else 
				for (Sig s : sigs)
					if (s.label.equals(pckPrefix(ovar.getType().getPackage().getName(),type))) range = s;
		
			if (range.equals(Sig.NONE)) throw new ErrorTransform ("Sig not found: "+type+sigs,"AlloyUtil",ovar);
			Decl d;
			try { d = range.oneOf(ovar.getName()); }
			catch (Err a) {throw new ErrorAlloy (a.getMessage(),"AlloyUtil",range);}
			avars.add(d);
		}
		return avars;
	}

	// creates an Alloy declaration from an OCL variable
	public static Decl variableListToExpr (Variable ovar, List<Sig> sigs) throws ErrorAlloy {
		Expr range = Sig.NONE;
		String type = ovar.getType().getName();
		if (type.equals("String")) range = Sig.STRING;
		else for (Sig s : sigs)
			if (s.label.equals(pckPrefix(ovar.getType().getPackage().getName(),type))) range = s;
		
		if (range.equals(Sig.NONE)) throw new Error ("Sig not found: "+type);
		Decl d;
		try { d = range.oneOf(ovar.getName()); }
		catch (Err a) {throw new ErrorAlloy (a.getMessage(),"ECore2Alloy",ovar);}
		return d;
	}
	
	// composes an expression with the respective state variable
	public static Expr localStateAttribute(Property prop, String mdl, List<Sig> sigs) throws ErrorAlloy, ErrorTransform{
		Sig statesig = getStateSig(sigs,mdl);

		if (statesig == null) throw new ErrorTransform("State sig not found.","AlloyUtil",mdl);
		Expr exp = propertyToField(prop,mdl,sigs);
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
	
	public static PrimSig getStateSig(List<Sig> sigs, String mdl) {
		PrimSig statesig = null;
		for (Sig sig : sigs)
			if (sig.toString().equals(mdl+"2") || (sig.toString().equals(mdl) && statesig == null))
				statesig = (PrimSig) sig;
		return statesig;
	}

	// retrieves the Alloy field corresponding to an OCL property (attribute)
	public static Sig.Field propertyToField (Property prop, String mdl, List<Sig> sigs) {
		Sig sig = null;
		for (Sig s : sigs)
			if (s.toString().equals(pckPrefix(mdl,prop.getClass_().getName()))) sig = s;
		if (sig == null) throw new Error ("Sig not found: "+pckPrefix(mdl,prop.getClass_().getName()));

		Sig.Field exp = null;
		for (Sig.Field field : sig.getFields())
			if ((field.label).equals(pckPrefix(mdl,prop.getName())))
				exp = field;
		if (exp == null) throw new Error ("Field not found: "+pckPrefix(mdl,prop.getName()));
		return exp;
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
	
	public static List<CommandScope> createScope (List<PrimSig> sigs) throws ErrorAlloy {
		Map<String,CommandScope> scopes = new HashMap<String,CommandScope>();
		
		for (PrimSig sig : sigs) {
			String type = sig.parent.toString();
			CommandScope scope = scopes.get(type);
			if (scope == null)
				try { scope = new CommandScope(sig.parent, false, 1);}
				catch (Err e) { throw new ErrorAlloy(e.getMessage(),"AlloyUtil",sig);}
			else 
				try { scope = new CommandScope(sig.parent, false, scope.startingScope+1);}
				catch (Err e) { throw new ErrorAlloy(e.getMessage(),"AlloyUtil",sig);}
			scopes.put(type, scope);
		}
		
		return new ArrayList<CommandScope>(scopes.values());
	}
	public static ConstList<CommandScope> incrementScope (List<CommandScope> scopes) throws ErrorSyntax  {
		List<CommandScope> list = new ArrayList<CommandScope>();
		
		for (CommandScope scope : scopes)
			list.add(new CommandScope(scope.sig, false, scope.startingScope+1));

		return ConstList.make(list);
	}
	
	
}
