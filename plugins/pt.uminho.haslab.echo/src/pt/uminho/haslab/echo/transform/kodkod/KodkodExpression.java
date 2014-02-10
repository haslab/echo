package pt.uminho.haslab.echo.transform.kodkod;

import kodkod.ast.Expression;
import kodkod.ast.Variable;
import pt.uminho.haslab.echo.transform.ast.*;

/**
 * Created by tmg on 2/4/14.
 */
class KodkodExpression implements IExpression{

    public final Expression EXPR;

    KodkodExpression(Expression expression){
        EXPR = expression;
    }

    @Override
    public IExpression closure() {
        return new KodkodExpression(EXPR.closure());
    }

    @Override
    public IExpression reflexiveClosure() {
        return new KodkodExpression(EXPR.reflexiveClosure());
    }

    @Override
    public IExpression transpose() {
        return new KodkodExpression(EXPR.closure());
    }

    @Override
    public IExpression join(IExpression e) {
        return new KodkodExpression(EXPR.join(((KodkodExpression)e).EXPR));
    }

    @Override
    public IExpression difference(IExpression e) {
        return new KodkodExpression(EXPR.difference(((KodkodExpression)e).EXPR));
    }

    @Override
    public IExpression intersection(IExpression e) {
        return new KodkodExpression(EXPR.intersection(((KodkodExpression)e).EXPR));
    }

    @Override
    public IExpression union(IExpression e) {
        return new KodkodExpression(EXPR.union(((KodkodExpression)e).EXPR));
    }


    @Override
    public IFormula in(IExpression e) {
        return new KodkodFormula(EXPR.in(((KodkodExpression)e).EXPR));
    }

    @Override
    public IFormula eq(IExpression e) {
        return new KodkodFormula(EXPR.eq(((KodkodExpression) e).EXPR));
    }

    @Override
    public IFormula some() {
        return new KodkodFormula(EXPR.some());
    }

    @Override
    public IFormula one() {
        return new KodkodFormula(EXPR.one());
    }

    @Override
    public IFormula no() {
        return new KodkodFormula(EXPR.no());
    }

    @Override
    public IFormula lone() {
        return new KodkodFormula(EXPR.lone());
    }


    @Override
    public IDecl oneOf(String name) {
        Variable v = Variable.unary(name);

        return new KodkodDecl(v.oneOf(EXPR));
    }

    @Override
    public IIntExpression cardinality() {
        return Constants.makeNumber(EXPR.arity());
    }
}
