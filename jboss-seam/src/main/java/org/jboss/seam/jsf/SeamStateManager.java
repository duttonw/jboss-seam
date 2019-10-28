package org.jboss.seam.jsf;

import java.io.IOException;

import javax.faces.application.StateManager;
import javax.faces.application.StateManagerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.navigation.Pages;

/**
 * A wrapper for the JSF implementation's StateManager that allows
 * us to intercept saving of the serialized component tree. This
 * is quite ugly but was needed in order to allow conversations to
 * be started and manipulated during the RENDER_RESPONSE phase.
 * 
 * @author Gavin King
 */
@SuppressWarnings("deprecation")
public class SeamStateManager extends StateManagerWrapper {

	public SeamStateManager(StateManager sm) {
		super(sm);
	}



	@Override
	public SerializedView saveSerializedView(FacesContext facesContext) {

		if (Contexts.isPageContextActive()) {
			//store the page parameters in the view root
			Pages.instance().updateStringValuesInPageContextUsingModel(facesContext);
		}

		return getWrapped().saveSerializedView(facesContext);
	}

	@Override
	public Object saveView(FacesContext facesContext) {

		if (Contexts.isPageContextActive()) {
			//store the page parameters in the view root
			Pages.instance().updateStringValuesInPageContextUsingModel(facesContext);
		}

		return getWrapped().saveView(facesContext);
	}
}