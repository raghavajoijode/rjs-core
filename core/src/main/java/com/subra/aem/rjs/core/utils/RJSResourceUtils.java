package com.subra.aem.rjs.core.utils;

import com.adobe.cq.export.json.SlingModelFilter;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.subra.aem.rjs.core.RJSResource;
import com.subra.aem.rjs.core.UserMapperService;
import com.subra.aem.rjs.core.constants.JcrFileNames;
import com.subra.aem.rjs.core.constants.JcrPrimaryType;
import com.subra.aem.rjs.core.constants.JcrProperties;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.resource.*;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.models.factory.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.constants.HttpType;
import org.subra.commons.exceptions.RJSRuntimeException;
import org.subra.commons.helpers.CommonHelper;
import org.subra.commons.utils.RJSDateTimeUtils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.subra.aem.rjs.core.constants.JcrProperties.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * @author Raghava Joijode
 */
public class RJSResourceUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RJSResourceUtils.class);

    private static final String PN_POLICY_STYLE_DEFAULT_CLASSES = "cq:styleDefaultClasses";
    private static final String PN_POLICY_STYLE_CLASSES = "cq:styleClasses";
    private static final String PN_POLICY_STYLE_ID = "cq:styleId";
    private static final Map<String, Object> AUTH_INFO;

    static {
        AUTH_INFO = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, UserMapperService.ADMIN_SERVICE.value());
    }

    private RJSResourceUtils() {
        throw new IllegalStateException(this.getClass().getSimpleName());
    }

    public static Map<String, Object> getAuthInfo(UserMapperService mapper) {
        return Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, mapper.value());
    }

    /**
     * @param parentResource Parent resource whose children needs to be adopted
     * @param clazz          Class to which children to be adopted
     * @return List of Model Objects
     */
    public static <T> List<T> adoptChildNodesToModel(final Resource parentResource, final Class<T> clazz) {
        final List<T> children = new ArrayList<>();
        if (null != parentResource) {
            final Iterable<Resource> childResources = parentResource.getChildren();
            for (Resource childResource : childResources) {
                children.add(childResource.adaptTo(clazz));
            }
        }
        return children;
    }

    public static InputStream getFileAsIS(final Resource resource) throws RepositoryException {
        final Node node = adoptToOrThrow(resource, Node.class);
        return node.getProperty(JcrConstants.JCR_CONTENT + CommonHelper.SLASH + JcrConstants.JCR_DATA).getBinary().getStream();
    }

    public static String getFileJCRData(final Resource resource) {
        return Optional.ofNullable(resource).map(r -> {
            if (r.getChild(JcrConstants.JCR_CONTENT) != null) {
                return r;
            }
            return r.getChild(JcrFileNames.DEFAULT_TEXT_FILE.value());
        }).map(file -> {
            try {
                return IOUtils.toString(getFileAsIS(file), HttpType.CHARSET_UTF_8.value());
            } catch (IOException | RepositoryException e) {
                LOGGER.error("Error getting content from template...", e);
            }
            return null;
        }).orElseThrow(RJSRuntimeException::new);
    }

    public static <K extends Adaptable, T> Optional<T> adaptTo(final K adaptableClazz, final Class<T> adaptorClazz) {
        return Optional.of(adaptableClazz).map(r -> r.adaptTo(adaptorClazz));
    }

    public static <K extends Adaptable, T> T adoptToOrThrow(final K adaptableClazz, final Class<T> adaptorClazz) {
        return adaptTo(adaptableClazz, adaptorClazz).orElseThrow(RJSRuntimeException::new);
    }

    public static List<String> getAssociatedStyleSystem(Resource policy, List<String> styleIds) {
        List<String> styleSystemClasses = new LinkedList<>();
        if (policy != null) {
            if (CollectionUtils.isEmpty(styleIds)) {
                Optional.of(policy.getValueMap()).map(vm -> vm.get(PN_POLICY_STYLE_DEFAULT_CLASSES, String.class)).ifPresent(styleSystemClasses::add);
            } else {
                checkStyleIdAndSetClass(policy, styleIds, styleSystemClasses);
            }
        }
        return styleSystemClasses;
    }

    private static void checkStyleIdAndSetClass(Resource resource, List<String> styleIds, List<String> styleSystemClasses) {
        ValueMap vm = resource.getValueMap();
        if (styleIds.contains(vm.get(PN_POLICY_STYLE_ID, String.class))) {
            styleSystemClasses.add(vm.get(PN_POLICY_STYLE_CLASSES, String.class));
        }
        resource.getChildren().forEach(child -> checkStyleIdAndSetClass(child, styleIds, styleSystemClasses));
    }

    public static <T> Map<String, T> getChildModels(final SlingHttpServletRequest request, final SlingModelFilter modelFilter, final ModelFactory modelFactory, final Class<T> modelClass) {
        Map<String, T> itemWrappers = new LinkedHashMap<>();
        modelFilter.filterChildResources(request.getResource().getChildren()).forEach(child -> itemWrappers.put(child.getName(), modelFactory.getModelFromWrappedRequest(request, child, modelClass)));
        return itemWrappers;
    }

    /**
     * @param resource     Parent resource whose children properties needs to be
     *                     found
     * @param childResName Name of specific resource
     * @return Map of properties
     */
    private static Map<String, Object> getChildrenPropsMap(Resource resource, String childResName) {
        ValueMap vm = resource.getValueMap();
        Map<String, Object> result = new HashMap<>();
        if (childResName.equals(resource.getName()) || EMPTY.equals(childResName)) {
            result = vm.keySet().stream().collect(Collectors.toMap(Function.identity(), vm::get));
        }
        for (Resource child : resource.getChildren()) {
            Map<String, Object> prop = getChildrenPropsMap(child, childResName);
            result.putAll(prop);
        }
        return result;
    }

    /**
     * @param resource Parent resource whose children properties needs to be found
     * @return Consolidated Value Map
     */
    public static ValueMap getChildrenValueMap(Resource resource) {
        return new ValueMapDecorator(getChildrenPropsMap(resource, EMPTY));
    }

    /**
     * @param <T>      Return Type Class
     * @param resource Parent resource whose children properties needs to be found
     * @param property Name of property
     * @param type     Return Type Class
     * @return Property value at last child, if available
     */
    public static <T> T getChildProperty(Resource resource, String property, Class<T> type) {
        return getChildrenValueMap(resource).get(property, type);
    }

    /**
     * @param resource     Parent resource whose children properties needs to be found
     * @param childResName Name of specific resource
     * @return ValueMap of specific resource
     */
    public static ValueMap getChildValueMap(Resource resource, String childResName) {
        return new ValueMapDecorator(getChildrenPropsMap(resource, childResName));
    }

    /**
     * @param <T>          Return Type Class
     * @param resource     Parent resource whose children properties needs to be found
     * @param childResName Name of specific resource
     * @param property     Name of property
     * @param type         Return Type Class
     * @return Property value at specific resource
     */
    public static <T> T getChildPropertyAtResource(Resource resource, String childResName, String property,
                                                   Class<T> type) {
        return getChildValueMap(resource, childResName).get(property, type);
    }

    public static ResourceResolver getResourceResolverFromRequest(final SlingHttpServletRequest request) {
        return request.getResourceResolver();
    }

    public static <T> T adaptResolverToClassUsingRequest(final SlingHttpServletRequest request, final Class<T> clazz) {
        return getResourceResolverFromRequest(request).adaptTo(clazz);
    }

    public static ResourceResolver getAdminServiceResourceResolver(final ResourceResolverFactory resourceResolverFactory) throws LoginException {
        return resourceResolverFactory.getServiceResourceResolver(AUTH_INFO);
    }

    public static ResourceResolver getServiceResourceResolver(final ResourceResolverFactory resourceResolverFactory, UserMapperService userMapperService) throws LoginException {
        return resourceResolverFactory.getServiceResourceResolver(getAuthInfo(userMapperService));
    }

    public static Session getAdminSession(final SlingRepository slingRepository) throws RepositoryException {
        return slingRepository.loginService(UserMapperService.ADMIN_SERVICE.value(), null);
    }

    public static Resource getResource(final SlingHttpServletRequest request, final String path) {
        return getResourceResolverFromRequest(request).getResource(path);
    }

    public static Resource getResource(final ResourceResolver resolver, final String path) {
        final String resourcePath = path.startsWith(CommonHelper.SLASH) ? path : CommonHelper.SLASH + path;
        return resolver.getResource(resourcePath);
    }

    public static Resource createResource(ResourceResolver resolver, final Resource base, final String title, final JcrPrimaryType type) {
        Resource resource = null;
        try {
            Map<String, Object> properties = new HashMap<>();
            properties.put(type.property(), type.value());
            properties.put(PN_TITLE.property(), title);
            properties.put(PN_CREATED_ON.property(), RJSDateTimeUtils.nowInCalendar());
            properties.put(PN_UPDATED_ON.property(), RJSDateTimeUtils.nowInCalendar());

            resource = resolver.create(base, CommonHelper.createNameFromTitle(title), properties);
            resolver.commit();
        } catch (PersistenceException e) {
            LOGGER.error("Error creating root resource..", e);
        }
        return resource;
    }

    public static Resource getOrCreateResource(ResourceResolver resolver, final String path, final JcrPrimaryType type) {
        Resource resource = getResource(resolver, path);
        if (resource == null) {
            final String parentPath = StringUtils.substringBeforeLast(path, "/");
            final Resource parent = getOrCreateResource(resolver, parentPath, type);
            final String name = StringUtils.substringAfterLast(path, "/");
            resource = createResource(resolver, parent, CommonHelper.decodeTitleFromName(name), type);
        }
        return resource;
    }

    public static void deleteResource(ResourceResolver resolver, Resource resource) throws PersistenceException {
        resolver.delete(resource);
        resolver.commit();
    }

    public static Resource getChildResource(final Resource base, final String childTitle) {
        return Optional.of(base).map(r -> r.getChild(CommonHelper.createNameFromTitle(childTitle))).orElse(null);
    }

    public static Object addOrUpdateProperty(Resource resource, String property, Object value) {
        try {
            ModifiableValueMap mvp = resource.adaptTo(ModifiableValueMap.class);
            mvp.put(property, value);
            resource.getResourceResolver().commit();
            return value;
        } catch (PersistenceException | NullPointerException e) {
            LOGGER.error("Error creating/updating property", e);
        }
        return null;
    }

    public static Object addOrUpdateProperty(Resource resource, JcrProperties property, Object value) {
        return addOrUpdateProperty(resource, property.property(), value);
    }

    public static Object deleteProperty(Resource resource, JcrProperties property) {
        try {
            ModifiableValueMap mvp = resource.adaptTo(ModifiableValueMap.class);
            Object value = mvp.remove(property.property());
            resource.getResourceResolver().commit();
            return value;
        } catch (PersistenceException | NullPointerException e) {
            LOGGER.error("Error deleting property", e);
        }
        return null;
    }

    public static <T> T getPropertyValue(Resource resource, JcrProperties property, T defaultValue) {
        return Optional.ofNullable(resource).map(Resource::getValueMap).map(vm -> vm.get(property.property(), defaultValue)).orElse(defaultValue);
    }

    public static Page getPageFromPath(final SlingHttpServletRequest request, final String path) {
        Resource targetResource = getResource(request, path);
        PageManager pageManager = adaptResolverToClassUsingRequest(request, PageManager.class);
        return (pageManager != null && targetResource != null) ? pageManager.getContainingPage(targetResource) : null;
    }

    public static String getPageURL(final SlingHttpServletRequest request, final Page page) {
        final String vanityURL = page.getVanityUrl();
        return StringUtils.isEmpty(vanityURL) ? (request.getContextPath() + page.getPath() + ".html") : (request.getContextPath() + vanityURL);
    }

    private static void updateRJSResource(final RJSResource rjsResource, final Resource resource) {
        rjsResource.setResource(resource);
        rjsResource.setName(resource.getName());
        rjsResource.setPath(resource.getPath());
        rjsResource.setTitle(getPropertyValue(resource, PN_TITLE, EMPTY));
        rjsResource.setDescription(getPropertyValue(resource, PN_DESCRIPTION, EMPTY));
        rjsResource.setLocked(getPropertyValue(resource, PN_LOCKED, false));
        rjsResource.setHidden(getPropertyValue(resource, PN_HIDDEN, false));
        rjsResource.setWfPending(getPropertyValue(resource, PN_WF_PENDING, EMPTY));
        rjsResource.setWfApproved(getPropertyValue(resource, PN_WF_APPROVED, EMPTY));
        rjsResource.setWfRejected(getPropertyValue(resource, PN_WF_REJECTED, EMPTY));
        rjsResource.setWfStatus(getPropertyValue(resource, PN_WF_STATUS, EMPTY));
        rjsResource.setToDeleteOn(getPropertyValue(resource, PN_TO_DELETE_ON, null));
        rjsResource.setCreatedOn(getPropertyValue(resource, PN_CREATED_ON, null));
        rjsResource.setUpdatedBy(getPropertyValue(resource, PN_UPDATED_BY, EMPTY));
        rjsResource.setUpdatedOn(getPropertyValue(resource, PN_UPDATED_ON, null));
        rjsResource.setCreatedBy(getPropertyValue(resource, PN_CREATED_BY, EMPTY));
    }

    public static void updateRJSResource(final Resource resource, final RJSResource rjsResource) {
        if (resource != null) updateRJSResource(rjsResource, resource);
    }

}
