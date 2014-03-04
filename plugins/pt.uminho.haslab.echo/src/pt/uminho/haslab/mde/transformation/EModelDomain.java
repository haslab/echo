package pt.uminho.haslab.mde.transformation;

import pt.uminho.haslab.echo.EchoError;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.mde.model.EPredicate;
import pt.uminho.haslab.mde.model.EVariable;

/**
 * Embeds a model domain of an EMF relation in Echo.
 * Consists of a root variable from a model restricted by a condition.
 * Should be extended by concrete EMF transformations (QVT-R, ATL).
 *
 * @author nmm
 * @version 0.4 14/02/2014
 */
public abstract class EModelDomain {

	/** the parent consistency relation */
	abstract public ERelation getRelation();
	
	/** the model parameter regarding this model domain 
	 * @throws ErrorParser 
	 * @throws ErrorUnsupported 
	 * @throws EchoError */
	abstract public EModelParameter getModel() throws ErrorUnsupported, ErrorParser, EchoError;
	
	/** the root variable of the domain */
	abstract public EVariable getRootVariable();
	
	/** the condition restricting the root variable */
	abstract public EPredicate getCondition();

	@Override
	public String toString() {
		try {
			EModelParameter model = getModel();
			return model.getName() + "::" + model.getMetamodel().ID;
		} catch (EchoError e) {
			return "Err";
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EModelDomain)) return false;
		try {
			EModelParameter model = getModel();
			EModelDomain in = (EModelDomain) obj;
			return model.equals(in.getModel());
		} catch (EchoError e) {
			return false;
		}
	}

}
