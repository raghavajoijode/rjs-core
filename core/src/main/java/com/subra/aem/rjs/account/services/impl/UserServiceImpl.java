package com.subra.aem.rjs.account.services.impl;

import com.subra.aem.rjs.account.services.UserService;
import com.subra.aem.rjs.core.helpers.SubraGenericService;
import org.apache.commons.lang3.BooleanUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.constants.HttpType;
import org.subra.commons.dtos.account.Login;
import org.subra.commons.dtos.account.NewUser;
import org.subra.commons.dtos.account.User;
import org.subra.commons.helpers.CommonHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(immediate = true, service = UserService.class)
@Designate(ocd = UserServiceImpl.Config.class)
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final String USER_API_URL_VALUE = "http://localhost:8301/api/v1/users";
    private static final String CREATE_USER_PATH_VALUE = "register";
    private static final String AUTHENTICATE_USER_PATH_VALUE = "authenticate";
    private static final String VALIDATE_USER_PATH_VALUE = "validate";
    private static final String USER_API_URL_TEMPLATE = "user.api.url.template";
    private static final String CREATE_USER_PATH = "create.user.path";
    private static final String AUTHENTICATE_USER_PATH = "authenticate.user.path";
    private static final String VALIDATE_USER_PATH = "validate.user.path";
    @Reference
    SubraGenericService serviceHelper;
    private String userApiServicePath;
    private String createUserPath;
    private String authenticateServicePath;
    private String validateUserNamePath;

    @Activate
    protected void activate(final Config config) {
        userApiServicePath = config.userApiUrlTemplate();
        createUserPath = config.createUserPath();
        authenticateServicePath = config.authenticateUserPath();
        validateUserNamePath = config.validateUserPath();
    }

    @Override
    public String createUser(String email, String password, String name) {
        NewUser newUser = new NewUser();
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setPassword(password);
        Map<String, Object> response = serviceHelper.callBackendService(userApiServicePath, createUserPath, null, null,
                newUser, HttpType.POST);
        User userDto = (User) response.get("user");
        return userDto != null ? userDto.getEmail() : null;
    }

    @Override
    public boolean isExistingUser(String email) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("email", email);
        Map<String, Object> response = serviceHelper.callBackendService(userApiServicePath, validateUserNamePath, null,
                parameters, null, HttpType.GET);
        return BooleanUtils.toBoolean((boolean) response.get("isExisting"));
    }

    @Override
    public String authenticateUser(String email, String password) {
        User user = null;
        Login login = new Login();
        login.setEmail(email);
        login.setPassword(password);
        Map<String, Object> response = serviceHelper.callBackendService(userApiServicePath, authenticateServicePath,
                null, null, login, HttpType.POST);

        try {
            user = CommonHelper.convertToClass(response.get("user"), User.class);
        } catch (IOException e) {
            LOGGER.error("Error converting to User...", e);
            e.printStackTrace();
        }
        return user != null ? (String) response.getOrDefault("token", null) : null;
    }

    @Override
    public User getUser(String userId) {
        return null;
    }

    @ObjectClassDefinition(name = "Subra - SubraUserServiceImpl configuration", description = "SubraUserServiceImpl configuration")
    public @interface Config {
        @AttributeDefinition(name = USER_API_URL_TEMPLATE, description = "Template url for user service")
        String userApiUrlTemplate() default USER_API_URL_VALUE;

        @AttributeDefinition(name = CREATE_USER_PATH, description = "CREATE User Path for user service")
        String createUserPath() default CREATE_USER_PATH_VALUE;

        @AttributeDefinition(name = AUTHENTICATE_USER_PATH, description = "AUTHENTICATE User Path for user service")
        String authenticateUserPath() default AUTHENTICATE_USER_PATH_VALUE;

        @AttributeDefinition(name = VALIDATE_USER_PATH, description = "Validate User Path for user service")
        String validateUserPath() default VALIDATE_USER_PATH_VALUE;
    }

}
