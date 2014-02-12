package pt.uminho.haslab.echo.transform;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.transform.ast.IDecl;
import pt.uminho.haslab.echo.transform.ast.IExpression;
import pt.uminho.haslab.mde.model.EVariable;

import java.util.Collection;

/**
 * Created by tmg on 2/4/14.
 */
public interface IContext {

    IExpression getVar(String name);

    void addVar(String name, IExpression var);


    //TODO check if needed
    void addVar(String name, IExpression var, String extra);

    void remove(String name);

    IDecl getDecl(EVariable x) throws EchoError;

    IDecl getDecl(Collection<EVariable> x, String name) throws EchoError;

    IExpression getFieldExpression(String metaModelID, String className, String fieldName);

    IExpression getClassExpression(String metaModelID, String className);
}
