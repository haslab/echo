package pt.uminho.haslab.echo.engine.alloy;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprITE;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IExpression;
import pt.uminho.haslab.echo.engine.ast.IFormula;

/**
 * Created by nmm
 */
class AlloyFormula implements IFormula {

    public final Expr formula;
    public AlloyFormula(Expr f){
        this.formula = f;
    }

    @Override
    public IFormula and(IFormula f) {
        return new AlloyFormula(formula.and(((AlloyFormula) f).formula));
    }

    @Override
    public IFormula or(IFormula f) {
        return new AlloyFormula(formula.or(((AlloyFormula) f).formula));
    }

    @Override
    public IFormula iff(IFormula f) {
        return new AlloyFormula(formula.iff(((AlloyFormula) f).formula));
    }

    @Override
    public IFormula implies(IFormula f) {
        return new AlloyFormula(formula.implies(((AlloyFormula) f).formula));
    }

    @Override
    public IFormula not() {
        return new AlloyFormula(formula.not());
    }

    @Override
	public IExpression thenElse(IExpression thenExpr, IExpression elseExpr) {
		return new AlloyExpression(ExprITE.make(null, formula,
				((AlloyExpression) thenExpr).EXPR,
				((AlloyExpression) elseExpr).EXPR));
	}

    //TODO check
    @Override
    public IExpression comprehension(IDecl firstDecl, IDecl... extraDecls) throws ErrorInternalEngine {
    	Decl[] ds = new Decl[extraDecls.length];
    	for(int i = 0; i < extraDecls.length; i++)
            ds[i] = (((AlloyDecl) extraDecls[i]).decl);

    	Expr res;
		try {
			res = formula.comprehensionOver(((AlloyDecl) firstDecl).decl,ds);
		} catch (Err e) {
			throw ErrorInternalEngine.thrownew("", "", e, Task.TRANSLATE_OCL);
		}
        return new AlloyExpression(res);
    }

    @Override
    public IFormula forAll(IDecl decl, IDecl... extraDecls) throws ErrorInternalEngine {
    	Decl[] ds = new Decl[extraDecls.length];
    	for(int i = 0; i < extraDecls.length; i++)
            ds[i] = (((AlloyDecl) extraDecls[i]).decl);

        try {
			return new AlloyFormula(formula.forAll(((AlloyDecl) decl).decl,ds));
		} catch (Err e) {
			throw ErrorInternalEngine.thrownew("", "", e, Task.TRANSLATE_OCL);
		}
    }

    @Override
    public IFormula forSome(IDecl decl, IDecl... extraDecls) throws ErrorInternalEngine {
    	Decl[] ds = new Decl[extraDecls.length];
    	for(int i = 0; i < extraDecls.length; i++)
            ds[i] = (((AlloyDecl) extraDecls[i]).decl);

        try {
			return new AlloyFormula(formula.forSome(((AlloyDecl) decl).decl,ds));
		} catch (Err e) {
			throw ErrorInternalEngine.thrownew("", "", e, Task.TRANSLATE_OCL);
		}
    }


    @Override
    public IFormula forOne(IDecl d) throws ErrorInternalEngine {
        try {
			return new AlloyFormula(formula.forOne(((AlloyDecl) d).decl));
		} catch (Err e) {
			throw ErrorInternalEngine.thrownew("", "", e, Task.TRANSLATE_OCL);
		}
    }
    
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof AlloyFormula)) return false;
		AlloyFormula a = (AlloyFormula) o;
		return this.formula.isSame(a.formula);				
	}
	
	@Override
	public String toString() {
		return formula.toString();
	}
	
}

