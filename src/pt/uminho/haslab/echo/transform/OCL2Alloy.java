package pt.uminho.haslab.echo.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.eclipse.qvtd.pivot.qvtbase.TypedModel;
import org.eclipse.qvtd.pivot.qvtrelation.RelationCallExp;
import org.eclipse.qvtd.pivot.qvttemplate.ObjectTemplateExp;
import org.eclipse.qvtd.pivot.qvttemplate.PropertyTemplateItem;

import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.alloy.AlloyUtil;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprConstant;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprHasName;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprITE;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;

public class OCL2Alloy {

	private TypedModel modelvar;
	private EMF2Alloy translator;
	private Set<Decl> vardecls;

	public OCL2Alloy(TypedModel modelvar, EMF2Alloy translator, Set<Decl> vardecls) {
		this (translator,vardecls);
		this.modelvar = modelvar;		
	}
	
	public OCL2Alloy(EMF2Alloy translator, Set<Decl> vardecls) {
		this.vardecls = vardecls;
		this.translator = translator;
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
			localfield = AlloyUtil.localStateAttribute(prop, translator.getInstanceStateSigFromArg(modelvar.getName()), translator);
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
		if (modelvar == null) throw new ErrorTransform ("No QVT transformation available,","OCL2Alloy");
		QVTRelation2Alloy trans = new QVTRelation2Alloy (expr.getReferredRelation(), modelvar, false, translator);
		List<OCLExpression> vars = expr.getArgument();
		List<Expr> avars = new ArrayList<Expr>();
		
		Expr res = trans.getField();
		for (OCLExpression var : vars){
			Expr avar = oclExprToAlloy(var);
			avars.add(avar);
		}
		
		Expr insig = avars.get(avars.size()-1);
		avars.remove(insig);
		for (Expr avar : avars)
		  res = avar.join(res);
		res = insig.in(res);
		
		return res;
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

		Decl d = variableListToExpr(new HashSet<Variable>(variterator),true,translator).iterator().next();

		vardecls.add(d);
		Expr src = oclExprToAlloy(expr.getSource());
		Expr bdy = oclExprToAlloy(expr.getBody());
		
		
		if (expr.getReferredIteration().getName().equals("forAll")) {
			try {
				res = ((d.get().in(src)).implies(bdy));
				res = res.forAll(d);
			}
			catch (Err e) { throw new ErrorAlloy(e.getMessage(),"OCL2Alloy",src);}
		} else if (expr.getReferredIteration().getName().equals("forAll")) {
			try {
				res = ((d.get().in(src)).and(bdy));
				res = res.forSome(d);
			}
			catch (Err e) { throw new ErrorAlloy(e.getMessage(),"OCL2Alloy",src);}
		} else if (expr.getReferredIteration().getName().equals("select")) {
			try {
				res = ((d.get().in(src)).and(bdy));
				res = res.comprehensionOver(d);
			} catch (Err e) { throw new ErrorAlloy(e.getMessage(),"OCL2Alloy",src);}
		} else if (expr.getReferredIteration().getName().equals("reject")) {
			try {
				res = ((d.get().in(src)).and(bdy.not()));
				res = res.comprehensionOver(d);
			} catch (Err e) { throw new ErrorAlloy(e.getMessage(),"OCL2Alloy",src);}
		} else if (expr.getReferredIteration().getName().equals("closure")) {
			res = Sig.NONE.no();
			try {
				Decl dd = bdy.oneOf("2_");
				res = res.comprehensionOver(d,dd);
			} catch (Err e) { throw new ErrorAlloy(e.getMessage(),"OCL2Alloy",src);}
			res = src.join(res.closure());	
		}
		else throw new ErrorUnsupported ("OCL expression not supported.","OCL2Alloy",expr);
		vardecls.remove(d);
		
		return res;
	}
	
	public Expr oclExprToAlloy (PropertyCallExp expr) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		Expr res = null;
		String mdl = expr.getReferredProperty().getOwningType().getPackage().getName();
		PrimSig sig = translator.getModelStateSig(mdl);
		if (sig == null) throw new ErrorTransform("State sig "+mdl+" not found.","OCL2Alloy");
		Expr aux = AlloyUtil.localStateAttribute(expr.getReferredProperty(), sig, translator);
		res = oclExprToAlloy(expr.getSource()).join(aux);
		
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
		else if (expr.getReferredOperation().getName().equals("or")) {
			try{
				res = closure2Reflexive(expr.getArgument().get(0),expr.getSource());
			}
			catch (Error a) {
				res = src.or(oclExprToAlloy((OCLExpression) expr.getArgument().get(0)));
			}
		}
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
	public static Sig.Field propertyToField (Property prop, EMF2Alloy translator) {
		String mdl = prop.getOwningType().getPackage().getName();
		List<PrimSig> sigs = translator.getModelSigs(mdl);
		Sig sig = null;
		for (PrimSig s : sigs)
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
		public static Collection<Decl> variableListToExpr (Collection<? extends VariableDeclaration> ovars, boolean set, EMF2Alloy translator) throws ErrorTransform, ErrorAlloy {
			Collection<Decl> avars = set?(new HashSet<Decl>()):(new ArrayList<Decl>());
			
			for (VariableDeclaration ovar : ovars) {
				Sig range = Sig.NONE;
				String mdl = ovar.getType().getPackage().getName();
				Expr state = translator.getModelStateSig(mdl);

				String type = ovar.getType().getName();
				try {
					if (type.equals("String")) {
					range = Sig.STRING;
					avars.add(range.oneOf(ovar.getName()));
					}
					else  {
						List<PrimSig> sigs = translator.getModelSigs(mdl);
						for (Sig s : sigs)
							if (s.label.equals(AlloyUtil.pckPrefix(ovar.getType().getPackage().getName(),type))) range = s;
				
						if (range.equals(Sig.NONE)) throw new ErrorTransform ("Sig not found: "+type+sigs,"AlloyUtil",ovar);
						Decl d = AlloyUtil.localStateSig(range,state).oneOf(ovar.getName()); 
						avars.add(d);
					}
				} catch (Err a) {throw new ErrorAlloy (a.getMessage(),"AlloyUtil",range);}
			}
			return avars;
		}

		/**
		 * Tries to convert an OCL transitive closure into an Alloy reflexive closure
		 * @param x
		 * @param y
		 * @return
		 * @throws ErrorTransform
		 * @throws ErrorAlloy
		 * @throws ErrorUnsupported
		 */
		private Expr closure2Reflexive (OCLExpression x, OCLExpression y) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
			Expr res = Sig.NONE.no();
			OperationCallExp a = null,b = null;
			if (((OperationCallExp)x).getReferredOperation().getName().equals("includes") && 
					((OperationCallExp)y).getReferredOperation().getName().equals("=")) {
				a = (OperationCallExp) x;
				b = (OperationCallExp) y;
			} else if (((OperationCallExp)y).getReferredOperation().getName().equals("includes") && 
					((OperationCallExp)x).getReferredOperation().getName().equals("=")) {
				a = (OperationCallExp) y;
				b = (OperationCallExp) x;
			} else throw new Error();
			
			IteratorExp it = (IteratorExp) a.getSource();
			OperationCallExp itsrc = (OperationCallExp) it.getSource();

			VariableExp a1 = ((VariableExp) itsrc.getSource());
			VariableExp a2 = ((VariableExp) a.getArgument().get(0));
			VariableExp b1 = ((VariableExp) b.getSource());
			VariableExp b2 = ((VariableExp) b.getArgument().get(0));
			if((a2.getReferredVariable().equals(b1.getReferredVariable()) && 
					a1.getReferredVariable().equals(b2.getReferredVariable())) || 
					(a2.getReferredVariable().equals(b2.getReferredVariable()) && 
					a1.getReferredVariable().equals(b1.getReferredVariable()))) {
				Decl d = variableListToExpr(new HashSet<Variable>(it.getIterator()),true,translator).iterator().next();
				try{
					vardecls.add(d);
					Expr bdy = oclExprToAlloy(it.getBody());
					Decl dd = bdy.oneOf("2_");
					res = res.comprehensionOver(d,dd);
				} catch (Err e) {vardecls.remove(d); throw new ErrorAlloy(e.getMessage(),"OCL2Alloy",res);}
				Expr v1 = oclExprToAlloy(a1);
				Expr v2 = oclExprToAlloy(a2);
				res = v2.in(v1.join(res.reflexiveClosure()));
				vardecls.remove(d);
			}
				
			return res;
		}
		

}
