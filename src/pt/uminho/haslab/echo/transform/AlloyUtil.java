package pt.uminho.haslab.echo.transform;

import java.util.ArrayList;
import java.util.List;

import pt.uminho.haslab.emof.ast.EMOF.Property;
import pt.uminho.haslab.emof.ast.EssentialOCL.Variable;
import pt.uminho.haslab.emof.ast.QVTBase.TypedModel;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.Browsable;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprVar;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;

public class AlloyUtil {

	// creates a list of Alloy declarations from a list of OCL variables
	public static List<Decl> variableListToExpr (List<Variable> ovars) throws Err{
		List<Decl> avars = new ArrayList<Decl>();
		for (Variable ovar : ovars) {
			ExprVar avar = ExprVar.make(null,ovar.getName());
			List<ExprVar> vars = new ArrayList<ExprVar>();
			vars.add(avar);
			Expr range = new PrimSig(ovar.getType().getName());
			Decl d = new Decl(null,null,null,vars,range);		
			avars.add(d);
		}
		return avars;
	}
	
	public static Decl variableListToExpr (Variable ovar) throws Err{
		ExprVar avar = ExprVar.make(null,ovar.getName());
		List<ExprVar> vars = new ArrayList<ExprVar>();
		vars.add(avar);
		Expr range = new PrimSig(ovar.getType().getName());
		Decl d = new Decl(null,null,null,vars,range);		
		return d;
	}
	
	public static Expr localStateAttribute(Expr exp, TypedModel mdl, boolean target) throws Err{
		PrimSig statesig = createStateSig(mdl.getUsedPackage().get(0).getName());
		PrimSig stateins = createStateInstance(statesig,target);
		return exp.join(stateins);
	}

	public static Expr localStateAttribute(Property prop, TypedModel mdl, boolean target) throws Err{
		PrimSig statesig = createStateSig(mdl.getUsedPackage().get(0).getName());
		PrimSig stateins = createStateInstance(statesig,target);
		Expr exp = (Expr) Sig.Field.make(prop.getName(),(Browsable)null);
		return exp.join(stateins);
	}

	
	public static PrimSig createStateSig(String sig) throws Err{
		PrimSig s = new PrimSig(sig,Attr.ABSTRACT);
		return s;
	}
	public static PrimSig createStateInstance(PrimSig sig,boolean target) throws Err{
		String suffix = "";
		if (target) suffix = "\'";
		PrimSig s = new PrimSig(sig.toString()+suffix,sig,Attr.ONE);
		return s;
	}
	
}
