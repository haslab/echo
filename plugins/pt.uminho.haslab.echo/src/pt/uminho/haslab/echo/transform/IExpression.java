package pt.uminho.haslab.echo.transform;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 12/19/13
 * Time: 4:25 PM
 */
public interface IExpression {




    public IExpression closure();
    public IExpression reflexiveClosure();
    public IExpression transpose();

    public IExpression join(IExpression e);
    public IExpression diference(IExpression e);
    public IExpression intersection(IExpression e);
    public IExpression union(IExpression e);


    public IFormula in(IExpression e);
    public IFormula eq(IExpression e);



    public IFormula some();
    public IFormula one();
    public IFormula no();
    public IFormula lone();


}
