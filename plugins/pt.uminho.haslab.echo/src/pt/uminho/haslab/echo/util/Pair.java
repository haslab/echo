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

    public boolean equals(Pair p2) {
        return this.left.equals(p2.left) && this.right.equals(p2.right);
    }

    @Override
    public String toString(){
            return "{" + left.toString() + " , " + right.toString() + "}";
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof Pair && equals((Pair) o);
    }
}
