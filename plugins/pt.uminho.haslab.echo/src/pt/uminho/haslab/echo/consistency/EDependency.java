package pt.uminho.haslab.echo.consistency;

import java.util.List;

public class EDependency {

	private List<EModelDomain> sources;
	private EModelDomain target;
	
	public EDependency(EModelDomain target, List<EModelDomain> sources) {
		this.target = target;
		this.sources = sources;
	}

	public EModelDomain getTarget() {
		return target;
	}

	public List<EModelDomain> getSources() {
		return sources;
	}
	
	public String toString() {
		return sources.toString() + " -> " + target.toString();
	}
}
