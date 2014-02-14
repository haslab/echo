package pt.uminho.haslab.echo.engine.alloy;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.engine.ast.*;

/**
 * Created by tmg on 2/4/14.
 */
class AlloyExpression implements IExpression{

    public final Expr EXPR;

    AlloyExpression(Expr expression){
        EXPR = expression;
    }

    @Override
    public IExpression closure() {
        return new AlloyExpression(EXPR.closure());
    }

    @Override
    public IExpression reflexiveClosure() {
        return new AlloyExpression(EXPR.reflexiveClosure());
    }

    @Override
    public IExpression transpose() {
        return new AlloyExpression(EXPR.closure());
    }

    @Override
    public IExpression join(IExpression e) {
        return new AlloyExpression(EXPR.join(((AlloyExpression)e).EXPR));
    }

    @Override
    public IExpression difference(IExpression e) {
        return new AlloyExpression(EXPR.minus(((AlloyExpression)e).EXPR));
    }

    @Override
    public IExpression intersection(IExpression e) {
        return new AlloyExpression(EXPR.intersect(((AlloyExpression)e).EXPR));
    }

    @Override
    public IExpression union(IExpression e) {
        return new AlloyExpression(EXPR.plus(((AlloyExpression)e).EXPR));
    }

    @Override
    public IFormula in(IExpression e) {
        return new AlloyFormula(EXPR.in(((AlloyExpression)e).EXPR));
    }

    @Override
    public IFormula eq(IExpression e) {
        return new AlloyFormula(EXPR.equal(((AlloyExpression) e).EXPR));
    }

    @Override
    public IFormula some() {
        return new AlloyFormula(EXPR.some());
    }

    @Override
    public IFormula one() {
        return new AlloyFormula(EXPR.one());
    }

    @Override
    public IFormula no() {
        return new AlloyFormula(EXPR.no());
    }

    @Override
    public IFormula lone() {
        return new AlloyFormula(EXPR.lone());
    }

	@Override
	public IDecl oneOf(String name) throws ErrorInternalEngine {
		try {
			return new AlloyDecl(EXPR.oneOf(name));
		} catch (Err e) {
			throw ErrorInternalEngine.thrownew("", "", e, Task.TRANSLATE_OCL);
		}
	}

    @Override
    public IIntExpression cardinality() {
        return new AlloyIntExpression(EXPR.cardinality());
    }
}
