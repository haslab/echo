package pt.uminho.haslab.echo.engine.alloy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.engine.ITContext;
import pt.uminho.haslab.echo.engine.ast.Constants;
import pt.uminho.haslab.echo.engine.ast.EEngineRelation;
import pt.uminho.haslab.echo.engine.ast.IDecl;
import pt.uminho.haslab.echo.engine.ast.IExpression;
import pt.uminho.haslab.mde.MDEManager;
import pt.uminho.haslab.mde.model.EMetamodel;
import pt.uminho.haslab.mde.model.EVariable;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;

/**
 * Auxiliary context for the translation of artifacts to Alloy.
 * Mainly used for variable declaration management.
 * Variables are uniquely identified by name.
 *
 * @author nmm
 * @version 0.4 20/02/2014
 */
public class AlloyContext implements ITContext {

	/** variables representation in Alloy */
	private Map<String,AlloyExpression> varExp = new HashMap<String,AlloyExpression>();
	/** variables owning model, if inferred */
	private Map<String,String> varModel = new HashMap<String,String>();
	
	/** pre-state model variables, indexed by metamodel ID */
	private Map<String,AlloyExpression> modelPre = new HashMap<String,AlloyExpression>();
	/** post-state model variables, indexed by metamodel ID */
	private Map<String,AlloyExpression> modelPos = new HashMap<String,AlloyExpression>();
	/** pre-state model variables, indexed by transformation model parameter */
	private Map<String,AlloyExpression> modelPreT = new HashMap<String,AlloyExpression>();
	/** post-state model variables, indexed by transformation model parameter */
	private Map<String,AlloyExpression> modelPosT = new HashMap<String,AlloyExpression>();
	
	/** the model to be currently considered in expression management */
	private String currentModel;
	/** the current relation being translated */
	private EAlloyRelation currentRel;
	/** if methods should be run in pre-state mode */
	private boolean currentPre = false;
	
	/** {@inheritDoc} */
	@Override
	public AlloyExpression getVar(String name) {
		return varExp.get(name);
	}

	/** {@inheritDoc} */
	@Override
	public void addVar(IDecl decl) {
		varExp.put(decl.name(), (AlloyExpression) decl.variable());
	}

	/** {@inheritDoc} */
	@Override
	public void addVar(IDecl decl, String ownerModel) {
		varModel.put(decl.name(),ownerModel);
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
	public AlloyDecl getDecl(EVariable var, boolean addContext) throws EchoError {
		// gets the type of the variable
		String type = var.getType();
	
		try {
			// calculates the expression representing the type in the state
			Expr range = Sig.NONE;
			if (type.equals("String"))
				range = Sig.STRING;
			else if (type.equals("Int"))
				range = Sig.SIGINT;
			else {
				EMetamodel metamodel = MDEManager.getInstance().getMetamodel(var.getMetamodel(), false);
				// if owning model was set, retrieve it
				String temp = currentModel;
				if (getVarModel(var.getName()) != null)
					setCurrentModel(getVarModel(var.getName()));
				range = getClassExpression(metamodel.ID, type).EXPR;
				currentModel = temp;
			}
			EchoReporter.getInstance().debug("Context decl: "+var.getName()+"::"+range);
			AlloyDecl d = new AlloyDecl(range.oneOf(var.getName()));
			if (addContext) addVar(d);
			return d;

		} catch (Err a) {
			throw new ErrorAlloy(a.getMessage());
		}
	}

	/** {@inheritDoc} */
	@Override
	public AlloyExpression getPropExpression(String metaModelID, String className, String fieldName) {
		EAlloyMetamodel ameta = AlloyEchoTranslator.getInstance().getMetamodel(metaModelID);
		AlloyExpression state = (AlloyExpression) Constants.EMPTY();

		// tries to retrieve the state sig of the current model context
		if (currentModel != null) state = getModelExpression(currentModel);		
        // otherwise uses the generic metamodel sig
		if (state.equals(Constants.EMPTY())) state = getModelExpression(metaModelID);		

		// fetches the corresponding field
		EClass eclass = ((EClass) ameta.metamodel.getEObject().getEClassifier(className));
		EStructuralFeature feature = eclass.getEStructuralFeature(fieldName);
		Field field = AlloyEchoTranslator.getInstance().getFieldFromFeature(metaModelID,feature);
		
		// calculates the expression field.state
		if (field == null) return null;
		return (AlloyExpression) (new AlloyExpression(field)).join(state);
	}

	/** {@inheritDoc} */
	@Override
	public AlloyExpression getClassExpression(String metaModelID, String className) throws ErrorParser, ErrorUnsupported {
		EMetamodel emeta = MDEManager.getInstance().getMetamodelID(metaModelID);		
		AlloyExpression state = (AlloyExpression) Constants.EMPTY();
		
		// tries to retrieve the state sig of the current model context
		if (currentModel != null) state = getModelExpression(currentModel);
        // otherwise uses the generic metamodel sig
		if (state.equals(Constants.EMPTY())) state = getModelExpression(metaModelID);

		// fetches the corresponding field
		EClass eclass = (EClass) emeta.getEObject().getEClassifier(className);
		Field field = AlloyEchoTranslator.getInstance().getStateFieldFromClass(metaModelID, eclass);

		// calculates the expression statefield.state
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

	/** {@inheritDoc} */
	@Override
	public void setVarModel(String name, String model) throws ErrorParser {
		varModel.put(name,model);
	}

	/** {@inheritDoc} */
	@Override
	public AlloyExpression getModelExpression(String name) {
//		EchoReporter.getInstance().debug("Var: "+name + " at "+ currentPre);
//		EchoReporter.getInstance().debug("But: "+modelPre.keySet() + " and "+ modelPos.keySet());	
		AlloyExpression e = currentPre?modelPreT.get(name):modelPosT.get(name);
		if (e == null) e = currentPre?modelPre.get(name):modelPos.get(name);
		return e;
	}

	/** {@inheritDoc} */
	@Override
	public AlloyExpression addMetamodelExpression(boolean pre, String name, IExpression var) {
		return pre?modelPre.put(name,(AlloyExpression) var):modelPos.put(name,(AlloyExpression) var);
	}

	/** {@inheritDoc} */
	@Override
	public AlloyExpression addParamExpression(boolean pre, String name, IExpression var) {
		return pre?modelPreT.put(name,(AlloyExpression) var):modelPosT.put(name,(AlloyExpression) var);
	}

	/** {@inheritDoc} */
	@Override
	public List<AlloyExpression> getModelExpressions() {
		Collection<AlloyExpression> res = currentPre?modelPreT.values():modelPosT.values();
		if (res == null) res = currentPre?modelPre.values():modelPos.values();
		return new ArrayList<AlloyExpression>(res);
	}

	/** {@inheritDoc} */
	@Override
	public void setCurrentRel(EEngineRelation parentRelation) {
		currentRel = (EAlloyRelation) parentRelation;
	}
	
	/** {@inheritDoc} */
	@Override
	public EAlloyRelation getCallerRel() {
		return currentRel;
	}

}
