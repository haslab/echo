package pt.uminho.haslab.echo.consistency.qvt;

import org.eclipse.qvtd.pivot.qvtbase.Rule;
import org.eclipse.qvtd.pivot.qvtbase.TypedModel;
import org.eclipse.qvtd.pivot.qvtrelation.RelationalTransformation;
import pt.uminho.haslab.echo.ErrorParser;
import pt.uminho.haslab.echo.consistency.Model;
import pt.uminho.haslab.echo.consistency.Relation;
import pt.uminho.haslab.echo.consistency.Transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class QVTTransformation implements Transformation {

	private static Map<RelationalTransformation,QVTTransformation> list = new HashMap<RelationalTransformation,QVTTransformation>();

	private List<Model> models = new ArrayList<Model>();
	private List<Relation> relations = new ArrayList<Relation>();
	private RelationalTransformation transformation;
	
	public QVTTransformation(org.eclipse.qvtd.pivot.qvtbase.Transformation transformation) throws ErrorParser {
		this.transformation = (RelationalTransformation) transformation;
		for (TypedModel mdl : transformation.getModelParameter()){
			models.add(new QVTModel(mdl));}
		

		for (Rule rule : transformation.getRule())
			relations.add(new QVTRelation(rule));

		list.put(this.transformation, this);
	}

	@Override
	public List<Model> getModels() {

		return models;
	}

	@Override
	public List<Relation> getRelations() {
		return relations;
	}

	@Override
	public String getName() {
		return transformation.getName();
	}
	
	public static QVTTransformation get(RelationalTransformation t) {
		return list.get(t);
	}
	
	

}
