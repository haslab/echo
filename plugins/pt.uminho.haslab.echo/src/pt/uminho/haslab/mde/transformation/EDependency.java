package pt.uminho.haslab.mde.transformation;

import java.util.List;

import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.mde.transformation.EConstraintManager.EConstraint;

/**
 * Denotes the dependencies between the domain models of a relation is a particular constraint instantiation
 * Consists of a target model domain depending on a set of target model domains ([sources] -> target)
 * @author nmm
 *
 */
public class EDependency {

	/** the parent constraint (all domains must belong to a relation contained in it) */
	public final EConstraint constraint;
	/** the source model domains (all domains must belong to the same relation) */
	private List<EModelDomain> sources;
	/** the target model domain (all domains must belong to the same relation) */
	public final EModelDomain target;
	
	/**
	 * Creates a new dependency between source and target models
	 * Owning relation should be the same for all
	 * @param target
	 * @param sources
	 * @throws ErrorParser 
	 */
	public EDependency(EModelDomain target, List<EModelDomain> sources, EConstraint constraint) throws ErrorParser {
		//if (!constraint.transformation.getRelations().contains(target.getRelation())) 
		//	throw new ErrorParser(ErrorParser.CONSTRAINT,"Error creating dependency.","Constraint relation does not mais model domains.", Task.TRANSLATE_TRANSFORMATION); 
		this.constraint = constraint;
		this.target = target;
		this.sources = sources;
	}

	public List<EModelDomain> getSources() {
		return sources;
	}
	
	public String toString() {
		return sources.toString() + " -> " + target.toString();
	}
}
