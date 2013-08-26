package uk.ac.cam.sup.controllers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

/**
 * This class forces resteasy to interpret strings as UTF-8 if there is no
 * charset given. You should register it in the Application class with the other
 * resteasy controllers.
 */
@Provider
@ServerInterceptor
public class UTF8Interceptor implements PreProcessInterceptor {

	@Override
	public ServerResponse preProcess(HttpRequest request, ResourceMethod method)
			throws Failure, WebApplicationException {
		request.setAttribute(InputPart.DEFAULT_CONTENT_TYPE_PROPERTY,
				"*/*; charset=UTF-8");
		return null;
	}

}
