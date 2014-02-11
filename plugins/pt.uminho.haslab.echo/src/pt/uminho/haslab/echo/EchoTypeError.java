package pt.uminho.haslab.echo;

/**
 * Created by tmg on 2/11/14.
 *
 */
public class EchoTypeError extends EchoError{
    private final String expected;



    public EchoTypeError(String expected) {
        super("Typecheck error! \nExpected Type: "+ expected);
        this.expected = expected;
    }

    @Override
    public String toString(){
           return super.getMessage();
    }

}
