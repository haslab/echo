package pt.uminho.haslab.echo.plugin.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.uminho.haslab.echo.EchoReporter;

class EchoProjectProperties {
		
	private List<String> models = new ArrayList<String>();
	private Map<String,QVTConstraints> qvts = new HashMap<String,QVTConstraints>();
	
	
	boolean addModel(String modeluri) {
		if (models.contains(modeluri)) return false;
		else models.add(modeluri);
		return true;
	}
	
	boolean removeModel(String modeluri) {
		if (!models.contains(modeluri)) return false;
		else models.remove(modeluri);
		return true;
	}
	
	boolean hasModel(String modeluri) {
		return models.contains(modeluri);
	}
	
	Set<String> getModels() {
		return new HashSet<String>(models);
	}
	
	
	
	boolean addQVT(String qvturi, List<String> modeluris) {
		QVTConstraints models = qvts.get(qvturi);
		if (models == null) {
			models = new QVTConstraints(qvturi);
			qvts.put(qvturi,models);
		}

		models.add(modeluris.get(0), modeluris.get(1));
		return true;
	}
	
	boolean removeQVT(String qvturi, List<String> modeluris) {
		QVTConstraints models = qvts.get(qvturi);
		if (models == null) return false;
		else return models.rem(modeluris.get(0), modeluris.get(1));
	}
	
	String writeString() {
		StringBuilder builder = new StringBuilder();
		for (String modeluri : models) {
			builder.append(modeluri);
			builder.append(",");
		}
		builder.append(";");
		for (String qvturi : qvts.keySet()) {
			for (QVTConstraintEntry qvtmdls : qvts.get(qvturi).entries()) {
				builder.append(qvturi);
				builder.append("@");
				builder.append(qvtmdls.getFst());
				builder.append("@");
				builder.append(qvtmdls.getSnd());
				builder.append(",");
			}
		}
		return builder.toString();
	}
	
	void readString(String properties) {
		String[] uris = properties.split(";");
		models = new ArrayList<String>(Arrays.asList(uris[0].split(",")));
		
		qvts = new HashMap<String,QVTConstraints>();
		if (uris.length > 1)
			for (String qvttrio : uris[1].split(",")) {
				String[] qvttriolist = qvttrio.split("@");
				List<String> modeluris = new ArrayList<String>();
				modeluris.add(qvttriolist[1]);
				modeluris.add(qvttriolist[2]);
				addQVT(qvttriolist[0], modeluris);
			}
	}

	Map<String,Set<String>> getQVTsModelFst(String modeluri) {
		Map<String,Set<String>> entries = new HashMap<String,Set<String>>();
		for (String qvturi : qvts.keySet()) {
			entries.put(qvturi,qvts.get(qvturi).relatedFst(modeluri));
		}			
		return entries;
	}
	
	Map<String,Set<String>> getQVTsModelSnd(String modeluri) {
		Map<String,Set<String>> entries = new HashMap<String,Set<String>>();
		for (String qvturi : qvts.keySet()) {
			entries.put(qvturi,qvts.get(qvturi).relatedSnd(modeluri));
		}			
		return entries;
	}
	
	List<QVTConstraintEntry> getQVTConstraints() {
		List<QVTConstraintEntry> entries = new ArrayList<QVTConstraintEntry>();
		for (String qvturi : qvts.keySet()) {
			entries.addAll(qvts.get(qvturi).entries());
		}			
		return entries;
	}
	
	
	
	private class QVTConstraints {
		private Map<String,Set<String>> fst2snd = new HashMap<String,Set<String>>();
		private Map<String,Set<String>> snd2fst = new HashMap<String,Set<String>>();
		private String qvt;
		
		public QVTConstraints(String qvt) {
			this.qvt = qvt;
		}
				
		private void add(String fst, String snd) {
			Set<String> snds = fst2snd.get(fst);
			if (snds == null) {
				snds = new HashSet<String>();
				snds.add(snd);
				fst2snd.put(fst, snds);
			} else
				snds.add(snd);
			
			Set<String> fsts = snd2fst.get(snd);
			if (fsts == null) {
				fsts = new HashSet<String>();
				fsts.add(fst);
				snd2fst.put(snd, fsts);
			} else
				fsts.add(fst);
		}
		
		private boolean rem(String fst, String snd) {
			boolean good;
			
			Set<String> snds = fst2snd.get(fst);
			if (snds == null) return false;
			else good =  snds.remove(snd);
			
			Set<String> fsts = snd2fst.get(snd);
			if (fsts == null) return false;
			else good = good && fsts.remove(fst);

			return good;
		}
		
		private Set<String> relatedFst(String modeluri) {
			Set<String> result = new HashSet<String>();
			if (fst2snd.get(modeluri) != null)
				result.addAll(fst2snd.get(modeluri));			
			return result;
		}

		private Set<String> relatedSnd(String modeluri) {
			Set<String> result = new HashSet<String>();
			if (snd2fst.get(modeluri) != null)
				result.addAll(snd2fst.get(modeluri));
			return result;
		}

		private List<QVTConstraintEntry> entries() {
			List<QVTConstraintEntry> entries = new ArrayList<QVTConstraintEntry>();
			for (String fstmodel : fst2snd.keySet())
				for (String sndmodel : fst2snd.get(fstmodel))
					entries.add(new QVTConstraintEntry(qvt,fstmodel,sndmodel));
			return entries;
		}
		
		
	}
	
	public class QVTConstraintEntry {
		private String fst;
		private String snd;
		private String qvt;
		
		QVTConstraintEntry(String qvt, String fst, String snd) {
			this.fst = fst;
			this.snd = snd;
			this.qvt = qvt;
		}
		
		public String getFst() {
			return fst;
		}
		public String getSnd() {
			return snd;
		}
		public String getQvt() {
			return qvt;
		}

		
	}

	
}
