package com.subra.aem.rjs.account.services.impl;

import com.day.cq.replication.Replicator;
import com.subra.aem.rjs.account.services.MyAEMUserService;
import com.subra.aem.rjs.core.utils.RJSResourceUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.AuthorizableExistsException;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.oak.spi.security.principal.EveryonePrincipal;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFactory;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;
import java.security.Principal;
import java.util.NoSuchElementException;

@Component(service = MyAEMUserService.class, immediate = true)
@ServiceRanking(60000)
@ServiceDescription("RJS - User Service")
public final class MyAEMUserServiceImpl implements MyAEMUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyAEMUserServiceImpl.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Activate
    protected void activate() {
        // do nothing as of now
    }

    @Deactivate
    protected void deactivate() {
        // do nothing as of now
    }

    @Override
    public String createUser(String userName, String password, String groupName) {
        try (ResourceResolver resourceResolver = RJSResourceUtils.getAdminServiceResourceResolver(resourceResolverFactory)) {
            UserManager userManager = resourceResolver.adaptTo(UserManager.class);
            Session session = resourceResolver.adaptTo(Session.class);
            User user = null;
            if (userManager != null) {
                if (null == userManager.getAuthorizable(userName)) {
                    user = createUser(userName, password, userManager, session.getValueFactory());
                    Group group = (Group) (userManager.getAuthorizable(createGroup(groupName)));
                    group.addMember(userManager.getAuthorizable(userName));
                    if (user != null)
                        setAclPrivileges(user.getPath());
                } else {
                    user = (User) userManager.getAuthorizable(userName);
                    LOGGER.debug("User already exist..");
                }
            }
            return user != null ? user.getID() : null;
        } catch (Exception e) {
            LOGGER.debug("Not able to perform User Management.. {}", resourceResolverFactory);
        }
        return null;
    }

    @Override
    public boolean isExistingUserName(String userName) {
        try (ResourceResolver resourceResolver = RJSResourceUtils.getAdminServiceResourceResolver(resourceResolverFactory)) {
            return (null != resourceResolver.adaptTo(UserManager.class).getAuthorizable(userName));
        } catch (RepositoryException | LoginException e) {
            LOGGER.debug("Exception Occurred.. {}", e.getMessage());
        }
        return false;
    }

    @Override
    public String createGroup(String groupName) {
        try (ResourceResolver resourceResolver = RJSResourceUtils.getAdminServiceResourceResolver(resourceResolverFactory)) {
            UserManager userManager = resourceResolver.adaptTo(UserManager.class);
            Session session = resourceResolver.adaptTo(Session.class);
            Group group;
            if (null == userManager.getAuthorizable(groupName)) {
                group = createGroup(groupName, userManager, session.getValueFactory());
            } else {
                group = (Group) userManager.getAuthorizable(groupName);
                LOGGER.debug("Group already exist..");
            }
            return group != null ? group.getID() : null;
        } catch (Exception e) {
            LOGGER.debug("Not able to perform User Management..{}", resourceResolverFactory);
        }
        return null;
    }

    public void setAclPrivileges(String path) {
        try (ResourceResolver resourceResolver = RJSResourceUtils.getAdminServiceResourceResolver(resourceResolverFactory)) {
            Session session = resourceResolver.adaptTo(Session.class);
            AccessControlManager accessControlManager = session.getAccessControlManager();
            AccessControlList acl = getACL(path, accessControlManager);
            // remove all existing entries
            for (AccessControlEntry e : acl.getAccessControlEntries()) {
                acl.removeAccessControlEntry(e);
            }
            // create a privilege set
            Privilege[] privileges = {accessControlManager.privilegeFromName(Privilege.JCR_READ), accessControlManager.privilegeFromName(Replicator.REPLICATE_PRIVILEGE)};
            // add a new one for the special "everyone" principal
            acl.addAccessControlEntry(EveryonePrincipal.getInstance(), privileges);
            // the policy must be re-set
            accessControlManager.setPolicy(path, acl);
            // and the session must be saved for the changes to be applied
            session.save();
        } catch (LoginException | RepositoryException e) {
            LOGGER.error("---> Not able to perform ACL Privileges..{}", e.getMessage());
        }
    }

    private AccessControlList getACL(String path, AccessControlManager accessControlManager)
            throws RepositoryException {
        AccessControlList acl;
        try {
            acl = (AccessControlList) accessControlManager.getApplicablePolicies(path).nextAccessControlPolicy();
        } catch (NoSuchElementException e) {
            acl = (AccessControlList) accessControlManager.getPolicies(path)[0];
        }
        return acl;
    }

    private User createUser(String userName, String password, UserManager userManager, ValueFactory valueFactory) {
        User user = null;
        try {
            user = userManager.createUser(userName, password, new CustomPrincipal(userName), "/home/users/test");
            user.setProperty("./profile/familyName", valueFactory.createValue("Issac", PropertyType.STRING));
            user.setProperty("./profile/givenName", valueFactory.createValue("Albin", PropertyType.STRING));
            user.setProperty("./profile/aboutMe", valueFactory.createValue("Test User", PropertyType.STRING));
            user.setProperty("./profile/email", valueFactory.createValue("albin@gmail.com", PropertyType.STRING));
        } catch (AuthorizableExistsException e) {
            LOGGER.debug(
                    "Exception Occurred.. given principal is already in use with another Authorizable. {}", e.getMessage());
        } catch (RepositoryException e) {
            LOGGER.debug("Exception Occurred..{}", e.getMessage());
        }
        return user;
    }

    private Group createGroup(String groupName, UserManager userManager, ValueFactory valueFactory) {
        Group group = null;
        try {
            group = userManager.createGroup(groupName, new CustomPrincipal(groupName), "/home/groups/test");
            group.setProperty("./profile/givenName", valueFactory.createValue("Sample Group", PropertyType.STRING));
            group.setProperty("./profile/aboutMe", valueFactory.createValue("Test Group", PropertyType.STRING));
            group.setProperty("./profile/email", valueFactory.createValue("abc@gmail.com", PropertyType.STRING));
        } catch (AuthorizableExistsException e) {
            LOGGER.debug(
                    "Exception Occurred.. given principal is already in use with another Authorizable.{}", e.getMessage());
        } catch (RepositoryException e) {
            LOGGER.debug("Exception Occurred..{}", e.getMessage());
        }
        return group;
    }

    private static class CustomPrincipal implements Principal {
        protected final String name;

        public CustomPrincipal(String name) {
            if (StringUtils.isBlank(name)) {
                throw new IllegalArgumentException("Principal name cannot be blank.");
            }
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Principal) {
                return name.equals(((Principal) obj).getName());
            }
            return false;
        }
    }

}
