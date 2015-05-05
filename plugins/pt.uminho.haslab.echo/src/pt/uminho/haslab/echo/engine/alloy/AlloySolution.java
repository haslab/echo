package pt.uminho.haslab.echo.engine.alloy;

import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;

import java.util.Map;

/**
 * Represents an Alloy solution.
 * 
 * @author tmg
 * @version 0.4 10/25/2013
 */
public class AlloySolution {

    private A4Solution sol;
    private Map<String,Sig.PrimSig> state;

    AlloySolution(A4Solution sol, Map<String,Sig.PrimSig> state) {
        this.sol = sol;
        this.state = state;
    }

    public Sig.PrimSig getState(String modeluri) {
        return state.get(modeluri);
    }

    public A4Solution getSolution() {
        return sol;
    }
}
