package pt.uminho.haslab.mde.transformation;

import pt.uminho.haslab.echo.EErrorParser;
import pt.uminho.haslab.mde.transformation.EConstraintManager.EConstraint;

import java.util.List;

/**
 * Denotes a dependency between the domain models of a relation is a particular constraint instantiation.
 * Consists of a target model domain depending on a set of target model domains ([sources] -> target).
 *
 * @author nmm
 * @version 0.4 14/02/2014
 */
public class EDependency {

	/** the parent constraint (all domains must belong to a relation contained in it) */
	public final EConstraint constraint;
	/** the source model domains (all domains must belong to the same relation) */
	private List<EModelDomain> sources;
	/** the target model domain (all domains must belong to the same relation) */
	public final EModelDomain target;

	/**
	 * Creates a new dependency between source and target models.
	 * Owning relation should be the same for all.
	 * @param target
	 * @param sources
	 * @throws EErrorParser
	 */
	public EDependency(EModelDomain target, List<EModelDomain> sources, EConstraint constraint) throws EErrorParser {
		//if (!constraint.transformation.getRelations().contains(target.getRelation()))
		//	throw new ErrorParser(ErrorParser.CONSTRAINT,"Error creating dependency.","Constraint relation does not mais model domains.", Task.TRANSLATE_TRANSFORMATION);
		this.constraint = constraint;
		this.target = target;
		this.sources = sources;
	}

	/** 
	 * Returns the source domains of the dependency.
	 * @return the source domains
	 */
	public List<EModelDomain> getSources() {
		return sources;
	}

	@Override
	public String toString() {
		return sources.toString() + " -> " + target.toString();
	}
}
