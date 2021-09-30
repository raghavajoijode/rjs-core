package com.subra.aem.rjs.solr.utils;

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;

/**
 * @author Raghava Joijode
 */
public final class SolrUtils {

    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String LAST_MODIFIED = "lastModified";
    public static final String CONTENT_TYPE = "contentType";
    private SolrUtils() {
    }

    /**
     * This method is used to extract the tags from the content page
     *
     * @param pageContent jcr:content resource of a page
     * @return Array of tags which are attached to the page. Empty array if no tags
     * are attached
     */
    public static String[] getPageTags(Resource pageContent) {
        return Optional.ofNullable(pageContent).map(Resource::getParent).map(r -> r.adaptTo(Page.class)).map(page -> {
            Tag[] tags = page.getTags();
            String[] tagsArray = new String[tags.length];
            for (int i = 0; i < tags.length; i++) {
                Tag tag = tags[i];
                tagsArray[i] = tag.getTitle();
            }
            return tagsArray;
        }).orElse(new String[0]);
    }

    /**
     * This method converts jcr formatted date to Solr specification format
     *
     * @param cal Takes input as Calendar
     * @return Solr formatted date of type string
     */
    public static String solrDate(Calendar cal) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        return dateFormat.format(cal.getTime()) + "Z";
    }

    /**
     * This method returns "" if string is null.
     *
     * @param property Takes input as string
     * @return String value. if string value is "null" then ""
     */
    public static String checkNull(String property) {
        if (StringUtils.isEmpty(property)) {
            return "";
        }
        return property;

    }

}
