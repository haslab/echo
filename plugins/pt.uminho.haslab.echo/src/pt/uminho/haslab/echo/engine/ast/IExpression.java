package pt.uminho.haslab.echo.engine.ast;

import pt.uminho.haslab.echo.ErrorInternalEngine;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 12/19/13
 * Time: 4:25 PM
 */
public interface IExpression extends INode{



    IExpression closure();
    IExpression reflexiveClosure();
    IExpression transpose();

    IExpression join(IExpression e);
    IExpression difference(IExpression e);
    IExpression intersection(IExpression e);
    IExpression union(IExpression e);


    IFormula in(IExpression e);
    IFormula eq(IExpression e);



    IFormula some();
    IFormula one();
    IFormula no();
    IFormula lone();


    IDecl oneOf(String name) throws ErrorInternalEngine;

    IIntExpression cardinality();
	boolean hasVar(IExpression var);
}
