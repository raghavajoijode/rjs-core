package com.subra.aem.rjs.v.search.utils;

import com.day.crx.JcrConstants;

public class GSSearchResultConstants {
    private GSSearchResultConstants() {
        //
    }
    public static final String NODE_TYPE_CQ_PAGE = "cq:Page";
    public static final String NODE_TYPE_CQ_PSEUDO_PAGE = "cq:PseudoPage";
    public static final String PROPERTY_JCR_TITLE = JcrConstants.JCR_CONTENT + "/jcr:title";
    public static final String PROPERTY_DC_TITLE = JcrConstants.JCR_CONTENT + "/metadata/dc:title";
    public static final String PROPERTY_DC_DESCRIPTION = JcrConstants.JCR_CONTENT + "/metadata/dc:description";
    public static final String PROPERTY_SRCH_DESC = JcrConstants.JCR_CONTENT + "/data/srchdisp";
    public static final String PROPERTY_SEO_TITLE = "seoTitle";
    public static final String PDF_FILE = "application/pdf";
    public static final String MSWORD_FILE = "application/msword";
    public static final String APPL_VND_FILE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    public static final int EXCERPT_LENGTH = 150;
    public static final String SLASH = "/";
}
