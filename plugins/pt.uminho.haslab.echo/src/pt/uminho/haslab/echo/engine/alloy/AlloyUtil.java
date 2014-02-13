package pt.uminho.haslab.echo.engine.alloy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ocl.examples.pivot.Type;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.engine.EchoHelper;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.model.EVariable;
import pt.uminho.haslab.mde.transformation.EModelDomain;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.atl.EATLTransformation;
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
import edu.mit.csail.sdg.alloy4compiler.ast.ExprHasName;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprITE;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprLet;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprList;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprQt;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprUnary;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprVar;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;
import edu.mit.csail.sdg.alloy4compiler.ast.VisitQuery;

public class AlloyUtil {

	/** the top state signature name */	
	public static String STRINGNAME = "String";
	public static String INTNAME = "Int";

	private static Map<String,Integer> counter = new HashMap<String,Integer>();

	/** retrieves the meta-model URI from an Alloy signature 
	 * @param sig the Alloy signature
	 * @return the meta-model URI
	 */
	public static String getMetamodelIDfromExpr(ExprHasName sig) {
		return EchoHelper.getMetamodelIDfromLabel(sig.label);
	}

	public static String elementName(PrimSig parent) {
		Integer c = counter.get(parent.label);
		if (c == null) {
			c = 0;
		}

		counter.put(parent.label, ++c);

		return parent.label +"#"+ c +"#";
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
		}

		@Override public final Expr visit(ExprBinary x) throws Err { 
			if (x.isSame(find)) return replace;
			else {
				Expr left = visitThis(x.left);
				Expr right = visitThis(x.right);
				return x.op.make(null, null, left, right); 
			}
		}

		@Override public final Expr visit(ExprCall x) throws Err {
			List<Expr> aux = new ArrayList<Expr>();
			for (Expr e : x.args) {
				aux.add(visitThis(e));
			}
			return ExprCall.make(null, null, x.fun, aux, 0);

		}

		@Override public final Expr visit(ExprList x) throws Err {
			if (x.isSame(find)) return replace;
			else {
				List<Expr> args = new ArrayList<Expr>();
				for (Expr arg : x.args)
					args.add(visitThis(arg));
				return ExprList.make(null, null, x.op, args); 
			}
		}

		@Override public final Expr visit(ExprConstant x) {
			if (x.isSame(find)) return replace;
			else return x;
		}

		@Override public final Expr visit(ExprITE x) throws Err {
			throw new ErrorFatal("Failed to replace: "+x);
		}

		@Override public final Expr visit(ExprLet x) throws Err {
			throw new ErrorFatal("Failed to replace: "+x);
		}

		@Override public final Expr visit(ExprUnary x) throws Err {
			if (x.isSame(find)) return replace;
			else {
				Expr sub = visitThis(x.sub);
				return x.op.make(null, sub); 
			}
		}

		@Override public final Expr visit(ExprVar x) {
			if (x.isSame(find)) return replace;
			else return x;
		}

		@Override public final Expr visit(Sig x) {
			if (x.isSame(find)) return replace;
			else return x; 
		}

		@Override public final Expr visit(Sig.Field x) {
			if (x.isSame(find)) return replace;
			else return x; 
		}

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
		}

		@Override public final List<ExprVar> visit(ExprBinary x) throws Err { 
			List<ExprVar> aux = new ArrayList<ExprVar>();
			aux.addAll(visitThis(x.left));
			aux.addAll(visitThis(x.right));
			return aux;
		}

		@Override public final List<ExprVar> visit(ExprCall x) throws Err {
			List<ExprVar> aux = new ArrayList<ExprVar>();
			for (Expr exp : x.args)
				aux.addAll(visitThis(exp));
			return aux;
		}

		@Override public final List<ExprVar> visit(ExprList x) throws Err {
			List<ExprVar> aux = new ArrayList<ExprVar>();
			for (Expr arg : x.args)
				aux.addAll(visitThis(arg));
			return aux;
		}

		@Override public final List<ExprVar> visit(ExprConstant x) {
			return new ArrayList<ExprVar>();
		}

		@Override public final List<ExprVar> visit(ExprITE x) throws Err {
			throw new ErrorFatal("Failed to get vars: "+x);
		}

		@Override public final List<ExprVar> visit(ExprLet x) throws Err {
			throw new ErrorFatal("Failed to get vars: "+x);
		}

		@Override public final List<ExprVar> visit(ExprUnary x) throws Err {
			return visitThis(x.sub);
		}

		@Override public final List<ExprVar> visit(ExprVar x) {
			List<ExprVar> aux = new ArrayList<ExprVar>();
			aux.add(x);
			return aux;
		}

		@Override public final List<ExprVar> visit(Sig x) {
			return new ArrayList<ExprVar>();
		}

		@Override public final List<ExprVar> visit(Sig.Field x) {
			return new ArrayList<ExprVar>();
		}

	}


	/** 
	 * Converts a list of variable declarations to their representation in Alloy
	 * If the variable has the owning model defined in <code>variable_models</code>, uses the appropriate model sig in the range
	 * Otherwise uses the respective metamodel sig
	 * @param variable_decls the variables to convert
	 * @param variable_models maps variables to their owning model
	 * @param modelparam2var the Alloy variables representing each model parameter
	 * @return the mapping between variable names and their Alloy declaration
	 * @throws EchoError
	 */
	public static Map<String, Decl> variableListToExpr(
			Collection<EVariable> variable_decls,
			Map<String, Entry<ExprHasName, String>> variable_models,
			Map<String, ExprHasName> modelparam2var) throws EchoError {
		AlloyEchoTranslator translator = AlloyEchoTranslator.getInstance();
		Map<String, Decl> alloy_variable_decls = new LinkedHashMap<String, Decl>();

		for (EVariable variable_decl : variable_decls) {
			try {
				Expr range = Sig.NONE;
				EObject t = variable_decl.getType();
				String type = null;
				if (t instanceof Type)
					type = ((Type) t).getName();
				else {
					type = (String) t.eGet(t.eClass().getEStructuralFeature(
							"name"));
				}
				if (type.equals("String"))
					range = Sig.STRING;
				else if (type.equals("Int"))
					range = Sig.SIGINT;
				else {
					String metamodeluri = null;
					if (t instanceof Type) {
						metamodeluri = EcoreUtil.getURI(((Type) t).getPackage()).path().replace(".oclas", "").replace("resource/", "");
					} else {
						EObject aux = (EObject) t.eGet(t.eClass()
								.getEStructuralFeature("model"));
						metamodeluri = EATLTransformation.metamodeluris.get(aux
								.eGet(aux.eClass()
										.getEStructuralFeature("name")));
					}
					Expr state = null;
					if (variable_models.get(variable_decl.getName()) != null
							&& modelparam2var != null)
						state = modelparam2var.get(variable_models
								.get(variable_decl.getName()).getValue());

					EMetamodel metamodel = MDEManager.getInstance().getMetamodel(metamodeluri, false);
					if (state == null) {
						state = translator.getMetamodel(metamodel.ID).sig_metamodel;
					}
					
					EClass eclass = (EClass) translator.getEClassifierFromName(
							metamodel.ID, type);
					Expr statefield = translator.getStateFieldFromClass(
							metamodel.ID, eclass);
					range = statefield.join(state);

				}
				alloy_variable_decls.put(variable_decl.getName(), range.oneOf(variable_decl.getName()));

			} catch (Err a) {
				throw new ErrorAlloy(a.getMessage());
			}
		}
		return alloy_variable_decls;
	}

	public static String targetName(PrimSig sig) {
		return "'"+sig.label;
	}


}
