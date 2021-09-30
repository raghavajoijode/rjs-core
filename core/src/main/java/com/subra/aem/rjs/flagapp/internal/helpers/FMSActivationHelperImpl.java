package com.subra.aem.rjs.flagapp.internal.helpers;

import com.subra.aem.rjs.core.helpers.ActivationHelper;
import com.subra.aem.rjs.flagapp.internal.services.FlagService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = ActivationHelper.class, immediate = true)
@ServiceDescription("FMS - Activator Helper")
public class FMSActivationHelperImpl implements ActivationHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(FMSActivationHelperImpl.class);

    @Reference
    private FlagService flagService;

    @Activate
    private void activate() {
        LOGGER.info("FMS Activation HelperImpl activated");
    }

    @Deactivate
    private void deActivate() {
        flagService = null;
    }
}