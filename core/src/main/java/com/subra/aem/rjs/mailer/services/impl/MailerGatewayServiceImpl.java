package com.subra.aem.rjs.mailer.services.impl;

import com.subra.aem.rjs.mailer.services.MailerGatewayService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = MailerGatewayService.class, immediate = true)
@ServiceDescription("Subra Mailer Gateway Service")
@Designate(ocd = MailerGatewayService.Config.class, factory = true)
public class MailerGatewayServiceImpl implements MailerGatewayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailerGatewayServiceImpl.class);
    private String host;
    private int port;
    private String user;
    private String password;
    private boolean isSSL;
    private boolean isDebug;
    private String fromAddress;
    private String fromName;

    @Activate
    protected void activate(final Config config) {
        setHost(config.smtp_host());
        setPort(config.smtp_port());
        setUser(config.smtp_user());
        setPassword(config.smtp_password());
        setSSL(config.smtp_ssl());
        setDebug(config.smtp_debug());
        setFromAddress(config.smtp_from_address());
        setFromName(config.smtp_from_name());
        LOGGER.trace("Email Service Connector Started");
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSSL() {
        return isSSL;
    }

    public void setSSL(boolean isSSL) {
        this.isSSL = isSSL;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean isDebug) {
        this.isDebug = isDebug;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    private void createConnection(Email email) throws EmailException {
        email.setHostName(host);
        email.setSmtpPort(port);
        email.setAuthenticator(new DefaultAuthenticator(user, password));
        email.setSSLOnConnect(isSSL);
        if (email.getFromAddress() == null)
            email.setFrom(fromAddress, fromName);
        email.setDebug(isDebug);
        LOGGER.trace("Created Connection...with host \"{}\" for sending mail with subject \"{}\"", email.getHostName(),
                email.getSubject());
    }

    @Override
    public boolean send(Email email) throws EmailException {
        createConnection(email);
        String messageId = email.send();
        LOGGER.trace("Sent email, transaction id - {}", messageId);
        return StringUtils.isNotBlank(messageId);
    }

}
