package pt.uminho.haslab.echo.transform.kodkod;

import kodkod.ast.Decl;
import pt.uminho.haslab.echo.transform.ast.IDecl;
import pt.uminho.haslab.echo.transform.ast.IExpression;

/**
 * Created by tmg on 2/10/14.
 */
class KodkodDecl implements IDecl{

    public final Decl decl;

    KodkodDecl(Decl decl) {
        this.decl = decl;
    }


    @Override
    public IExpression expression() {
        return new KodkodExpression(decl.variable());
    }

    @Override
    public String name() {
        return decl.variable().name();
    }
}
