package pt.uminho.haslab.echo.engine.kodkod;

import kodkod.ast.Expression;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ocl.examples.pivot.Type;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.engine.IContext;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IExpression;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.model.EVariable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by tmg on 2/5/14.
 */
class KodkodContext implements IContext{

    private Map<String, IExpression> map = new HashMap<>();

    public KodkodContext(){}

    @Override
    public IExpression getVar(String name) {
        return map.get(name);
    }

    @Override
    public void addVar(String name, IExpression var) {
           map.put(name,var);
    }

    @Override
    public void addVar(String name, IExpression var, String extra) {
              //TODO
    }

    @Override
    public void remove(String name) {
          map.remove(name);
    }

    @Override
    public IDecl getDecl(EVariable x) throws EchoError {
        Expression range;
        EObject t = x.getType();
        String type = null;
        if (t instanceof Type)
            type = ((Type) t).getName();
        else {
            type = (String) t.eGet(t.eClass().getEStructuralFeature(
                    "name"));
        }
        if (type.equals("String"))
            range = KodkodUtil.stringRel;
        else if (type.equals("Int"))
            range = Expression.INTS;
        else {
            String metamodeluri = null;
            if (t instanceof Type) {
            	//EchoReporter.getInstance().debug(EcoreUtil.getURI(((Type) t).getPackage()).path());
                //EchoReporter.getInstance().debug(EcoreUtil.getURI(((Type) t).getPackage()).path().replace("resource/", ""));
                metamodeluri = EcoreUtil.getURI(((Type) t).getPackage()).path().replace(".oclas", "").replace("resource/", "");
            } else{
                System.out.println("Nao deveria entrar aqui.");
            }


            KodkodEchoTranslator translator = KodkodEchoTranslator.getInstance();
            EMetamodel metaModel = MDEManager.getInstance().getMetamodel(metamodeluri, false);
            EKodkodMetamodel e2k = translator.getMetamodel(metaModel.ID);
            range = e2k.getRelation((EClass) e2k.metamodel.getEObject().getEClassifier(type));
        }
        return (new KodkodExpression(range)).oneOf(x.getName());
    }

    @Override
    public IDecl getDecl(Collection<EVariable> x, String name) throws EchoError {
        IDecl aux=null;
        boolean found = false;
        Iterator<EVariable> it  = x.iterator();
        while(it.hasNext() && !found)
        {
           aux = getDecl(it.next());
           if(aux.name().equals(name))
               found=true;
        }

        return found?aux:null;
    }

    @Override
    public IExpression getFieldExpression(String metaModelID, String className, String fieldName) {
        EKodkodMetamodel e2k = KodkodEchoTranslator.getInstance().getMetamodel(metaModelID);

        return new KodkodExpression(
                e2k.getRelation(((EClass) e2k.metamodel.getEObject().getEClassifier(className)).getEStructuralFeature(fieldName))
        );
    }

    @Override
    public IExpression getClassExpression(String metaModelID, String className) {

        EKodkodMetamodel e2k = KodkodEchoTranslator.getInstance().getMetamodel(metaModelID);

        return new KodkodExpression(
                e2k.getRelation((EClass) e2k.metamodel.getEObject().getEClassifier(className))
        );
    }
}
