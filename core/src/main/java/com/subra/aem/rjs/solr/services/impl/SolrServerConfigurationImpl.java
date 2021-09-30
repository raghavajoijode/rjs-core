package com.subra.aem.rjs.solr.services.impl;

import com.subra.aem.rjs.solr.services.SolrServerConfiguration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.Designate;

/**
 * @author Raghava Joijode
 */
@Component(service = SolrServerConfiguration.class, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = SolrServerConfiguration.Config.class)
public class SolrServerConfigurationImpl implements SolrServerConfiguration {

    private String solrProtocol;

    private String solrServerName;

    private String solrServerPort;

    private String solrCoreName;

    private String contentPagePath;

    @Activate
    public void activate(Config config) {
        this.solrProtocol = config.protocolValue();
        this.solrServerName = config.serverName();
        this.solrServerPort = config.serverPort();
        this.solrCoreName = config.serverCollection();
        this.contentPagePath = config.serverPath();
    }

    public String getSolrProtocol() {
        return this.solrProtocol;
    }

    public String getSolrServerName() {
        return this.solrServerName;
    }

    public String getSolrServerPort() {
        return this.solrServerPort;
    }

    public String getSolrCoreName() {
        return this.solrCoreName;
    }

    public String getContentPagePath() {
        return this.contentPagePath;
    }

}