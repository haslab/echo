package pt.uminho.haslab.echo.transform.ast;

import pt.uminho.haslab.echo.ErrorInternalEngine;



/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 12/19/13
 * Time: 4:25 PM
 */
public interface IFormula extends INode{

    IFormula and(IFormula f);
    IFormula or(IFormula f);


    IFormula iff(IFormula f);
    IFormula implies(IFormula f);

    IFormula not();

    IExpression thenElse(IExpression thenExpr, IExpression elseExpr);

    IExpression comprehension(IDecl firstDecl, IDecl... extraDecls) throws ErrorInternalEngine;


    IFormula forAll(IDecl decl, IDecl... moreDecls) throws ErrorInternalEngine;
    IFormula forSome(IDecl decl, IDecl... moreDecls) throws ErrorInternalEngine;
    IFormula forOne(IDecl d) throws ErrorInternalEngine;
}