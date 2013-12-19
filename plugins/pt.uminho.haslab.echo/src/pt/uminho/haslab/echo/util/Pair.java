package pt.uminho.haslab.echo.util;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 12/18/13
 * Time: 6:13 PM
 */
public class Pair<L,R> {

    // TODO: methods like hashcode, they are not needed now, but they might be in the future!

    public final L left;
    public final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }
}
