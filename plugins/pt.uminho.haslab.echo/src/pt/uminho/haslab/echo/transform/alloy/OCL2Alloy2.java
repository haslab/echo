package pt.uminho.haslab.echo.transform.alloy;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprConstant;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprHasName;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.transform.ConditionTranslator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class OCL2Alloy2 implements ConditionTranslator{

	private Map<String,Entry<ExprHasName,String>> varstates;
	private Map<String,ExprHasName> posvars;
	private Map<String,ExprHasName> prevars;
	private EAlloyRelation parentq;
	private boolean isPre = false;

	private Map<String,Integer> news = new HashMap<String,Integer>();
	
	public OCL2Alloy2(EAlloyRelation q2a, Map<String,Entry<ExprHasName,String>> vardecls, Map<String,ExprHasName> argsvars, Map<String,ExprHasName> prevars) {
		this (vardecls,argsvars,prevars);
		this.parentq = q2a;
	}
	
	public OCL2Alloy2(Map<String,Entry<ExprHasName,String>> vardecls, Map<String,ExprHasName> argsvars, Map<String,ExprHasName> prevars) {
		this.varstates = vardecls;
		this.prevars = prevars;
		this.posvars = argsvars;
	}
	
	Expr oclExprToAlloyV (EObject expr) {
		EStructuralFeature x = expr.eClass().getEStructuralFeature("referredVariable");
		EObject vardecl = (EObject) expr.eGet(x);
		EStructuralFeature name = vardecl.eClass().getEStructuralFeature("varName");
		String varname = (String) vardecl.eGet(name);
		return varstates.get(varname).getKey();
	}
	
	Expr oclExprToAlloyBL (EObject expr){
		EStructuralFeature symb = expr.eClass().getEStructuralFeature("booleanSymbol");
		if (expr.eGet(symb).toString().equals("true")) return ExprConstant.TRUE;
		else return ExprConstant.FALSE;
	}	
	
	Expr oclExprToAlloyAC (EObject expr) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		Expr res = null;
		isPre = false;
		EStructuralFeature source = expr.eClass().getEStructuralFeature("source");
		EObject sourceo = (EObject) expr.eGet(source);
		
		/* Type unused
		EStructuralFeature type = sourceo.eClass().getEStructuralFeature("type");
		EObject typeo = (EObject) sourceo.eGet(type);
		*/
		
		Expr var = oclExprToAlloy(sourceo);
		EStructuralFeature oname = expr.eClass().getEStructuralFeature("name");
		
		String[] str = null;		
		try {
			str = var.type().toExpr().toString().split("@");
		} catch (Err e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String metamodeluri = str[0];
		EStructuralFeature feature = AlloyEchoTranslator.getInstance().getESFeatureFromName(metamodeluri, str[1], (String) expr.eGet(oname));
		Expr aux = propertyToField((String) expr.eGet(oname),var);

		String nameo = feature.getEType().getName();

		if(nameo.equals("EBoolean"))
			res = var.in(aux);	
		else
			res = var.join(aux);	
		return res;
	}
	
	Expr oclExprToAlloyBD (EObject expr) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		Expr res = null;
		isPre = false;
		EStructuralFeature value = expr.eClass().getEStructuralFeature("value");
		Expr val = oclExprToAlloy((EObject) expr.eGet(value));

		
		EStructuralFeature source = expr.eClass().getEStructuralFeature("outPatternElement");
		EObject sourceo = (EObject) expr.eGet(source);
		EStructuralFeature name = sourceo.eClass().getEStructuralFeature("varName");
		String varname = (String) sourceo.eGet(name);
		System.out.println("v "+varname + " at " + varstates);
		Expr var = varstates.get(varname).getKey();

		EStructuralFeature oname = expr.eClass().getEStructuralFeature("propertyName");
		
		String[] str = null;		
		try {
			str = var.type().toExpr().toString().split("@");
		} catch (Err e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String metamodeluri = str[0];
		EStructuralFeature feature = AlloyEchoTranslator.getInstance().getESFeatureFromName(metamodeluri, str[1], (String) expr.eGet(oname));
		Expr aux = propertyToField((String) expr.eGet(oname),var);

		String nameo = feature.getEType().getName();

		if(nameo.equals("EBoolean"))
			res = (var.join(aux)).iff(val);	
		else
			res = (var.join(aux)).equal(val);	
		return res;
	}
	
	Expr oclExprToAlloyOC (EObject expr) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		Expr res = null; 
		isPre = false;
		EStructuralFeature source = expr.eClass().getEStructuralFeature("source");
		EStructuralFeature operat = expr.eClass().getEStructuralFeature("operationName");
		String operatorname = (String) expr.eGet(operat);
		Expr src = oclExprToAlloy((EObject) expr.eGet(source));
		EStructuralFeature arguments = expr.eClass().getEStructuralFeature("arguments");
		@SuppressWarnings("unchecked")
		EList<EObject> argumentso = (EList<EObject>) expr.eGet(arguments);
		if (operatorname.equals("not"))
			res = src.not();
		else if (operatorname.equals("isEmpty"))
			res = src.no();
		else if (operatorname.equals("size"))
			res = src.cardinality();
		else if (operatorname.equals("=")) {
			Expr aux = oclExprToAlloy(argumentso.get(0));
			if (aux.type().is_bool)
				res = src.iff(aux);
			else 
				res = src.equal(aux);
		}
		else if (operatorname.equals("<>")){
			Expr aux = oclExprToAlloy(argumentso.get(0));
			EStructuralFeature type = argumentso.get(0).eClass().getEStructuralFeature("type");
			EObject typeo = (EObject) argumentso.get(0).eGet(type);
			EStructuralFeature name = typeo.eClass().getEStructuralFeature("name");
			String nameo = (String) typeo.eGet(name);
			if (nameo.equals("Boolean"))
				res = src.iff(aux).not();
			else 
				res = src.equal(aux).not();
		}
		else if (operatorname.equals("and"))
			res = src.and(oclExprToAlloy(argumentso.get(0)));
		else if (operatorname.equals("or")) {
			try{
				//res = closure2Reflexive(argumentso.get(0),(EObject) expr.eGet(source));
			}
			catch (Error a) {
				res = src.or(oclExprToAlloy(argumentso.get(0)));
			}
		}
		else if (operatorname.equals("implies"))
			res = src.implies(oclExprToAlloy(argumentso.get(0)));
		else if (operatorname.equals("<"))
			res = src.lt(oclExprToAlloy(argumentso.get(0)));
		else if (operatorname.equals(">"))
			res = src.gt(oclExprToAlloy(argumentso.get(0)));
		else if (operatorname.equals("<="))
			res = src.lte(oclExprToAlloy(argumentso.get(0)));
		else if (operatorname.equals(">="))
			res = src.gte(oclExprToAlloy(argumentso.get(0)));
		else if (operatorname.equals("union"))
			res = src.plus(oclExprToAlloy(argumentso.get(0)));
		else if (operatorname.equals("intersection"))
			res = src.intersect(oclExprToAlloy(argumentso.get(0)));
		else if (operatorname.equals("includes"))
			res =(oclExprToAlloy(argumentso.get(0))).in(src);
		else if (operatorname.equals("oclAsSet") || operatorname.equals("asSet")) 
			res = src;
		else if (operatorname.equals("+"))
			res = src.iplus(oclExprToAlloy(argumentso.get(0)));
		else if (operatorname.equals("-"))
			res = src.iminus(oclExprToAlloy(argumentso.get(0)));
		else if (operatorname.equals("allInstances"))
			res = src;
		/*else if (operatorname.equals("oclIsNew")) {
			EObject container = expr.eContainer();
			while (!(container instanceof IteratorExp) && container != null)
				container = container.eContainer();
			if (container == null || !((IteratorExp)container).getReferredIteration().getName().equals("one"))
				throw new ErrorTransform("oclIsNew may only occur in a \"one\" iteration");

			
			EObject var = (EObject) expr.eGet(source);
			EStructuralFeature type = var.eClass().getEStructuralFeature("type");
			EObject typeo = (EObject) var.eGet(type);
			EStructuralFeature name = typeo.eClass().getEStructuralFeature("name");
			String cl = (String) typeo.eGet(name);
			
			String metamodeluri = URIUtil.resolveURI(typeo.eResource());
			
			Integer newi = news.get(cl);
			if (newi == null) news.put(cl,1);
			else news.put(cl,newi+1);
			
			Field statefield = AlloyEchoTranslator.getInstance().getStateFieldFromClassName(metamodeluri,cl);
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
		}*/
			
		
		else throw new ErrorUnsupported ("OCL operation not supported: "+expr.toString()+".");

		return res;
	}

	
	public Expr oclExprToAlloy (EObject expr) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
		if (expr.eClass().getName().equals("OperatorCallExp")) { return oclExprToAlloyOC(expr); }
		else if (expr.eClass().getName().equals("NavigationOrAttributeCallExp")) { return oclExprToAlloyAC(expr); }
		else if (expr.eClass().getName().equals("VariableExp")) { return oclExprToAlloyV(expr); }
		else if (expr.eClass().getName().equals("BooleanExp")) { return oclExprToAlloyBL(expr); }
		else if (expr.eClass().getName().equals("Binding")) { return oclExprToAlloyBD(expr); }


		else throw new ErrorUnsupported ("OCL expression not supported: "+expr+".");
	}
	
	

	// retrieves the Alloy field corresponding to an OCL property (attribute)
	Expr propertyToField (String propn, Expr var) throws ErrorTransform {		
		String[] str = null;		
		try {
			str = var.type().toExpr().toString().split("@");
		} catch (Err e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String metamodeluri = str[0];
		EStructuralFeature feature = AlloyEchoTranslator.getInstance().getESFeatureFromName(metamodeluri, str[1], propn);
		Field field = AlloyEchoTranslator.getInstance().getFieldFromFeature(metamodeluri, feature);
		Expr exp = null;
		
		Expr statesig = null;
		if ((isPre?prevars:posvars) != null && var instanceof ExprHasName) 
			statesig = (isPre?prevars:posvars).get(varstates.get(((ExprHasName)var).label).getValue());
		if (statesig == null)
			statesig = AlloyEchoTranslator.getInstance().getMetamodel(metamodeluri).sig_metamodel;

		if (field == null && feature instanceof EchoReporter && ((EReference) feature).getEOpposite() != null && EchoOptionsSetup.getInstance().isOptimize()) {
			feature = AlloyEchoTranslator.getInstance().getESFeatureFromName(metamodeluri, ((EReference) feature).getEOpposite().getEContainingClass().getName(),((EReference) feature).getEOpposite().getName());
			field = AlloyEchoTranslator.getInstance().getFieldFromFeature(metamodeluri,feature);
			exp = (field.join(statesig)).transpose();
		}
		else {
			exp = (field.join(statesig));
		}

		if (exp == null) throw new Error ("Field not found: "+metamodeluri +", "+propn); 
		return exp;
	}
	
	

		/*
		 * Tries to convert an OCL transitive closure into an Alloy reflexive closure
		 * @param x
		 * @param y
		 * @return
		 * @throws ErrorTransform
		 * @throws ErrorAlloy
		 * @throws ErrorUnsupported
		 */
		/*private Expr closure2Reflexive (OCLExpression x, OCLExpression y) throws ErrorTransform, ErrorAlloy, ErrorUnsupported {
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
		}*/
		
		
		public Map<String,Integer> getOCLAreNews() {
			return news;
		}

		@Override
		public Expr translateExpressions(List<Object> lex) throws ErrorAlloy,
				ErrorTransform, ErrorUnsupported {
			Expr expr = Sig.NONE.no();
			for (Object ex : lex) {
				expr = AlloyUtil.cleanAnd(expr, oclExprToAlloy((EObject) ex));
			}
			return expr;
		}

}
