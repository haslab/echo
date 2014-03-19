package pt.uminho.haslab.mde.model;

import org.eclipse.emf.ecore.EObject;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.engine.IContext;
import pt.uminho.haslab.echo.engine.ITContext;
import pt.uminho.haslab.echo.engine.ast.IFormula;

import java.util.List;
import java.util.Map;

/**
 * Echo representation of an EMF condition
 * 
 * @author nmm
 * @version 0.4 15/02/2014
 */
public interface EPredicate {

	public void addCondition(EObject expr);
	public List<? extends EObject> getConditions();
	public IFormula translate(ITContext context) throws EchoError;
	public Map<EVariable, String> getVariables(String metamodel) throws EchoError;
	
}
