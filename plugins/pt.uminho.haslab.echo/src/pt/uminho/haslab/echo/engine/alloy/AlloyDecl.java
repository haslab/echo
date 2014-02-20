package pt.uminho.haslab.echo.engine.alloy;

import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IExpression;

/**
 * Alloy representation of a variable declaration.
 * 
 * @author nmm,tmg
 * @version 0.4 20/02/2014
 */
class AlloyDecl implements IDecl{

    public final Decl DECL;

    AlloyDecl(Decl decl) {
        this.DECL = decl;
    }

	/** {@inheritDoc} */
   @Override
    public IExpression variable() {
        return new AlloyExpression(DECL.get());
    }

	/** {@inheritDoc} */
   	@Override
    public String name() {
        return DECL.get().label;
    }
}
