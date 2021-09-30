package com.subra.aem.rjs.authoring.rendercondition.models;

import com.adobe.granite.ui.components.rendercondition.RenderCondition;
import com.subra.aem.rjs.authoring.AuthoringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.jcr.RepositoryException;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ConditionalGroupRenderCondition {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConditionalGroupRenderCondition.class);

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private ResourceResolver resourceResolver;

    @ValueMapValue(name = "group-suffix")
    private String groupSuffix;

    @PostConstruct
    public void init() {
        LOGGER.info("In ConditionalGroupRenderCondition group {}", groupSuffix);
        final String groupName = AuthoringUtils.getGroupName(request, groupSuffix);
        AtomicBoolean show = new AtomicBoolean(true);
        if (StringUtils.isNotBlank(groupName)) {
            Optional.of(resourceResolver).map(r -> r.adaptTo(Authorizable.class)).ifPresent(auth -> {
                try {
                    Iterator<Group> memberGroups = auth.declaredMemberOf();
                    while (memberGroups.hasNext()) {
                        Group g = memberGroups.next();
                        if (StringUtils.equalsIgnoreCase(g.getID(), groupName)) {
                            show.set(false);
                            break;
                        }
                    }
                } catch (RepositoryException e) {
                    LOGGER.error("RepositoryException accessing memberGroups", e);
                } catch (Exception e) {
                    LOGGER.error("Exception occurred when handling page-properties render condition", e);
                }
            });
        }
        request.setAttribute(RenderCondition.class.getName(), new com.adobe.granite.ui.components.rendercondition.SimpleRenderCondition(show.get()));
    }

}