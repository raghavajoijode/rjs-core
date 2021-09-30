package com.subra.aem.rjs.v.search.forms.models;

import com.day.cq.wcm.api.Page;
import com.subra.aem.rjs.v.search.GSSearchManagerUtils;
import com.subra.aem.rjs.v.search.forms.FacetsInfo;
import com.subra.aem.rjs.v.search.forms.services.FormsDocumentsSearch;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.List;

@Model(adaptables = {SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FormsAndDocumentsSearchModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(FormsAndDocumentsSearchModel.class);

    @Self
    private SlingHttpServletRequest request;

    @ScriptVariable
    private Page currentPage;

    @OSGiService
    private FormsDocumentsSearch formsDocumentsSearch;

    @ValueMapValue
    private boolean useCustomTagList;

    @ValueMapValue
    private String[] tagList;

    private String query;
    private List<String> tags;
    private boolean isAdvanced;
    private List<FacetsInfo> facets;

    @PostConstruct
    protected void init() {
        query = GSSearchManagerUtils.getQueryParam(request);
        tags = GSSearchManagerUtils.getTags(request);
        isAdvanced = !tags.isEmpty() || request.getRequestPathInfo().getSuffix() != null || !StringUtils.isAllBlank(getStartDate(), getEndDate(), getFormType());
        facets = GSSearchManagerUtils.retrieveFacets(request, formsDocumentsSearch, useCustomTagList, tagList, currentPage.getAbsoluteParent(1).getName());
        LOGGER.debug("Initialized FormsAndDocumentsSearchModel...");
    }

    public List<FacetsInfo> getFacets() {
        return facets;
    }

    public String getQuery() {
        return query;
    }

    public String getStartDate() {
        return GSSearchManagerUtils.getRequestParam(request, "startDate");
    }

    public String getEndDate() {
        return GSSearchManagerUtils.getRequestParam(request, "endDate");
    }

    public String getFormType() {
        return GSSearchManagerUtils.getRequestParam(request, "formType");
    }

    public List<String> getTags() {
        return tags;
    }

    public boolean isAdvanced() {
        return isAdvanced;
    }

    public Page getCurrentPage() {
        return currentPage;
    }
}
