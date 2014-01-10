package pt.uminho.haslab.echo.transform.alloy;

import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: tmg
 * Date: 10/25/13
 * Time: 11:51 AM

 */
class AlloyTuple {


    private A4Solution sol;
    private Map<String,Sig.PrimSig> state;

    public AlloyTuple(A4Solution sol, Map<String,Sig.PrimSig> state)
    {
        this.sol =sol;
        this.state = state;
    }

    public Sig.PrimSig getState(String modeluri) {
        return state.get(modeluri);
    }

    public A4Solution getSolution() {
        return sol;
    }
}
