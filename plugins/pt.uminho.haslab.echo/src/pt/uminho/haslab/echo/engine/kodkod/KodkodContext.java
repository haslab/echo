package pt.uminho.haslab.echo.engine.kodkod;

import kodkod.ast.Expression;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ocl.examples.pivot.Type;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.engine.ITContext;
import pt.uminho.haslab.echo.engine.ast.EEngineRelation;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IExpression;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.model.EVariable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Auxiliary context for the translation of artifacts to Kodkod.
 * Mainly used for variable declaration management.
 * Variables are uniquely identified by name.
 *
 * @author tmg, nmm
 * @version 0.4 17/02/2014
 */
class KodkodContext implements ITContext {
	 
	private Map<String,KodkodExpression> varExp = new HashMap<String,KodkodExpression>();
	private Map<String,String> varModel = new HashMap<String,String>();
	private Map<String,KodkodExpression> modelPre = new HashMap<String,KodkodExpression>();
	private Map<String,KodkodExpression> modelPos = new HashMap<String,KodkodExpression>();
	private Map<String,KodkodExpression> modelPreT = new HashMap<String,KodkodExpression>();
	private Map<String,KodkodExpression> modelPosT = new HashMap<String,KodkodExpression>();
	
	private String currentModel;
	private EKodkodRelation currentRel;
	private boolean currentPre = false;

    public KodkodContext(){}

    @Override
    public IExpression getVar(String name) {
        return varExp.get(name);
    }

	/** {@inheritDoc} */
	@Override
	public void addVar(IDecl decl) {
		varExp.put(decl.name(), (KodkodExpression) decl.variable());
	}

	/** {@inheritDoc} */
	@Override
	public void addVar(IDecl decl, String extra) {
		varModel.put(((KodkodDecl) decl).decl.variable().name(),extra);
		addVar(decl);
	}

	/** {@inheritDoc} */
    @Override
    public void remove(String name) {
    	varExp.remove(name);
		varModel.remove(name);
	}

	/** {@inheritDoc} */
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
    public IExpression getPropExpression(String metaModelID, String className, String fieldName) {
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

    /** {@inheritDoc} */
	@Override
	public void setCurrentModel(String name) {
		currentModel = name;
	}

	/** {@inheritDoc} */
	@Override
	public void setCurrentPre(boolean isPre) {
		currentPre = isPre;
	}

	public void setVarModel(String name, String model) throws ErrorParser {
		varModel.put(name,model);
	}


	/** {@inheritDoc} */
	@Override
	public KodkodExpression getModelExpression(String name) {
		KodkodExpression e = currentPre?modelPreT.get(name):modelPosT.get(name);
		if (e == null) e = currentPre?modelPre.get(name):modelPos.get(name);
		return e;
	}

	/** {@inheritDoc} */
	@Override
	public KodkodExpression addMetamodelExpression(boolean pre, String name, IExpression var) {
		return pre?modelPre.put(name,(KodkodExpression) var):modelPos.put(name,(KodkodExpression) var);
	}

	/** {@inheritDoc} */
	@Override
	public KodkodExpression addParamExpression(boolean pre, String name, IExpression var) {
		return pre?modelPreT.put(name,(KodkodExpression) var):modelPosT.put(name,(KodkodExpression) var);
	}

	/** {@inheritDoc} */
	@Override
	public List<IExpression> getModelExpressions() {
		Collection<KodkodExpression> res = currentPre?modelPreT.values():modelPosT.values();
		if (res == null) res = currentPre?modelPre.values():modelPos.values();
		return new ArrayList<IExpression>(res);
	}

	/** {@inheritDoc} */
	@Override
	public void setCurrentRel(EEngineRelation parentRelation) {
		currentRel = (EKodkodRelation) parentRelation;
	}
	
	/** {@inheritDoc} */
	@Override
	public String getVarModel(String name) {
		return varModel.get(name);
	}

	/** {@inheritDoc} */
	@Override
	public List<String> getVars() {
		return new ArrayList<String>(varExp.keySet());
	}

	/** {@inheritDoc} */
	@Override
	public EEngineRelation getCallerRel() {
		return currentRel;
	}


}
