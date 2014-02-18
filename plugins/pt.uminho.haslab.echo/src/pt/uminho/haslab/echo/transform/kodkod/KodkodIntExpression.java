package pt.uminho.haslab.echo.transform.kodkod;


import kodkod.ast.IntExpression;
import pt.uminho.haslab.echo.EchoTypeError;
import pt.uminho.haslab.echo.transform.ast.IEq;
import pt.uminho.haslab.echo.transform.ast.IFormula;
import pt.uminho.haslab.echo.transform.ast.IIntExpression;

/**
 * Created by tmg on 2/10/14.
 */
class KodkodIntExpression implements IIntExpression, IEq{

    public final IntExpression expr;

    public KodkodIntExpression(IntExpression expr) {
        this.expr = expr;
    }


    @Override
    public IFormula lt(IIntExpression intExpression) {
        return new KodkodFormula(
                expr.lt(
                        ((KodkodIntExpression) intExpression).expr
                )
        );
    }

    @Override
    public IFormula gt(IIntExpression expression) {
        return new KodkodFormula(
                expr.gt(
                        ((KodkodIntExpression) expression).expr
                )
        );
    }

    @Override
    public IFormula lte(IIntExpression expression) {
        return new KodkodFormula(
                expr.lte(
                        ((KodkodIntExpression) expression).expr
                )
        );
    }

    @Override
    public IFormula gte(IIntExpression expression) {
        return new KodkodFormula(
                expr.gte(
                        ((KodkodIntExpression) expression).expr
                )
        );
    }

    @Override
    public IIntExpression plus(IIntExpression expression) {
        return new KodkodIntExpression(
                expr.plus(
                        ((KodkodIntExpression) expression).expr
                )
        );
    }

    @Override
    public IIntExpression minus(IIntExpression iIntExpression) {

        return new KodkodIntExpression(
                expr.minus(
                        ((KodkodIntExpression) iIntExpression).expr
                )
        );
    }

    @Override
    public IFormula eq(IEq exp) throws EchoTypeError {
        if(exp instanceof IIntExpression)
            return eq((IIntExpression) exp);
        else
            throw new EchoTypeError("IIntExpression");
    }

    private IFormula eq(IIntExpression exp) {
        return new KodkodFormula(expr.eq(((KodkodIntExpression) exp).expr));
    }
}
