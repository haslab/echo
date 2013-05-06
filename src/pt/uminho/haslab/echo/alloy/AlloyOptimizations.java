package pt.uminho.haslab.echo.alloy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;

import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.transform.EMF2Alloy;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorFatal;
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


public class AlloyOptimizations {

	private final EMF2Alloy translator;

	public AlloyOptimizations (EMF2Alloy translator) {
		this.translator = translator;
	}
	
	public Expr trading(Expr expr) throws ErrorUnsupported {
		
		TradeQnt trader = new TradeQnt();
		Expr res = null;
		try {
			res = trader.visitThis(expr);
		} catch (Err e) { throw new ErrorUnsupported(e.getMessage()); }
	
		return res;
	}
	
	public Expr onePoint(Expr expr) throws ErrorUnsupported {
		
		OnePointQnt onpointer = new OnePointQnt();
		Expr res = null;
		try {
			res = onpointer.visitThis(expr);
		} catch (Err e) { throw new ErrorUnsupported(e.getMessage()+": "+expr); }

		return res;
	}
	
	/** Finds inclusion expression over the given variable
	 */
	private final class TradeForm extends VisitQuery<Entry<Map<Decl,Expr>,Expr>> {
		Map<Decl,Expr> rngs = new HashMap<Decl,Expr>();
		Map<ExprVar,Decl> vars = new HashMap<ExprVar,Decl>();
		List<Decl> decls = new ArrayList<Decl>();
		
		TradeForm(List<Decl> decls) {
			for (Decl d : decls) {
				vars.put((ExprVar) d.get(),d);
				rngs.put(d,((ExprUnary) d.expr).sub);
				this.decls.add(d);
			}
		}

		@Override public Entry<Map<Decl,Expr>,Expr> visit(ExprList x) throws Err { 
			switch(x.op) {
				case AND: 	
             		List<Expr> exps = new ArrayList<Expr>();
	    			for (Expr arg : x.args) {
	    				Entry<Map<Decl,Expr>,Expr> aux = visitThis(arg);
	    				if (!AlloyUtil.isTrue(aux.getValue())) exps.add(aux.getValue());
	    			}
	    			Expr exp = Sig.NONE.no();
	    			if (exps.size() > 1) exp = ExprList.make(null, null, ExprList.Op.AND, exps);
	    			else if (exps.size() == 1) exp = exps.get(0);
	    			return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,exp);
				default: return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,x);
        	 }
		}
        @Override public final Entry<Map<Decl,Expr>,Expr> visit(ExprBinary x) throws Err { 
			switch(x.op){
        		case EQUALS:
        			if (x.right instanceof ExprVar) {
						Decl d = vars.get((ExprVar) x.right);
						if (d != null) {
							Expr r = rngs.get(d);
							if (r == null || r.isSame(((ExprUnary)d.expr).sub)) rngs.put(d,x.left);
							else rngs.put(d,r.intersect(x.left));
						}
						else {
							for (ExprVar v : vars.keySet()) {
								if (x.left.hasVar(v)) {
									PushVar pusher = new PushVar(v);
									Expr nr = pusher.visitThis(x.left);
									if (nr instanceof ExprBinary && ((ExprBinary) nr).left.isSame(v))
										nr = ((ExprBinary) nr).right.join(x.right);
									else if (nr instanceof ExprBinary && ((ExprBinary) nr).right.isSame(v))
										nr = x.right.join(((ExprBinary) nr).left);
									Expr r = rngs.get(v);
									if (r == null) rngs.put(vars.get(v),nr);
									else rngs.put(vars.get(v),r.intersect(nr));
								}
							}
						}
						return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,Sig.NONE.no());
					}
        		case IN: 
					if (x.left instanceof ExprVar) {
						Decl d = vars.get((ExprVar) x.left);
						if (d != null) {
							Expr r = rngs.get(d);
							if (r == null || r.isSame(((ExprUnary)d.expr).sub)) rngs.put(d,x.right);
							else rngs.put(d,r.intersect(x.right));
						}
						else {
							for (ExprVar v : vars.keySet()) {
								if (x.right.hasVar(v)) {
									PushVar pusher = new PushVar(v);
									Expr nr = pusher.visitThis(x.right);
									if (nr instanceof ExprBinary && ((ExprBinary) nr).left.isSame(v))
										nr = ((ExprBinary) nr).right.join(x.left);
									else if (nr instanceof ExprBinary && ((ExprBinary) nr).right.isSame(v))
										nr = x.left.join(((ExprBinary) nr).left);
									Expr r = rngs.get(v);
									if (r == null) rngs.put(vars.get(v),nr);
									else rngs.put(vars.get(v),r.intersect(nr));
								}
							}
						}
						return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,Sig.NONE.no());
					}
        		default: return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,x);
        	}
        };
        @Override public final Entry<Map<Decl,Expr>,Expr> visit(ExprCall x) { return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,x); };
        @Override public final Entry<Map<Decl,Expr>,Expr> visit(ExprConstant x) { return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,x); };
        @Override public final Entry<Map<Decl,Expr>,Expr> visit(ExprITE x) { return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,x); };
        @Override public final Entry<Map<Decl,Expr>,Expr> visit(ExprLet x) { return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,x); };
        @Override public final Entry<Map<Decl,Expr>,Expr> visit(ExprQt x) { return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,x); };
        @Override public final Entry<Map<Decl,Expr>,Expr> visit(ExprUnary x) throws Err { 
			switch(x.op){
        		case NO:
        			for (ExprVar v : vars.keySet()) {
						if (x.sub.hasVar(v)) {
							PushVar pusher = new PushVar(v);
							Expr nr = pusher.visitThis(x.sub);
							if (nr instanceof ExprBinary && ((ExprBinary) nr).left.isSame(v))
								nr = ((ExprBinary) nr).right.join(Sig.UNIV);
							else if (nr instanceof ExprBinary && ((ExprBinary) nr).right.isSame(v))
								nr = Sig.UNIV.join(((ExprBinary) nr).left);
							Expr r = rngs.get(v);
							if (r == null) rngs.put(vars.get(v),nr);
							else rngs.put(vars.get(v),r.minus(nr));
						}
					}
       				return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,Sig.NONE.no());	
        		default: return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,x);
        	}
        };
        @Override public final Entry<Map<Decl,Expr>,Expr> visit(ExprVar x) { return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,x); };
        @Override public final Entry<Map<Decl,Expr>,Expr> visit(Sig x) { return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,x); };
        @Override public final Entry<Map<Decl,Expr>,Expr> visit(Sig.Field x) { return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,x); };

	}
	
	
	private final class PushVar extends VisitQuery<Expr> {
		ExprVar var;

		PushVar(ExprVar var) {
			this.var = var;
		}

        @Override public final Expr visit(ExprBinary x) throws Err { 
        	switch(x.op){
        		case JOIN: 
        			if (x.left.isSame(var) || x.right.isSame(var))
        				return x;
        			else if (x.left.hasVar(var)) {
        				Expr aux = visitThis(x.left);
        				if (!(aux instanceof ExprBinary && ((ExprBinary)aux).left.isSame(var))) throw new Error();
        				return ((ExprBinary)aux).left.join((((ExprBinary)aux).right).join(x.right));
        			} else if (x.right.hasVar(var)) {
        				Expr aux = visitThis(x.right);
        				if (!(aux instanceof ExprBinary && ((ExprBinary)aux).right.isSame(var))) throw new Error();
        				return ((x.left).join(((ExprBinary)aux).left)).join(((ExprBinary)aux).right);
        			}

        		default: return x;
        	}
        };
        @Override public final Expr visit(ExprCall x) { return x; };
        @Override public final Expr visit(ExprList x) { return x; };
        @Override public final Expr visit(ExprConstant x) { return x; };
        @Override public final Expr visit(ExprITE x) { return x; };
        @Override public final Expr visit(ExprLet x) { return x; };
        @Override public final Expr visit(ExprQt x) { return x; };
        @Override public final Expr visit(ExprUnary x) { return x; };
        @Override public final Expr visit(ExprVar x) { return x; };
        @Override public final Expr visit(Sig x) { return x; };
        @Override public final Expr visit(Sig.Field x) { return x; };

      }

	/** Finds quantifications and applies trading on the respective quantifications 
	 * */
	private final class TradeQnt extends VisitQuery<Expr> {
		
        @Override public final Expr visit(ExprQt x) throws Err { 
			Expr ebody = visitThis(x.sub);
			List<Decl> aux = new ArrayList<Decl>();

        	switch (x.op){
        	case ALL :
        		if ((ebody instanceof ExprBinary) && ((ExprBinary)ebody).op.equals(ExprBinary.Op.IMPLIES)) {
    				Expr abody = ((ExprBinary)ebody).left;
    				Expr bbody = ((ExprBinary)ebody).right;

					Entry<Map<Decl,Expr>, Expr> rngs;
					try {
						TradeForm finder = new TradeForm(x.decls);
						rngs = finder.visitThis(abody);
						for (Decl d : rngs.getKey().keySet()) {
							System.out.println("trading on var "+d.get()+" with range "+rngs.getKey().get(d));
							Decl d2 = new Decl(null,null,null,d.names,rngs.getKey().get(d));
							aux.add(d2);
						}
						abody = rngs.getValue();
					} catch (Err e) { e.printStackTrace(); }
					if (!AlloyUtil.isTrue(abody)) ebody = ExprBinary.Op.IMPLIES.make(null, null, abody, bbody);
					else ebody = bbody;

    				aux = AlloyUtil.ordDecls(aux);
    				Expr res = x.op.make(null, null,aux, ebody);
    				return res;
        		}
        	case SOME : 
				Entry<Map<Decl,Expr>, Expr> rngs;
				try {
					TradeForm finder = new TradeForm(x.decls);
					rngs = finder.visitThis(ebody);
					for (Decl d : rngs.getKey().keySet()) {
						//System.out.println("trading on var "+d.get()+" with range "+rngs.getKey().get(d));
						Decl d2 = new Decl(null,null,null,d.names,rngs.getKey().get(d));
						aux.add(d2);
					}
					ebody = rngs.getValue();
				} catch (Err e) { e.printStackTrace(); }

				aux = AlloyUtil.ordDecls(aux);
				Expr res;
				if (ebody.isSame(Sig.NONE.no())) {
					Decl last = aux.remove(aux.size()-1);
					if (!aux.isEmpty())
						res = x.op.make(null, null, aux, last.expr.some());
					else
						res = last.expr.some();
				} else res = x.op.make(null, null, aux, ebody);
				return res;
        	default: return x;
        	}

        };
		
        @Override public final Expr visit(ExprBinary x) throws Err { 
        	Expr left = visitThis(x.left);
        	Expr right = visitThis(x.right);
        	return x.op.make(null, null, left, right); 
        };
        @Override public final Expr visit(ExprCall x) { return x; };
        @Override public final Expr visit(ExprList x) { return x; };
        @Override public final Expr visit(ExprConstant x) { return x; };
        @Override public final Expr visit(ExprITE x) { return x; };
        @Override public final Expr visit(ExprLet x) { return x; };
        @Override public final Expr visit(ExprUnary x) { return x; };
        @Override public final Expr visit(ExprVar x) { return x; };
        @Override public final Expr visit(Sig x) { return x; };
        @Override public final Expr visit(Sig.Field x) { return x; };

      }


	private final class OnePointQnt extends VisitQuery<Expr> {
		   @Override public final Expr visit(ExprQt x) throws Err { 
				Expr sub = x.sub;
	        	switch (x.op){
	        	case ALL :
	        	case SOME : 
	        		List<Decl> decls = new ArrayList<Decl>(x.decls);
	        		for (Decl d : x.decls) {
	        			try {
	        				//System.out.println("Onepointing "+d.expr+ " which is "+translator.isFunctional(d.expr));
							if (translator.isFunctional(d.expr)) {
								sub = AlloyUtil.replace(sub, d.get(), d.expr);
								decls.remove(d);
							}
						} catch (ErrorUnsupported e) { throw new ErrorFatal(e.getMessage());}
	        		}
	        		if (decls.size() > 0) return x.op.make(null, null, decls, sub);
	        		else return sub;
	        		
	        	default: return x;
	        	}

	        };
			
	        @Override public final Expr visit(ExprBinary x) throws Err { 
	        	Expr left = visitThis(x.left);
	        	Expr right = visitThis(x.right);
	        	return x.op.make(null, null, left, right); 
	        };
	        @Override public final Expr visit(ExprCall x) { return x; };
	        @Override public final Expr visit(ExprList x) { return x; };
	        @Override public final Expr visit(ExprConstant x) { return x; };
	        @Override public final Expr visit(ExprITE x) { return x; };
	        @Override public final Expr visit(ExprLet x) { return x; };
	        @Override public final Expr visit(ExprUnary x) { return x; };
	        @Override public final Expr visit(ExprVar x) { return x; };
	        @Override public final Expr visit(Sig x) { return x; };
	        @Override public final Expr visit(Sig.Field x) { return x; };

	}
	
}
