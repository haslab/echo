package pt.uminho.haslab.echo.engine.alloy;

import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import pt.uminho.haslab.echo.engine.ast.IFormula;
import pt.uminho.haslab.echo.engine.ast.IIntExpression;

/**
 * Alloy representation of integer expressions.
 * 
 * @author nmm,tmg
 * @version 0.4 20/02/2014
 */
class AlloyIntExpression extends AlloyExpression implements IIntExpression {

    final Expr EXPR;

    AlloyIntExpression(Expr expr) {
    	super (expr);
        this.EXPR = expr;
    }

	/** {@inheritDoc} */
    @Override
    public IFormula lt(IIntExpression intExpression) {
        return new AlloyFormula(
                EXPR.lt(
                        ((AlloyIntExpression) intExpression).EXPR
                )
        );
    }

	/** {@inheritDoc} */
    @Override
    public IFormula gt(IIntExpression expression) {
        return new AlloyFormula(
                EXPR.gt(
                        ((AlloyIntExpression) expression).EXPR
                )
        );
    }

	/** {@inheritDoc} */
    @Override
    public IFormula lte(IIntExpression expression) {
        return new AlloyFormula(
                EXPR.lte(
                        ((AlloyIntExpression) expression).EXPR
                )
        );
    }

	/** {@inheritDoc} */
    @Override
    public IFormula gte(IIntExpression expression) {
        return new AlloyFormula(
                EXPR.gte(
                        ((AlloyIntExpression) expression).EXPR
                )
        );
    }

	/** {@inheritDoc} */
    @Override
    public IIntExpression plus(IIntExpression expression) {
        return new AlloyIntExpression(
                EXPR.plus(
                        ((AlloyIntExpression) expression).EXPR
                )
        );
    }

	/** {@inheritDoc} */
    @Override
    public IIntExpression minus(IIntExpression iIntExpression) {

        return new AlloyIntExpression(
                EXPR.minus(
                        ((AlloyIntExpression) iIntExpression).EXPR
                )
        );
    }
}
