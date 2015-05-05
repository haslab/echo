package pt.uminho.haslab.echo.engine.ast;

import pt.uminho.haslab.echo.engine.CoreTranslator;

/**
 * Created by tmg on 2/4/14.
 */
public class Constants {
    public static IFormula TRUE(){
        return CoreTranslator.getInstance().getTrueFormula();
    }
    
    public static IExpression EMPTY() {
        return CoreTranslator.getInstance().getEmptyExpression();	
    }


    public static IFormula FALSE(){
        return CoreTranslator.getInstance().getFalseFormula();
    }

    //TODO
    public static IIntExpression makeNumber(int n)
    {
       return CoreTranslator.getInstance().makeNumber(n);
    }
}
