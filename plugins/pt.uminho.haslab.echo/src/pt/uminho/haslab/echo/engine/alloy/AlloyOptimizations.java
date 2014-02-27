package pt.uminho.haslab.echo.engine.alloy;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorFatal;
import edu.mit.csail.sdg.alloy4.ErrorWarning;
import edu.mit.csail.sdg.alloy4compiler.ast.*;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;
import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.ErrorUnsupported;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;

/**
 * Simplifies Alloy expressions.
 * 
 * @author nmm
 * @version 0.4 20/02/2014
 */
class AlloyOptimizations {

	Expr trading(Expr expr) throws ErrorUnsupported {
		TradeQnt trader = new TradeQnt();
		Expr res = null;
		try {
			res = trader.visitThis(expr);
		} catch (Err e) { throw new ErrorUnsupported(e.getMessage()); }
	
		return res;
	}
	
	Expr onePoint(Expr expr) throws ErrorUnsupported {
		
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
				//System.out.println(d.expr);
				if (d.expr instanceof ExprUnary && ((ExprUnary)d.expr).op.equals(ExprUnary.Op.ONEOF))
					rngs.put(d,((ExprUnary) d.expr).sub);
				else
					rngs.put(d,d.expr);			
				this.decls.add(d);
			}
		}

		@Override public Entry<Map<Decl,Expr>,Expr> visit(ExprList x) throws Err { 
			switch(x.op) {
				case AND: 	
             		List<Expr> exps = new ArrayList<Expr>();
	    			for (Expr arg : x.args) {
	    				Entry<Map<Decl,Expr>,Expr> aux = visitThis(arg);
	    				if (!AlloyHelper.isTrue(aux.getValue())) exps.add(aux.getValue());
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
						Decl d = vars.get(x.right);
						if (d != null) {
							Expr r = rngs.get(d);
//							if (r.isSame(PrimSig.STRING)) rngs.put(d,x.left);
							if (d.expr instanceof ExprUnary && r.isSame(((ExprUnary) d.expr).sub)) rngs.put(d,x.left);
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
									Expr r = rngs.get(vars.get(v));
//									if (r.isSame(PrimSig.STRING)) rngs.put(vars.get(v),nr);
									if (vars.get(v).expr instanceof ExprUnary && r.isSame(((ExprUnary) vars.get(v).expr).sub)) rngs.put(vars.get(v),nr);
									else rngs.put(vars.get(v),r.intersect(nr));
								}
							}
						}
						return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,Sig.NONE.no());
					}
        		case IN: 
					if (x.left instanceof ExprVar) {
						Decl d = vars.get(x.left);
						if (d != null) {
							Expr r = rngs.get(d);
//							if (r.isSame(PrimSig.STRING)) rngs.put(d,x.right);
							if (d.expr instanceof ExprUnary && r.isSame(((ExprUnary) d.expr).sub)) rngs.put(d,x.right);
							else rngs.put(d,r.intersect(x.right));
						}
						else {
							for (ExprVar v : vars.keySet()) {
								if (x.right.hasVar(v)) {
									try {
										PushVar pusher = new PushVar(v);
										Expr nr = pusher.visitThis(x.right);
										if (nr instanceof ExprBinary && ((ExprBinary) nr).left.isSame(v))
											nr = ((ExprBinary) nr).right.join(x.left);
										else if (nr instanceof ExprBinary && ((ExprBinary) nr).right.isSame(v))
											nr = x.left.join(((ExprBinary) nr).left);
										Expr r = rngs.get(vars.get(v));
	//									if (r.isSame(PrimSig.STRING)) rngs.put(vars.get(v),nr);
										if (vars.get(v).expr instanceof ExprUnary && r.isSame(((ExprUnary) vars.get(v).expr).sub)) rngs.put(vars.get(v),nr);
										else rngs.put(vars.get(v),r.intersect(nr));
									} catch (ErrorWarning e) {}
								}
							}
						}
						return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,Sig.NONE.no());
					}
        		default: return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,x);
        	}
        }

        @Override public final Entry<Map<Decl,Expr>,Expr> visit(ExprCall x) { return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,x); }

        @Override public final Entry<Map<Decl,Expr>,Expr> visit(ExprConstant x) { return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,x); }

        @Override public final Entry<Map<Decl,Expr>,Expr> visit(ExprITE x) { return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,x); }

        @Override public final Entry<Map<Decl,Expr>,Expr> visit(ExprLet x) { return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,x); }

        @Override public final Entry<Map<Decl,Expr>,Expr> visit(ExprQt x) { return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,x); }

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
							Expr r = rngs.get(vars.get(v));
							rngs.put(vars.get(v),r.minus(nr));
						}
					}
       				return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,Sig.NONE.no());	
        		default: return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,x);
        	}
        }

        @Override public final Entry<Map<Decl,Expr>,Expr> visit(ExprVar x) { return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,x); }

        @Override public final Entry<Map<Decl,Expr>,Expr> visit(Sig x) { return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,x); }

        @Override public final Entry<Map<Decl,Expr>,Expr> visit(Sig.Field x) { return new SimpleEntry<Map<Decl,Expr>,Expr>(rngs,x); }

    }
	
	
	private final class PushVar extends VisitQuery<Expr> {
		ExprVar var;

		PushVar(ExprVar var) {
			this.var = var;
		}

        @Override public final Expr visit(ExprBinary x) throws Err { 
        	switch(x.op){
        		case JOIN: 
        			if (x.right.isSame(var))
        				return x;
        			else if (x.left.isSame(var))
        				return x.right.transpose().join(var);
        			else if (x.left.hasVar(var)) {
        				Expr aux = visitThis(x.left);
        				if (!(aux instanceof ExprBinary && ((ExprBinary)aux).right.isSame(var))) throw new ErrorWarning("Failed to push: "+x.left);
        				return (x.right.transpose().join(((ExprBinary)aux).left)).join(var);
        			} else if (x.right.hasVar(var)) {
        				Expr aux = visitThis(x.right);
        				if (!(aux instanceof ExprBinary && ((ExprBinary)aux).right.isSame(var))) throw new ErrorWarning("Failed to push: "+x.right);
        				return (x.left.join(((ExprBinary)aux).left)).join(var);
        			}

        		default: return x;
        	}
        }

        @Override public final Expr visit(ExprCall x) { return x; }

        @Override public final Expr visit(ExprList x) { return x; }

        @Override public final Expr visit(ExprConstant x) { return x; }

        @Override public final Expr visit(ExprITE x) { return x; }

        @Override public final Expr visit(ExprLet x) { return x; }

        @Override public final Expr visit(ExprQt x) { return x; }

        @Override public final Expr visit(ExprUnary x) { return x; }

        @Override public final Expr visit(ExprVar x) { return x; }

        @Override public final Expr visit(Sig x) { return x; }

        @Override public final Expr visit(Sig.Field x) { return x; }

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
							//EchoReporter.getInstance().debug("trading on var "+d.get()+" with range "+rngs.getKey().get(d));
							Decl d2 = new Decl(null,null,null,d.names,rngs.getKey().get(d));
							aux.add(d2);
						}
						abody = rngs.getValue();
					} catch (Err e) { e.printStackTrace(); }
					if (!AlloyHelper.isTrue(abody)) ebody = ExprBinary.Op.IMPLIES.make(null, null, abody, bbody);
					else ebody = bbody;

    				aux = AlloyHelper.ordDecls(aux);
    				Expr res = x.op.make(null, null,aux, ebody);
    				return res;
        		} else return x;
        	case SOME : 
				Entry<Map<Decl,Expr>, Expr> rngs;
				try {
					TradeForm finder = new TradeForm(x.decls);
					rngs = finder.visitThis(ebody);
					for (Decl d : rngs.getKey().keySet()) {
						//EchoReporter.getInstance().debug("trading on var "+d.get()+" with range "+rngs.getKey().get(d));
						Decl d2 = new Decl(null,null,null,d.names,rngs.getKey().get(d));
						aux.add(d2);
					}
					ebody = rngs.getValue();
				} catch (Err e) { e.printStackTrace(); }

				aux = AlloyHelper.ordDecls(aux);
				Expr res;
				if (ebody.isSame(Sig.NONE.no())&&true) {
					Decl last = aux.remove(aux.size()-1);
					Expr body;
					try {
						body = cutExists(last.expr);
						if(body != null) {
							//System.out.println("went "+body);
							if (!aux.isEmpty()) res = x.op.make(null, null, aux, body);
							else res = body;
						}
						else {
							aux.add(last);
							res = x.op.make(null, null, aux, ebody);
						}
					} catch (ErrorUnsupported e) {
						aux.add(last);
						res = x.op.make(null, null, aux, ebody);
					}
				} else res = x.op.make(null, null, aux, ebody);
				return res;
        	default: return x;
        	}

        }

        @Override public final Expr visit(ExprBinary x) throws Err { 
        	Expr left = visitThis(x.left);
        	Expr right = visitThis(x.right);
        	return x.op.make(null, null, left, right); 
        }

        @Override public final Expr visit(ExprCall x) { return x; }

        @Override public final Expr visit(ExprList x) { return x; }

        @Override public final Expr visit(ExprConstant x) { return x; }

        @Override public final Expr visit(ExprITE x) { return x; }

        @Override public final Expr visit(ExprLet x) { return x; }

        @Override public final Expr visit(ExprUnary x) { return x; }

        @Override public final Expr visit(ExprVar x) { return x; }

        @Override public final Expr visit(Sig x) { return x; }

        @Override public final Expr visit(Sig.Field x) { return x; }

    }


	private final class OnePointQnt extends VisitQuery<Expr> {
		   @Override public final Expr visit(ExprQt x) throws Err { 
				Expr sub = x.sub;
				List<Decl> decls = new ArrayList<Decl>(x.decls);
        		switch (x.op){
	        	case ALL :
					for (int i = 0; i < decls.size() ; i++) {
						Decl d = decls.get(i);
	        			try {
	        				//EchoReporter.getInstance().debug("Onepointing "+d.get().label + " over "+d.expr+ " which is "+AlloyEchoTranslator.getInstance().isFunctional(d.expr));
							if (AlloyEchoTranslator.getInstance().isFunctional(d.expr)) {
								sub = AlloyHelper.replace(sub, d.get(), d.expr);
								decls.remove(d);
								for (int j = 0; j < decls.size() ; j++)
									decls.set(j, new Decl(decls.get(j).isPrivate, decls.get(j).disjoint, decls.get(j).disjoint2, decls.get(j).names, AlloyHelper.replace(decls.get(j).expr,d.get(),d.expr)));
							}
						} catch (EchoError e) { throw new ErrorFatal(e.getMessage());}
	        		}
	        		if (true) {
		        		List<Decl> delcsaux = new ArrayList<Decl>(decls);
	        			for (Decl d : delcsaux)
		        			try {
		        				Expr aux = cutForall(sub,d);
		        				if (aux != null) {
		        					sub = aux;
		        					decls.remove(d);
		        				}
			        		} catch (ErrorUnsupported e) {}
	        		}
	        		if (decls.size() > 0) return x.op.make(null, null, decls, sub);
	        		else return sub;    		
	        	case SOME : 
	        		for (Decl d : x.decls) {
	        			try {
	        				//System.out.println("Onepointing "+d.expr+ " which is "+translator.isFunctional(d.expr));
							if (AlloyEchoTranslator.getInstance().isFunctional(d.expr)) {
								sub = AlloyHelper.replace(sub, d.get(), d.expr);
								decls.remove(d);
							}
						} catch (EchoError e) { throw new ErrorFatal(e.getMessage());}
	        		}
	        		if (decls.size() > 0) return x.op.make(null, null, decls, sub);
	        		else return sub;
	        		
	        	default: return x;
	        	}

	        }

        @Override public final Expr visit(ExprBinary x) throws Err {
	        	Expr left = visitThis(x.left);
	        	Expr right = visitThis(x.right);
	        	return x.op.make(null, null, left, right); 
	        }

        @Override public final Expr visit(ExprCall x) { return x; }

        @Override public final Expr visit(ExprList x) { return x; }

        @Override public final Expr visit(ExprConstant x) { return x; }

        @Override public final Expr visit(ExprITE x) { return x; }

        @Override public final Expr visit(ExprLet x) { return x; }

        @Override public final Expr visit(ExprUnary x) { return x; }

        @Override public final Expr visit(ExprVar x) { return x; }

        @Override public final Expr visit(Sig x) { return x; }

        @Override public final Expr visit(Sig.Field x) { return x; }
    }
	
	private Expr cutExists(Expr e) throws ErrorUnsupported, Err {
		CutExists cutter = new CutExists();
		Entry<Expr,List<ExprVar>> pair = cutter.visitThis(e);
		if(pair==null) return null;
		else if(pair.getValue().size()==1)
			return pair.getValue().get(0).in((pair.getKey().transpose().join(pair.getKey())).join(pair.getValue().get(0)));
		else if(pair.getValue().size()==2)
			return pair.getValue().get(0).in(pair.getKey().join(pair.getValue().get(1)));
		else return null;
	}
	
	private Expr cutForall(Expr e, Decl d) throws ErrorUnsupported {
		if (e instanceof ExprBinary && ((ExprBinary)e).op.equals(ExprBinary.Op.IN)) {
			try {
				if (((ExprBinary)e).left.hasVar((ExprVar)d.get()) && ((ExprBinary)e).right.hasVar((ExprVar)d.get())) {
					PushVar pusher = new PushVar((ExprVar) d.get());
					Expr body1 = pusher.visitThis(((ExprBinary)e).left);
					//System.out.println("HI "+e +"; "+d.get()+", "+body1);
					Expr body2 = pusher.visitThis(((ExprBinary)e).right);
					if (body1 instanceof ExprBinary && ((ExprBinary) body1).right.isSame(d.get())) {
						if (body2 instanceof ExprBinary && ((ExprBinary) body2).right.isSame(d.get())) {
							Expr res = ((((ExprBinary) body1).left).range(d.expr)).in(((ExprBinary) body2).left);
							//System.out.println(e + " over "+ d.get() + " went "+res);
							return res;
						}
					}
				}
			} catch (Err er) {}
		}
		return null;
	}	
	
	private final class CutExists extends VisitQuery<Entry<Expr,List<ExprVar>>> {
		   	@Override public final Entry<Expr,List<ExprVar>> visit(ExprQt x) throws Err { return null;  }

        @Override public final Entry<Expr,List<ExprVar>> visit(ExprBinary x) throws Err {
	       		switch (x.op) {
	       		case JOIN : 
	    			List<ExprVar> vars;
					try {
						vars = AlloyHelper.getVars(x);
					} catch (ErrorUnsupported e) { throw new ErrorFatal(e.getMessage()); }
	    			for (ExprVar var : vars) {
	    				try {
	    					if (!(var.type().toExpr() instanceof PrimSig && ((PrimSig)var.type().toExpr()).parent.isSame(AlloyEchoTranslator.STATE))) {
	    						//System.out.println ("var "+var.type().toExpr());
	    						PushVar pusher = new PushVar(var);
	    						Expr body = pusher.visitThis(x);
	    						//System.out.println("Out "+var.in(((ExprBinary) body).left.transpose().join(((ExprBinary) body).left).join(var)));
	    						Expr res = ((ExprBinary) body).left; 
	    						return new SimpleEntry<Expr,List<ExprVar>>(res,new ArrayList<ExprVar>(Arrays.asList(var)));
	    					} else 
	    						return new SimpleEntry<Expr,List<ExprVar>>(x,new ArrayList<ExprVar>());
	    						
	    				} 
	    				catch (Err er) {}
	    				
	    			} 
	    			return null;
	       		case INTERSECT :
	       			Entry<Expr,List<ExprVar>> e1 = visitThis(x.left);
	       			Entry<Expr,List<ExprVar>> e2 = visitThis(x.right);
	       			if (e1 == null || e2 == null) return null;
	       			if (e2.getValue() == null && e1.getValue() == null) 
	       				return new SimpleEntry<Expr,List<ExprVar>>(e1.getKey().intersect(e2.getKey()),new ArrayList<ExprVar>());
	       			if (e1.getValue().size() == 1 && e2.getValue().size() == 1) {
	       				Expr f = e1.getKey().transpose().join(e2.getKey());
	       				List<ExprVar> res = new ArrayList<ExprVar>(e1.getValue());
	       				res.addAll(e2.getValue());
		       			return new SimpleEntry<Expr,List<ExprVar>>(f,res);
	       			}
	       			else if ((e1.getValue().size() + e2.getValue().size() == 1)) {
		       			Entry<Expr,List<ExprVar>> e = (e1.getValue() == null)?e2:e1;
		       			Expr r = (e1.getValue() == null)?e1.getKey():e2.getKey();
		       			Expr f = r.domain(e.getKey());
		       			//System.out.println("INE "+x);
		       			//System.out.println("OUT "+f);
		       			return new SimpleEntry<Expr,List<ExprVar>>(f,e.getValue());
	       			} else return null;

	    		default : return null;
	    		}
	       	}

        @Override public final Entry<Expr,List<ExprVar>> visit(ExprCall x) { return null; }

        @Override public final Entry<Expr,List<ExprVar>> visit(ExprList x) { return null; }

        @Override public final Entry<Expr,List<ExprVar>> visit(ExprConstant x) { return null; }

        @Override public final Entry<Expr,List<ExprVar>> visit(ExprITE x) { return null; }

        @Override public final Entry<Expr,List<ExprVar>> visit(ExprLet x) { return null; }

        @Override public final Entry<Expr,List<ExprVar>> visit(ExprUnary x) { return null; }

        @Override public final Entry<Expr,List<ExprVar>> visit(ExprVar x) { return null; }

        @Override public final Entry<Expr,List<ExprVar>> visit(Sig x) { return null; }

        @Override public final Entry<Expr,List<ExprVar>> visit(Sig.Field x) { return null; }
    }
	
}
