package pt.uminho.haslab.echo.transform.kodkod;

import kodkod.ast.Decls;
import kodkod.ast.Formula;
import pt.uminho.haslab.echo.transform.ast.IDecl;
import pt.uminho.haslab.echo.transform.ast.IExpression;
import pt.uminho.haslab.echo.transform.ast.IFormula;
import pt.uminho.haslab.echo.transform.ast.INode;

/**
 * Created by tmg on 2/5/14.
 */
class KodkodFormula implements IFormula {

    public final Formula formula;
    public KodkodFormula(Formula f){
        this.formula = f;
    }

    @Override
    public IFormula and(IFormula f) {
        return new KodkodFormula(formula.and(((KodkodFormula) f).formula));
    }

    @Override
    public IFormula or(IFormula f) {
        return new KodkodFormula(formula.or(((KodkodFormula) f).formula));
    }

    @Override
    public IFormula iff(IFormula f) {
        return new KodkodFormula(formula.iff(((KodkodFormula) f).formula));
    }

    @Override
    public IFormula implies(IFormula f) {
        return new KodkodFormula(formula.implies(((KodkodFormula) f).formula));
    }

    @Override
    public IFormula not() {
        return new KodkodFormula(formula.not());
    }

    @Override
    public IExpression thenElse(IExpression thenExpr, IExpression elseExpr) {

        return new KodkodExpression(formula.thenElse(
                ((KodkodExpression) thenExpr).EXPR,((KodkodExpression) elseExpr).EXPR));
    }

    //TODO check
    @Override
    public IExpression comprehension(IDecl firstDecl, IDecl... extraDecls) {
        Decls ds = ((KodkodDecl) firstDecl).decl;
        for(IDecl d : extraDecls)

            ds = ds.and(((KodkodDecl) d).decl);


        return new KodkodExpression(formula.comprehension(ds));
    }

    @Override
    public IFormula forAll(IDecl decl) {
        return new KodkodFormula(
                formula.forAll(((KodkodDecl) decl).decl)
        );
    }

    @Override
    public IFormula forSome(IDecl decl) {
        return new KodkodFormula(
                formula.forSome(((KodkodDecl) decl).decl)
        );
    }


    //TODO
    @Override
    public INode forOne(IDecl d) {

        return forSome(d);
    }
}
