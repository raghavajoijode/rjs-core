package com.subra.aem.rjs.v.search.utils;

import com.subra.aem.rjs.v.search.GSSearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class GSSearchResultComparator implements Comparator<GSSearchResult> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GSSearchResultComparator.class);

    String orderBy;

    public GSSearchResultComparator() {
        this.orderBy = "score";
    }

    public GSSearchResultComparator(String orderBy) {
        this.orderBy = orderBy;
    }

    @Override
    public int compare(GSSearchResult result1, GSSearchResult result2) {
        int result;
        switch (orderBy) {
            case "title":
                String title1 = result1.getTitle();
                String title2 = result2.getTitle();
                result = title2.compareTo(title1);
                break;
            case "date":
                Date date1 = result1.getLastModifiedDate();
                Date date2 = result2.getLastModifiedDate();
                result = date2.compareTo(date1);
                break;
            case "category":
                String category1 = result1.getCategory();
                String category2 = result2.getCategory();
                result = category2.compareTo(category1);
                break;
            case "type":
                Integer type1 = result1.getTypeScore();
                Integer type2 = result2.getTypeScore();
                result = type2.compareTo(type1);
                break;
            default:
                Double score1 = result1.getScore();
                Double score2 = result2.getScore();
                result = score1.compareTo(score2);
                break;
        }

        if (result == 0) {
            try {
                Calendar date1 = result1.getResultNode().getProperty("jcr:created").getDate();
                Calendar date2 = result2.getResultNode().getProperty("jcr:created").getDate();
                result = date1.compareTo(date2);
            } catch (RepositoryException e) {
                LOGGER.error("Exception occurred when retrieving property jcr:created", e);
            }
        }
        return result;
    }

}
