package pt.uminho.haslab.echo.engine.ast;

/**
 * Created by tmg on 2/10/14.
 */
public interface IIntExpression extends INode {
    IFormula lt(IIntExpression intExpression);

    IFormula gt(IIntExpression expression);

    IFormula lte(IIntExpression expression);

    IFormula gte(IIntExpression expression);

    IIntExpression plus(IIntExpression expression);

    IIntExpression minus(IIntExpression iIntExpression);
}
