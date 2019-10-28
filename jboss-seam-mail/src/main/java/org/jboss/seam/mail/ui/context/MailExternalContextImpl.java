/**
 * 
 */
package org.jboss.seam.mail.ui.context;

import javax.faces.context.ExternalContext;
import javax.faces.context.ExternalContextWrapper;

public class MailExternalContextImpl extends ExternalContextWrapper {

	private String urlBase;

	public MailExternalContextImpl(ExternalContext delegate) {
		this(delegate, null);
	}

	public MailExternalContextImpl(ExternalContext delegate, String urlBase) {
		super(delegate);
		this.urlBase = urlBase;
	}

	@Override
	public String getRequestContextPath() {
		if (urlBase == null) {
			return super.getRequestContextPath();
		}
		return urlBase;
	}

}