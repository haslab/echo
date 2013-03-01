package pt.uminho.haslab.echo.transform;

import java.util.ArrayList;
import java.util.List;

import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;

import net.sourceforge.qvtparser.model.emof.Property;
import net.sourceforge.qvtparser.model.essentialocl.BooleanLiteralExp;
import net.sourceforge.qvtparser.model.essentialocl.IteratorExp;
import net.sourceforge.qvtparser.model.essentialocl.OclExpression;
import net.sourceforge.qvtparser.model.essentialocl.OperationCallExp;
import net.sourceforge.qvtparser.model.essentialocl.Variable;
import net.sourceforge.qvtparser.model.essentialocl.VariableExp;
import net.sourceforge.qvtparser.model.essentialocl.impl.PropertyCallExpImpl;
import net.sourceforge.qvtparser.model.qvtbase.Transformation;
import net.sourceforge.qvtparser.model.qvtbase.TypedModel;
import net.sourceforge.qvtparser.model.qvtrelation.RelationCallExp;
import net.sourceforge.qvtparser.model.qvttemplate.ObjectTemplateExp;
import net.sourceforge.qvtparser.model.qvttemplate.PropertyTemplateItem;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprConstant;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprHasName;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;

public class OCL2Alloy {

	private List<Sig> sigs = new ArrayList<Sig>();
	private TypedModel target;
	private List<Decl> vardecls;
	private Transformation qvt;

	public OCL2Alloy(TypedModel target, List<Sig> modelsigs, List<Decl> vardecls, Transformation qvt) {
		this.qvt = qvt;
		this.sigs = modelsigs;
		this.target = target;
		this.vardecls = vardecls;
	}
	
	public Expr oclExprToAlloy (VariableExp expr) throws ErrorTransform {
		String varname = expr.getName();
		Decl decl = null;
		for (Decl d : vardecls){
			if (d.get().label.equals(varname))
				decl = d;}
		if (decl == null) throw new ErrorTransform ("Variable not declared.","OCL2Alloy",expr);
		ExprHasName var = decl.get();
		return var;	
	}
	
	public Expr oclExprToAlloy (BooleanLiteralExp expr){
		if (expr.getBooleanSymbol()) return ExprConstant.TRUE;
		else return ExprConstant.FALSE;
	}
	
	public Expr oclExprToAlloy (ObjectTemplateExp temp) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		Expr result = Sig.NONE.no();
		for (Object part1: ((ObjectTemplateExp) temp).getPart()) { // should be PropertyTemplateItem
			
			// calculates OCL expression
			PropertyTemplateItem part = (PropertyTemplateItem) part1;
			OclExpression value = part.getValue();
			Expr ocl = this.oclExprToAlloy(value);
			// retrieves the Alloy field
			Property prop = part.getReferredProperty();
			Expr localfield = null;
			String mdl = prop.getClass_().getPackage().getName();
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
		return result;
	}
	
	public Expr oclExprToAlloy (RelationCallExp expr) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		QVTRelation2Alloy trans = new QVTRelation2Alloy (target, expr.getReferredRelation(), sigs, qvt, vardecls);
		return trans.getFact();
	}

	public Expr oclExprToAlloy (IteratorExp expr) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		Expr res = null;

		List<Decl> decls = variableListToExpr(expr.getIterator(),sigs);
		vardecls.addAll(decls);
		Expr src = oclExprToAlloy(expr.getSource());
		Expr bdy = oclExprToAlloy(expr.getBody());
		
		if (expr.getName().equals("forAll")) {
			res = (src.implies(bdy));
			for (Decl d : decls)
				try {res = res.forAll(d);}
				catch (Err e) { throw new ErrorAlloy(e.getMessage(),"OCL2Alloy",expr);}
		} else if (expr.getName().equals("forAll")) {
			res = (src.implies(bdy));
			for (Decl d : decls)
				try {res = res.forSome(d);}
				catch (Err e) { throw new ErrorAlloy(e.getMessage(),"OCL2Alloy",expr);}
		} else if (expr.getName().equals("select")) {
			res = (src.and(bdy));
			for (Decl d : decls)
				try {res = res.comprehensionOver(d);}
				catch (Err e) { throw new ErrorAlloy(e.getMessage(),"OCL2Alloy",expr);}
		} else if (expr.getName().equals("reject")) {
			res = (src.and(bdy.not()));
			for (Decl d : decls)
				try {res = res.comprehensionOver(d);}
				catch (Err e) { throw new ErrorAlloy(e.getMessage(),"OCL2Alloy",expr);}
		}
		
		//else if (expr.getName().equals("closure")) {}
		else throw new ErrorUnsupported ("OCL expression not supported.","OCL2Alloy",expr);

		return res;
	}
	
	public Expr oclExprToAlloy (PropertyCallExpImpl expr) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
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
		else if (expr.getReferredOperation().getName().equals("="))
			res = src.equal(oclExprToAlloy((OclExpression) expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("and"))
			res = src.and(oclExprToAlloy((OclExpression) expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("or"))
			res = src.or(oclExprToAlloy((OclExpression) expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("implies"))
			res = src.implies(oclExprToAlloy((OclExpression) expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("<"))
			res = src.lt(oclExprToAlloy((OclExpression) expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals(">"))
			res = src.gt(oclExprToAlloy((OclExpression) expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("<="))
			res = src.lte(oclExprToAlloy((OclExpression) expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals(">="))
			res = src.gte(oclExprToAlloy((OclExpression) expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("union"))
			res = src.plus(oclExprToAlloy((OclExpression) expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("intersection"))
			res = src.intersect(oclExprToAlloy((OclExpression) expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("includes"))
			res =(oclExprToAlloy((OclExpression) expr.getArgument().get(0))).in(src);
		
	
		//else if (expr.getReferredOperation().getName().equals("oclIsKindOf")) {}
		
		else throw new ErrorUnsupported ("OCL expression not supported.","OCL2Alloy",expr);

		return res;
	}

	
	public Expr oclExprToAlloy (OclExpression expr) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		if (expr instanceof ObjectTemplateExp) return oclExprToAlloy((ObjectTemplateExp) expr);
		else if (expr instanceof BooleanLiteralExp) return oclExprToAlloy((BooleanLiteralExp) expr);
		else if (expr instanceof VariableExp) return oclExprToAlloy((VariableExp) expr);
		else if (expr instanceof RelationCallExp) return oclExprToAlloy((RelationCallExp) expr);
		else if (expr instanceof IteratorExp) return oclExprToAlloy((IteratorExp) expr);
		else if (expr instanceof OperationCallExp) return oclExprToAlloy((OperationCallExp) expr);
		else if (expr instanceof PropertyCallExpImpl) return oclExprToAlloy((PropertyCallExpImpl) expr);
		else throw new ErrorUnsupported ("OCL expression not supported.","OCL2Alloy",expr);
	}


	// retrieves the Alloy field corresponding to an OCL property (attribute)
	public static Sig.Field propertyToField (Property prop, List<Sig> sigs) {
		String mdl = prop.getClass_().getPackage().getName();
		
		Sig sig = null;
		for (Sig s : sigs)
			if (s.toString().equals(AlloyUtil.pckPrefix(mdl,prop.getClass_().getName()))) sig = s;
		if (sig == null) throw new Error ("Sig not found: "+AlloyUtil.pckPrefix(mdl,prop.getClass_().getName()));

		Sig.Field exp = null;
		for (Sig.Field field : sig.getFields())
			if ((field.label).equals(AlloyUtil.pckPrefix(mdl,prop.getName())))
				exp = field;
		if (exp == null) throw new Error ("Field not found: "+AlloyUtil.pckPrefix(mdl,prop.getName()));
		return exp;
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
