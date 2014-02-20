package pt.uminho.haslab.echo.engine.alloy;


import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.echo.engine.ast.IIntExpression;

/**
 * Created by tmg on 2/10/14.
 */
class AlloyIntExpression extends AlloyExpression implements IIntExpression {

    final Expr expr;

    AlloyIntExpression(Expr expr) {
    	super (expr);
        this.expr = expr;
    }

    @Override
    public IFormula lt(IIntExpression intExpression) {
        return new AlloyFormula(
                expr.lt(
                        ((AlloyIntExpression) intExpression).expr
                )
        );
    }

    @Override
    public IFormula gt(IIntExpression expression) {
        return new AlloyFormula(
                expr.gt(
                        ((AlloyIntExpression) expression).expr
                )
        );
    }

    @Override
    public IFormula lte(IIntExpression expression) {
        return new AlloyFormula(
                expr.lte(
                        ((AlloyIntExpression) expression).expr
                )
        );
    }

    @Override
    public IFormula gte(IIntExpression expression) {
        return new AlloyFormula(
                expr.gte(
                        ((AlloyIntExpression) expression).expr
                )
        );
    }

    @Override
    public IIntExpression plus(IIntExpression expression) {
        return new AlloyIntExpression(
                expr.plus(
                        ((AlloyIntExpression) expression).expr
                )
        );
    }

    @Override
    public IIntExpression minus(IIntExpression iIntExpression) {

        return new AlloyIntExpression(
                expr.minus(
                        ((AlloyIntExpression) iIntExpression).expr
                )
        );
    }
}
