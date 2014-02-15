package pt.uminho.haslab.echo.engine.alloy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.engine.IContext;
import pt.uminho.haslab.echo.engine.ast.Constants;
import pt.uminho.haslab.echo.engine.ast.EEngineRelation;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IExpression;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.model.EVariable;
import edu.mit.csail.sdg.alloy4compiler.ast.Decl;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;

/**
 * Auxiliary context for the translation to Alloy.
 * Mainly used for variable declaration management.
 * Variables are uniquely identified by name.
 *
 * @author nmm
 * @version 0.4 14/02/2014
 */
public class AlloyContext implements IContext {

	private Map<String,AlloyExpression> varExp = new HashMap<String,AlloyExpression>();
	private Map<String,String> varModel = new HashMap<String,String>();
	private Map<String,AlloyExpression> modelPre = new HashMap<String,AlloyExpression>();
	private Map<String,AlloyExpression> modelPos = new HashMap<String,AlloyExpression>();
	
	private String currentModel;
	private EAlloyRelation currentRel;
	private boolean currentPre = false;
	
	/** {@inheritDoc} */
	@Override
	public AlloyExpression getVar(String name) {
		return varExp.get(name);
	}

	/** {@inheritDoc} */
	@Override
	public void addVar(IDecl decl) {
		EchoReporter.getInstance().debug("AddVar: "+((AlloyDecl) decl).decl.get().label + " and "+ ((AlloyDecl) decl).decl.get());
		varExp.put(((AlloyDecl) decl).decl.get().label, (AlloyExpression) decl.expression());
	}

	/** {@inheritDoc} */
	@Override
	public void addVar(IDecl decl, String extra) {
		varModel.put(((AlloyDecl) decl).decl.get().label,extra);
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
        Decl d = AlloyUtil.variableListToExpr(new HashSet<EVariable>(Arrays.asList(x)),this).get(x.getName());
        return new AlloyDecl(d);
	}

	/** {@inheritDoc} */
	@Override
	public IDecl getDecl(Collection<EVariable> x, String name) throws EchoError {
		// TODO Auto-generated method stub
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public AlloyExpression getPropExpression(String metaModelID, String className, String fieldName) {
        EAlloyMetamodel ameta = AlloyEchoTranslator.getInstance().getMetamodel(metaModelID);
        Expr statesig = null;
        if (currentModel != null)
			statesig = getModelParam(currentModel).EXPR;
		
		if (statesig == null)
			statesig = getModelParam(metaModelID).EXPR;		

		EClass eclass = ((EClass)ameta.metamodel.getEObject().getEClassifier(className));
		EStructuralFeature feature = eclass.getEStructuralFeature(fieldName);
		Field field = AlloyEchoTranslator.getInstance().getFieldFromFeature(metaModelID,feature);
		EchoReporter.getInstance().debug("** Debug result: "+field.join(statesig));
		return new AlloyExpression(field.join(statesig));
	}

	/** {@inheritDoc} */
	@Override
	public AlloyExpression getClassExpression(String metaModelID, String className) throws ErrorParser, ErrorUnsupported {
		EMetamodel emeta = MDEManager.getInstance().getMetamodelID(metaModelID);
		EClass eclass = (EClass) emeta.getEObject().getEClassifier(className);
		
		Field field = AlloyEchoTranslator.getInstance().getStateFieldFromClass(metaModelID, eclass);
		
		AlloyExpression state = (AlloyExpression) Constants.EMPTY();
		
		
		if (currentModel != null) state = getModelParam(currentModel);
		else state = getModelParam(metaModelID);

		EchoReporter.getInstance().debug("** SDebug result: "+metaModelID+" and "+ className +" did "+field.join(state.EXPR));
		return (AlloyExpression) (new AlloyExpression(field)).join(state);
		
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
	public AlloyExpression getModelParam(String name) {
		EchoReporter.getInstance().debug("Var: "+name + " at "+ currentPre);
		EchoReporter.getInstance().debug("But: "+modelPre.keySet() + " and "+ modelPos.keySet());	
		return currentPre?modelPre.get(name):modelPos.get(name);
	}

	public AlloyExpression addModelParam(boolean pre, String name, IExpression var) {
		return pre?modelPre.put(name,(AlloyExpression) var):modelPos.put(name,(AlloyExpression) var);
	}

	/** {@inheritDoc} */
	@Override
	public List<IExpression> getModelParams() {
		return  new ArrayList<IExpression>(currentPre?modelPre.values():modelPos.values());
	}

	public void setCurrentRel(EEngineRelation parentRelation) {
		currentRel = (EAlloyRelation) parentRelation;
	}
	
	public EAlloyRelation getCurrentRel() {
		return currentRel;
	}

}
