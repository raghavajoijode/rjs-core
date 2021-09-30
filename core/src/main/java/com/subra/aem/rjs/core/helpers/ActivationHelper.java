package com.subra.aem.rjs.core.helpers;

import org.apache.commons.io.FileUtils;
import org.subra.commons.exceptions.RJSRuntimeException;

import java.io.File;
import java.io.IOException;

public interface ActivationHelper {

    public static void clearLogFiles(final String slingHomePath) {
        final String logsFolderPath = slingHomePath.concat("/logs");
        try {
            FileUtils.cleanDirectory(new File(logsFolderPath));
        } catch (final IOException e) {
            throw new RJSRuntimeException("Caught Expected exception : ", e);
        }
    }

}