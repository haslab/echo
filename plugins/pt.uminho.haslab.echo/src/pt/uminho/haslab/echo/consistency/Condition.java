package pt.uminho.haslab.echo.consistency;

import edu.mit.csail.sdg.alloy4compiler.ast.ExprHasName;
import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.transform.alloy.Relation2Alloy;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public interface Condition {

	public void addCondition(Object expr);
	public List<Object> getConditions();
	public void initTranslation(Relation2Alloy q2a, Map<String,Entry<ExprHasName,String>> vardecls, Map<String,ExprHasName> argsvars, Map<String,ExprHasName> prevars);	
	public void initTranslation(Map<String,Entry<ExprHasName,String>> vardecls, Map<String,ExprHasName> argsvars, Map<String,ExprHasName> prevars);
	public Object translate() throws EchoError;
	public Map<Variable,String> getVariables(String metamodel) throws EchoError;
	

}
