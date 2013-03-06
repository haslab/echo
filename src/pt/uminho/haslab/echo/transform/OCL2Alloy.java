package pt.uminho.haslab.echo.transform;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.ocl.examples.pivot.BooleanLiteralExp;
import org.eclipse.ocl.examples.pivot.IteratorExp;
import org.eclipse.ocl.examples.pivot.OCLExpression;
import org.eclipse.ocl.examples.pivot.OperationCallExp;
import org.eclipse.ocl.examples.pivot.Property;
import org.eclipse.ocl.examples.pivot.UnlimitedNaturalLiteralExp;
import org.eclipse.ocl.examples.pivot.Variable;
import org.eclipse.ocl.examples.pivot.VariableDeclaration;
import org.eclipse.ocl.examples.pivot.VariableExp;
import org.eclipse.ocl.examples.pivot.IfExp;
import org.eclipse.ocl.examples.pivot.PropertyCallExp;
import org.eclipse.qvtd.pivot.qvtbase.Transformation;
import org.eclipse.qvtd.pivot.qvtbase.TypedModel;
import org.eclipse.qvtd.pivot.qvtrelation.RelationCallExp;
import org.eclipse.qvtd.pivot.qvttemplate.ObjectTemplateExp;
import org.eclipse.qvtd.pivot.qvttemplate.PropertyTemplateItem;

import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprConstant;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprHasName;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprITE;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;

public class OCL2Alloy {

	private List<Sig> sigs = new ArrayList<Sig>();
	private TypedModel target;
	private Set<Decl> vardecls;
	private Transformation qvt;

	public OCL2Alloy(TypedModel target, List<Sig> modelsigs, Set<Decl> vardecls, Transformation qvt) {
		this.qvt = qvt;
		this.sigs = modelsigs;
		this.target = target;
		this.vardecls = vardecls;
	}
	
	public Expr oclExprToAlloy (VariableExp expr) throws ErrorTransform {
		String varname = expr.toString();
		Decl decl = null;
		for (Decl d : vardecls){
			if (d.get().label.equals(varname))
				decl = d;}
		if (decl == null) throw new ErrorTransform ("Variable not declared.","OCL2Alloy",varname);
		ExprHasName var = decl.get();
		return var;	
	}
	
	public Expr oclExprToAlloy (BooleanLiteralExp expr){
		if (expr.isBooleanSymbol()) return ExprConstant.TRUE;
		else return ExprConstant.FALSE;
	}

	public Expr oclExprToAlloy (UnlimitedNaturalLiteralExp expr) throws ErrorTransform{
		Number n = expr.getUnlimitedNaturalSymbol();

		if (n.toString().equals("*"))  throw new ErrorTransform ("No support for unlimited integers.","OCL2Alloy");
		
		return ExprConstant.makeNUMBER(n.intValue());
	}

	public Expr oclExprToAlloy (ObjectTemplateExp temp) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		Expr result = Sig.NONE.no();
		
		for (PropertyTemplateItem part: temp.getPart()) {
			// calculates OCL expression
			OCLExpression value = part.getValue();
			Expr ocl = this.oclExprToAlloy(value);
			// retrieves the Alloy field
			Property prop = part.getReferredProperty();
			Expr localfield = null;
			localfield = AlloyUtil.localStateAttribute(prop, sigs);
			// retrieves the Alloy root variable
			String varname = ((ObjectTemplateExp) temp).getBindsTo().getName();
			Decl decl = null;
			for (Decl d : vardecls)
				if (d.get().label.equals(varname))
					decl = d;
			if (decl == null) throw new ErrorTransform ("Variable not declared.","OCL2Alloy",((ObjectTemplateExp) temp).getBindsTo());
			ExprHasName var = decl.get();
			
			// merges the whole thing
			Expr item;
			if (ocl.equals(ExprConstant.TRUE)) item = var.in(localfield);
			else if (ocl.equals(ExprConstant.FALSE)) item = var.not().in(localfield);
			else if (value instanceof ObjectTemplateExp) {
				varname = ((ObjectTemplateExp) value).getBindsTo().getName();
				decl = null;
				for (Decl d : vardecls)
					if (d.get().label.equals(varname))
						decl = d;
				if (decl == null) throw new ErrorTransform ("Variable not declared.","OCL2Alloy",((ObjectTemplateExp) value).getBindsTo());
				ExprHasName var1 = decl.get();
				item = var1.in(var.join(localfield));
				item = AlloyUtil.cleanAnd(item,ocl);
			}
			else {
				item = ocl.in(var.join(localfield));
			}
			result = AlloyUtil.cleanAnd(result,item);
		}

		OCLExpression where = temp.getWhere();
		if (where != null) {
			Expr awhere = oclExprToAlloy(where);
			result = AlloyUtil.cleanAnd(result,awhere);
		}
		
		return result;
	}
	
	public Expr oclExprToAlloy (RelationCallExp expr) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		QVTRelation2Alloy trans = new QVTRelation2Alloy (target, expr.getReferredRelation(), sigs, qvt, vardecls);
		return trans.getFact();
	}

	public Expr oclExprToAlloy (IfExp expr) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		Expr res = null;
		
		Expr eif = oclExprToAlloy(expr.getCondition());
		Expr ethen = oclExprToAlloy(expr.getThenExpression());
		Expr eelse = oclExprToAlloy(expr.getElseExpression());

		res = ExprITE.make(null, eif, ethen, eelse);
		return res;
	}
	
	public Expr oclExprToAlloy (IteratorExp expr) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		Expr res = null;
		
		List<Variable> variterator = expr.getIterator();
		if (variterator.size() != 1) throw new ErrorTransform ("Invalid variables on closure.","OCL2Alloy",variterator);

		Decl d = variableListToExpr(new HashSet<Variable>(variterator),sigs).iterator().next();

		vardecls.add(d);
		Expr src = oclExprToAlloy(expr.getSource());
		Expr bdy = oclExprToAlloy(expr.getBody());
		
		
		if (expr.getReferredIteration().getName().equals("forAll")) {
			try {
				res = ((d.get().in(src)).implies(bdy));
				res = res.forAll(d);
			}
			catch (Err e) { throw new ErrorAlloy(e.getMessage(),"OCL2Alloy",expr);}
		} else if (expr.getReferredIteration().getName().equals("forAll")) {
			try {
				res = ((d.get().in(src)).and(bdy));
				res = res.forSome(d);
			}
			catch (Err e) { throw new ErrorAlloy(e.getMessage(),"OCL2Alloy",expr);}
		} else if (expr.getReferredIteration().getName().equals("select")) {
			try {
				res = ((d.get().in(src)).and(bdy));
				res = res.comprehensionOver(d);
			} catch (Err e) { throw new ErrorAlloy(e.getMessage(),"OCL2Alloy",expr);}
		} else if (expr.getReferredIteration().getName().equals("reject")) {
			try {
				res = ((d.get().in(src)).and(bdy.not()));
				res = res.comprehensionOver(d);
			} catch (Err e) { throw new ErrorAlloy(e.getMessage(),"OCL2Alloy",expr);}
		} else if (expr.getReferredIteration().getName().equals("closure")) {
			res = Sig.NONE.no();
			try {
				Decl dd = bdy.oneOf("2_");
				res = res.comprehensionOver(d,dd);
			} catch (Err e) { throw new ErrorAlloy(e.getMessage(),"OCL2Alloy",expr);}
			res = src.join(res.closure());	
		}
		else throw new ErrorUnsupported ("OCL expression not supported.","OCL2Alloy",expr);

		//System.out.println("Iterator: "+d+", "+src+", "+bdy+", "+expr.getReferredIteration().getName()+": "+res);
		
		return res;
	}
	
	public Expr oclExprToAlloy (PropertyCallExp expr) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		Expr res = null;

		res = oclExprToAlloy(expr.getSource()).join(AlloyUtil.localStateAttribute(expr.getReferredProperty(), sigs));
		
		return res;
	}
	
	public Expr oclExprToAlloy (OperationCallExp expr) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		Expr res = null; 

		Expr src = oclExprToAlloy(expr.getSource());
		
		if (expr.getReferredOperation().getName().equals("not"))
			res = src.not();
		else if (expr.getReferredOperation().getName().equals("isEmpty"))
			res = src.no();
		else if (expr.getReferredOperation().getName().equals("size"))
			res = src.cardinality();
		else if (expr.getReferredOperation().getName().equals("="))
			res = src.equal(oclExprToAlloy((OCLExpression) expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("and"))
			res = src.and(oclExprToAlloy((OCLExpression) expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("or"))
			res = src.or(oclExprToAlloy((OCLExpression) expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("implies"))
			res = src.implies(oclExprToAlloy((OCLExpression) expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("<"))
			res = src.lt(oclExprToAlloy((OCLExpression) expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals(">"))
			res = src.gt(oclExprToAlloy((OCLExpression) expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("<="))
			res = src.lte(oclExprToAlloy((OCLExpression) expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals(">="))
			res = src.gte(oclExprToAlloy((OCLExpression) expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("union"))
			res = src.plus(oclExprToAlloy((OCLExpression) expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("intersection"))
			res = src.intersect(oclExprToAlloy((OCLExpression) expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("includes"))
			res =(oclExprToAlloy((OCLExpression) expr.getArgument().get(0))).in(src);
		else if (expr.getReferredOperation().getName().equals("oclAsSet")) 
			res = src;
		else if (expr.getReferredOperation().getName().equals("+"))
			res = src.iplus(oclExprToAlloy((OCLExpression) expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("-"))
			res = src.iminus(oclExprToAlloy((OCLExpression) expr.getArgument().get(0)));

		
		else throw new ErrorUnsupported ("OCL expression not supported."+expr.getName()+","+expr.getArgument().toString(),"OCL2Alloy",expr);

		return res;
	}

	
	public Expr oclExprToAlloy (OCLExpression expr) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		if (expr instanceof ObjectTemplateExp) return oclExprToAlloy((ObjectTemplateExp) expr);
		else if (expr instanceof BooleanLiteralExp) return oclExprToAlloy((BooleanLiteralExp) expr);
		else if (expr instanceof VariableExp) return oclExprToAlloy((VariableExp) expr);
		else if (expr instanceof RelationCallExp) return oclExprToAlloy((RelationCallExp) expr);
		else if (expr instanceof IteratorExp) return oclExprToAlloy((IteratorExp) expr);
		else if (expr instanceof OperationCallExp) return oclExprToAlloy((OperationCallExp) expr);
		else if (expr instanceof PropertyCallExp) return oclExprToAlloy((PropertyCallExp) expr);
		else if (expr instanceof IfExp) return oclExprToAlloy((IfExp) expr);
		else if (expr instanceof UnlimitedNaturalLiteralExp) return oclExprToAlloy((UnlimitedNaturalLiteralExp) expr);
		else throw new ErrorUnsupported ("OCL expression not supported.","OCL2Alloy",expr);
	}


	// retrieves the Alloy field corresponding to an OCL property (attribute)
	public static Sig.Field propertyToField (Property prop, List<Sig> sigs) {
		String mdl = prop.getOwningType().getPackage().getName();
		
		Sig sig = null;
		for (Sig s : sigs)
			if (s.toString().equals(AlloyUtil.pckPrefix(mdl,prop.getOwningType().getName()))) sig = s;
		if (sig == null) throw new Error ("Sig not found: "+AlloyUtil.pckPrefix(mdl,prop.getOwningType().getName()));

		Sig.Field exp = null;
		for (Sig.Field field : sig.getFields())
			if ((field.label).equals(AlloyUtil.pckPrefix(mdl,prop.getName())))
				exp = field;
		if (exp == null) throw new Error ("Field not found: "+AlloyUtil.pckPrefix(mdl,prop.getName()));
		return exp;
	}
	
	// creates a list of Alloy declarations from a list of OCL variables
		public static Set<Decl> variableListToExpr (Set<? extends VariableDeclaration> ovars, List<Sig> sigs) throws ErrorTransform, ErrorAlloy {
			Set<Decl> avars = new HashSet<Decl>();
			for (VariableDeclaration ovar : ovars) {
				Expr range = Sig.NONE;
				String type = ovar.getType().getName();
				if (type.equals("String")) range = Sig.STRING;
				else 
					for (Sig s : sigs)
						if (s.label.equals(AlloyUtil.pckPrefix(ovar.getType().getPackage().getName(),type))) range = s;
			
				if (range.equals(Sig.NONE)) throw new ErrorTransform ("Sig not found: "+type+sigs,"AlloyUtil",ovar);
				Decl d;
				try { d = range.oneOf(ovar.getName()); }
				catch (Err a) {throw new ErrorAlloy (a.getMessage(),"AlloyUtil",range);}
				avars.add(d);
			}
			return avars;
		}


}
