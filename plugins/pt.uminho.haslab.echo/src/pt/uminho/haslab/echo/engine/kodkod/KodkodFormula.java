package pt.uminho.haslab.echo.engine.kodkod;

import kodkod.ast.Decls;
import kodkod.ast.Formula;
import kodkod.util.nodes.PrettyPrinter;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IExpression;
import pt.uminho.haslab.echo.engine.ast.IFormula;

/**
 * Created by tmg on 2/5/14.
 */
class KodkodFormula implements IFormula {

    public final Formula formula;
    public KodkodFormula(Formula f){
        this.formula = f;
    }

    @Override
    
    public KodkodFormula and(IFormula f) {
        return new KodkodFormula(formula.and(((KodkodFormula) f).formula));
    }

    @Override
    public KodkodFormula or(IFormula f) {
        return new KodkodFormula(formula.or(((KodkodFormula) f).formula));
    }

    @Override
    public KodkodFormula iff(IFormula f) {
        return new KodkodFormula(formula.iff(((KodkodFormula) f).formula));
    }

    @Override
    public KodkodFormula implies(IFormula f) {
        return new KodkodFormula(formula.implies(((KodkodFormula) f).formula));
    }

    @Override
    public KodkodFormula not() {
        return new KodkodFormula(formula.not());
    }

    @Override
    public KodkodExpression thenElse(IExpression thenExpr, IExpression elseExpr) {

        return new KodkodExpression(formula.thenElse(
                ((KodkodExpression) thenExpr).EXPR,((KodkodExpression) elseExpr).EXPR));
    }

    //TODO check
    @Override
    public KodkodExpression comprehension(IDecl firstDecl, IDecl... extraDecls) {
        Decls ds = ((KodkodDecl) firstDecl).decl;
        for(IDecl d : extraDecls)
            ds = ds.and(((KodkodDecl) d).decl);

        return new KodkodExpression(formula.comprehension(ds));
    }

    @Override
    public KodkodFormula forAll(IDecl decl, IDecl... extraDecls) {
        Decls ds = ((KodkodDecl) decl).decl;
        for(IDecl d : extraDecls)
            ds = ds.and(((KodkodDecl) d).decl);

        return new KodkodFormula(formula.forAll(ds));
    }

    @Override
    public KodkodFormula forSome(IDecl decl, IDecl... extraDecls) {
        Decls ds = ((KodkodDecl) decl).decl;
        for(IDecl d : extraDecls)
            ds = ds.and(((KodkodDecl) d).decl);

        return new KodkodFormula(formula.forSome(ds));
    }


    @Override
    public KodkodFormula forOne(IDecl d) {
        return comprehension(d).one();
    }

    @Override
    public String toString (){
        return PrettyPrinter.print(formula,3);
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
            return true;
        else if(! (o instanceof KodkodFormula))
            return false;
        else
            return formula.equals(((KodkodFormula)o).formula);
    }

}
