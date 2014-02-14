package pt.uminho.haslab.echo.engine;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner;
import pt.uminho.haslab.echo.EchoTypeError;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.engine.ast.Constants;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IExpression;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.echo.engine.ast.IIntExpression;
import pt.uminho.haslab.echo.engine.ast.INode;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.model.EVariable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ocl.examples.pivot.BooleanLiteralExp;
import org.eclipse.ocl.examples.pivot.IfExp;
import org.eclipse.ocl.examples.pivot.IteratorExp;
import org.eclipse.ocl.examples.pivot.OCLExpression;
import org.eclipse.ocl.examples.pivot.OperationCallExp;
import org.eclipse.ocl.examples.pivot.Property;
import org.eclipse.ocl.examples.pivot.PropertyCallExp;
import org.eclipse.ocl.examples.pivot.TypeExp;
import org.eclipse.ocl.examples.pivot.UnlimitedNaturalLiteralExp;
import org.eclipse.ocl.examples.pivot.Variable;
import org.eclipse.ocl.examples.pivot.VariableDeclaration;
import org.eclipse.ocl.examples.pivot.VariableExp;
import org.eclipse.qvtd.pivot.qvttemplate.ObjectTemplateExp;
import org.eclipse.qvtd.pivot.qvttemplate.PropertyTemplateItem;

/**
 * Translates EMF OCL expressions into engine representation.
 * 
 * @author nmm, tmg
 * @version 0.4 14/02/2014
 */
public class OCLTranslator {

	protected final IContext context;
	protected boolean isPre = false;
	private Map<String, Integer> news = new HashMap<>();

	public OCLTranslator(IContext context) {
		this.context = context;
	}

	public INode translate(OCLExpression expr) throws EchoError {
		if (expr instanceof ObjectTemplateExp)
			return translateFormula((ObjectTemplateExp) expr);
		else if (expr instanceof BooleanLiteralExp)
			return translateFormula((BooleanLiteralExp) expr);
		else if (expr instanceof VariableExp)
			return translateExpression((VariableExp) expr);
		// else if (expr instanceof RelationCallExp) return
		// translate((RelationCallExp) expr);
		else if (expr instanceof IteratorExp)
			return translate((IteratorExp) expr);
		else if (expr instanceof OperationCallExp)
			return translate((OperationCallExp) expr);
		else if (expr instanceof PropertyCallExp) {
			INode n = translate((PropertyCallExp) expr);
			return n;
		}
		else if (expr instanceof IfExp)
			return translateExpression((IfExp) expr);
		else if (expr instanceof UnlimitedNaturalLiteralExp)
			return translateInteger((UnlimitedNaturalLiteralExp) expr);
		else if (expr instanceof TypeExp)
			return translateExpression((TypeExp) expr);
		else
			throw new ErrorUnsupported("OCL expression not supported: " + expr
					+ ".");
	}

	public IFormula translateFormula(OCLExpression expr) throws EchoError {
		if (expr instanceof ObjectTemplateExp)
			return translateFormula((ObjectTemplateExp) expr);
		else if (expr instanceof BooleanLiteralExp)
			return translateFormula((BooleanLiteralExp) expr);
		else if (expr instanceof PropertyCallExp) {
			INode n = translate((PropertyCallExp) expr);
			if (n instanceof IFormula)
				return (IFormula) n;
		}
		else if (expr instanceof IteratorExp) {
			INode n = translate((IteratorExp) expr);
			if (n instanceof IFormula)
				return (IFormula) n;
		}
		else if (expr instanceof OperationCallExp) {
			INode n = translate((OperationCallExp) expr);
			if (n instanceof IFormula)
				return (IFormula) n;
		}

		throw new EchoTypeError("Formula");
	}

	public IExpression translateExpression(OCLExpression expr) throws EchoError {
		if (expr instanceof TypeExp)
			return translateExpression((TypeExp) expr);
		else if (expr instanceof VariableExp)
			return translateExpression((VariableExp) expr);
		else if (expr instanceof IfExp)
			return translateExpression((IfExp) expr);

		else if (expr instanceof PropertyCallExp) {
			INode n = translate((PropertyCallExp) expr);
			
			if (n instanceof IExpression)
				return (IExpression) n;
		}

		else if (expr instanceof OperationCallExp) {
			INode n = translate((OperationCallExp) expr);
			if (n instanceof IExpression)
				return (IExpression) n;
		}

		else if (expr instanceof IteratorExp) {
			INode n = translate((IteratorExp) expr);
			if (n instanceof IExpression)
				return (IExpression) n;
		}

		throw new EchoTypeError("Expression");
	}

	public IIntExpression translateInteger(OCLExpression expr) throws EchoError {
		if (expr instanceof UnlimitedNaturalLiteralExp)
			return translateInteger((UnlimitedNaturalLiteralExp) expr);

		else if (expr instanceof OperationCallExp) {
			INode n = translate((OperationCallExp) expr);
			if (n instanceof IIntExpression)
				return (IIntExpression) n;
		}

		throw new EchoTypeError("IntExpression");

	}

	protected IExpression translateExpression(VariableExp expr) {
		String varname = expr.toString();
		context.setCurrentModel(context.getVarModel(varname));
		return context.getVar(varname);
	}

	protected IFormula translateFormula(BooleanLiteralExp expr) {
		if (expr.isBooleanSymbol())
			return Constants.TRUE();
		else
			return Constants.FALSE();
	}

	private IIntExpression translateInteger(UnlimitedNaturalLiteralExp expr)
			throws EchoError {
		Number n = expr.getUnlimitedNaturalSymbol();

		if (n.toString().equals("*"))
			throw new ErrorTransform("No support for unlimited integers.");
		Integer bitwidth = EchoOptionsSetup.getInstance().getBitwidth();
		Integer max = (int) (Math.pow(2, bitwidth) / 2);
		if (n.intValue() >= max || n.intValue() < -max)
			throw new ErrorTransform("Bitwidth not enough to represent: " + n
					+ ".");

		return Constants.makeNumber(n.intValue());
	}

	private IFormula translateFormula(ObjectTemplateExp temp) throws EchoError {
		IFormula result = Constants.TRUE();

		for (PropertyTemplateItem part : temp.getPart()) {
			// calculates OCL expression
			OCLExpression value = part.getValue();
			INode ocl = this.translate(value);
			// retrieves the Alloy field
			Property prop = part.getReferredProperty();

			// retrieves the Alloy root variable
			String varName = temp.getBindsTo().getName();
			IExpression var = context.getVar(varName);

			if (var == null)
				throw new ErrorTransform("Variable not declared: "
						+ temp.getBindsTo());

			context.setCurrentModel(context.getVarModel(varName));
			IExpression localField = propertyToField(prop);
			if (part.isIsOpposite())
				localField = localField.transpose();
			context.setCurrentModel(null);

			// merges the whole thing
			IFormula item;
			if (ocl.equals(Constants.TRUE()))
				item = var.in(localField);
			else if (ocl.equals(Constants.FALSE()))
				item = var.in(localField).not();
			else if (value instanceof ObjectTemplateExp) {
				varName = ((ObjectTemplateExp) value).getBindsTo().getName();
				IExpression var1 = context.getVar(varName);
				if (var1 == null)
					throw new ErrorTransform("Variable not declared: "
							+ ((ObjectTemplateExp) value).getBindsTo());

				item = var1.in(var.join(localField));
				item = item.and((IFormula) ocl);
			} else {
				item = ((IExpression) ocl).in(var.join(localField));
			}
			result = result.and(item);
		}

		OCLExpression where = temp.getWhere();
		if (where != null) {
			IFormula awhere = (IFormula) translate(where);
			result = result.and(awhere);
		}

		return result;
	}

	private IExpression translateExpression(IfExp expr) throws EchoError {
		IExpression res = null;

		IFormula eif = translateFormula(expr.getCondition());
		IExpression thenExpr = translateExpression(expr.getThenExpression());
		IExpression elseExpr = translateExpression(expr.getElseExpression());

		res = eif.thenElse(thenExpr, elseExpr);
		return res;
	}

	private INode translate(PropertyCallExp expr) throws EchoError {
		context.setCurrentModel(null);
		context.setCurrentPre(((PropertyCallExp) expr).isPre());
		INode res = null;
		IExpression var = translateExpression(expr.getSource());
		IExpression aux = propertyToField(expr.getReferredProperty());
		context.setCurrentModel(null);
		context.setCurrentPre(false);
		if (expr.getType().getName().equals("Boolean"))
			res = var.in(aux);
		else
			res = var.join(aux);
		return res;
	}

	private IExpression translateExpression(TypeExp expr) throws EchoError {
		String metaModelUri = EcoreUtil.getURI(
				expr.getReferredType().getPackage().getEPackage()).path();
		EMetamodel metaModel = MDEManager.getInstance().getMetamodel(
				metaModelUri, false);
		return context.getClassExpression(metaModel.ID, expr.getReferredType()
				.getName());
	}

	public INode translate(IteratorExp expr) throws EchoError {
		INode res;

		List<Variable> varIterator = expr.getIterator();
		if (varIterator.size() != 1)
			throw new ErrorTransform("Invalid variables on closure: "
					+ varIterator);
		EVariable x = EVariable.getVariable(varIterator.get(0));

		IDecl d = context.getDecl(x);

		IExpression src = translateExpression(expr.getSource());
		context.addVar(d);

		// TODO: confirmar irrelevancia sem opera????es ou QVT
		/*
		 * for (String s : varstates.keySet()) { ExprVar var = (ExprVar)
		 * varstates.get(s).getKey(); if (src.hasVar(var) &&
		 * varstates.get(s).getValue() != null) varstates.put(d.get().label, new
		 * AbstractMap
		 * .SimpleEntry<ExprHasName,String>(d.get(),varstates.get(s).getValue
		 * ())); }
		 */

		INode body = translate(expr.getBody());

		IFormula aux;

		if (expr.getReferredIteration().getName().equals("forAll")) {
			aux = (IFormula) body;
			aux = ((d.expression().in(src)).implies(aux));
			res = aux.forAll(d);
		} else if (expr.getReferredIteration().getName().equals("exists")) {
			aux = (IFormula) body;
			aux = ((d.expression().in(src)).and(aux));
			res = aux.forSome(d);
		} else if (expr.getReferredIteration().getName().equals("one")) {
			aux = (IFormula) body;
			aux = ((d.expression().in(src)).and(aux));
			res = aux.forOne(d);
		} else if (expr.getReferredIteration().getName().equals("collect")) {
			aux = d.expression().in(src);
			res = src.join(aux.comprehension(d,
					((IExpression) body).oneOf("2_")));
		} else if (expr.getReferredIteration().getName().equals("select")) {
			aux = (IFormula) body;
			aux = ((d.expression().in(src)).and(aux));
			res = aux.comprehension(d);
		} else if (expr.getReferredIteration().getName().equals("reject")) {
			aux = (IFormula) body;
			aux = ((d.expression().in(src)).and(aux.not()));
			res = aux.comprehension(d);
		} else if (expr.getReferredIteration().getName().equals("closure")) {
			IDecl dd = ((IExpression) body).oneOf("2_");
			res = Constants.TRUE().comprehension(d, dd);
			res = src.join(((IExpression) res).closure());
		} else
			throw new ErrorUnsupported("OCL iterator not supported: "
					+ expr.getReferredIteration() + ".");
		context.remove(d.name());

		return res;
	}

	INode translate(OperationCallExp expr) throws EchoError {
		INode res;
		context.setCurrentPre(expr.isPre());
		INode src = translate(expr.getSource());
		if (expr.getReferredOperation().getName().equals("not"))
			res = ((IFormula) src).not();
		else if (expr.getReferredOperation().getName().equals("isEmpty"))
			res = ((IExpression) src).no();
		else if (expr.getReferredOperation().getName().equals("size")) {
			EchoReporter.getInstance().warning(
					"Integer operators (size) require suitable bitwidths.",
					EchoRunner.Task.TRANSLATE_OCL);
			res = ((IExpression) src).cardinality();
		} else if (expr.getReferredOperation().getName().equals("=")) {
			INode aux = translate(expr.getArgument().get(0));
			if (expr.getArgument().get(0).getType().getName().equals("Boolean"))
				res = ((IFormula) src).iff((IFormula) aux);
			else if (expr.getArgument().get(0).getType().getName()
					.equals("UnlimitedNatural"))
				res = ((IIntExpression) src).eq((IIntExpression) aux);
			else
				res = ((IExpression) src).eq((IExpression) aux);
		} else if (expr.getReferredOperation().getName().equals("<>")) {
			INode aux = translate(expr.getArgument().get(0));
			if (expr.getArgument().get(0).getType().getName().equals("Boolean"))
				res = ((IFormula) src).iff((IFormula) aux).not();
			else
				res = ((IExpression) src).eq((IExpression) aux).not();
		} else if (expr.getReferredOperation().getName().equals("and"))
			res = ((IFormula) src).and(translateFormula(expr.getArgument().get(
					0)));
		else if (expr.getReferredOperation().getName().equals("or")) {
			try {
				res = closure2Reflexive(expr.getArgument().get(0),
						expr.getSource());
			} catch (Error a) {
				res = ((IFormula) src).or(translateFormula(expr.getArgument()
						.get(0)));
			}
		} else if (expr.getReferredOperation().getName().equals("implies"))
			res = ((IFormula) src).implies(translateFormula(expr.getArgument()
					.get(0)));
		else if (expr.getReferredOperation().getName().equals("<"))
			res = ((IIntExpression) src).lt(translateInteger(expr.getArgument()
					.get(0)));
		else if (expr.getReferredOperation().getName().equals(">"))
			res = ((IIntExpression) src).gt(translateInteger(expr.getArgument()
					.get(0)));
		else if (expr.getReferredOperation().getName().equals("<="))
			res = ((IIntExpression) src).lte(translateInteger(expr
					.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals(">="))
			res = ((IIntExpression) src).gte(translateInteger(expr
					.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("union"))
			res = ((IExpression) src).union(translateExpression(expr
					.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("intersection"))
			res = ((IExpression) src).intersection(translateExpression(expr
					.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("includes"))
			res = (translateExpression(expr.getArgument().get(0)))
					.in((IExpression) src);
		else if (expr.getReferredOperation().getName().equals("oclAsSet")
				|| expr.getReferredOperation().getName().equals("asSet"))
			res = src;
		else if (expr.getReferredOperation().getName().equals("+"))
			res = ((IIntExpression) src).plus(translateInteger(expr
					.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("-"))
			res = ((IIntExpression) src).minus(translateInteger(expr
					.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("allInstances"))
			res = src;
		else if (expr.getReferredOperation().getName().equals("oclIsNew")) {
			EObject container = expr.eContainer();
			while (!(container instanceof IteratorExp) && container != null)
				container = container.eContainer();
			if (container == null
					|| !((IteratorExp) container).getReferredIteration()
							.getName().equals("one"))
				throw new ErrorTransform(
						"oclIsNew may only occur in a \"one\" iteration");

			VariableExp var = (VariableExp) expr.getSource();
			String cl = var.getType().getName();
			String metamodeluri = EcoreUtil.getURI(
					var.getType().getPackage().getEPackage()).path();

			Integer newi = news.get(cl);
			if (newi == null)
				news.put(cl, 1);
			else
				news.put(cl, newi + 1);

			EMetamodel metamodel = MDEManager.getInstance().getMetamodel(
					metamodeluri, false);
			context.setCurrentModel(context.getVarModel(var.getName()));
			// not exists in pre
			context.setCurrentPre(true);
			IFormula pree = (((IExpression) src).in(context.getClassExpression(
					metamodel.ID, cl))).not();
			// exists in pos
			context.setCurrentPre(false);
			IFormula pose = ((IExpression) src).in(context.getClassExpression(
					metamodel.ID, cl));

			res = pree.and(pose);
		}

		else
			throw new ErrorUnsupported("OCL operation not supported: "
					+ expr.toString() + ".");

		context.setCurrentPre(false);

		return res;
	}

	// Expr oclExprToAlloy (RelationCallExp expr) throws EchoError {
	//
	// List<IExpression> aux = new
	// ArrayList<IExpression>(context.getModelParams(isPre));
	//
	// AlloyFormula res = (AlloyFormula)
	// parentq.transformation_translator.callRelation(new
	// EQVTRelation(expr.getReferredRelation()), parentq.dependency,aux);
	// if (res == null) {
	// EQVTRelation rel = new EQVTRelation(expr.getReferredRelation());
	// new EAlloyRelation (parentq,rel);
	// res = (AlloyFormula)
	// parentq.transformation_translator.callRelation(rel,parentq.dependency,aux);
	// }
	//
	// List<OCLExpression> vars = expr.getArgument();
	// List<Expr> avars = new ArrayList<Expr>();
	// for (OCLExpression var : vars){
	// Expr avar = oclExprToAlloy(var);
	// avars.add(avar);
	// }
	//
	// Expr insig = avars.get(avars.size()-1);
	// avars.remove(insig);
	// Expr form = res.formula;
	// for (Expr avar : avars)
	// form = avar.join(form);
	// form = insig.in(form);
	//
	// return form;
	// }

	public Map<String, Integer> getOCLAreNews() {
		return news;
	}

	public IFormula translateExpressions(List<Object> lex) throws EchoError {

		IFormula expr = Constants.TRUE();

		for (Object ex : lex) {
			expr = expr.and((IFormula) this.translate((OCLExpression) ex));
		}
		return expr;
	}

	/**
	 * Tries to convert an OCL transitive closure into an Alloy reflexive
	 * closure
	 * 
	 * @param x
	 * @param y
	 * @return
	 * @throws ErrorTransform
	 * @throws ErrorUnsupported
	 */
	protected IFormula closure2Reflexive(OCLExpression x, OCLExpression y)
			throws EchoError {
		IFormula res = Constants.TRUE();
		IExpression exp;
		OperationCallExp a = null, b = null;
		if ((x instanceof OperationCallExp)
				&& ((OperationCallExp) x).getReferredOperation().getName()
						.equals("includes")
				&& ((OperationCallExp) y).getReferredOperation().getName()
						.equals("=")) {
			a = (OperationCallExp) x;
			b = (OperationCallExp) y;
		} else if ((y instanceof OperationCallExp)
				&& ((OperationCallExp) y).getReferredOperation().getName()
						.equals("includes")
				&& ((OperationCallExp) x).getReferredOperation().getName()
						.equals("=")) {
			a = (OperationCallExp) y;
			b = (OperationCallExp) x;
		} else
			throw new Error();

		IteratorExp it = (IteratorExp) a.getSource();
		OperationCallExp itsrc = (OperationCallExp) it.getSource();

		VariableExp a1 = ((VariableExp) itsrc.getSource());
		VariableExp a2 = ((VariableExp) a.getArgument().get(0));
		VariableExp b1 = ((VariableExp) b.getSource());
		VariableExp b2 = ((VariableExp) b.getArgument().get(0));
		if ((a2.getReferredVariable().equals(b1.getReferredVariable()) && a1
				.getReferredVariable().equals(b2.getReferredVariable()))
				|| (a2.getReferredVariable().equals(b2.getReferredVariable()) && a1
						.getReferredVariable().equals(b1.getReferredVariable()))) {
			HashSet<EVariable> aux = new HashSet<EVariable>();
			for (VariableDeclaration xx : it.getIterator())
				aux.add(EVariable.getVariable(xx));

			// Decl d = AlloyUtil.variableListToExpr(aux, varstates,
			// isPre?prevars:posvars).get(it.getIterator().get(0).getName());

			IDecl d = context.getDecl(aux, it.getIterator().get(0).getName());

			context.addVar(d);
			IExpression bdy = translateExpression(it.getBody());
			IDecl dd = bdy.oneOf("2_");
			exp = Constants.TRUE().comprehension(d, dd);

			IExpression v1 = translateExpression(a1);
			IExpression v2 = translateExpression(a2);
			res = v2.in(v1.join(exp.reflexiveClosure()));

			context.remove(d.name());
		}

		return res;
	}

	// retrieves the Alloy field corresponding to an OCL property (attribute)
	protected IExpression propertyToField(Property prop) throws EchoError {
		String metamodeluri = EcoreUtil
				.getURI(prop.getOwningType().getPackage().getEPackage()).path()
				.replace("/resource", "");
		EMetamodel metamodel = MDEManager.getInstance().getMetamodel(
				metamodeluri, false);
		IExpression exp;

		exp = context.getFieldExpression(metamodel.ID, prop.getOwningType()
				.getName(), prop.getName());

		if (exp == null && prop.getOpposite() != null
				&& EchoOptionsSetup.getInstance().isOptimize())
			exp = context.getFieldExpression(metamodel.ID,
					prop.getOpposite().getName(), prop.getName()).transpose();

		if (exp == null)
			throw new Error("Field not found: " + metamodeluri + ", "
					+ prop.getName());
		return exp;
	}

}
