package pt.uminho.haslab.echo.transform;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 12/19/13
 * Time: 4:25 PM
 */
public interface IFormula {

    IFormula and(IFormula f);
    IFormula or(IFormula f);


    IFormula iff(IFormula f);
    IFormula implies(IFormula f);

    IFormula not();

    //TODO below
    IFormula forAll();
    IFormula forSome();

}
