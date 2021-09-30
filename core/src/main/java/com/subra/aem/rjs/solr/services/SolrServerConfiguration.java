package com.subra.aem.rjs.solr.services;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * @author Raghava Joijode
 */
public interface SolrServerConfiguration {

    String getSolrProtocol();

    String getSolrServerName();

    String getSolrServerPort();

    String getSolrCoreName();

    String getContentPagePath();

    @ObjectClassDefinition(name = "AEM Solr Search - Solr Configuration Service", description = "Service Configuration")
    @interface Config {

        @AttributeDefinition(name = "Protocol", defaultValue = "http", description = "Configuration value")
        String protocolValue();

        @AttributeDefinition(name = "Solr Server Name", defaultValue = "localhost", description = "Server name or IP address")
        String serverName();

        @AttributeDefinition(name = "Solr Server Port", defaultValue = "8983", description = "Server port")
        String serverPort();

        @AttributeDefinition(name = "Solr Core Name", defaultValue = "collection", description = "Core name in solr server")
        String serverCollection();

        @AttributeDefinition(name = "Content page path", defaultValue = "/content/we-retail", description = "Content page path from where solr has to index the pages")
        String serverPath();

    }

}