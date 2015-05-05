package pt.uminho.haslab.echo.engine.alloy;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprVar;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.EErrorCore;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IExpression;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.echo.engine.ast.IIntExpression;

/**
 * Alloy representation of expressions.
 * 
 * @author nmm,tmg
 * @version 0.4 20/02/2014
 */
class AlloyExpression implements IExpression{

    final Expr EXPR;

    AlloyExpression(Expr expression){
        EXPR = expression;
    }

	/** {@inheritDoc} */
    @Override
    public IExpression closure() {
        return new AlloyExpression(EXPR.closure());
    }

	/** {@inheritDoc} */
    @Override
    public IExpression reflexiveClosure() {
        return new AlloyExpression(EXPR.reflexiveClosure());
    }

	/** {@inheritDoc} */
    @Override
    public IExpression transpose() {
        return new AlloyExpression(EXPR.transpose());
    }

	/** {@inheritDoc} */
    @Override
    public IExpression join(IExpression e) {
        return new AlloyExpression(EXPR.join(((AlloyExpression)e).EXPR));
    }

	/** {@inheritDoc} */
    @Override
    public IExpression difference(IExpression e) {
        return new AlloyExpression(EXPR.minus(((AlloyExpression)e).EXPR));
    }

	/** {@inheritDoc} */
    @Override
    public IExpression intersection(IExpression e) {
        return new AlloyExpression(EXPR.intersect(((AlloyExpression)e).EXPR));
    }

	/** {@inheritDoc} */
    @Override
    public IExpression union(IExpression e) {
        return new AlloyExpression(EXPR.plus(((AlloyExpression)e).EXPR));
    }

	/** {@inheritDoc} */
    @Override
    public IFormula in(IExpression e) {
        return new AlloyFormula(EXPR.in(((AlloyExpression)e).EXPR));
    }

	/** {@inheritDoc} */
    @Override
    public IFormula eq(IExpression e) {
        return new AlloyFormula(EXPR.equal(((AlloyExpression) e).EXPR));
    }

	/** {@inheritDoc} */
    @Override
    public IFormula some() {
        return new AlloyFormula(EXPR.some());
    }

	/** {@inheritDoc} */
    @Override
    public IFormula one() {
        return new AlloyFormula(EXPR.one());
    }

	/** {@inheritDoc} */
    @Override
    public IFormula no() {
        return new AlloyFormula(EXPR.no());
    }

    @Override
    public IFormula lone() {
        return new AlloyFormula(EXPR.lone());
    }

	/** {@inheritDoc} */
	@Override
	public IDecl oneOf(String name) throws EErrorCore {
		try {
			return new AlloyDecl(EXPR.oneOf(name));
		} catch (Err e) {
			throw EErrorCore.thrownew("", "", e, Task.TRANSLATE_OCL);
		}
	}

	/** {@inheritDoc} */
    @Override
    public IIntExpression cardinality() {
        return new AlloyIntExpression(EXPR.cardinality());
    }

	/** {@inheritDoc} */
	@Override
	public boolean hasVar(IExpression var) {
		Expr evar = ((AlloyExpression) var).EXPR;
		if (!(evar instanceof ExprVar)) return false;
		return EXPR.hasVar((ExprVar) evar);
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof AlloyExpression)) return false;
		AlloyExpression a = (AlloyExpression) o;
		return this.EXPR.isSame(a.EXPR);				
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return EXPR.toString();
	}
}
