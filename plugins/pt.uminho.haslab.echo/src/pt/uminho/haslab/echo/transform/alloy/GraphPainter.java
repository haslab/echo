package pt.uminho.haslab.echo.transform.alloy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import edu.mit.csail.sdg.alloy4graph.DotColor;
import edu.mit.csail.sdg.alloy4graph.DotShape;
import edu.mit.csail.sdg.alloy4viz.AlloyModel;
import edu.mit.csail.sdg.alloy4viz.AlloyRelation;
import edu.mit.csail.sdg.alloy4viz.AlloySet;
import edu.mit.csail.sdg.alloy4viz.AlloyType;
import edu.mit.csail.sdg.alloy4viz.VizState;

public class GraphPainter {

	private VizState vizstate;
	public GraphPainter (VizState vizstate) {
		this.vizstate = vizstate;
	}
	
	
	/**
	 * Generates the Alloy theme for a Viz instance
	 */
	public void generateTheme() {
		//vizstate = new VizState(vizstate.getOriginalInstance());
		List<DotShape> availableshapes = new ArrayList<DotShape>();
		availableshapes.add(DotShape.BOX);
		availableshapes.add(DotShape.ELLIPSE);
		availableshapes.add(DotShape.TRAPEZOID);
		availableshapes.add(DotShape.HOUSE);
		availableshapes.add(DotShape.PARALLELOGRAM);
		availableshapes.add(DotShape.HEXAGON);
		availableshapes.add(DotShape.INV_HOUSE);
		availableshapes.add(DotShape.INV_TRAPEZOID);		
		int i = 0;
		AlloyModel model = vizstate.getOriginalModel();
	
		//vizstate.resetTheme();
		vizstate.setFontSize(11);

		AlloyType statet = null;
		for (AlloyType atype : model.getTypes()){

			AlloyType superatype = model.getSuperType(atype);
			vizstate.nodeVisible.put(atype, false);
			String label = atype.toString();

			if (superatype != null) {
				if (superatype.toString().equals(AlloyUtil.STATESIGNAME))
					vizstate.label.put(atype,AlloyUtil.getMetaModelName(label));
				else if (model.getSuperType(superatype) != null && model.getSuperType(superatype).getName().equals(AlloyUtil.STATESIGNAME)) {
					vizstate.label.put(atype,AlloyUtil.getModelName(label));
				}
				else if (atype.getName().equals(AlloyUtil.STATESIGNAME)) {
					statet = atype;
				}
				else if (atype.getName().equals(AlloyUtil.STRINGNAME) || atype.getName().equals(AlloyUtil.INTNAME) || atype.getName().startsWith(AlloyUtil.ORDNAME)) {
					vizstate.label.put(atype, label.replace("\"", ""));
				}
				else if (!AlloyUtil.isElement(label)){
					String metamodeluri = AlloyUtil.getMetamodelURIfromLabel(label);
					String classname = AlloyUtil.getClassifierName(label);
					EClassifier eclass = (EClassifier) AlloyEchoTranslator.getInstance().getEClassifierFromName(metamodeluri, classname);					
					if (classname != null && AlloyEchoTranslator.getInstance().getSigFromClass(metamodeluri, eclass) != null) {
						vizstate.label.put(atype, classname);
						vizstate.nodeColor.put(atype, DotColor.GRAY);
						vizstate.shape.put(atype, availableshapes.get(i));
						if (++i >= availableshapes.size()) i = 0;
					}
				}
				else if (AlloyUtil.isElement(label)){
					String classname = AlloyUtil.getClassifierName(label);
					vizstate.label.put(atype, classname);
					vizstate.nodeColor.put(atype, null);
					vizstate.shape.put(atype, null);				
				} else {}
			}


		}
		if (statet != null) vizstate.project(statet);

		for (AlloySet t : vizstate.getCurrentModel().getSets()){
			String label = vizstate.label.get(t);
			
			if(t.getType().getName().startsWith(AlloyUtil.ORDNAME)) 
				vizstate.nodeVisible.put(t, false);
			else
				vizstate.nodeVisible.put(t, true);
			
			
			if (AlloyUtil.mayBeFeature(label)) {
				vizstate.label.put(t, AlloyUtil.getFeatureName(label));
			}

			if (AlloyUtil.isStateField(label)) {
				vizstate.label.put(t, AlloyUtil.getClassifierName(label));
				vizstate.showAsLabel.put(t, false);
			}

			if (label.equals(AlloyUtil.NEWSNAME)) {
				vizstate.nodeColor.put(t, DotColor.GREEN);
				vizstate.showAsLabel.put(t, false);
				vizstate.nodeVisible.put(t, false);
			}
		}
		for (AlloyRelation t : vizstate.getCurrentModel().getRelations()){
			String label = vizstate.label.get(t);
			if (AlloyUtil.mayBeFeature(label)) {
				String metamodeluri = AlloyUtil.getMetamodelURIfromLabel(label);
				String ref = AlloyUtil.getFeatureName(label);
				AlloyType sig = t.getTypes().get(0);
				String cla = AlloyUtil.getClassifierName(sig.getName());
				EStructuralFeature sf = AlloyEchoTranslator.getInstance().getESFeatureFromName(metamodeluri,cla,ref);
				if (sf != null) {
					if (sf instanceof EAttribute) {
						vizstate.edgeVisible.put(t, false);
						vizstate.attribute.put(t, true);
					} else if (sf instanceof EReference) {
						vizstate.edgeVisible.put(t, true);
						vizstate.attribute.put(t, false);
						if (((EReference) sf).isContainment()) 
							vizstate.constraint.put(t, true);
						else
							vizstate.constraint.put(t, false);
					}
					vizstate.label.put(t, ref);
				} else {
					vizstate.edgeVisible.put(t, false);
					vizstate.attribute.put(t, false);
				}
			}
			
		}
	}
	
}
