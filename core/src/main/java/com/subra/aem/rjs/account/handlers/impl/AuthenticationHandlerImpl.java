package com.subra.aem.rjs.account.handlers.impl;

import com.adobe.granite.crypto.CryptoException;
import com.adobe.granite.crypto.CryptoSupport;
import com.subra.aem.rjs.account.handlers.RJSAuthenticationHandler;
import com.subra.aem.rjs.account.services.UserService;
import com.subra.aem.rjs.core.utils.RJSInstanceUtils;
import com.subra.aem.rjs.core.utils.RJSResourceUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.auth.Authenticator;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.auth.core.AuthConstants;
import org.apache.sling.auth.core.AuthUtil;
import org.apache.sling.auth.core.spi.AuthenticationInfo;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.helpers.CookieHelper;
import org.subra.commons.utils.RJSJwtTokenUtils;

import javax.jcr.SimpleCredentials;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;

@Component(service = RJSAuthenticationHandler.class, immediate = true)
@ServiceRanking(10)
@ServiceDescription("RJS Authentication Handler")
@Designate(ocd = RJSAuthenticationHandler.Config.class)
public class AuthenticationHandlerImpl implements RJSAuthenticationHandler {

    public static final String TOKEN_KEY = "tokenKey";
    static final String REQUEST_URL_SUFFIX = "/j_security_check";
    private static final String U_NAME = "j_username";
    private static final String J_PASS = "j_password";
    private static final String PAR_LOOP_PROTECT = "$$login$$";
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationHandlerImpl.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private UserService userService;

    @Reference
    private CryptoSupport cryptoSupport;

    private Config config;
    private String webUserName;
    private String webUserPassword;

    private static String getReason(HttpServletRequest request, String parameter) {
        Object reason = request.getAttribute(parameter);
        if (null == reason)
            reason = request.getParameter(parameter);

        if (null == reason)
            reason = FAILURE_REASON_CODES.UNKNOWN;

        return (reason instanceof Enum) ? ((Enum<?>) reason).name().toLowerCase() : reason.toString();
    }

    @Activate
    private void activate(final Config c) {
        this.config = c;
        webUserName = getDecryptedValue(config.web_user_name());
        webUserPassword = getDecryptedValue(config.web_user_password());
        final String jwtSecretKey = getDecryptedValue(config.jwt_secret_key());
        final long expiryInMs = 3600000;
        RJSJwtTokenUtils.configure(expiryInMs, jwtSecretKey);
    }

    @Override
    public AuthenticationInfo extractCredentials(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.trace("Authenticating for {}...", request.getRequestURI());
        final boolean isLogin = HttpConstants.METHOD_POST.equals(request.getMethod()) && request.getRequestURI().endsWith(REQUEST_URL_SUFFIX);
        boolean isValid = false;
        if (isLogin) {
            final AuthenticationInfo authInfo = this.extractRequestParameterAuthentication(request);
            if (!AuthUtil.isValidateRequest(request))
                AuthUtil.setLoginResourceAttribute(request, request.getContextPath());

            if (authInfo != null) {
                final String token = userService.authenticateUser(authInfo.getUser(), String.valueOf(authInfo.getPassword()));
                if (StringUtils.equalsIgnoreCase(RJSJwtTokenUtils.extractUserName(token), authInfo.getUser())) {
                    request.setAttribute(TOKEN_KEY, token);
                    isValid = true;
                } else {
                    dropCredentials(request, response);
                }
            } else {
                dropCredentials(request, response);
            }
        } else {
            // we aren't logging in, but validate the authorization
            isValid = isAuthorized(request, response);
            LOGGER.trace("Validating existing token: {}", isValid ? "valid" : "");
        }
        return mappingTrustedAuthInfo(isValid, isLogin);
    }

    private AuthenticationInfo extractRequestParameterAuthentication(final HttpServletRequest request) {
        final String user = request.getParameter(U_NAME);
        final String pwd = request.getParameter(J_PASS);
        return StringUtils.isNoneBlank(user, pwd) ? new AuthenticationInfo(AUTH_TYPE, user, pwd.toCharArray()) : null;
    }

    protected AuthenticationInfo mappingTrustedAuthInfo(final boolean isValid, final boolean isLogin) {
        LOGGER.trace("Mapping trusted auth info, isValid: {}, isLogin: {}", isValid, isLogin);
        final SimpleCredentials repositoryCredentials = new SimpleCredentials(webUserName, webUserPassword.toCharArray());
        // return only this will go to authenticationFailed
        final AuthenticationInfo aInfo = new AuthenticationInfo(AUTH_TYPE, repositoryCredentials.getUserID());
        if (isValid) {
            // Add the credentials obj to the AuthenticationInfo obj - so it will go to authenticationSucceeded
            aInfo.put(JcrResourceConstants.AUTHENTICATION_INFO_CREDENTIALS, repositoryCredentials);
            if (isLogin) {
                // Marker property in the AuthenticationInfo object indicating a first authentication considered to be a login
                aInfo.put(AuthConstants.AUTH_INFO_LOGIN, new Object());
            }
            return aInfo;
        }
        return isLogin ? aInfo : null;
    }

    protected boolean isAuthorized(final HttpServletRequest request, final HttpServletResponse response) {
        boolean isAuthorized = false;
        if (request == null)
            return false;

        final String authKey = CookieHelper.getAuthKey(request);
        final String uri = request.getRequestURI();
        if (StringUtils.isBlank(authKey) || uri == null)
            return false;

        try {
            isAuthorized = !RJSJwtTokenUtils.isTokenExpired(authKey);
        } catch (Exception e) {
            LOGGER.error("Exception occurred authorizing ", e);
            dropCredentials(request, response);
        }
        return isAuthorized;
    }

    @Override
    public void dropCredentials(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.trace("dropCredentials-start {}", request.getPathInfo());
        CookieHelper.deleteAccountId(response);
        CookieHelper.deleteAuthKey(response, request.getServerName());
    }

    @Override
    public boolean requestCredentials(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.trace("requestCredentials-start {}", request.getPathInfo());
        String reason = getReason(request, FAILURE_REASON);
        String reasonCode = getReason(request, FAILURE_REASON_CODE);
        try {
            String path = rewrite(config.login_page_path());
            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put(Authenticator.LOGIN_RESOURCE, path);
            params.put(PAR_LOOP_PROTECT, PAR_LOOP_PROTECT);
            // append indication of previous login failure
            params.put(FAILURE_REASON, reason);
            params.put(FAILURE_REASON_CODE, reasonCode);
            AuthUtil.sendRedirect(request, response, path, params);
        } catch (IOException e) {
            LOGGER.error("[IOException] Failed to redirect to the login / change password form [{}]", e.getMessage());
        }
        final String requestLogin = request.getParameter(REQUEST_LOGIN_PARAMETER);
        return requestLogin != null && !AUTH_TYPE.equals(requestLogin);
    }

    private String rewrite(final String path) {
        String loginPagePath = config.default_login_page_path();
        try (ResourceResolver resourceResolver = RJSResourceUtils.getAdminServiceResourceResolver(resourceResolverFactory)) {
            if (resourceResolver.getResource(StringUtils.removeEndIgnoreCase(path, ".HTML")) != null
                    && RJSInstanceUtils.isPublish()) {
                loginPagePath = StringUtils.removeEndIgnoreCase(path, ".HTML");
            }
        } catch (LoginException e) {
            LOGGER.error("Error occurred when getting resource resolver - {}", e.getMessage());
        }
        return StringUtils.endsWith(loginPagePath, ".html") ? loginPagePath : loginPagePath.concat(".html");
    }

    @Override
    public void authenticationFailed(HttpServletRequest request, HttpServletResponse response, AuthenticationInfo authInfo) {
        request.setAttribute(TOKEN_KEY, StringUtils.EMPTY);
        LOGGER.trace("authenticationFailed");
        AuthUtil.sendInvalid(request, response); // 403 Forbidden
    }

    @Override
    public boolean authenticationSucceeded(HttpServletRequest request, HttpServletResponse response, AuthenticationInfo authInfo) {
        // if we are doing a redirect, then redirect
        if (HttpConstants.METHOD_POST.equals(request.getMethod()) && request.getRequestURI().endsWith(REQUEST_URL_SUFFIX) && authInfo != null && webUserName.equals(authInfo.getUser())) {
            final String tokenKey = (String) request.getAttribute(TOKEN_KEY);
            final String acid = RJSJwtTokenUtils.extractAccid(tokenKey);
            if (StringUtils.isNoneBlank(tokenKey, acid)) {
                CookieHelper.setAccountId(response, acid, false);
                CookieHelper.setAuthKey(response, request.getServerName(), tokenKey, false);
            } else {
                dropCredentials(request, response);
            }
        }
        return false;
    }

    protected String getDecryptedValue(final String value) {
        String decryptedValue = null;
        try {
            decryptedValue = cryptoSupport.isProtected(value) ? cryptoSupport.unprotect(value) : value;
        } catch (CryptoException ce) {
            LOGGER.error("Exception occurred decrypting value", ce);
        }
        return decryptedValue;
    }

}