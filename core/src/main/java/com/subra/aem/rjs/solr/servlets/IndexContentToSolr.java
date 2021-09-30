package com.subra.aem.rjs.solr.servlets;

import com.subra.aem.rjs.solr.services.SolrSearchService;
import com.subra.aem.rjs.solr.services.SolrServerConfiguration;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient.Builder;
import org.json.JSONArray;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author Raghava Joijode
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Solr Index Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/solr/push/pages"})
public class IndexContentToSolr extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(IndexContentToSolr.class);

    @Reference
    transient SolrServerConfiguration solrConfigurationService;

    @Reference
    transient SolrSearchService solrSearchService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);

    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String indexType = request.getParameter("indexType");
        final String protocol = solrConfigurationService.getSolrProtocol();
        final String serverName = solrConfigurationService.getSolrServerName();
        final String serverPort = solrConfigurationService.getSolrServerPort();
        final String coreName = solrConfigurationService.getSolrCoreName();
        final String pagesResourcePath = solrConfigurationService.getContentPagePath();
        String url = protocol + "://" + serverName + ":" + serverPort + "/solr/" + coreName;

        // Create an HTTPSolrClient instance
        HttpSolrClient server = new Builder(url).build();

        if (indexType.equalsIgnoreCase("indexpages")) {
            try {
                JSONArray indexPageData = solrSearchService.crawlContent(pagesResourcePath, "cq:PageContent");

                boolean resultIndexingPages = solrSearchService.indexPagesToSolr(indexPageData, server);
                if (resultIndexingPages) {
                    response.getWriter().write("<h3>Successfully indexed content pages to Solr server </h3>");
                } else {
                    response.getWriter().write("<h3>Something went wrong</h3>");
                }
            } catch (Exception e) {
                LOG.error("Exception due to", e);
                response.getWriter().write(
                        "<h3>Something went wrong. Please make sure Solr server is configured properly in Felix</h3>");
            }

        } else {
            response.getWriter().write("<h3>Something went wrong</h3>");
        }

    }

}
