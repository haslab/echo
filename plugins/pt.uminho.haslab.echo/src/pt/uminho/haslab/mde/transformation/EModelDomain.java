package pt.uminho.haslab.mde.transformation;

import pt.uminho.haslab.echo.EError;
import pt.uminho.haslab.echo.EErrorParser;
import pt.uminho.haslab.echo.EErrorUnsupported;
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
	 * @throws EErrorParser 
	 * @throws EErrorUnsupported 
	 * @throws EError */
	abstract public EModelParameter getModel() throws EErrorUnsupported, EErrorParser, EError;
	
	/** the root variable of the domain */
	abstract public EVariable getRootVariable();
	
	/** the condition restricting the root variable */
	abstract public EPredicate getCondition();

	@Override
	public String toString() {
		try {
			EModelParameter model = getModel();
			return model.getName() + "::" + model.getMetamodel().ID;
		} catch (EError e) {
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
		} catch (EError e) {
			return false;
		}
	}

}
