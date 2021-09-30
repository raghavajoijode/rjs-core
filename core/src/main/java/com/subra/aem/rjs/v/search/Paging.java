package com.subra.aem.rjs.v.search;

import java.util.List;

/**
 * @author Raghava Joijode
 */
public class Paging {

    private int currentPageNumber;
    private int pageSize;
    private int totalPages;
    private int totalRecordsSize;
    private int prevPageOffset;
    private int nextPageOffset;
    private boolean isFirstPage;
    private boolean isLastPage;
    private List<PageItem> list;

    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    public void setCurrentPageNumber(int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalRecordsSize() {
        return totalRecordsSize;
    }

    public void setTotalRecordsSize(int totalRecordsSize) {
        this.totalRecordsSize = totalRecordsSize;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public void setLastPage(boolean lastPage) {
        this.isLastPage = lastPage;
    }

    public boolean isFirstPage() {
        return isFirstPage;
    }

    public void setFirstPage(boolean firstPage) {
        this.isFirstPage = firstPage;
    }

    public List<PageItem> getList() {
        return list;
    }

    public void setList(List<PageItem> list) {
        this.list = list;
    }

    public int getPrevPageOffset() {
        return prevPageOffset;
    }

    public void setPrevPageOffset(int prevPageOffset) {
        this.prevPageOffset = prevPageOffset;
    }

    public int getNextPageOffset() {
        return nextPageOffset;
    }

    public void setNextPageOffset(int nextPageOffset) {
        this.nextPageOffset = nextPageOffset;
    }

    public static class PageItem {

        private String displayNumber;
        private int offset;
        private boolean isCurrentPage;
        private boolean isEllipse;

        public String getDisplayNumber() {
            return displayNumber;
        }

        public void setDisplayNumber(String displayNumber) {
            this.displayNumber = displayNumber;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public boolean isCurrentPage() {
            return isCurrentPage;
        }

        public void setCurrentPage(boolean currentPage) {
            isCurrentPage = currentPage;
        }

        public boolean isEllipse() {
            return isEllipse;
        }

        public void setEllipse(boolean ellipse) {
            isEllipse = ellipse;
        }

    }

}
