package pt.uminho.haslab.echo.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.EchoReporter;
import pt.uminho.haslab.echo.ErrorInternalEngine;
import pt.uminho.haslab.echo.EchoRunner.Task;
import pt.uminho.haslab.echo.transform.alloy.ErrorAlloy;
import pt.uminho.haslab.echo.transform.ast.IDecl;
import pt.uminho.haslab.echo.transform.ast.IExpression;
import pt.uminho.haslab.echo.transform.ast.IFormula;
import pt.uminho.haslab.mde.transformation.EDependency;
import pt.uminho.haslab.mde.transformation.ERelation;
import pt.uminho.haslab.mde.transformation.ETransformation;


public abstract class EEngineTransformation {

	/** the transformation being translated */
	public final ETransformation transformation;
	/** 
	 * Constructs a new QVT Transformation to Alloy translator.
	 * A {@code QVTRelation2Alloy} is called for every top QVT Relation and direction.
	 * @param transformation the QVT Transformation being translated
	 * @throws EchoError
	 */
	protected EEngineTransformation (ETransformation transformation, Map<String,List<EDependency>> dependencies) throws EchoError {
		EchoReporter.getInstance().start(Task.TRANSLATE_TRANSFORMATION, transformation.getName());
		this.transformation = transformation;
		List<IDecl> model_params_decls = new ArrayList<IDecl>();
		List<IExpression> model_params_vars = new ArrayList<IExpression>();

		createParams(model_params_decls,model_params_vars);
		

		for (ERelation rel : transformation.getRelations())
			if (rel.isTop()) {
				for (EDependency dep : dependencies.get(rel.getName()))
					createRelation(dep,rel);
			}
		
		generateConstraints(model_params_decls,model_params_vars);
		EchoReporter.getInstance().result(Task.TRANSLATE_TRANSFORMATION, "", true);

	}
	
	protected abstract void createRelation(EDependency dep, ERelation rel) throws EchoError;

	protected abstract void createParams(List<IDecl> model_params_decls,
			List<IExpression> model_params_vars) throws ErrorInternalEngine;

	protected abstract void generateConstraints(List<IDecl> model_params_decls,
			List<IExpression> model_params_vars) throws ErrorInternalEngine;
	
	/**
	 * Returns the Alloy function corresponding to this QVT Transformation
	 * Function parameters are the model variables
	 * @return this.fact
	 */	
	protected abstract IFormula getTransformationConstraint(List<String> modelIDs);

	/** 
	 * Adds a new sub-relation definition
	 * Function parameters are the model variables
	 * called by containing relations
	 * @param e 
	 * @param model_params_decls 
	 * @param field 
	 * @param f the function definition
	 * @throws ErrorAlloy 
	 */
	protected abstract void addSubRelationDef(EEngineRelation eAlloyRelation,
			List<IDecl> model_params_decls, IFormula e) throws ErrorInternalEngine;

	/** 
	 * Adds a new sub-relation call function
	 * Function parameters are the model variables and the domain variables
	 * called by containing relations
	 * @param field 
	 * @param expr 
	 * @param model_params_decls 
	 * @param eAlloyRelation 
	 * @param x the function definition
	 * @throws ErrorAlloy 
	 */
	public abstract void addSubRelationCall(EEngineRelation eAlloyRelation,
			List<IDecl> model_params_decls, IExpression exp) throws ErrorInternalEngine;
	
	
	
	/** 
	 * Adds a new top-relation call function
	 * Function parameters are the model variables
	 * called by containing relations
	 * @param fact 
	 * @param model_params_decls 
	 * @param arelation 
	 * @param x the function definition
	 * @throws ErrorAlloy 
	 */
	protected abstract void addTopRelationCall(EEngineRelation arelation,
			List<IDecl> model_params_decls, IFormula fact) throws ErrorInternalEngine;

	/** 
	 * Returns the function call of a sub-relation
	 * @param n the relation being called
	 * @param dep
	 * @param aux 
	 * @return the respective Alloy function
	 */
	public abstract IFormula callRelation(ERelation n, EDependency dep, List<IExpression> aux);

}
