package com.subra.aem.rjs.flagapp.internal.services.impl;

import com.subra.aem.rjs.core.constants.JcrFileNames;
import com.subra.aem.rjs.core.constants.JcrPrimaryType;
import com.subra.aem.rjs.core.constants.JcrProperties;
import com.subra.aem.rjs.core.utils.RJSResourceUtils;
import com.subra.aem.rjs.flagapp.helpers.FlagAppHelper;
import com.subra.aem.rjs.flagapp.internal.services.FlagService;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.sling.api.resource.*;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.dtos.flagapp.Flag;
import org.subra.commons.dtos.flagapp.NewFlag;
import org.subra.commons.dtos.flagapp.NewProject;
import org.subra.commons.dtos.flagapp.Project;
import org.subra.commons.exceptions.RJSRuntimeException;
import org.subra.commons.helpers.CommonHelper;
import org.subra.commons.utils.RJSDateTimeUtils;

import java.util.*;
import java.util.function.Consumer;

/**
 * The Service Implementaion to create, update, read and delete flag
 *
 * @author Raghava Joijode
 */
@Component(service = FlagService.class, immediate = true)
@ServiceDescription("RJS Flag App Service")
@Designate(ocd = FlagService.Config.class)
public class FlagServiceImpl implements FlagService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlagServiceImpl.class);

    private static final String PN_FMS_CREATED_ON = "fms-createdOn";
    private static final String PN_FMS_UPDATED_ON = "fms-updatedOn";
    private static final String PN_FMS_IS_BOOLEAN = "fms-isBoolean";
    private static final String PN_FMS_TEMP_CREATENOW = "fms-created-now";
    private static final String PN_FMS_TITLE = "fms-title";
    private static final String PN_FMS_VALUE = "fms-value";

    private Resource rootNode;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Activate
    protected void activate(final Config config) {
        try {
            useResourceResolver(resolver -> rootNode = RJSResourceUtils.getOrCreateResource(resolver,
                    config.flag_app_root_node_path(), JcrPrimaryType.SLING_FOLDER));
            if (rootNode != null)
                FlagAppHelper.configure(this);
            else
                deactivate();
        } catch (ConfigurationException e) {
            LOGGER.error("Error Configuring FlagAppHelper...");
            deactivate();
        }
    }

    @Override
    public void createOrUpdateFlag(final NewFlag newFlag) {
        if (newFlag == null)
            throw new RJSRuntimeException("Invalid NewFlag Request...");
        final String flagTitle = newFlag.getFlagTitle();
        final Object flagValue = newFlag.getFlagValue();
        try {
            Resource project = getProjectResource(newFlag.getProjectName());
            ResourceResolver resolver = project.getResourceResolver();
            Optional.of(project).map(p -> RJSResourceUtils.getChildResource(p, JcrFileNames.CONFIG_NODE.value()))
                    .map(c -> RJSResourceUtils.getOrCreateResource(resolver,
                            c.getPath().concat(CommonHelper.SLASH)
                                    .concat(CommonHelper.createNameFromTitle(flagTitle)),
                            JcrPrimaryType.SLING_FOLDER))
                    .ifPresent(flag -> {
                        try {
                            ModifiableValueMap mvm = flag.adaptTo(ModifiableValueMap.class);
                            if (Boolean.FALSE.equals(mvm.get(PN_FMS_TEMP_CREATENOW, false)))
                                mvm.put(PN_FMS_UPDATED_ON, RJSDateTimeUtils.localDateTimeString());
                            mvm.put(PN_FMS_VALUE, flagValue);
                            mvm.put(PN_FMS_TITLE, flagTitle);
                            mvm.put(PN_FMS_IS_BOOLEAN, flagValue instanceof Boolean);
                            mvm.remove(PN_FMS_TEMP_CREATENOW);
                            resolver.commit();
                        } catch (PersistenceException e) {
                            throw new RJSRuntimeException("Error Creating Flag...");
                        }
                    });
        } catch (NullPointerException e) {
            throw new RJSRuntimeException("Project Not Found...");
        }
    }

    /**
     * Method to read the flag value given flag name
     *
     * @param flagName The name of the flag whose value needs to be returned
     * @return The value associate with the flag
     */
    @Override
    public Object readFlag(final String projectName, final String flagName) {
        return Optional.ofNullable(rootNode).map(r -> r.getChild(projectName))
                .map(p -> p.getChild(JcrFileNames.CONFIG_NODE.value())).map(c -> c.getChild(flagName))
                .map(Resource::getValueMap).map(vm -> vm.get(PN_FMS_VALUE)).orElseThrow(() -> new RJSRuntimeException(
                        "Flag Not Found - Either of Project or Flag Doesn't exists..."));
    }

    /**
     * Method to delete the given flag
     *
     * @param flagName The flag which needs to be deleted
     */
    @Override
    public void deleteFlag(final String projectName, final String flagName) {
        try {
            Resource project = getProjectResource(projectName);
            ResourceResolver resolver = project.getResourceResolver();
            Optional.of(getProjectResource(projectName))
                    .map(p -> RJSResourceUtils.getChildResource(p, JcrFileNames.CONFIG_NODE.value()))
                    .map(c -> RJSResourceUtils.getChildResource(c, flagName)).ifPresent(flag -> {
                        try {
                            RJSResourceUtils.deleteResource(resolver, flag);
                        } catch (PersistenceException e) {
                            throw new RJSRuntimeException(e);
                        }
                    });
        } catch (NullPointerException e) {
            throw new RJSRuntimeException(e);
        }
    }

    @Override
    public Project createProject(final NewProject project) {
        if (project == null)
            throw new RJSRuntimeException("Invalid NewProject Request...");

        ResourceResolver resolver = rootNode.getResourceResolver();
        final String name = project.getProjectName();
        try {
            Optional<Resource> a = Optional.of(rootNode).map(r -> r.getChild(name));
            if (a.isPresent()) {
                Resource p = a.get();
                if (p.getChild(JcrFileNames.CONFIG_NODE.value()) == null)
                    RJSResourceUtils.getOrCreateResource(resolver,
                            p.getPath().concat(CommonHelper.SLASH).concat(
                                    CommonHelper.createNameFromTitle(JcrFileNames.CONFIG_NODE.value())),
                            JcrPrimaryType.UNSTRUCTURED);
            } else {
                RJSResourceUtils.getOrCreateResource(resolver,
                        rootNode.getPath().concat("/" + name).concat(CommonHelper.SLASH)
                                .concat(CommonHelper.createNameFromTitle(JcrFileNames.CONFIG_NODE.value())),
                        JcrPrimaryType.UNSTRUCTURED);
            }
        } catch (NullPointerException e) {
            LOGGER.error("Error Creating Project...", e);
            throw new RJSRuntimeException("Error Creating Project...");
        }
        return readProject(CommonHelper.createNameFromTitle(name));
    }

    @Override
    public Project readProject(final String name) {
        Project projectVo = new Project();
        Optional<Resource> a = Optional.ofNullable(rootNode).map(r -> r.getChild(name))
                .map(p -> p.getChild(JcrFileNames.CONFIG_NODE.value()));
        if (a.isPresent()) {
            Resource c = a.get();
            setProjectVo(c.getParent(), projectVo);
        } else {
            throw new RJSRuntimeException("Project Doesn't Exists...");
        }
        return projectVo;
    }

    @Override
    public void deleteProject(final String name) {
        Optional<Resource> a = Optional.ofNullable(rootNode).map(r -> r.getChild(name))
                .map(p -> p.getChild(JcrFileNames.CONFIG_NODE.value()));

        if (a.isPresent()) {
            Resource c = a.get();
            try {
                RJSResourceUtils.deleteResource(c.getResourceResolver(), c.getParent());
            } catch (PersistenceException e) {
                LOGGER.error("Error Deleting Project...", e);
                throw new RJSRuntimeException("Error Deleting Project...");
            }
        } else {
            throw new RJSRuntimeException("Project Doesn't Exists...");
        }
    }

    @Override
    public List<Flag> projectFlags(final String projectName) {
        List<Flag> flags = new ArrayList<>();
        try {
            Optional<Iterator<Resource>> a = Optional.ofNullable(rootNode).map(r -> RJSResourceUtils.getChildResource(r, projectName))
                    .map(p -> RJSResourceUtils.getChildResource(p, JcrFileNames.CONFIG_NODE.value()))
                    .map(Resource::listChildren);
            if (a.isPresent()) {
                Iterator<Resource> itr = a.get();
                while (itr.hasNext()) {
                    Resource f = itr.next();
                    flags.add(createFlagVo(f));
                }
            } else {
                throw new RJSRuntimeException("Flag Not Found - Either of Project or Flag Doesn't exists...");
            }
        } catch (NullPointerException e) {
            throw new RJSRuntimeException("Error Building Project");
        }
        return flags;
    }

    @Override
    public List<Project> projects() {
        List<Project> projects = new LinkedList<>();
        Optional.ofNullable(rootNode).map(Resource::listChildren).ifPresent(i -> {
            while (i.hasNext()) {
                projects.add(createProjectVo(i.next()));
            }
        });
        return projects;
    }

    private Project createProjectVo(Resource project) {
        Project projectVo = new Project();
        setProjectVo(project, projectVo);
        return projectVo;
    }

    private void setProjectVo(Resource project, Project projectVo) {
        projectVo.setTitle(project.getValueMap().get(JcrProperties.PN_TITLE.property(), project.getName()));
        projectVo.setFlagsCount(Optional.ofNullable(project.getChild(JcrFileNames.CONFIG_NODE.value()))
                .map(Resource::listChildren).map(IteratorUtils::size).orElse(0));
        projectVo.setName(project.getName());
        projectVo.setCreatedOn(RJSDateTimeUtils.toLocalDateTimeString(
                project.getValueMap().get(JcrProperties.PN_CREATED_ON.property(), Calendar.class)));
        projectVo.setUpdatedOn(RJSDateTimeUtils.toLocalDateTimeString(
                project.getValueMap().get(JcrProperties.PN_UPDATED_ON.property(), Calendar.class)));
    }

    private Flag createFlagVo(Resource flag) {
        Flag flagVo = new Flag();
        ValueMap vm = flag.getValueMap();
        flagVo.setTitle(vm.get(PN_FMS_TITLE, CommonHelper.decodeTitleFromName(flag.getName())));
        flagVo.setName(flag.getName());
        flagVo.setBoolean(vm.get(PN_FMS_IS_BOOLEAN, false));
        flagVo.setValue(vm.get(PN_FMS_VALUE));
        flagVo.setCreatedOn(vm.get(PN_FMS_CREATED_ON, String.class));
        flagVo.setUpdatedOn(vm.get(PN_FMS_UPDATED_ON, String.class));
        return flagVo;
    }

    private Resource getProjectResource(final String name) {
        return RJSResourceUtils.getChildResource(rootNode, name);
    }

    private void useResourceResolver(Consumer<ResourceResolver> consumer) {
        try {
            ResourceResolver resolver = getServiceResourceResolver();
            consumer.accept(resolver);
        } catch (LoginException e) {
            LOGGER.error("Error Getting ResourceResolver...", e);
        }
    }

    private ResourceResolver getServiceResourceResolver() throws LoginException {
        return RJSResourceUtils.getAdminServiceResourceResolver(resourceResolverFactory);
    }

    @Deactivate
    protected void deactivate() {
        LOGGER.info("De-Activating FlagService...");
        try {
            if (getServiceResourceResolver() != null && getServiceResourceResolver().isLive())
                getServiceResourceResolver().close();
        } catch (LoginException e) {
            LOGGER.error("Error Closing ResourceResolver...", e);
        }
    }

}
