package com.subra.aem.rjs.examples.servlets;

import com.day.cq.dam.api.AssetManager;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collections;

/**
 * @author Raghava Joijode
 *
 */

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "= QR Code Servlet for Authenticator",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + "/bin/subra/uploaddamfile" })
public class HandleDamFile extends SlingAllMethodsServlet {
	private static final Logger LOGGER = LoggerFactory.getLogger(HandleDamFile.class);
	private static final long serialVersionUID = 2598426539166789515L;

	@Reference
	private transient ResourceResolverFactory resourceResolverFactory;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
		this.doPost(request, response);
	}

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
		LOGGER.debug("*****Debugging {}*****", this.getClass());
		try {
			final boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			PrintWriter out = null;

			out = response.getWriter();
			if (isMultipart) {
				final java.util.Map<String, RequestParameter[]> params = request.getRequestParameterMap();

				for (final java.util.Map.Entry<String, RequestParameter[]> pairs : params.entrySet()) {
					final RequestParameter[] pArr = pairs.getValue();
					final RequestParameter param = pArr[0];
					final InputStream stream = param.getInputStream();
					ResourceResolver resolver = request.getResourceResolver();
					String type = request.getParameter("selectionValue");
					String theme = request.getParameter("selectionTheme");
					String uploader = request.getParameter("uploadedBy");
					if (param.getFileName() != null) {
						String assetPath = writeToDam(stream, type + "-wf-" + param.getFileName(),
								param.getContentType());
						Resource uploadedAsset = resolver.getResource(assetPath + "/jcr:content/metadata");
						ModifiableValueMap map = uploadedAsset.adaptTo(ModifiableValueMap.class);
						map.put("dc:creator", uploader);
						map.put("dc:contributor", type);
						if (!theme.equals("none")) {
							map.put("theme", theme);
						}
						uploadedAsset.getResourceResolver().commit();
						out.println("The Sling Servlet placed the uploaded file here: " + assetPath);
					}

				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		} finally {
			unbindResolverFactory(resourceResolverFactory);
		}

	}

	private String writeToDam(InputStream is, String fileName, String mimeType) {
		try {
			ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(
					Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "ResolveService"));
			AssetManager assetMgr = resourceResolver.adaptTo(AssetManager.class);
			String newFile = "/content/dam/demo/workflow/" + fileName;
			assetMgr.createAsset(newFile, is, mimeType, true);
			return newFile;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	protected void bindResolverFactory(ResourceResolverFactory paramResourceResolverFactory) {
		this.resourceResolverFactory = paramResourceResolverFactory;
	}

	protected void unbindResolverFactory(ResourceResolverFactory paramResourceResolverFactory) {
		if (this.resourceResolverFactory == paramResourceResolverFactory) {
			this.resourceResolverFactory = null;
		}
	}
}
