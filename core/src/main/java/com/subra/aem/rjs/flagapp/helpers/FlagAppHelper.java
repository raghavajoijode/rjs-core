package com.subra.aem.rjs.flagapp.helpers;

import com.subra.aem.rjs.flagapp.internal.services.FlagService;
import org.osgi.service.cm.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.dtos.flagapp.Flag;
import org.subra.commons.dtos.flagapp.Project;

import java.util.List;

public class FlagAppHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlagAppHelper.class);

    private static FlagService flagService;
    private final String projectName;

    private FlagAppHelper() {
        throw new IllegalStateException(this.getClass().getSimpleName());
    }

    private FlagAppHelper(final String projectName) {
        this.projectName = projectName;
    }

    public static void configure(FlagService fs) throws ConfigurationException {
        flagService = fs;
        checkConfiguration();
    }

    public static FlagAppHelper build(final String project) throws ConfigurationException {
        checkConfiguration();
        return new FlagAppHelper(project);
    }

    public static List<Project> projects() throws ConfigurationException {
        checkConfiguration();
        return flagService.projects();
    }

    private static void checkConfiguration() throws ConfigurationException {
        if (flagService == null) {
            LOGGER.debug("[RJSFlagApp] -> {} not configured...", FlagAppHelper.class.getSimpleName());
            throw new ConfigurationException(null,
                    "Either 'author' or 'publish' run modes may be specified, not both.");
        }
        LOGGER.debug("[RJSFlagApp] -> {} configured successfully...", FlagAppHelper.class.getSimpleName());
    }

    public String getProjectName() {
        return projectName;
    }

    public List<Flag> getAllFlags() {
        return flagService.projectFlags(projectName);
    }

    public Object getFlagValue(String flagName) {
        return flagService.readFlag(projectName, flagName);
    }

    public void deleteFlag(String flagName) {
        flagService.deleteFlag(projectName, flagName);
    }

}