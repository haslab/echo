package pt.uminho.haslab.echo.alloy;

import java.util.ArrayList;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
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

	private EMF2Alloy translator;

	public AlloyOptimizations (EMF2Alloy translator) {
		this.translator = translator;
	}
	
	public Expr trading(Expr expr) throws ErrorUnsupported {
		
		TradeQnt trader = new TradeQnt();
		Expr res = null;
		try {
			res = trader.visitThis(expr);
		} catch (Err e) { throw new ErrorUnsupported("", ""); }
	
		return res;
	}
	
	public Expr onePoint(Expr expr) throws ErrorUnsupported {
		
		OnePointQnt onpointer = new OnePointQnt();
		Expr res = null;
		try {
			res = onpointer.visitThis(expr);
		} catch (Err e) { throw new ErrorUnsupported("", ""); }

		return res;
	}
	
	/** Finds inclusion expression over the given variable
	 */
	private final class TradeForm extends VisitQuery<Entry<List<Expr>,Expr>> {
		List<Expr> rngs = new ArrayList<Expr>();
		ExprVar var;
		
		TradeForm(ExprVar var) {
			this.var = var;
		}
		

		@Override public Entry<List<Expr>,Expr> visit(ExprList x) throws Err { 
			switch(x.op) {
				case AND: 	
             		List<Expr> exps = new ArrayList<Expr>();
	    			for (Expr arg : x.args) {
	    				Entry<List<Expr>,Expr> aux = visitThis(arg);
	    				if (!AlloyUtil.isTrue(aux.getValue())) exps.add(aux.getValue());
	    			}
	    			Expr exp = Sig.NONE.no();
	    			if (exps.size() > 1) exp = ExprList.make(null, null, ExprList.Op.AND, exps);
	    			else if (exps.size() == 1) exp = exps.get(0);
	    			return new SimpleEntry<List<Expr>,Expr>(rngs,exp);
				default: return new SimpleEntry<List<Expr>,Expr>(rngs,x);
        	 }
		}
        @Override public final Entry<List<Expr>,Expr> visit(ExprBinary x) throws Err { 
			Expr range = Sig.NONE;
			switch(x.op){
        		case EQUALS:
        			if (x.hasVar(var) && (x.right instanceof ExprVar)) {
        				if (x.right.isSame(var)) range = x.left;
            			else if (x.left.hasVar(var)){
            				PushVar pusher = new PushVar(var);
            				range = pusher.visitThis(x.left);
            				if (range instanceof ExprBinary && ((ExprBinary) range).left.isSame(var))
            					range = ((ExprBinary) range).right.join(x.right);
            				else if (range instanceof ExprBinary && ((ExprBinary) range).right.isSame(var))
            					range = x.right.join(((ExprBinary) range).left);
            			}
        				rngs.add(range);
        				return new SimpleEntry<List<Expr>,Expr>(rngs,Sig.NONE.no());
        			}
        		case IN: 
        			if (x.hasVar(var) && (x.left instanceof ExprVar)) {
        				if (x.left.isSame(var)) range = x.right;
            			else if (x.right.hasVar(var)){
            				PushVar pusher = new PushVar(var);
            				range = pusher.visitThis(x.right);
            				if (range instanceof ExprBinary && ((ExprBinary) range).left.isSame(var))
            					range = ((ExprBinary) range).right.join(x.left);
            				else if (range instanceof ExprBinary && ((ExprBinary) range).right.isSame(var))
            					range = x.left.join(((ExprBinary) range).left);
            			}
        				rngs.add(range);
        				return new SimpleEntry<List<Expr>,Expr>(rngs,Sig.NONE.no());
        			}
        		default: return new SimpleEntry<List<Expr>,Expr>(rngs,x);
        	}
        };
        @Override public final Entry<List<Expr>,Expr> visit(ExprCall x) { return new SimpleEntry<List<Expr>,Expr>(rngs,x); };
        @Override public final Entry<List<Expr>,Expr> visit(ExprConstant x) { return new SimpleEntry<List<Expr>,Expr>(rngs,x); };
        @Override public final Entry<List<Expr>,Expr> visit(ExprITE x) { return new SimpleEntry<List<Expr>,Expr>(rngs,x); };
        @Override public final Entry<List<Expr>,Expr> visit(ExprLet x) { return new SimpleEntry<List<Expr>,Expr>(rngs,x); };
        @Override public final Entry<List<Expr>,Expr> visit(ExprQt x) { return new SimpleEntry<List<Expr>,Expr>(rngs,x); };
        @Override public final Entry<List<Expr>,Expr> visit(ExprUnary x) { return new SimpleEntry<List<Expr>,Expr>(rngs,x); };
        @Override public final Entry<List<Expr>,Expr> visit(ExprVar x) { return new SimpleEntry<List<Expr>,Expr>(rngs,x); };
        @Override public final Entry<List<Expr>,Expr> visit(Sig x) { return new SimpleEntry<List<Expr>,Expr>(rngs,x); };
        @Override public final Entry<List<Expr>,Expr> visit(Sig.Field x) { return new SimpleEntry<List<Expr>,Expr>(rngs,x); };

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

    				for (Decl d : x.decls) {
    					if (d.names.size()==1){
    						Entry<List<Expr>, Expr> rngs;
    						try {
    							TradeForm finder = new TradeForm((ExprVar) d.get());
    							rngs = finder.visitThis(abody);
    							if (rngs.getKey().size() > 0) {
    								//System.out.println("trading on var "+d.get()+" with range "+rngs.getKey().get(0)+" and "+rngs.getKey().size());
    								Expr meet = Sig.NONE;
    								for (Expr em : rngs.getKey())
    									if (meet.isSame(Sig.NONE)) meet = em;
    									else meet = meet.intersect(em);
    								//System.out.println("From "+d.expr + " to "+meet + ", "+translator.isFunctional(meet));
    								
    								d = new Decl(null,null,null,d.names,meet);
    								abody = rngs.getValue();
    							}
    						} catch (Err e) { e.printStackTrace(); }
    						aux.add(d);
    						if (!AlloyUtil.isTrue(abody)) ebody = ExprBinary.Op.IMPLIES.make(null, null, abody, bbody);
    						else ebody = bbody;
    					}
    				}
    				aux = AlloyUtil.ordDecls(aux);
    				Expr res = x.op.make(null, null,aux, ebody);
    				return res;
        		}
        	case SOME : 
        		for (Decl d : x.decls) {
					if (d.names.size()==1){
						Entry<List<Expr>, Expr> rngs;
						try {
							TradeForm finder = new TradeForm((ExprVar) d.get());
							rngs = finder.visitThis(ebody);
							if (rngs.getKey().size() > 0) {
								//System.out.println("trading on var "+d.get()+" with range "+rngs.getKey().get(0)+" and "+rngs.getKey().size());
								
								Expr meet = Sig.NONE;
								for (Expr em : rngs.getKey())
									if (meet.isSame(Sig.NONE)) meet = em;
									else meet = meet.intersect(em);
								d = new Decl(null,null,null,d.names,meet);
								ebody = rngs.getValue();
							} 
						} catch (Err e) { e.printStackTrace(); }
						aux.add(d);
					}
				}
				aux = AlloyUtil.ordDecls(aux);
				Expr res = x.op.make(null, null,aux, ebody);
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
							if (translator.isFunctional(d.expr))
								sub = AlloyUtil.replace(sub, d.get(), d.expr);
						} catch (ErrorUnsupported e) { throw new ErrorFatal("");}
	        		}
	        		return x.op.make(null, null, decls, sub);

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
