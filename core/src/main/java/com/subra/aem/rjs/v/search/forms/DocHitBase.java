package com.subra.aem.rjs.v.search.forms;


import javax.jcr.Node;
import javax.jcr.RepositoryException;

public class DocHitBase {

    private final Node node;
    private Node pageOrAssetNode = null;

    public DocHitBase(Node node) {
        this.node = node;
    }

    public String getTitle() throws RepositoryException {
        if (node.hasProperty("dc:title")) {
            return node.getProperty("dc:title").getString();
        }
        if (node.hasProperty("jcr:title")) {
            return node.getProperty("jcr:title").getString();
        }
        return getPageOrAsset().getName();
    }

    public String getURL() throws RepositoryException {
        Node n = getPageOrAsset();
        String url = n.getPath();
        if (isPage(n)) {
            url = url + ".html";
        }
        return url;
    }

    public String getIcon() throws RepositoryException {
        String url = getURL();
        int idx = url.lastIndexOf('.');
        if (idx == -1) {
            return "";
        }
        String ext = url.substring(idx + 1);
        if (ext.equals("html")) {
            return "";
        }
        return "<img src='/etc/designs/default/0.gif'/>";
    }

    public String getExtension() throws RepositoryException {
        String url = getURL();
        int idx = url.lastIndexOf('.');
        return idx >= 0 ? url.substring(idx + 1) : "";
    }

    private boolean isPageOrAsset(Node n) throws RepositoryException {
        return (isPage(n)) || (n.isNodeType("dam:Asset"));
    }

    protected boolean isPage(Node n) throws RepositoryException {
        return (n.isNodeType("cq:Page")) || (n.isNodeType("cq:PseudoPage"));
    }

    protected Node getPageOrAsset() throws RepositoryException {
        if (this.pageOrAssetNode != null) {
            return this.pageOrAssetNode;
        }

        Node n = this.node;
        while ((!isPageOrAsset(n)) && (n.getName().length() > 0)) {
            n = n.getParent();
        }
        this.pageOrAssetNode = n;
        return n;
    }
}