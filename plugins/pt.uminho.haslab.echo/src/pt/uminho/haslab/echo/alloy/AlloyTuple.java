package pt.uminho.haslab.echo.alloy;

import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 10/25/13
 * Time: 11:51 AM

 */
public class AlloyTuple {


    private A4Solution sol;
    private Sig.PrimSig state;

    public AlloyTuple(A4Solution sol, Sig.PrimSig state)
    {
        this.sol =sol;
        this.state = state;
    }

    public Sig.PrimSig getState() {
        return state;
    }

    public A4Solution getSolution() {
        return sol;
    }
}
