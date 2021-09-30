package com.subra.aem.rjs.core.helpers.impl;

import com.subra.aem.rjs.core.helpers.ActivationHelper;
import com.subra.aem.rjs.core.utils.RJSI18NUtils;
import com.subra.aem.rjs.core.utils.RJSInstanceUtils;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = ActivationHelper.class, immediate = true)
@ServiceDescription("RJS - Activator Helper")
public class ActivationHelperImpl implements ActivationHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivationHelperImpl.class);

    @Reference
    private SlingSettingsService slingSettings;

    @Reference
    private ResourceBundleProvider resourceBundleProvider;

    @Activate
    private void activate() {
        try {
            RJSInstanceUtils.configure(slingSettings);
            RJSI18NUtils.configure(resourceBundleProvider);
        } catch (ConfigurationException e) {
            // Sends an email in case of exceptions to analyse and handle
            // sendExceptionMessage(getClass().getSimpleName(), e.getMessage())
            LOGGER.error("ConfigurationException occurred");
        }
    }

    @Deactivate
    private void deActivate() {
        ActivationHelper.clearLogFiles(slingSettings.getSlingHomePath());
    }

}