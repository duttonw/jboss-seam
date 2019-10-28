package org.jboss.seam.jsf;

import java.util.Iterator;
import java.util.Map;

import javax.el.ELContext;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIViewRoot;
import javax.faces.context.*;
import javax.faces.render.RenderKit;

/**
 * Implementation of FacesContext that delegates all calls.
 * 
 * Further, it exposes {@link #setCurrentInstance(FacesContext)} as a public
 * method
 * 
 * @author Pete Muir
 *
 */
public class DelegatingFacesContext extends FacesContextWrapper {

	public DelegatingFacesContext(FacesContext delegate) {
		super(delegate);
	}

	@Override
	public void addMessage(String clientId, FacesMessage message) {
		getWrapped().addMessage(clientId, message);
	}

	@Override
	public Application getApplication() {
		return getWrapped().getApplication();
	}

	public Map<Object, Object> getAttributes() {
		return getWrapped().getAttributes();
	}

	@Override
	public Iterator getClientIdsWithMessages() {
		return  getWrapped().getClientIdsWithMessages();
	}

	@Override
	public ExternalContext getExternalContext() {
		return  getWrapped().getExternalContext();
	}

	@Override
	public Severity getMaximumSeverity() {
		return  getWrapped().getMaximumSeverity();
	}

	@Override
	public Iterator getMessages() {
		return  getWrapped().getMessages();
	}

	@Override
	public Iterator getMessages(String clientId) {
		return  getWrapped().getMessages(clientId);
	}

	@Override
	public RenderKit getRenderKit() {
		return  getWrapped().getRenderKit();
	}

	@Override
	public boolean getRenderResponse() {
		return  getWrapped().getRenderResponse();
	}

	@Override
	public boolean getResponseComplete() {
		return  getWrapped().getResponseComplete();
	}

	@Override
	public ResponseStream getResponseStream() {
		return  getWrapped().getResponseStream();
	}

	@Override
	public ResponseWriter getResponseWriter() {
		return  getWrapped().getResponseWriter();
	}

	@Override
	public UIViewRoot getViewRoot() {
		return  getWrapped().getViewRoot();
	}

	@Override
	public void release() {
		 getWrapped().release();
	}

	@Override
	public void renderResponse() {
		 getWrapped().renderResponse();
	}

	@Override
	public void responseComplete() {
		 getWrapped().responseComplete();
	}

	@Override
	public void setResponseStream(ResponseStream responseStream) {
		 getWrapped().setResponseStream(responseStream);
	}

	@Override
	public void setResponseWriter(ResponseWriter responseWriter) {
		 getWrapped().setResponseWriter(responseWriter);
	}

	@Override
	public void setViewRoot(UIViewRoot root) {
		getWrapped().setViewRoot(root);
	}

	public FacesContext getDelegate() {
		return getWrapped();
	}

	@Override
	public ELContext getELContext() {
		return getWrapped().getELContext();
	}

	public static void setCurrentInstance(FacesContext context) {
		FacesContext.setCurrentInstance(context);
	}

}
