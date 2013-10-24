package pt.uminho.haslab.echo.consistency;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pt.uminho.haslab.echo.alloy.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorTransform;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.transform.alloy.Relation2Alloy;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprHasName;

public interface Condition {

	public void addCondition(Object expr);
	public List<Object> getConditions();
	public void initTranslation(Relation2Alloy q2a, Map<String,Entry<ExprHasName,String>> vardecls, Map<String,ExprHasName> argsvars, Map<String,ExprHasName> prevars);	
	public void initTranslation(Map<String,Entry<ExprHasName,String>> vardecls, Map<String,ExprHasName> argsvars, Map<String,ExprHasName> prevars);
	public Expr translate() throws ErrorTransform, ErrorAlloy, ErrorUnsupported;
	public Map<Variable,String> getVariables(String metamodel) throws ErrorUnsupported, ErrorTransform;
	

}
