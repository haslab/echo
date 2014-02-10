package pt.uminho.haslab.echo.transform.ast;

import pt.uminho.haslab.echo.transform.EchoTranslator;

/**
 * Created by tmg on 2/4/14.
 */
public class Constants {
    public static IFormula TRUE(){
        return EchoTranslator.getInstance().getTrueFormula();
    }


    public static IFormula FALSE(){
        return EchoTranslator.getInstance().getFalseFormula();
    }

    //TODO
    public static IIntExpression makeNumber(int n)
    {
       return EchoTranslator.getInstance().makeNumber(n);
    }
}
