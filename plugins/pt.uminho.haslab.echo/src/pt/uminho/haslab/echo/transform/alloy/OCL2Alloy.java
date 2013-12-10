package pt.uminho.haslab.echo.transform.alloy;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.ocl.examples.pivot.BooleanLiteralExp;
import org.eclipse.ocl.examples.pivot.IfExp;
import org.eclipse.ocl.examples.pivot.IteratorExp;
import org.eclipse.ocl.examples.pivot.OCLExpression;
import org.eclipse.ocl.examples.pivot.OperationCallExp;
import org.eclipse.ocl.examples.pivot.Property;
import org.eclipse.ocl.examples.pivot.PropertyCallExp;
import org.eclipse.ocl.examples.pivot.TypeExp;
import org.eclipse.ocl.examples.pivot.UnlimitedNaturalLiteralExp;
import org.eclipse.ocl.examples.pivot.VariableDeclaration;
import org.eclipse.ocl.examples.pivot.VariableExp;
import org.eclipse.qvtd.pivot.qvtrelation.RelationCallExp;
import org.eclipse.qvtd.pivot.qvttemplate.ObjectTemplateExp;
import org.eclipse.qvtd.pivot.qvttemplate.PropertyTemplateItem;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.alloy.AlloyUtil;
import pt.uminho.haslab.echo.alloy.ErrorAlloy;
import pt.uminho.haslab.echo.consistency.Variable;
import pt.uminho.haslab.echo.consistency.qvt.QVTRelation;
import pt.uminho.haslab.echo.emf.URIUtil;
import pt.uminho.haslab.echo.transform.OCLTranslator;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprConstant;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprHasName;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprITE;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprVar;
import edu.mit.csail.sdg.alloy4compiler.ast.Func;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;

public class OCL2Alloy implements OCLTranslator{

	private Map<String,Entry<ExprHasName,String>> varstates;
	private Map<String,ExprHasName> posvars;
	private Map<String,ExprHasName> prevars;
	private Relation2Alloy parentq;
	private boolean isPre = false;

	private Map<String,Integer> news = new HashMap<String,Integer>();
	
	public OCL2Alloy(Relation2Alloy q2a, Map<String,Entry<ExprHasName,String>> vardecls, Map<String,ExprHasName> argsvars, Map<String,ExprHasName> prevars) {
		this (vardecls,argsvars,prevars);
		this.parentq = q2a;
	}
	
	public OCL2Alloy(Map<String,Entry<ExprHasName,String>> vardecls, Map<String,ExprHasName> argsvars, Map<String,ExprHasName> prevars) {
		EchoReporter.getInstance().debug("OCL2Alloy created: "+vardecls +", "+prevars+", "+argsvars);
		this.varstates = vardecls;
		this.prevars = prevars;
		this.posvars = argsvars;
	}
	
	Expr oclExprToAlloy (VariableExp expr) {
		String varname = expr.toString();
		return varstates.get(varname).getKey();
	}
	
	Expr oclExprToAlloy (BooleanLiteralExp expr){
		if (expr.isBooleanSymbol()) return ExprConstant.TRUE;
		else return ExprConstant.FALSE;
	}

	Expr oclExprToAlloy (UnlimitedNaturalLiteralExp expr) throws EchoError {
		Number n = expr.getUnlimitedNaturalSymbol();

		if (n.toString().equals("*"))  throw new ErrorTransform ("No support for unlimited integers.");
		Integer bitwidth = EchoOptionsSetup.getInstance().getBitwidth();
		Integer max = (int) (Math.pow(2, bitwidth) / 2);
		if (n.intValue() >= max || n.intValue() < -max) throw new ErrorTransform("Bitwidth not enough to represent: "+n+".");

		
		
		return ExprConstant.makeNUMBER(n.intValue());
	}

	Expr oclExprToAlloy (ObjectTemplateExp temp) throws EchoError {
		Expr result = Sig.NONE.no();
		
		for (PropertyTemplateItem part: temp.getPart()) {
			// calculates OCL expression
			OCLExpression value = part.getValue();
			Expr ocl = this.oclExprToAlloy(value);
			// retrieves the Alloy field
			Property prop = part.getReferredProperty();

			// retrieves the Alloy root variable
			String varname = temp.getBindsTo().getName();
			ExprHasName var = varstates.get(varname).getKey();
			if (var == null) throw new ErrorTransform ("Variable not declared: "+ temp.getBindsTo());

			Expr localfield = propertyToField(prop,var);

			if (part.isIsOpposite()) {
				localfield = localfield.transpose();
			}

			// merges the whole thing
			Expr item;
			if (ocl.equals(ExprConstant.TRUE)) item = var.in(localfield);
			else if (ocl.equals(ExprConstant.FALSE)) item = var.in(localfield).not();
			else if (value instanceof ObjectTemplateExp) {
				varname = ((ObjectTemplateExp) value).getBindsTo().getName();
				ExprHasName var1 = varstates.get(varname).getKey();
				if (var1 == null) throw new ErrorTransform ("Variable not declared: "+((ObjectTemplateExp) value).getBindsTo());

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
	
	Expr oclExprToAlloy (RelationCallExp expr) throws EchoError {

		Func func = null;
		func = parentq.transformation_translator.callRelation(new QVTRelation(expr.getReferredRelation()), parentq.direction);
		EchoReporter.getInstance().debug("Should not be null: "+func);
		if (func == null) {
			QVTRelation rel = new QVTRelation(expr.getReferredRelation());
			new Relation2Alloy (parentq,rel);
			func = parentq.transformation_translator.callRelation(rel,parentq.direction);
		}
		List<ExprHasName> aux = new ArrayList<ExprHasName>();
		for (Entry<String, ExprHasName> x : (isPre?prevars:posvars).entrySet())
			aux.add(x.getValue());
		Expr res = func.call(aux.toArray(new ExprHasName[aux.size()]));
		
		List<OCLExpression> vars = expr.getArgument();
		List<Expr> avars = new ArrayList<Expr>();
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

	Expr oclExprToAlloy (IfExp expr) throws EchoError {
		Expr res = null;
		
		Expr eif = oclExprToAlloy(expr.getCondition());
		Expr ethen = oclExprToAlloy(expr.getThenExpression());
		Expr eelse = oclExprToAlloy(expr.getElseExpression());

		res = ExprITE.make(null, eif, ethen, eelse);
		return res;
	}
	
	Expr oclExprToAlloy (IteratorExp expr) throws EchoError {
		Expr res = null;
		
		List<org.eclipse.ocl.examples.pivot.Variable> variterator = expr.getIterator();
		if (variterator.size() != 1) throw new ErrorTransform("Invalid variables on closure: "+variterator);
		Variable x = Variable.getVariable(variterator.get(0));
	
		Decl d = AlloyUtil.variableListToExpr(new HashSet<Variable>(Arrays.asList(x)),varstates,isPre?prevars:posvars).get(variterator.get(0).getName());
		
		Expr src = oclExprToAlloy(expr.getSource());
		varstates.put(d.get().label, new SimpleEntry<ExprHasName,String>(d.get(),null));

		for (String s : varstates.keySet()) {
			ExprVar var = (ExprVar) varstates.get(s).getKey();
			if (src.hasVar(var) && varstates.get(s).getValue() != null)
				varstates.put(d.get().label, new SimpleEntry<ExprHasName,String>(d.get(),varstates.get(s).getValue()));
		}
		
		//EchoReporter.getInstance().debug(varstates+"");
		

		
		Expr bdy = oclExprToAlloy(expr.getBody());

		
		if (expr.getReferredIteration().getName().equals("forAll")) {
			try {
				res = ((d.get().in(src)).implies(bdy));
				res = res.forAll(d);
			}
			catch (Err e) { throw new ErrorAlloy(e.getMessage());}
		} else if (expr.getReferredIteration().getName().equals("exists")) {
				try {
					res = ((d.get().in(src)).and(bdy));
					res = res.forSome(d);
				}
				catch (Err e) { throw new ErrorAlloy(e.getMessage());}
		} else if (expr.getReferredIteration().getName().equals("one")) {
			try {
				res = ((d.get().in(src)).and(bdy));
				res = res.forOne(d);
			}
			catch (Err e) { throw new ErrorAlloy(e.getMessage());}
		} else if (expr.getReferredIteration().getName().equals("forAll")) { //????
			try {
				res = ((d.get().in(src)).and(bdy));
				res = res.forSome(d);
			}
			catch (Err e) { throw new ErrorAlloy(e.getMessage());}
		} else if (expr.getReferredIteration().getName().equals("select")) {
			try {
				res = ((d.get().in(src)).and(bdy));
				res = res.comprehensionOver(d);
			} catch (Err e) { throw new ErrorAlloy(e.getMessage());}
		} else if (expr.getReferredIteration().getName().equals("reject")) {
			try {
				res = ((d.get().in(src)).and(bdy.not()));
				res = res.comprehensionOver(d);
			} catch (Err e) { throw new ErrorAlloy(e.getMessage());}
		} else if (expr.getReferredIteration().getName().equals("closure")) {
			res = Sig.NONE.no();
			try {
				Decl dd = bdy.oneOf("2_");
				res = res.comprehensionOver(d,dd);
			} catch (Err e) { throw new ErrorAlloy(e.getMessage());}
			res = src.join(res.closure());	
		}
		else throw new ErrorUnsupported("OCL iterator not supported: "+expr.getReferredIteration()+".");
		varstates.remove(d.get());
		
		return res;
	}
	
	Expr oclExprToAlloy (TypeExp expr) throws EchoError {
		String metamodeluri = URIUtil.resolveURI(expr.getReferredType().getPackage().getEPackage().eResource());
		EClassifier eclass = AlloyEchoTranslator.getInstance().getEClassifierFromName(metamodeluri, expr.getReferredType().getName());
		Field field = AlloyEchoTranslator.getInstance().getStateFieldFromClass(metamodeluri, (EClass) eclass);
		Expr state = (isPre?prevars:posvars).get(metamodeluri);
		return field.join(state);
	}
	
	
	Expr oclExprToAlloy (PropertyCallExp expr) throws EchoError {
		Expr res = null;
		isPre = expr.isPre();
		Expr var = oclExprToAlloy(expr.getSource());
		Expr aux = propertyToField(expr.getReferredProperty(),var);
		if(expr.getType().getName().equals("Boolean"))
			res = var.in(aux);	
		else
			res = var.join(aux);	
		return res;
	}
	
	Expr oclExprToAlloy (OperationCallExp expr) throws EchoError {
		Expr res = null; 
		isPre = expr.isPre();
		Expr src = oclExprToAlloy(expr.getSource());
		EchoReporter.getInstance().debug("Hi "+src + ", "+expr.getReferredOperation().getName());
		if (expr.getReferredOperation().getName().equals("not"))
			res = src.not();
		else if (expr.getReferredOperation().getName().equals("isEmpty"))
			res = src.no();
		else if (expr.getReferredOperation().getName().equals("size")) {
			EchoReporter.getInstance().warning("Integer operators (size) require suitable bitwidths.", Task.TRANSLATE_OCL);
			res = src.cardinality();
		}
		else if (expr.getReferredOperation().getName().equals("=")) {
			Expr aux = oclExprToAlloy(expr.getArgument().get(0));
			if (expr.getArgument().get(0).getType().getName().equals("Boolean"))
				res = src.iff(aux);
			else 
				res = src.equal(aux);
		}
		else if (expr.getReferredOperation().getName().equals("<>")){
			Expr aux = oclExprToAlloy(expr.getArgument().get(0));
			if (expr.getArgument().get(0).getType().getName().equals("Boolean"))
				res = src.iff(aux).not();
			else 
				res = src.equal(aux).not();
		}
		else if (expr.getReferredOperation().getName().equals("and"))
			res = src.and(oclExprToAlloy(expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("or")) {
			try{
				res = closure2Reflexive(expr.getArgument().get(0),expr.getSource());
			}
			catch (Error a) {
				res = src.or(oclExprToAlloy(expr.getArgument().get(0)));
			}
		}
		else if (expr.getReferredOperation().getName().equals("implies"))
			res = src.implies(oclExprToAlloy(expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("<"))
			res = src.lt(oclExprToAlloy(expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals(">"))
			res = src.gt(oclExprToAlloy(expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("<="))
			res = src.lte(oclExprToAlloy(expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals(">="))
			res = src.gte(oclExprToAlloy(expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("union"))
			res = src.plus(oclExprToAlloy(expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("intersection"))
			res = src.intersect(oclExprToAlloy(expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("includes"))
			res =(oclExprToAlloy(expr.getArgument().get(0))).in(src);
		else if (expr.getReferredOperation().getName().equals("oclAsSet") || expr.getReferredOperation().getName().equals("asSet")) 
			res = src;
		else if (expr.getReferredOperation().getName().equals("+"))
			res = src.iplus(oclExprToAlloy(expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("-"))
			res = src.iminus(oclExprToAlloy(expr.getArgument().get(0)));
		else if (expr.getReferredOperation().getName().equals("allInstances"))
			res = src;
		else if (expr.getReferredOperation().getName().equals("oclIsNew")) {
			EObject container = expr.eContainer();
			while (!(container instanceof IteratorExp) && container != null)
				container = container.eContainer();
			if (container == null || !((IteratorExp)container).getReferredIteration().getName().equals("one"))
				throw new ErrorTransform("oclIsNew may only occur in a \"one\" iteration");

			
			VariableExp var = (VariableExp) expr.getSource();
			String cl = var.getType().getName();
			String metamodeluri = URIUtil.resolveURI(var.getType().getPackage().getEPackage().eResource());
			
			Integer newi = news.get(cl);
			if (newi == null) news.put(cl,1);
			else news.put(cl,newi+1);
			
			EClass ecl =  (EClass) AlloyEchoTranslator.getInstance().getEClassifierFromName(metamodeluri, cl);
			Field statefield = AlloyEchoTranslator.getInstance().getStateFieldFromClass(metamodeluri,ecl);
			Expr pre = Sig.NONE;
			Expr pos = Sig.NONE;
			if (varstates.get(var.toString()) != null && varstates.get(var.toString()).getValue() != null) {
				if (posvars != null)
					pos = posvars.get(varstates.get(var.toString()).getValue());
				if (prevars != null) 
					pre = prevars.get(varstates.get(var.toString()).getValue());
			} else {
				try {
					for (ExprHasName x : posvars.values())
						if (((PrimSig) x.type().toExpr()).label.equals(metamodeluri))
							pos = pos.plus(x);
					for (ExprHasName x : prevars.values())
						if (((PrimSig) x.type().toExpr()).label.equals(metamodeluri))
							pre = pre.plus(x);
				} catch (Err a) {}
			}
			Expr pree = (src.in(statefield.join(pre))).not();
			Expr pose = src.in(statefield.join(pos));
			
			res = pree.and(pose);
		}
			
		
		else throw new ErrorUnsupported ("OCL operation not supported: "+expr.toString()+".");

		return res;
	}

	
	public Expr oclExprToAlloy (OCLExpression expr) throws EchoError {
		if (expr instanceof ObjectTemplateExp) return oclExprToAlloy((ObjectTemplateExp) expr);
		else if (expr instanceof BooleanLiteralExp) return oclExprToAlloy((BooleanLiteralExp) expr);
		else if (expr instanceof VariableExp) return oclExprToAlloy((VariableExp) expr);
		else if (expr instanceof RelationCallExp) return oclExprToAlloy((RelationCallExp) expr);
		else if (expr instanceof IteratorExp) return oclExprToAlloy((IteratorExp) expr);
		else if (expr instanceof OperationCallExp) return oclExprToAlloy((OperationCallExp) expr);
		else if (expr instanceof PropertyCallExp) return oclExprToAlloy((PropertyCallExp) expr);
		else if (expr instanceof IfExp) return oclExprToAlloy((IfExp) expr);
		else if (expr instanceof UnlimitedNaturalLiteralExp) return oclExprToAlloy((UnlimitedNaturalLiteralExp) expr);
		else if (expr instanceof TypeExp) return oclExprToAlloy((TypeExp) expr);
		else throw new ErrorUnsupported ("OCL expression not supported: "+expr+".");
	}
	

	// retrieves the Alloy field corresponding to an OCL property (attribute)
	Expr propertyToField (Property prop, Expr var) throws EchoError {		
		String metamodeluri = URIUtil.resolveURI(prop.getOwningType().getPackage().getEPackage().eResource());
		
		Expr exp;
		Expr statesig = null;
		if ((isPre?prevars:posvars) != null && var instanceof ExprHasName) 
			statesig = (isPre?prevars:posvars).get(varstates.get(((ExprHasName)var).label).getValue());
		if (statesig == null) {
				statesig = AlloyEchoTranslator.getInstance().getMetaModelStateSig(metamodeluri);
				for (Entry<ExprHasName,String> x : varstates.values()) {
					try {
						if(x.getKey().type().toExpr().isSame(statesig))
							statesig = x.getKey();
					} catch (Err e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
		EStructuralFeature feature = AlloyEchoTranslator.getInstance().getESFeatureFromName(metamodeluri, prop.getOwningType().getName(),prop.getName());
		Field field = AlloyEchoTranslator.getInstance().getFieldFromFeature(metamodeluri,feature);
		
		if (field == null && prop.getOpposite() != null && EchoOptionsSetup.getInstance().isOptimize()) {
			feature = AlloyEchoTranslator.getInstance().getESFeatureFromName(metamodeluri, prop.getOpposite().getOwningType().getName(),prop.getOpposite().getName());
			field = AlloyEchoTranslator.getInstance().getFieldFromFeature(metamodeluri,feature);
			exp = (field.join(statesig)).transpose();
		}
		else {
			exp = (field.join(statesig));
		}

		if (exp == null) throw new Error ("Field not found: "+metamodeluri+", "+prop.getName());
		return exp;
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
		private Expr closure2Reflexive (OCLExpression x, OCLExpression y) throws EchoError {
			Expr res = Sig.NONE.no();
			OperationCallExp a = null,b = null;
			if ((x instanceof OperationCallExp) && ((OperationCallExp)x).getReferredOperation().getName().equals("includes") && 
					((OperationCallExp)y).getReferredOperation().getName().equals("=")) {
				a = (OperationCallExp) x;
				b = (OperationCallExp) y;
			} else if ((y instanceof OperationCallExp) && ((OperationCallExp)y).getReferredOperation().getName().equals("includes") && 
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
				HashSet<Variable> aux = new HashSet<Variable>();
				for (VariableDeclaration xx : it.getIterator())
					aux.add(Variable.getVariable(xx));
				Decl d = AlloyUtil.variableListToExpr(aux, varstates, isPre?prevars:posvars).get(it.getIterator().get(0).getName());
				try{
					varstates.put(d.get().label,new SimpleEntry<ExprHasName,String>(d.get(),null));
					Expr bdy = oclExprToAlloy(it.getBody());
					Decl dd = bdy.oneOf("2_");
					res = res.comprehensionOver(d,dd);
				} catch (Err e) {varstates.remove(d.get()); throw new ErrorAlloy(e.getMessage());}
				Expr v1 = oclExprToAlloy(a1);
				Expr v2 = oclExprToAlloy(a2);
				res = v2.in(v1.join(res.reflexiveClosure()));
				varstates.remove(d.get());
			}
				
			return res;
		}
		
		
		public Map<String,Integer> getOCLAreNews() {
			return news;
		}

}
