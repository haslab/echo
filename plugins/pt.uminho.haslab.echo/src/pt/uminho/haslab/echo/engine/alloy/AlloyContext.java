package pt.uminho.haslab.echo.engine.alloy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.EchoRunner.Task;
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
 * @version 0.4 17/02/2014
 */
public class AlloyContext implements ITContext {

	private Map<String,AlloyExpression> varExp = new HashMap<String,AlloyExpression>();
	private Map<String,String> varModel = new HashMap<String,String>();
	private Map<String,AlloyExpression> modelPre = new HashMap<String,AlloyExpression>();
	private Map<String,AlloyExpression> modelPos = new HashMap<String,AlloyExpression>();
	private Map<String,AlloyExpression> modelPreT = new HashMap<String,AlloyExpression>();
	private Map<String,AlloyExpression> modelPosT = new HashMap<String,AlloyExpression>();
	
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
		varExp.put(decl.name(), (AlloyExpression) decl.variable());
	}

	/** {@inheritDoc} */
	@Override
	public void addVar(IDecl decl, String extra) {
		varModel.put(decl.name(),extra);
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
	public IDecl getDecl(EVariable var) throws EchoError {
		// gets the type of the variable
		EClass type = var.getType();
	
		try {
			// calculates the expression representing the type in the state
			Expr range = Sig.NONE;
			if (type.equals("String"))
				range = Sig.STRING;
			else if (type.equals("Int"))
				range = Sig.SIGINT;
			else {
				EMetamodel metamodel = MDEManager.getInstance().getMetamodel(EcoreUtil.getURI(type.getEPackage()).path(), false);
				// if owning model was set, retrieve it
				if (getVarModel(var.getName()) != null) {
					// TODO if already exists should be used to create class expression
					EchoReporter.getInstance().warning("Creating var that already exists: "+var.getName(),Task.TRANSLATE_METAMODEL);
//					String varModel = getVarModel(var.getName());
//					state = getModelExpression(varModel).EXPR;
				}
				range = getClassExpression(metamodel.ID, type.getName()).EXPR;
			}
			
			EchoReporter.getInstance().debug("Created "+var.getName()+"::"+range);
			
			AlloyDecl d = new AlloyDecl(range.oneOf(var.getName()));
			addVar(d);
			return d;

		} catch (Err a) {
			throw new ErrorAlloy(a.getMessage());
		}
	}

	/** {@inheritDoc} */
	@Override
	public AlloyExpression getPropExpression(String metaModelID, String className, String fieldName) {
		EchoReporter.getInstance().debug("** getPropExpression: "+metaModelID+ ", " + className + ", "+fieldName + " with "+currentModel+ " so "+getModelExpression(currentModel));

		EAlloyMetamodel ameta = AlloyEchoTranslator.getInstance().getMetamodel(metaModelID);
        Expr statesig = null;
        if (currentModel != null)
			statesig = getModelExpression(currentModel).EXPR;
		
		if (statesig == null)
			statesig = getModelExpression(metaModelID).EXPR;		

		EClass eclass = ((EClass) ameta.metamodel.getEObject().getEClassifier(className));
		EStructuralFeature feature = eclass.getEStructuralFeature(fieldName);
		Field field = AlloyEchoTranslator.getInstance().getFieldFromFeature(metaModelID,feature);
		EchoReporter.getInstance().debug("** getPropExpression result: "+field + " . " + statesig);
		if (field == null) return null;
		return new AlloyExpression(field.join(statesig));
	}

	/** {@inheritDoc} */
	@Override
	public AlloyExpression getClassExpression(String metaModelID, String className) throws ErrorParser, ErrorUnsupported {
		EMetamodel emeta = MDEManager.getInstance().getMetamodelID(metaModelID);
		EClass eclass = (EClass) emeta.getEObject().getEClassifier(className);
		
		Field field = AlloyEchoTranslator.getInstance().getStateFieldFromClass(metaModelID, eclass);
		
		AlloyExpression state = (AlloyExpression) Constants.EMPTY();
		
		if (currentModel != null) state = getModelExpression(currentModel);
		else state = getModelExpression(metaModelID);

//		EchoReporter.getInstance().debug("** SDebug result: "+metaModelID+" and "+ className +" did "+field.join(state.EXPR));
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
	public List<IExpression> getModelExpressions() {
		Collection<AlloyExpression> res = currentPre?modelPreT.values():modelPosT.values();
		if (res == null) res = currentPre?modelPre.values():modelPos.values();
		return new ArrayList<IExpression>(res);
	}

	public void setCurrentRel(EEngineRelation parentRelation) {
		currentRel = (EAlloyRelation) parentRelation;
	}
	
	@Override
	public EAlloyRelation getCallerRel() {
		return currentRel;
	}

}
