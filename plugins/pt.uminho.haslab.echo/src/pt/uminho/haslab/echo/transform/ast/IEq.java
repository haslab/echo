package pt.uminho.haslab.echo.transform.ast;

import pt.uminho.haslab.echo.EchoTypeError;

/**
 * Created by tmg on 2/12/14.
 */
public interface IEq extends INode{

    IFormula eq(IEq exp) throws EchoTypeError;
}
