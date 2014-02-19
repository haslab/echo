package pt.uminho.haslab.echo.engine.alloy;

import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IExpression;

/**
 * Created by tmg on 2/10/14.
 */
class AlloyDecl implements IDecl{

    public final Decl decl;

    AlloyDecl(Decl decl) {
        this.decl = decl;
    }

    @Override
    public IExpression variable() {
        return new AlloyExpression(decl.get());
    }

    @Override
    public String name() {
        return decl.get().label;
    }
}
