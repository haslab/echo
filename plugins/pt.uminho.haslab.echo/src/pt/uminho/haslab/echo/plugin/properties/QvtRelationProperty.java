package pt.uminho.haslab.echo.plugin.properties;

import java.util.ArrayList;
import java.util.List;

public class QvtRelationProperty {
	
	String modelA;
	String modelB;
	String QVTrule;
	
	public static QvtRelationProperty parseString(String property) throws Exception{
		String ma=null,
				mb=null,
				qvt=null;
		
		String[] list = property.split(",");
		if(list.length != 3) throw new Exception("Badly formed Qvt Relation Property");
		else
		{
			ma = list[0];
			mb = list[1];
			qvt = list[2];
			return new QvtRelationProperty(ma,mb,qvt);
		}
	}
	
	public QvtRelationProperty(String qvtRule, List<String> models) {
		super();
		this.modelA = models.get(0);
		this.modelB = models.get(1);
		QVTrule = qvtRule;
	}
	
	public QvtRelationProperty(String modelA, String modelB, String qVTrule) {
		super();
		this.modelA = modelA;
		this.modelB = modelB;
		QVTrule = qVTrule;
	}
	
	
	public String getModelA() {
		return modelA;
	}
	
	public void setModelA(String modelA) {
		this.modelA = modelA;
	}
	public String getModelB() {
		return modelB;
	}
	public void setModelB(String modelB) {
		this.modelB = modelB;
	}
	public String getQVTrule() {
		return QVTrule;
	}
	public void setQVTrule(String qVTrule) {
		QVTrule = qVTrule;
	}


	
	@Override
	public String toString() {
		return modelA + "," + modelB
				+ "," + QVTrule;
	}


	public List<String> getModels()
	{
		ArrayList<String> al = new ArrayList<String>(2);
		al.add(modelA);
		al.add(modelB);
		return al;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((QVTrule == null) ? 0 : QVTrule.hashCode());
		result = prime * result + ((modelA == null) ? 0 : modelA.hashCode());
		result = prime * result + ((modelB == null) ? 0 : modelB.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QvtRelationProperty other = (QvtRelationProperty) obj;
		if (QVTrule == null) {
			if (other.QVTrule != null)
				return false;
		} else if (!QVTrule.equals(other.QVTrule))
			return false;
		if (modelA == null) {
			if (other.modelA != null)
				return false;
		} else if (!modelA.equals(other.modelA))
			return false;
		if (modelB == null) {
			if (other.modelB != null)
				return false;
		} else if (!modelB.equals(other.modelB))
			return false;
		return true;
	}
	
	

}
