package pt.uminho.haslab.echo.transform.ast;



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

    IExpression comprehension(IDecl firstDecl, IDecl... extraDecls);



    IFormula forAll(IDecl decl);
    IFormula forSome(IDecl decl);

    IFormula forOne(IDecl d);
}