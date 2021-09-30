package com.subra.aem.rjs.account.handlers;

import org.apache.sling.auth.core.spi.AuthenticationFeedbackHandler;
import org.apache.sling.auth.core.spi.AuthenticationHandler;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

public interface RJSAuthenticationHandler extends AuthenticationHandler, AuthenticationFeedbackHandler {

    String AUTH_TYPE = "RJS_AUTH";

    @ObjectClassDefinition(name = "RJS AuthenticationHandler Interface", description = "Two Factor Authentication Handler Interface Configuration")
    @interface Config {

        @AttributeDefinition(name = "Path", description = "Repository path for which this authentication handler should be used by Sling. If this is empty, the authentication handler will be disabled. (path)")
        String path() default "/content/rjs";

        @AttributeDefinition(name = "Path", description = "Repository path for which this authentication handler should be used by Sling. If this is empty, the authentication handler will be disabled. (path)")
        String auth_type() default AUTH_TYPE;

        @AttributeDefinition(name = "Custom Login Page", description = "If no mappings are defined, nor no mapping matches the request, this is the default login page being redirected to. This can be overridden in the content page configuration")
        String login_page_path() default "/libs/granite/core/content/login";

        @AttributeDefinition(name = "Web User Name")
        String web_user_name() default "{d12c3a8fef5c00b348a3bd4052a88c856e3b9ed457ba9f50827e5cdd5a5d0b6a}";

        @AttributeDefinition(name = "Web User Password")
        String web_user_password() default "{b0cf2c15da0a2a02c48db1c4abe99a737f7b16919fd5c9c91b545facd7016f2d}";

        @AttributeDefinition(name = "Default Login Page", description = "If no mappings are defined, nor no mapping matches the request, this is the default login page being redirected to. This can be overridden in the content page configuration")
        String default_login_page_path() default "/libs/granite/core/content/login";

        @AttributeDefinition(name = "Default Login Page", description = "If no mappings are defined, nor no mapping matches the request, this is the default login page being redirected to. This can be overridden in the content page configuration")
        String jwt_secret_key() default "{a27f7a0bba35eaa3117fdb4e9c114d5f6d1213d00be375013f6b805d3c64cb285dab8799c9c2ab74081555b6ad2c946e}";
    }

}
