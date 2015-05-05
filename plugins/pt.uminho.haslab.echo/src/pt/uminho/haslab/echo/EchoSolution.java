package pt.uminho.haslab.echo;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 10/24/13
 * Time: 6:04 PM
 * To change this template use File | Settings | File Templates.
 */
public interface EchoSolution {

    boolean satisfiable();
    
    void writeXML(String filename);

    Object getContents();
    
    void next();
}
