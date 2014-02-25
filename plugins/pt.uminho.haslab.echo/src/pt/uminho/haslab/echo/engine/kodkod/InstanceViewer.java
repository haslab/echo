package pt.uminho.haslab.echo.engine.kodkod;

import kodkod.ast.Relation;
import kodkod.instance.TupleSet;
import pt.uminho.haslab.echo.engine.kodkod.viewer.Instance;
import pt.uminho.haslab.echo.engine.kodkod.viewer.ViewerFactory;
import pt.uminho.haslab.echo.engine.kodkod.viewer.alloy;

import java.util.Map;

/**
 * Created by tmg on 2/25/14.
 */
class InstanceViewer {
    /**result*/
    private alloy res;

    /** all content*/
    private Map<Relation,TupleSet> mapRelaions;

    private EKodkodMetamodel e2k;
    private ViewerFactory factory;

    public InstanceViewer(Map<Relation,TupleSet> map, EKodkodMetamodel e2k){
        mapRelaions = map;
        this.e2k = e2k;
        factory = ViewerFactory.eINSTANCE;



        makeBasic();


        for(Relation rel : mapRelaions.keySet())
        {
            if(rel.arity() == 2)
                handleRefs(rel);
            else if(e2k.getBoolType(rel) != null)
                handleBool(rel);
            else
                handleRel(rel);
        }
    }

    private void makeBasic() {
        res = factory.createalloy();
        Instance ins = factory.createInstance();
        res.getInstance().add(ins);



    }

    private void handleRel(Relation rel) {

    }

    private void handleBool(Relation rel) {

    }

    private void handleRefs(Relation rel) {

    }


    public alloy getAlloyInstance(){
        return res;
    }

}
