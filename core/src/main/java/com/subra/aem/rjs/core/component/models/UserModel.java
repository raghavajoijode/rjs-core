package com.subra.aem.rjs.core.component.models;

import com.subra.aem.rjs.account.services.UserService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.dtos.account.User;
import org.subra.commons.helpers.CommonHelper;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Optional;

@Model(adaptables = {SlingHttpServletRequest.class})
public class UserModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserModel.class);

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private String suid;

    @OSGiService
    private UserService userService;

    private User userDto;

    @PostConstruct
    protected void init() {
        LOGGER.trace("UserModel init..");
        userDto = CommonHelper.getCacheData(request, suid, () -> userService.getUser(suid));
    }

    public String getName() {
        return Optional.ofNullable(userDto.getName()).orElse(null);
    }

}
