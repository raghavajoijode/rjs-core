package com.subra.aem.rjs.examples.documentlibrary.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class FileAssetComparator implements Comparator<Asset> {

    private final Logger log = LoggerFactory.getLogger(FileAssetComparator.class);

    public int compare(Asset file1, Asset file2) {
        // 06/14/2016 02:45 PM
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");

        Date file1Date = null;
        Date file2Date = null;
        try {
            file1Date = formatter.parse(file1.getModifiedDate());
            file2Date = formatter.parse(file2.getModifiedDate());

        } catch (ParseException e) {
            log.error(e.getMessage());
        }
        if (file1Date == null) {
            return 0;
        } else if (file1Date.after(file2Date)) {
            return 1;
        } else if (file1Date.before(file2Date)) {
            return -1;
        } else {
            return 0;
        }
    }

}
