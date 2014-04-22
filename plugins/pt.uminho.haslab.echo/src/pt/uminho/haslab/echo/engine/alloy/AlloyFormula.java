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
import pt.uminho.haslab.echo.engine.ast.INode;

/**
 * Alloy representation of formulas.
 * 
 * @author nmm,tmg
 * @version 0.4 20/02/2014
 */
class AlloyFormula implements IFormula {

    final Expr FORMULA;
    
    AlloyFormula(Expr f){
        this.FORMULA = f;
    }

	/** {@inheritDoc} */
    @Override
    public AlloyFormula and(IFormula f) {
        return new AlloyFormula(FORMULA.and(((AlloyFormula) f).FORMULA));
    }

	/** {@inheritDoc} */
    @Override
    public AlloyFormula or(IFormula f) {
        return new AlloyFormula(FORMULA.or(((AlloyFormula) f).FORMULA));
    }

	/** {@inheritDoc} */
    @Override
    public AlloyFormula iff(IFormula f) {
        return new AlloyFormula(FORMULA.iff(((AlloyFormula) f).FORMULA));
    }

	/** {@inheritDoc} */
    @Override
    public AlloyFormula implies(IFormula f) {
        return new AlloyFormula(FORMULA.implies(((AlloyFormula) f).FORMULA));
    }

	/** {@inheritDoc} */
    @Override
    public AlloyFormula not() {
        return new AlloyFormula(FORMULA.not());
    }

	/** {@inheritDoc} */
    @Override
	public AlloyExpression thenElse(IExpression thenExpr, IExpression elseExpr) {
		return new AlloyExpression(ExprITE.make(null, FORMULA,
				((AlloyExpression) thenExpr).EXPR,
				((AlloyExpression) elseExpr).EXPR));
	}

    /** {@inheritDoc} */
    @Override
	public AlloyFormula thenElse(IFormula thenExpr, IFormula elseExpr) {
		return new AlloyFormula(ExprITE.make(null, FORMULA,
				((AlloyFormula) thenExpr).FORMULA,
				((AlloyFormula) elseExpr).FORMULA));
	}

    //TODO check
	/** {@inheritDoc} */
    @Override
    public AlloyExpression comprehension(IDecl firstDecl, IDecl... extraDecls) throws ErrorInternalEngine {
    	Decl[] ds = new Decl[extraDecls.length];
    	for(int i = 0; i < extraDecls.length; i++)
            ds[i] = (((AlloyDecl) extraDecls[i]).DECL);

    	Expr res;
		try {
			res = FORMULA.comprehensionOver(((AlloyDecl) firstDecl).DECL,ds);
		} catch (Err e) {
			throw ErrorInternalEngine.thrownew("", "", e, Task.TRANSLATE_OCL);
		}
        return new AlloyExpression(res);
    }

	/** {@inheritDoc} */
    @Override
    public AlloyFormula forAll(IDecl decl, IDecl... extraDecls) throws ErrorInternalEngine {
    	Decl[] ds = new Decl[extraDecls.length];
    	for(int i = 0; i < extraDecls.length; i++)
            ds[i] = (((AlloyDecl) extraDecls[i]).DECL);

        try {
			return new AlloyFormula(FORMULA.forAll(((AlloyDecl) decl).DECL,ds));
		} catch (Err e) {
			throw ErrorInternalEngine.thrownew("", "", e, Task.TRANSLATE_OCL);
		}
    }

	/** {@inheritDoc} */
    @Override
    public AlloyFormula forSome(IDecl decl, IDecl... extraDecls) throws ErrorInternalEngine {
    	Decl[] ds = new Decl[extraDecls.length];
    	for(int i = 0; i < extraDecls.length; i++)
            ds[i] = (((AlloyDecl) extraDecls[i]).DECL);

        try {
			return new AlloyFormula(FORMULA.forSome(((AlloyDecl) decl).DECL,ds));
		} catch (Err e) {
			throw ErrorInternalEngine.thrownew("", "", e, Task.TRANSLATE_OCL);
		}
    }

	/** {@inheritDoc} */
    @Override
    public AlloyFormula forOne(IDecl d) throws ErrorInternalEngine {
        try {
			return new AlloyFormula(FORMULA.forOne(((AlloyDecl) d).DECL));
		} catch (Err e) {
			throw ErrorInternalEngine.thrownew("", "", e, Task.TRANSLATE_OCL);
		}
    }
    
	/** {@inheritDoc} */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof AlloyFormula)) return false;
		AlloyFormula a = (AlloyFormula) o;
		return this.FORMULA.isSame(a.FORMULA);				
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return FORMULA.toString();
	}
	
}

