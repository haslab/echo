package pt.uminho.haslab.echo.plugin;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class EchoPropertyTester extends PropertyTester {

	public static final String PROPERTY_NAMESPACE = "pt.uminho.haslab.echo.plugin.propertyTester";
	public static final String PROPERTY_HAS_MARK = "hasmark";
 
	
	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (PROPERTY_HAS_MARK.equals(property)) {
			if(receiver instanceof IResource)
			{
				IResource res = (IResource) receiver;
				try {
					if (res.findMarkers(EchoMarker.META_ERROR, true, 0).length != 0) 
						return true;
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		}
		return false;
	}

}
