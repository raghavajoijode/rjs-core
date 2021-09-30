package com.subra.aem.rjs.examples.servlets;

import com.subra.aem.rjs.core.utils.MySecurityUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.servlet.Servlet;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/*
  This is generic implmentation of QR to show how it works using path based, Not a production ready code.
*/

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "= QR Code Servlet for Authenticator",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/subra/qrcode"})

public class MyQRCodeServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(MyQRCodeServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String userId = "";
        String secretKey = "";
        try {

            ResourceResolver resourceResolver = request.getResourceResolver();
            Authorizable auth = resourceResolver.adaptTo(Authorizable.class);
            userId = auth.getID();

            LOGGER.debug("Generating QR Code for user {} ", userId);

            Session userSession = resourceResolver.adaptTo(Session.class);
            secretKey = MySecurityUtils.updateSecurityKey(auth, userSession);

            // generate a bar code using google chart api,
            URL qrURL = new URL("https://www.google.com/chart?chs=250x250&cht=qr&chl=otpauth://totp/Example:" + userId
                    + "?secret=" + secretKey);

            URLConnection conn = qrURL.openConnection();

            response.setContentType("image/png");
            response.setStatus(200);

            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            OutputStream os = response.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os);
            byte[] buff = new byte[8192];
            int sz = 0;
            while ((sz = bis.read(buff)) != -1) {
                bos.write(buff, 0, sz);
            }
            bos.flush();

        } catch (Exception e) {
            LOGGER.error("Exception while generate QR Code for User {}, {}", userId, e);

        }

    }

}
