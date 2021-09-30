package com.subra.aem.rjs.pdf.servlets;

import com.subra.aem.rjs.pdf.BadgeGenerator;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;

@SuppressWarnings("serial")

@Component(service = { Servlet.class })
@SlingServletResourceTypes(resourceTypes ="test/rj/testpdf" , methods = HttpConstants.METHOD_GET)
@ServiceDescription("Simple Demo Servlet")

public class PDFGeneratorServlet extends SlingAllMethodsServlet {

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException{
		process(request, response);
	}

	public static final ThreadLocal<ResourceResolver> resolverLocal = new ThreadLocal<>();
	private void process(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException{
		try (ByteArrayOutputStream bais = new ByteArrayOutputStream()) {
			response.setContentType("application/pdf");
			resolverLocal.set(request.getResourceResolver());
			new BadgeGenerator(URLDecoder.decode(request.getParameter("html"), "UTF-8"), bais).generatePdf();
			response.setContentLength(bais.size());
			bais.writeTo(response.getOutputStream());
			bais.flush();
			response.flushBuffer();
		}finally {
			resolverLocal.remove();
		}
	}

}
