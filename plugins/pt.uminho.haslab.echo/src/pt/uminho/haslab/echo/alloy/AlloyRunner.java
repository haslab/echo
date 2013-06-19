package pt.uminho.haslab.echo.alloy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import pt.uminho.haslab.echo.ErrorAlloy;
import pt.uminho.haslab.echo.ErrorUnsupported;
import pt.uminho.haslab.echo.transform.EMF2Alloy;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.ConstList;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorWarning;
import edu.mit.csail.sdg.alloy4compiler.ast.Attr;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.CommandScope;
import edu.mit.csail.sdg.alloy4compiler.ast.Expr;
import edu.mit.csail.sdg.alloy4compiler.ast.ExprConstant;
import edu.mit.csail.sdg.alloy4compiler.ast.Func;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.PrimSig;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;
import edu.mit.csail.sdg.alloy4graph.DotColor;
import edu.mit.csail.sdg.alloy4viz.AlloyModel;
import edu.mit.csail.sdg.alloy4viz.AlloyRelation;
import edu.mit.csail.sdg.alloy4viz.AlloySet;
import edu.mit.csail.sdg.alloy4viz.AlloyType;
import edu.mit.csail.sdg.alloy4viz.VizState;

public class AlloyRunner {
	
	/** the EMF translator, containing information about the EMF artifacts */
	private EMF2Alloy translator;
	/** the Alloy solution */
	private A4Solution sol;
	/** the Alloy command options*/
	private A4Options aoptions;
	/** the Alloy reporter*/
	private A4Reporter rep;
	
	/** the expression representing the delta (must be equaled to the desired delta)*/
	private Expr edelta = Sig.NONE.no();
	/** the final command fact (without the delta expression)*/
	private Expr finalfact = Sig.NONE.no();
	/** all the Alloy signatures of the model*/
	private Set<Sig> allsigs = new HashSet<Sig>(Arrays.asList(EMF2Alloy.STATE));
	
	/** the current delta value*/
	private int delta = 1;	
	/** the current int bitwidth*/
	private int intscope;
	/** the current overall scope */
	private int overall;
	/** the current specific scopes */
	private ConstList<CommandScope> scopes;
	/** the state signature of the target instance */	
	private PrimSig targetstate;
	
	private Command cmd = null;

	/** 
	 * Constructs a new Alloy Runner that performs tests and generates instances
	 * @param translator the translator containing information about the EMF artifacts
	 */
	public AlloyRunner (EMF2Alloy translator) {	
		this.translator = translator;
		rep = new A4Reporter() {
			@Override public void warning(ErrorWarning msg) {
				System.out.print("Relevance Warning:\n"+(msg.toString().trim())+"\n\n");
				System.out.flush();
			}
		};
		aoptions = new A4Options();
		aoptions.solver = A4Options.SatSolver.SAT4J;
		aoptions.noOverflow = true;
		intscope = translator.options.getBitwidth();
		overall = translator.options.getOverallScope();
	}

	/**
	 * Tests the conformity of instances
	 * @param modeluris the URIs if the instances to be checked
	 * @throws ErrorAlloy
	 */
	public void conforms(List<String> modeluris) throws ErrorAlloy {
		for (String modeluri : modeluris) {
			addInstanceSigs(modeluri);
			finalfact = finalfact.and(translator.getConformsInstance(modeluri));
			finalfact = finalfact.and(translator.getModelFact(modeluri));
		}
		
		/*for (Sig sig : allsigs) {
			System.out.println(sig +" : "+((PrimSig)sig).parent);
			for (Field f : sig.getFields())
				System.out.println(f.label +" : "+f.type());
		}*/
		try {
			cmd = new Command(true, overall, intscope, -1, finalfact);
		} catch (Err a) {throw new ErrorAlloy (a.getMessage());}
		try {
			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, aoptions);	
		} catch (Err a) {throw new ErrorAlloy (a.getMessage());}
	}

	/**
	 * Initializes a repair command
	 * @param modeluris the URIs of the instances
	 * @param dirarg the URI of the instance to repair
	 * @throws ErrorAlloy
	 */
	public void repair(String modeluri) throws ErrorAlloy {
		if (translator.options.isOperationBased())
			translator.createScopesFromOps(modeluri);
		else
			translator.createScopesFromURI(modeluri);
		conforms(new ArrayList<String>(Arrays.asList(modeluri)));	
		if (sol.satisfiable()) throw new ErrorAlloy ("Instances already consistent.");
		else {			
			try {
				scopes = translator.getScopes(cmd.getAllStringConstants(allsigs).size());
			} catch (Err e1) {
				throw new ErrorAlloy(e1.getMessage());
			}
			allsigs = new HashSet<Sig>(Arrays.asList(EMF2Alloy.STATE));
			finalfact = Sig.NONE.no();
			PrimSig original;
			List<PrimSig> sigs = new ArrayList<PrimSig>();
			PrimSig state = addInstanceSigs(modeluri);
			original = state;
			try { 
				targetstate = new PrimSig("'"+original.label, original.parent, Attr.ONE); 
			}
			catch (Err e) { throw new ErrorAlloy(e.getMessage()); }
			allsigs.add(targetstate);
			sigs.add(targetstate);
			String metamodeluri = translator.getModelMetamodel(modeluri);
			edelta = translator.getMetamodelDeltaExpr(metamodeluri).call(original, targetstate);
			translator.createScopesFromURI(modeluri);
			finalfact = finalfact.and(translator.getConformsInstance(modeluri, targetstate));
			finalfact = finalfact.and(translator.getModelFact(modeluri));
		} 
	}
	
	/** 
	 * Generates instances conforming to given models
	 * @param uris the URIs of the models
	 * @throws ErrorAlloy 
	 * @throws ErrorUnsupported 
	 */
	public void generate(String metamodeluri, Map<Entry<String,String>,Integer> scope) throws ErrorAlloy, ErrorUnsupported {
		List<EClass> rootobjects = translator.getRootClass(metamodeluri);
		if (rootobjects.size() != 1) throw new ErrorUnsupported("Could not resolve root class: "+rootobjects);

		if (scope.get(rootobjects.get(0).getName()) == null)
			scope.put(new SimpleEntry<String,String>(rootobjects.get(0).getEPackage().eResource().getURI().path(),rootobjects.get(0).getName()),1);
		translator.createScopesFromSizes(translator.options.getOverallScope(), scope, metamodeluri);
		
		allsigs.addAll(translator.getMetamodelSigs(metamodeluri));			
		scopes = translator.getScopes();
		
		PrimSig state = (PrimSig) translator.getMetamodelStateSig(metamodeluri);
		try { 
			targetstate = new PrimSig("'"+state, state, Attr.ONE); 
		} catch (Err e) { throw new ErrorAlloy(e.getMessage()); }

		finalfact = finalfact.and(translator.getGenerateInstance(metamodeluri,targetstate));
		allsigs.add(targetstate);

		try {
			//System.out.println(finalfact);
			//System.out.println(allsigs);
			Command cmd = new Command(true, overall, intscope, -1, finalfact);
			cmd = cmd.change(scopes);
			//System.out.println("DELTA "+delta+", SCOPE " + overall +" " + intscope+" "+ scopes);

			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, aoptions);	
		} catch (Err a) {throw new ErrorAlloy (a.getMessage());}
	}
		
	/** 
	 * Runs a QVT-R checking command
	 * @param qvturi the URI of the QVT-R transformation to be applied
	 * @param insturis the URIs of the instances to be checked
	 * @throws ErrorAlloy 
	 */
	public void check(String qvturi, List<String> insturis) throws ErrorAlloy {
		Func func = translator.getQVTFact(qvturi);
		List<PrimSig> sigs = new ArrayList<PrimSig>();
		for (String uri : insturis) {
			PrimSig state = addInstanceSigs(uri);
			sigs.add(state);
			//System.out.println("Model fact: "+ translator.getModelFact(uri));
			finalfact = finalfact.and(translator.getModelFact(uri));
			finalfact = finalfact.and(translator.getConformsInstance(uri));
		}
		finalfact = finalfact.and(func.call(sigs.toArray(new Expr[sigs.size()])));
		
		//System.out.println("Check: DELTA "+delta+", SCOPE " + overall +" " + intscope+" "+ scopes);
		//System.out.println("Check: SIGS "+allsigs);
		try {
			cmd = new Command(true, 0, intscope, -1, finalfact);
			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, aoptions);	
		} catch (Err a) {throw new ErrorAlloy (a.getMessage());}
	}
	
	/**
	 * Initializes q QVT-R enforcement command
	 * @param qvturi the URI of the QVT-R transformation
	 * @param modeluris the URIs of the instances
	 * @param diruri the URI of the target model
	 * @throws ErrorAlloy
	 */
	public void enforce(String qvturi, List<String> modeluris, String diruri) throws ErrorAlloy {
		if (translator.options.isOperationBased())
			translator.createScopesFromOps(diruri);
		else
			translator.createScopesFromURI(diruri);
		check(qvturi,modeluris);
		if (sol.satisfiable()) throw new ErrorAlloy ("Instances already consistent.");
		else {			
			try {
				scopes = translator.getScopes(cmd.getAllStringConstants(allsigs).size());
			} catch (Err e1) {
				throw new ErrorAlloy(e1.getMessage());
			}
			finalfact = Sig.NONE.no();
			Func func = translator.getQVTFact(qvturi);
			PrimSig original;
			List<PrimSig> sigs = new ArrayList<PrimSig>();
			for (String modeluri : modeluris) {
				PrimSig state = addInstanceSigs(modeluri);
				if (modeluri.equals(diruri)) {
					original = state;
					try { 
						targetstate = new PrimSig("'"+original.label, original.parent, Attr.ONE);
					}
					catch (Err e) { throw new ErrorAlloy(e.getMessage()); }
					allsigs.add(targetstate);
					sigs.add(targetstate);
					String metamodeluri = translator.getModelMetamodel(modeluri);
					edelta = translator.getMetamodelDeltaExpr(metamodeluri).call(original, targetstate);
					finalfact = finalfact.and(translator.getConformsInstance(modeluri, targetstate));
				} else {
					sigs.add(state);			
				}
				//System.out.println("INST "+translator.getModelFact(modeluri));
				finalfact = finalfact.and(translator.getModelFact(modeluri));
			}
			System.out.println(sigs + " for "+ func.decls.get(0).expr + " , "+func.getBody());
			finalfact = finalfact.and(func.call(sigs.toArray(new Expr[sigs.size()])));
		} 
	}
	
	
	public void generateqvt(String qvturi, List<String> insturis, String diruri, String metamodeluri) throws ErrorAlloy, ErrorUnsupported {
		Map<Entry<String,String>,Integer> scope = new HashMap<Entry<String,String>,Integer>();
		
		List<EClass> rootobjects = translator.getRootClass(metamodeluri);
		if (rootobjects.size() != 1) throw new ErrorUnsupported("Could not resolve root class: "+rootobjects);
		scope.put(new SimpleEntry<String,String>(rootobjects.get(0).getEPackage().eResource().getURI().path(),rootobjects.get(0).getName()),1);
		translator.createScopesFromSizes(translator.options.getOverallScope(), scope, metamodeluri);
	
		ArrayList<String> insts = new ArrayList<String>(insturis);
		insts.remove(diruri);
		allsigs.addAll(translator.getMetamodelSigs(metamodeluri));
		scopes = translator.getScopes();
		
		List<PrimSig> sigs = new ArrayList<PrimSig>();

		for (String uri : insturis) {
			if (!uri.equals(diruri)) {
				PrimSig state = addInstanceSigs(uri);
				finalfact = finalfact.and(translator.getModelFact(uri));
				sigs.add(state);			
			} else {
				PrimSig state = (PrimSig) translator.getMetamodelStateSig(metamodeluri);
				try { 
					targetstate = new PrimSig("'"+state, state, Attr.ONE); 
				} catch (Err e) { throw new ErrorAlloy(e.getMessage()); }
				sigs.add(targetstate);
				finalfact = finalfact.and(translator.getGenerateInstance(metamodeluri,targetstate));
				allsigs.add(targetstate);
			}
		}
		Func func = translator.getQVTFact(qvturi);
		finalfact = finalfact.and(func.call(sigs.toArray(new Expr[sigs.size()])));

		try {
			Command cmd = new Command(true, overall, intscope, -1, finalfact);
			cmd = cmd.change(scopes);
			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, aoptions);	
		} catch (Err a) {throw new ErrorAlloy (a.getMessage());}
	}
	
	/**
	 * Increments the scopes and tries to generate an instance
	 * Increments the overall scope if different than zero and the concrete scopes if any
	 * @throws ErrorAlloy
	 */
	public void increment() throws ErrorAlloy {
		//System.out.println(edelta);
		Expr runfact = finalfact;
		if (edelta.isSame(Sig.NONE.no())) {
			scopes = AlloyUtil.incrementStringScopes(scopes);
			overall++;
			if (overall >= translator.options.getMaxDelta()) throw new ErrorAlloy ("Maximum delta reached.");
		}
		else {
			
			try {
				intscope = (int) Math.ceil(1+(Math.log(delta+1) / Math.log(2)));
				if(!translator.options.isOperationBased())
					runfact = finalfact.and(edelta.equal(ExprConstant.makeNUMBER(delta)));
				scopes = translator.incrementScopes(scopes);
			} catch (Err a) {throw new ErrorAlloy (a.getMessage());}
		}
		try {
			Command cmd = new Command(false, overall, intscope, -1, runfact);
			cmd = cmd.change(scopes);
			System.out.println("DELTA "+delta+", SCOPE " + overall +" " + intscope+" "+ scopes);
			for (Sig s : allsigs)
				System.out.println("sig "+s + " : " +((PrimSig) s).children());

			sol = TranslateAlloyToKodkod.execute_command(rep, allsigs, cmd, aoptions);	
			//if (sol.satisfiable()) System.out.println(sol.eval(edelta));
			delta++;
		} catch (Err a) {throw new ErrorAlloy (a.getMessage());}

	}
	
	/** 
	 * Calculates the next Alloy solution.
	 * @throws ErrorAlloy 
	 */
	public void nextInstance() throws ErrorAlloy  {
		try { sol = sol.next(); }
		catch (Err a) {throw new ErrorAlloy (a.getMessage());}
	}
	
	/**
	 * Adds the signatures of an instance to this.allsigs
	 * @param modeluri the URI of the instance
	 * @return the signature representing the instance
	 * @throws ErrorAlloy 
	 */
	private PrimSig addInstanceSigs (String modeluri) throws ErrorAlloy {
		for (List<PrimSig> x : translator.getInstanceSigs(modeluri).values())
			allsigs.addAll(x);
		PrimSig state = translator.getModelStateSig(modeluri);		
		allsigs.add(state);
		allsigs.add(state.parent);
		String metamodeluri = translator.getModelMetamodel(modeluri);
		allsigs.addAll(translator.getMetamodelSigs(metamodeluri));
		//System.out.println("All sigs: "+allsigs);
		return state;
	}
	
	/**
	 * Generates the Alloy theme for a Viz instance
	 * @param vizstate the state where the instance is stored
	 */
	public void generateTheme(VizState vizstate) {
		//vizstate = new VizState(vizstate.getOriginalInstance());
		List<DotColor> availablecolors = new ArrayList<DotColor>();
		availablecolors.add(DotColor.GRAY);
		availablecolors.add(DotColor.GREEN);
		availablecolors.add(DotColor.BLUE);
		availablecolors.add(DotColor.RED);
		availablecolors.add(DotColor.YELLOW);
		AlloyModel model = vizstate.getCurrentModel();
		vizstate.setFontSize(11);
		int i = 0;
		for (AlloyType t : model.getTypes()){
			AlloyType aux = model.getSuperType(t);
			String label = vizstate.label.get(t);
			if (aux != null && model.getSuperType(aux) != null && model.getSuperType(aux).equals(AlloyUtil.STATESIGNAME)) {}
			else if (AlloyUtil.mayBeClassOrFeature(label)) {
				vizstate.hideUnconnected.put(t, true);
				String metamodeluri = AlloyUtil.getMetamodelURIfromLabel(label);
				List<EClass> rootobjects = translator.getRootClass(metamodeluri);
				for (EClass rootobject : rootobjects)
					if (rootobject.getName().equals(AlloyUtil.getClassOrFeatureName(label)))
						vizstate.hideUnconnected.put(t, false);
				vizstate.label.put(t, AlloyUtil.getClassOrFeatureName(label));
				vizstate.nodeColor.put(t, availablecolors.get(i));
			} else if (AlloyUtil.mayBeStateOrLiteral(label)) {
				if (t.getName().equals(AlloyUtil.STATESIGNAME)) {
					vizstate.project(t);
				}
				if (t.getName().equals(AlloyUtil.STRINGNAME) || t.getName().equals(AlloyUtil.INTNAME) || t.getName().startsWith(AlloyUtil.ORDNAME))
					vizstate.nodeVisible.put(t, false);
			}	
			if (++i >= availablecolors.size()) i = 0;
		}
		for (AlloySet t : vizstate.getCurrentModel().getSets()){
			String label = vizstate.label.get(t);
			if (AlloyUtil.mayBeClassOrFeature(label)) {
				vizstate.label.put(t, AlloyUtil.getClassOrFeatureName(label));
				if (AlloyUtil.isStateField(label)) vizstate.showAsLabel.put(t, false);
			}
		}
		for (AlloyRelation t : vizstate.getCurrentModel().getRelations()){
			String label = vizstate.label.get(t);
			if (AlloyUtil.mayBeClassOrFeature(label)) {
				String metamodeluri = AlloyUtil.getMetamodelURIfromLabel(label);
				String ref = AlloyUtil.getClassOrFeatureName(label);
				AlloyType sig = t.getTypes().get(0);
				String cla = AlloyUtil.getClassOrFeatureName(sig.getName());
				EStructuralFeature sf = translator.getESFeatureFromName(metamodeluri,cla,ref);
				if (sf != null) {
					if (sf instanceof EAttribute) {
						vizstate.edgeVisible.put(t, false);
						vizstate.attribute.put(t, true);
					} else if (sf instanceof EReference && !((EReference) sf).isContainment())
						vizstate.layoutBack.put(t, true);
					vizstate.label.put(t, ref);
				} else {
					vizstate.edgeVisible.put(t, false);
					vizstate.attribute.put(t, false);
				}
			}
			
		}
	}
	
	/** 
	 * Returns the Alloy solution.
	 * @return this.sol
	 */
	public A4Solution getSolution() {
		return sol;
	}

	/** 
	 * Returns the current delta value.
	 * @return this.delta
	 */
	public int getDelta() {
		return delta;
	}
	
	/** 
	 * Returns the current int bitwidth.
	 * @return this.instscope
	 */
	public int getIntScope() {
		return intscope;
	}	
	
	/** 
	 * Returns the final command fact (instance + qvt).
	 * @return this.fact
	 */
	public Expr getFact() {
		return finalfact;
	}	
	
	/** 
	 * Returns the current target scopes.
	 * @return this.targetscopes
	 */
	public ConstList<CommandScope> getScopes() {
		return scopes;
	}	
	
	/** 
	 * Returns the state signature of the target instance.
	 * @return this.targetstate
	 */
	public PrimSig getTargetStateSig(){
		return targetstate;
	}
}

