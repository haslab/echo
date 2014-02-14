package pt.uminho.haslab.echo.engine.ast;

import pt.uminho.haslab.echo.engine.EchoTranslator;

/**
 * Created by tmg on 2/4/14.
 */
public class Constants {
    public static IFormula TRUE(){
        return EchoTranslator.getInstance().getTrueFormula();
    }
    
    public static IExpression EMPTY() {
        return EchoTranslator.getInstance().getEmptyExpression();	
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
