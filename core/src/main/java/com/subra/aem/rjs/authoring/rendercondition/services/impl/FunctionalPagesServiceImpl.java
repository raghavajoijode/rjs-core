package com.subra.aem.rjs.authoring.rendercondition.services.impl;

import com.subra.aem.rjs.authoring.rendercondition.services.FunctionalPagesService;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

@Designate(ocd = FunctionalPagesService.Config.class)
@Component(service = FunctionalPagesService.class, immediate = true, property = {Constants.SERVICE_DESCRIPTION + "=Functional Page Configuration Service"})
public class FunctionalPagesServiceImpl implements FunctionalPagesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionalPagesServiceImpl.class);

    private String[] functionalPages;

    @Activate
    protected void activate(final Config config) {
        LOGGER.info("FunctionalPagesService activated...");
        functionalPages = config.functional_page_list();
    }

    @Override
    public List<String> getFunctionalPages() {
        return Arrays.asList(functionalPages);
    }

    @Override
    public boolean isPageFunctional(final String pagePath) {
        return isGeneralizedPageFunctional(generalizedPagePath(pagePath));
    }

    public boolean isGeneralizedPageFunctional(final String generalizedPagePath) {
        return getFunctionalPages().stream().anyMatch(functionalPagePath -> StringUtils.equalsIgnoreCase(functionalPagePath, generalizedPagePath));
    }

    /**
     * Generalizing page path as we cannot have multiple similar pages in OSGi Config
     * So we need to generalize - /content/rjs/mx/es/checkout -> /content/rjs/global/en/checkout (and can be checked in OSGi config)
     * /content/rjs/mx/es/checkout -> /content/rjs/global/en/checkout (and can be checked in OSGi config)
     */
    private String generalizedPagePath(String path) {
        String[] pageStructure = path.split("/");
        if (pageStructure.length > 3) {
            /*
             *   pageStructure[2] = "rjs"; // to be used if rjs-core and rjs-etc also need to generalized to rjs
             */
            pageStructure[3] = "global";
            pageStructure[4] = "en";
        }
        return String.join("/", pageStructure);
    }

}
