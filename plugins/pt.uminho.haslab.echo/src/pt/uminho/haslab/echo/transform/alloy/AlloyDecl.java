package pt.uminho.haslab.echo.transform.alloy;


import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import pt.uminho.haslab.echo.transform.ast.IDecl;
import pt.uminho.haslab.echo.transform.ast.IExpression;

/**
 * Created by tmg on 2/10/14.
 */
class AlloyDecl implements IDecl{

    public final Decl decl;

    AlloyDecl(Decl decl) {
        this.decl = decl;
    }

    @Override
    public IExpression expression() {
        return new AlloyExpression(decl.expr);
    }

    @Override
    public String name() {
        return decl.get().label;
    }
}
