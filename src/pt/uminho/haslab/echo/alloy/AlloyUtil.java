package pt.uminho.haslab.echo.alloy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.qvtd.pivot.qvtbase.TypedModel;
import org.eclipse.qvtd.pivot.qvtrelation.Relation;

import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import edu.mit.csail.sdg.alloy4.ConstList;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorFatal;
import edu.mit.csail.sdg.alloy4.ErrorSyntax;
import edu.mit.csail.sdg.alloy4compiler.ast.CommandScope;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
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
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;

public class AlloyUtil {
		
	public static Expr localStateSig(Sig sig, Expr var) throws ErrorTransform, ErrorAlloy{
		Expr exp = null;
		
		for (Field field : sig.getFields()) {
			if (field.label.endsWith("_") && field.label.substring(0, field.label.length()-1).equals(sig.label) ){	
				exp = field;
			}
		}
		if (exp == null) throw new ErrorTransform ("State field not found.");
		
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
	
	public static ConstList<CommandScope> createScope(Map<PrimSig,Integer> sizes, Map<PrimSig,Integer> sizesexact) throws ErrorAlloy {
		List<CommandScope> scopes = new ArrayList<CommandScope>();
	
		for (PrimSig sig : sizes.keySet()) 
			try {scopes.add(new CommandScope(sig, false, sizes.get(sig)));}
			catch (Err e) { throw new ErrorAlloy(e.getMessage());}
		for (PrimSig sig : sizesexact.keySet()) 
			try {scopes.add(new CommandScope(sig, true, sizesexact.get(sig)));}
			catch (Err e) { throw new ErrorAlloy(e.getMessage());}

		return ConstList.make(scopes);
	}
	
	
	public static ConstList<CommandScope> incrementStringScopes (List<CommandScope> scopes) throws ErrorAlloy {
		List<CommandScope> list = new ArrayList<CommandScope>();
		
		for (CommandScope scope : scopes)
			try {
				if (scope.sig.label.equals("String")) list.add(new CommandScope(scope.sig, true, scope.startingScope+1));
				else list.add(new CommandScope(scope.sig, scope.isExact, scope.startingScope));
			} catch (ErrorSyntax e) { throw new ErrorAlloy(e.getMessage());}

		return ConstList.make(list);
	}

	public static List<Decl> ordDecls (List<Decl> decls){
		List<Decl> res = new ArrayList<Decl>();
		int last = decls.size()+1;
		while (last > decls.size() && decls.size() != 1) {
			last = decls.size();
			for (int i = 0; i<decls.size(); i++) {
				boolean safe = true;
				for (int j = 0; j<decls.size(); j++)
					if (decls.get(i).expr.hasVar((ExprVar)(decls.get(j)).get())) safe = false;
				if (safe) res.add(decls.get(i));
			}
			decls.removeAll(res);
		}

		if (decls.size() > 1) {
			String error = "Could not order: \n";
			for (Decl d : decls)
				error = error.concat(d.get()+ " : "+ d.expr+"\n");
			throw new Error(error);
				
		}
		if (decls.size()==1){
			res.add(decls.get(0));
			decls.remove(0);
		}
		
		return res;
	}
	/**
	 * returns true is able to determine true;
	 * false otherwise
	 * @param exp
	 * @return
	 */
	public static boolean isTrue (Expr exp) {
		if (exp.isSame(Sig.NONE.no())) return true;
		if (exp.isSame(ExprConstant.TRUE)) return true;
		return false;
	}
	
	public static Expr replace(Expr in, Expr find, Expr replace) throws Err {
		Replacer replacer = new Replacer(find, replace);
		return replacer.visitThis(in);
	}
	
	private static class Replacer extends VisitQuery<Expr> {
		
		Expr find, replace;
		
		Replacer (Expr find, Expr replace) {
			this.find = find;
			this.replace = replace;
		}
		
        @Override public final Expr visit(ExprQt x) throws Err { 
        	if (x.isSame(find)) return replace;
        	else {
        		List<Decl> decls = new ArrayList<Decl>();
        		Expr sub = visitThis(x.sub);
        		for (Decl d : x.decls) {
        			Expr expr = visitThis(d.expr);
        			decls.add(new Decl(null,null,null,d.names,expr));
        		}

           	    return x.op.make(null, null, decls, sub); 
        	}
        };		
        @Override public final Expr visit(ExprBinary x) throws Err { 
        	if (x.isSame(find)) return replace;
        	else {
        		Expr left = visitThis(x.left);
           		Expr right = visitThis(x.right);
           	    return x.op.make(null, null, left, right); 
        	}
        };
        @Override public final Expr visit(ExprCall x) throws Err { 
        	List<Expr> aux = new ArrayList<Expr>();
        	for (Expr e : x.args) {
        		aux.add(visitThis(e));
        	}
        	return ExprCall.make(null, null, x.fun, aux, 0);

        };
        @Override public final Expr visit(ExprList x) throws Err { 
        	if (x.isSame(find)) return replace;
        	else {
        		List<Expr> args = new ArrayList<Expr>();
        		for (Expr arg : x.args)
        			args.add(visitThis(arg));
           	    return ExprList.make(null, null, x.op, args); 
        	}
        };
        @Override public final Expr visit(ExprConstant x) { 
        	if (x.isSame(find)) return replace;
        	else return x;
        };
        @Override public final Expr visit(ExprITE x) throws Err {         	
        	throw new ErrorFatal("Failed to replace: "+x);
        };
        @Override public final Expr visit(ExprLet x) throws Err { 
        	throw new ErrorFatal("Failed to replace: "+x);
        };
        @Override public final Expr visit(ExprUnary x) throws Err { 
        	if (x.isSame(find)) return replace;
        	else {
        		Expr sub = visitThis(x.sub);
        		return x.op.make(null, sub); 
        	}
        };
        @Override public final Expr visit(ExprVar x) { 
        	if (x.isSame(find)) return replace;
        	else return x;
        };
        @Override public final Expr visit(Sig x) {       
        	if (x.isSame(find)) return replace;
        	else return x; 
        };
        @Override public final Expr visit(Sig.Field x) { 
        	if (x.isSame(find)) return replace;
        	else return x; 
        };

      }
	
	public static List<ExprVar> getVars(Expr in) throws ErrorUnsupported {
		VarGetter getter = new VarGetter();
		List<ExprVar> res;
		try {
			res = getter.visitThis(in);
		} catch (Err e) { throw new ErrorUnsupported(e.getMessage()); }
		return res;
	}
	
	private static class VarGetter extends VisitQuery<List<ExprVar>> {
		
		VarGetter () {}
		
        @Override public final List<ExprVar> visit(ExprQt x) throws Err { 
        	List<ExprVar> aux = new ArrayList<ExprVar>();
        	aux.addAll(visitThis(x.sub));
         	for (Decl d : x.decls)
            	aux.addAll(visitThis(d.expr));
         	return aux;
        };		
        @Override public final List<ExprVar> visit(ExprBinary x) throws Err { 
        	List<ExprVar> aux = new ArrayList<ExprVar>();
        	aux.addAll(visitThis(x.left));
        	aux.addAll(visitThis(x.right));
        	return aux;
        };
        @Override public final List<ExprVar> visit(ExprCall x) throws Err { 
        	List<ExprVar> aux = new ArrayList<ExprVar>();
        	for (Expr exp : x.args)
            	aux.addAll(visitThis(exp));
        	return aux;
        };
        @Override public final List<ExprVar> visit(ExprList x) throws Err { 
        	List<ExprVar> aux = new ArrayList<ExprVar>();
        	for (Expr arg : x.args)
       			aux.addAll(visitThis(arg));
       		return aux;
        };
        @Override public final List<ExprVar> visit(ExprConstant x) { 
        	return new ArrayList<ExprVar>();
        };
        @Override public final List<ExprVar> visit(ExprITE x) throws Err {         	
        	throw new ErrorFatal("Failed to get vars: "+x);
        };
        @Override public final List<ExprVar> visit(ExprLet x) throws Err { 
        	throw new ErrorFatal("Failed to get vars: "+x);
        };
        @Override public final List<ExprVar> visit(ExprUnary x) throws Err { 
        	return visitThis(x.sub);
        };
        @Override public final List<ExprVar> visit(ExprVar x) { 
        	List<ExprVar> aux = new ArrayList<ExprVar>();
        	aux.add(x);
        	return aux;
        };
        @Override public final List<ExprVar> visit(Sig x) {       
        	return new ArrayList<ExprVar>();
        };
        @Override public final List<ExprVar> visit(Sig.Field x) { 
        	return new ArrayList<ExprVar>();
        };

      }
	
}
