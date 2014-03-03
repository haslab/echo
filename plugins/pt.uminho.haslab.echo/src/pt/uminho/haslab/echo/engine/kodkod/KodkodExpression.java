package pt.uminho.haslab.echo.engine.kodkod;

import kodkod.ast.Expression;
import kodkod.ast.Variable;
import pt.uminho.haslab.echo.engine.ast.Constants;
import pt.uminho.haslab.echo.engine.ast.IExpression;

/**
 * Created by tmg on 2/4/14.
 */
class KodkodExpression implements IExpression {

    public final Expression EXPR;

    KodkodExpression(Expression expression){
        EXPR = expression;
    }

    @Override
    public KodkodExpression closure() {
        return new KodkodExpression(EXPR.closure());
    }

    @Override
    public KodkodExpression reflexiveClosure() {
        return new KodkodExpression(EXPR.reflexiveClosure());
    }

    @Override
    public KodkodExpression transpose() {
        return new KodkodExpression(EXPR.transpose());
    }

    @Override
    public KodkodExpression join(IExpression e) {
        return new KodkodExpression(EXPR.join(((KodkodExpression)e).EXPR));
    }

    @Override
    public KodkodExpression difference(IExpression e) {
        return new KodkodExpression(EXPR.difference(((KodkodExpression)e).EXPR));
    }

    @Override
    public KodkodExpression intersection(IExpression e) {
        return new KodkodExpression(EXPR.intersection(((KodkodExpression)e).EXPR));
    }

    @Override
    public KodkodExpression union(IExpression e) {
        return new KodkodExpression(EXPR.union(((KodkodExpression)e).EXPR));
    }


    @Override
    public KodkodFormula in(IExpression e) {
        return new KodkodFormula(EXPR.in(((KodkodExpression)e).EXPR));
    }

    @Override
    public KodkodFormula eq(IExpression e) {
        return new KodkodFormula(
        		EXPR.eq(
        		((KodkodExpression) e).EXPR));
    }

    @Override
    public KodkodFormula some() {
        return new KodkodFormula(EXPR.some());
    }

    @Override
    public KodkodFormula one() {
        return new KodkodFormula(EXPR.one());
    }

    @Override
    public KodkodFormula no() {
        return new KodkodFormula(EXPR.no());
    }

    @Override
    public KodkodFormula lone() {
        return new KodkodFormula(EXPR.lone());
    }


    @Override
    public KodkodDecl oneOf(String name) {
        Variable v = Variable.unary(name);

        return new KodkodDecl(v.oneOf(EXPR));
    }

    @Override
    public KodkodIntExpression cardinality() {
        return (KodkodIntExpression) Constants.makeNumber(EXPR.arity());
    }

	@Override
	public boolean hasVar(IExpression var) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public String toString(){
		return EXPR.toString();
	}
}
