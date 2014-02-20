package pt.uminho.haslab.echo.engine.kodkod;

import kodkod.ast.Decls;
import kodkod.ast.Formula;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IExpression;
import pt.uminho.haslab.echo.engine.ast.IFormula;

/**
 * Created by tmg on 2/5/14.
 */
class KodkodFormula implements IFormula {

    public final Formula FORMULA;
    public KodkodFormula(Formula f){
        this.FORMULA = f;
    }

    @Override
    public IFormula and(IFormula f) {
        return new KodkodFormula(FORMULA.and(((KodkodFormula) f).FORMULA));
    }

    @Override
    public IFormula or(IFormula f) {
        return new KodkodFormula(FORMULA.or(((KodkodFormula) f).FORMULA));
    }

    @Override
    public IFormula iff(IFormula f) {
        return new KodkodFormula(FORMULA.iff(((KodkodFormula) f).FORMULA));
    }

    @Override
    public IFormula implies(IFormula f) {
        return new KodkodFormula(FORMULA.implies(((KodkodFormula) f).FORMULA));
    }

    @Override
    public IFormula not() {
        return new KodkodFormula(FORMULA.not());
    }

    @Override
    public IExpression thenElse(IExpression thenExpr, IExpression elseExpr) {

        return new KodkodExpression(FORMULA.thenElse(
                ((KodkodExpression) thenExpr).EXPR,((KodkodExpression) elseExpr).EXPR));
    }

    //TODO check
    @Override
    public IExpression comprehension(IDecl firstDecl, IDecl... extraDecls) {
        Decls ds = ((KodkodDecl) firstDecl).decl;
        for(IDecl d : extraDecls)
            ds = ds.and(((KodkodDecl) d).decl);

        return new KodkodExpression(FORMULA.comprehension(ds));
    }

    @Override
    public IFormula forAll(IDecl decl, IDecl... extraDecls) {
        Decls ds = ((KodkodDecl) decl).decl;
        for(IDecl d : extraDecls)
            ds = ds.and(((KodkodDecl) d).decl);

        return new KodkodFormula(FORMULA.forAll(ds));
    }

    @Override
    public IFormula forSome(IDecl decl, IDecl... extraDecls) {
        Decls ds = ((KodkodDecl) decl).decl;
        for(IDecl d : extraDecls)
            ds = ds.and(((KodkodDecl) d).decl);

        return new KodkodFormula(FORMULA.forSome(ds));
    }


    @Override
    public IFormula forOne(IDecl d) {

        return comprehension(d).one();
    }

}
