package pt.uminho.haslab.echo.engine.kodkod;

import kodkod.ast.Relation;
import kodkod.instance.Bounds;
import kodkod.instance.Tuple;
import kodkod.instance.TupleSet;
import kodkod.instance.Universe;
import kodkod.util.ints.IndexedEntry;

import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EStructuralFeature;

import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.engine.EchoHelper;
import pt.uminho.haslab.echo.util.Pair;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author tmg,nmm
 * @version 0.4 23/03/2015
 */
class TargetBinder extends AbstractBinder implements Binder {

	private Set<String> freshatoms;

	TargetBinder(KodkodModel model, Map<String, Integer> scope) {
		// create the integer atoms
		Set<Object> uni = numberCollection();
		// create fresh atoms from the maximum delta
		createFreshAtoms();
		uni.addAll(freshatoms);
		// adds atoms from the existing models
		uni.addAll(model.getUniverse());
		// add default string atoms from the overall scope
		uni.addAll(stringCollection());

		universe = new Universe(uni);
		bounds = new Bounds(universe);
		factory = universe.factory();
		bindInts();
		Set<KodkodModel> models = new HashSet<KodkodModel>();
		models.add(model);
		bindStrings(models);

		Map<Relation, Set<Object>> map = model.getBounds();

		for (Relation rel : model.getMetamodel().getClassRelations())
			bindClassRelation(rel, map.get(rel), scope.get(EchoHelper.getClassifierName(rel.toString())));
		
		for (Relation rel : model.getMetamodel().getSfRelations())
			bindSfRelation(rel, map.get(rel), model.getMetamodel());
		
		//TODO: atoms from exact scopes are no longer fresh
		//TODO: enum bounds
	}

	TargetBinder(Set<KodkodModel> models, Set<KodkodModel> targets,
			Map<Relation, Pair<Set<Relation>, Set<Relation>>> extraRels, Map<String,Map<String,Integer>> scope) {
		// create the integer atoms
		Set<Object> uni = numberCollection();
		// create fresh atoms from the maximum delta
		createFreshAtoms();
		uni.addAll(freshatoms);
		// adds atoms from the existing models
		for (KodkodModel model : models)
			uni.addAll(model.getUniverse());
		// add default string atoms from the overall scope
		uni.addAll(stringCollection());

		universe = new Universe(uni);
		bounds = new Bounds(universe);
		factory = universe.factory();
		bindInts();
		makeStringBounds(models);

		models.removeAll(targets);
		for (KodkodModel model : models)
			makeExactlyBounds(model);

		for (KodkodModel model : targets) {
			Map<Relation, Set<Object>> map = model.getBounds();

			for (Relation rel : model.getMetamodel().getClassRelations()) {
				Map<String,Integer> scope_m = scope.get(model.getMetamodel().metamodel.ID);
				bindClassRelation(rel, map.get(rel), scope_m==null?null:scope_m.get(EchoHelper.getClassifierName(rel.toString())));
			}
			
			for (Relation rel : model.getMetamodel().getSfRelations())
				bindSfRelation(rel, map.get(rel), model.getMetamodel());
		}

		for (Relation r : extraRels.keySet()) {

			TupleSet leftTuples = factory.noneOf(1);
			for (Relation relation : extraRels.get(r).left)
				leftTuples.addAll(bounds.upperBound(relation));

			TupleSet rightTuples = factory.noneOf(1);
			for (Relation relation : extraRels.get(r).right)
				rightTuples.addAll(bounds.upperBound(relation));

			bounds.bound(r, leftTuples.product(rightTuples));
		}
	}

	/**
	 * Binds the upper bound and the target of the provided relation representing a structural feature.
	 * @param rel
	 * @param target
	 * @param metamodel
	 */
	private void bindSfRelation(Relation rel, Set<Object> target,
			KodkodMetamodel metamodel) {

		// sets the upper bound of rel
		if (rel.arity() == 2) { // binary relations
			Pair<Set<Relation>, Set<Relation>> type = metamodel.getRefTypes(rel);
			EStructuralFeature sf = metamodel.getSf(rel);
			if (type != null) { // non primitive
				TupleSet leftTuples = factory.noneOf(1);
				for (Relation relation : type.left)
					leftTuples.addAll(bounds.upperBound(relation));

				TupleSet rightTuples = factory.noneOf(1);
				for (Relation relation : type.right)
					rightTuples.addAll(bounds.upperBound(relation));

				bounds.bound(rel, leftTuples.product(rightTuples));
			} else if (sf.getEType().getName().equals("EInt")) {
				Set<Relation> newType = metamodel.getType(rel);
				TupleSet leftTuples = factory.noneOf(1);
				for (Relation relation : newType)
					leftTuples.addAll(bounds.upperBound(relation));

				TupleSet rightTuples = factory.noneOf(1);
				for (IndexedEntry<TupleSet> ts : bounds.intBounds()) {
					rightTuples.addAll(ts.value());
				}

				bounds.bound(rel, leftTuples.product(rightTuples));
			} else if (sf.getEType().getName().equals("EString")) {
				Set<Relation> newType = metamodel.getType(rel);
				TupleSet leftTuples = factory.noneOf(1);
				for (Relation relation : newType)
					leftTuples.addAll(bounds.upperBound(relation));

				TupleSet rightTuples = factory.noneOf(1);
				rightTuples.addAll(bounds.upperBound(KodkodUtil.stringRel));
				bounds.bound(rel, leftTuples.product(rightTuples));
			} else { // EENUM
				Set<Relation> newType = metamodel.getType(rel);
				TupleSet leftTuples = factory.noneOf(1);
				for (Relation relation : newType)
					leftTuples.addAll(bounds.upperBound(relation));

				EEnum eenum = (EEnum) sf.getEType();
				Relation r = metamodel.getRelation(eenum);
				TupleSet rightTuples = bounds.upperBound(r);
				bounds.bound(rel, leftTuples.product(rightTuples));
			}
		} else { // sets
			Set<Relation> type = metamodel.getBoolType(rel);
			TupleSet tuples = factory.noneOf(1);
			for (Relation relation : type) {
				tuples.addAll(bounds.upperBound(relation));
			}
			bounds.bound(rel, tuples);
		}

		// set the target of rel
		Set<Tuple> targets = new HashSet<>();
		for (Object obj : target) {
			if (obj instanceof Pair) {
				Pair<?, ?> p = (Pair<?, ?>) obj;
				targets.add(factory.tuple(p.left, p.right));
			} else
				targets.add(factory.tuple(obj));
		}

		if (!targets.isEmpty())
			bounds.setTarget(rel, factory.setOf(targets));
		else
			bounds.setTarget(rel, factory.noneOf(rel.arity()));
	}
	
	/**
	 * Binds the upper bound and the target of the provided relation representing a class.
	 * @param rel
	 * @param target
	 * @param scope
	 */
	private void bindClassRelation(Relation rel, Set<Object> target, Integer scope) {
		Set<Tuple> targets = new HashSet<>();
		Set<Tuple> total = new HashSet<>();

		for (Object obj : target) {
			targets.add(factory.tuple(obj));
			total.add(factory.tuple(obj));
		}

		// if no exact scope, add fresh variables
		if (scope == null) {
			for (String s : freshatoms)
				total.add(factory.tuple(s));
			bounds.bound(rel, factory.setOf(total));
			if (!targets.isEmpty())
				bounds.setTarget(rel, factory.setOf(targets));
			else
				bounds.setTarget(rel, factory.noneOf(rel.arity()));
		} else {
			Iterator<String> it = freshatoms.iterator();
			while (total.size() < scope)
				total.add(factory.tuple(it.next()));
			bounds.boundExactly(rel, factory.setOf(total));
		}

	}

	/**
	 * Creates fresh atoms that may be used. As many as the maximum delta
	 * allowed.
	 */
	private void createFreshAtoms() {
		freshatoms = new HashSet<>();
		int max = EchoOptionsSetup.getInstance().getMaxDelta();
		for (int i = 0; i < max; i++)
			freshatoms.add("new" + i);
	}

	private void makeExactlyBounds(KodkodModel model) {
		Map<Relation, Set<Object>> map = model.getBounds();

		for (Relation rel : map.keySet()) {
			Set<Tuple> tuples = new HashSet<>();
			for (Object obj : map.get(rel)) {
				if (obj instanceof Pair) {
					Pair<?, ?> p = (Pair<?, ?>) obj;
					tuples.add(factory.tuple(p.left, p.right));
				} else
					tuples.add(factory.tuple(obj));
			}
			if (!tuples.isEmpty())
				bounds.boundExactly(rel, factory.setOf(tuples));
			else
				bounds.boundExactly(rel, factory.noneOf(rel.arity()));
		}
	}
}
