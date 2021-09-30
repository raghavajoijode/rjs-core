package com.subra.aem.rjs.flagapp.internal.services;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.subra.commons.dtos.flagapp.Flag;
import org.subra.commons.dtos.flagapp.NewFlag;
import org.subra.commons.dtos.flagapp.NewProject;
import org.subra.commons.dtos.flagapp.Project;

import java.util.List;

/**
 * The Service Interface to create, update, read and delete flag
 *
 * @author Raghava Joijode
 */
public interface FlagService {
    void createOrUpdateFlag(final NewFlag newFlag);

    List<Flag> projectFlags(final String projectName);

    Object readFlag(final String projectName, final String flagName);

    void deleteFlag(final String projectName, final String flagName);

    Project createProject(final NewProject newProject);

    Project readProject(final String name);

    void deleteProject(final String name);

    List<Project> projects();

    @ObjectClassDefinition(name = "Subra Flag App Service Configuration")
    public @interface Config {

        @AttributeDefinition(name = "Flag App Root Node Path")
        String flag_app_root_node_path() default "/var/rjs/flag-app";

    }

}
