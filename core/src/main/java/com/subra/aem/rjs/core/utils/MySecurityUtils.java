package com.subra.aem.rjs.core.utils;

import org.apache.commons.codec.binary.Base32;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.util.Arrays;
import java.util.Random;

public class MySecurityUtils {
    // TODO: WIP
    private static final Logger LOGGER = LoggerFactory.getLogger(MySecurityUtils.class);

    private MySecurityUtils() {
        throw new IllegalStateException(this.getClass().getSimpleName());
    }

    public static String updateSecurityKey(Authorizable userId, Session adminSession) throws RepositoryException {
        String key = null;

        try {
            ValueFactory vf = adminSession.getValueFactory();
            String userPath = userId.getPath();
            String userProfilePath = userPath + "/profile";
            key = createSecretKey();

            Value val = vf.createValue(key);
            if (adminSession.itemExists(userProfilePath)) {
                Node profile = adminSession.getNode(userProfilePath);
                profile.setProperty("secretKey", val);
                adminSession.save();

            } else {
                Node user = adminSession.getNode(userPath);
                Node profile = user.addNode("profile", "nt:unstructured");
                profile.setProperty("secretKey", val);
                adminSession.save();
            }

        } catch (Exception e) {
            LOGGER.error("[Exception] while creating security key for user: {}", userId, e);

        } finally {
            adminSession.logout();
        }
        return key;
    }

    private static String createSecretKey() {
        byte[] buffer = new byte[30];
        new Random().nextBytes(buffer);

        byte[] secretKey = Arrays.copyOf(buffer, 10);
        return new Base32().encodeToString(secretKey);
    }
}
