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
    public IFormula and(IFormula f) {
        return new AlloyFormula(FORMULA.and(((AlloyFormula) f).FORMULA));
    }

	/** {@inheritDoc} */
    @Override
    public IFormula or(IFormula f) {
        return new AlloyFormula(FORMULA.or(((AlloyFormula) f).FORMULA));
    }

	/** {@inheritDoc} */
    @Override
    public IFormula iff(IFormula f) {
        return new AlloyFormula(FORMULA.iff(((AlloyFormula) f).FORMULA));
    }

	/** {@inheritDoc} */
    @Override
    public IFormula implies(IFormula f) {
        return new AlloyFormula(FORMULA.implies(((AlloyFormula) f).FORMULA));
    }

	/** {@inheritDoc} */
    @Override
    public IFormula not() {
        return new AlloyFormula(FORMULA.not());
    }

	/** {@inheritDoc} */
    @Override
	public IExpression thenElse(IExpression thenExpr, IExpression elseExpr) {
		return new AlloyExpression(ExprITE.make(null, FORMULA,
				((AlloyExpression) thenExpr).EXPR,
				((AlloyExpression) elseExpr).EXPR));
	}

    //TODO check
	/** {@inheritDoc} */
    @Override
    public IExpression comprehension(IDecl firstDecl, IDecl... extraDecls) throws ErrorInternalEngine {
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
    public IFormula forAll(IDecl decl, IDecl... extraDecls) throws ErrorInternalEngine {
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
    public IFormula forSome(IDecl decl, IDecl... extraDecls) throws ErrorInternalEngine {
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
    public IFormula forOne(IDecl d) throws ErrorInternalEngine {
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

