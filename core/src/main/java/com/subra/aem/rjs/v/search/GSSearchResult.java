package com.subra.aem.rjs.v.search;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.DamConstants;
import com.subra.aem.rjs.v.search.utils.GSSearchResultConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.Row;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public final class GSSearchResult {

    private static final Logger LOGGER = LoggerFactory.getLogger(GSSearchResult.class);

    private final Node resultNode;
    private final Double score;
    private final String path;
    private final Row row;
    private String category;

    public GSSearchResult(Row row) throws RepositoryException {
        Value jcrScore = row.getValue("jcr:score");
        if (row.getValue("jcr:score") != null) {
            score = jcrScore.getDouble();
        } else {
            score = (double) 0;
        }

        resultNode = getPageOrAsset(row.getNode());
        if (resultNode != null) {
            path = resultNode.getPath();
        } else {
            path = null;
        }
        this.row = row;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getTypeScore() {
        int typeScore = 0;
        try {
            // Online -> PDF -> Doc
            if ((GSSearchManagerUtils.isPage(resultNode))) { // Page
                typeScore = 10;
            } else if (StringUtils.equalsIgnoreCase(getIconType(), "pdf")) { // PDF
                typeScore = 5;
            }
        } catch (RepositoryException e) {
            LOGGER.error("Error checking if result type is page", e);
        }
        return typeScore;
    }

    private Node getPageOrAsset(Node node) throws RepositoryException {
        if (node != null) {
            while ((!GSSearchManagerUtils.isPageOrAsset(node)) && (node.getName().length() > 0)) {
                node = node.getParent();
            }
        }
        return node;
    }

    public String getDescription() {
        String description = "";
        try {
            if (resultNode.hasProperty(GSSearchResultConstants.PROPERTY_SRCH_DESC)) {
                return resultNode.getProperty(GSSearchResultConstants.PROPERTY_SRCH_DESC).getString();
            }
            if (resultNode.hasProperty(GSSearchResultConstants.PROPERTY_DC_DESCRIPTION)) {
                if (resultNode.getProperty(GSSearchResultConstants.PROPERTY_DC_DESCRIPTION).isMultiple()) {
                    Value[] value = resultNode.getProperty(GSSearchResultConstants.PROPERTY_DC_DESCRIPTION).getValues();
                    if ((!value[0].getString().isEmpty()) && (value[0].getString() != null)) {
                        return (value[0].getString());
                    }
                }
                return resultNode.getProperty(GSSearchResultConstants.PROPERTY_DC_DESCRIPTION).getString();
            }
        } catch (RepositoryException e) {
            LOGGER.error("Cannot get description. Return empty string.");
        }
        return description;
    }

    public Date getLastModifiedDate() {
        try {
            if (resultNode.hasProperty(JcrConstants.JCR_LASTMODIFIED)) {
                return resultNode.getProperty(JcrConstants.JCR_LASTMODIFIED).getDate().getTime();
            }
        } catch (RepositoryException e) {
            LOGGER.error("Cannot get {}. Return empty string.", JcrConstants.JCR_LASTMODIFIED);
        }
        return new Date(Long.MIN_VALUE);
    }

    public String getTitle() {
        String title = "";
        try {
            String seoTitle = resultNode.getProperty(GSSearchResultConstants.PROPERTY_SEO_TITLE).getString();
            if (seoTitle != null) {
                return seoTitle;
            }
        } catch (RepositoryException e) {
            LOGGER.error("Cannot getting title. Return empty string.");
        }
        try {
            String primaryType = resultNode.getPrimaryNodeType().getName();
            if (primaryType.equals(GSSearchResultConstants.NODE_TYPE_CQ_PAGE) && resultNode.hasProperty(GSSearchResultConstants.PROPERTY_JCR_TITLE)) {
                return resultNode.getProperty(GSSearchResultConstants.PROPERTY_JCR_TITLE).getString();
            } else if (primaryType.equals(DamConstants.NT_DAM_ASSET) && resultNode.hasProperty(GSSearchResultConstants.PROPERTY_DC_TITLE)) {
                if (resultNode.getProperty(GSSearchResultConstants.PROPERTY_DC_TITLE).isMultiple()) {
                    Value[] value = resultNode.getProperty(GSSearchResultConstants.PROPERTY_DC_TITLE).getValues();
                    if ((!value[0].getString().isEmpty()) && (value[0].getString() != null)) {
                        return (value[0].getString());
                    }
                }
                title = resultNode.getProperty(GSSearchResultConstants.PROPERTY_DC_TITLE).getString();
            }
            if (StringUtils.isBlank(title)) {
                LOGGER.info("Cannot get the title. Use node name instead.");
                title = resultNode.getName();
            }
        } catch (RepositoryException e) {
            LOGGER.error("Cannot getting title. Return empty string.");
        }
        return title;
    }

    public String getUrl() {
        String url = path;
        try {
            String primaryType = resultNode.getPrimaryNodeType().getName();
            if (primaryType.equals(GSSearchResultConstants.NODE_TYPE_CQ_PAGE)) {
                url = path + ".html";
            }
        } catch (RepositoryException e) {
            LOGGER.error("Error retrieving node type", e);
        }
        return url;
    }

    public String getIconType() {
        final String extension = getExtension();
        if (!extension.equalsIgnoreCase("html")) {
            return extension;
        }
        return null;
    }

    public String getExtension() {
        return StringUtils.substringAfterLast(getUrl(), ".");
    }

    public String getExcerpt() {
        String excerpt = "";
        try {
            String[] excerptProperties = {"text", "jcr:description", "jcr:title"};
            final Set<String> excerptPropNames = new HashSet<>(Arrays.asList(excerptProperties));
            GSSearchResultExcerpt resultExcerpt = GSSearchResultExcerpt.create(this, excerptPropNames, GSSearchResultConstants.EXCERPT_LENGTH);
            if (resultExcerpt != null) {
                excerpt = resultExcerpt.getText();
            }
        } catch (RepositoryException e) {
            LOGGER.error("Cannot getting excerpt. Return empty string.");
        }

        return excerpt;
    }

    public Node getResultNode() {
        return resultNode;
    }

    public Double getScore() {
        return score;
    }

    public String getPath() {
        return path;
    }

    public Row getRow() {
        return row;
    }

}
