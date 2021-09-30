package com.subra.aem.rjs.v.search;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.Row;
import javax.jcr.util.TraversingItemVisitor;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.DTD;
import javax.swing.text.html.parser.DocumentParser;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GSSearchResultExcerpt {
    private static final HTML.Tag HIGHLIGHT_TAG = HTML.Tag.STRONG;
    private static final int HIGHLIGHT_OFFSET = HIGHLIGHT_TAG.toString().length() * 2 + 5;
    private final String text;
    private final boolean hasHighlights;

    private GSSearchResultExcerpt(String text, boolean hasHighlights) {
        this.text = text;
        this.hasHighlights = hasHighlights;
    }

    private GSSearchResultExcerpt(String text) {
        this(text, false);
    }

    public static GSSearchResultExcerpt create(final GSSearchResult result, final Set<String> excerptPropNames, final int maxLength) throws RepositoryException {
        Node node = result.getResultNode();
        Node content = node.hasNode("jcr:content") ? node.getNode("jcr:content") : node;
        final List<GSSearchResultExcerpt> excerpt = new ArrayList<>();
        final int prefixLength = node.getPath().length() + 1;
        try {
            content.accept(new TraversingItemVisitor.Default(true) {
                @Override
                protected void entering(Property property, int level) throws RepositoryException {
                    if (!excerptPropNames.contains(property.getName())) {
                        return;
                    }
                    String relPath = property.getPath().substring(prefixLength);
                    GSSearchResultExcerpt e = GSSearchResultExcerpt.createGSSearchResultExcerpt(property, relPath,
                            result.getRow(), maxLength);
                    if (e == null) {
                        return;
                    }
                    if (e.hasHighlights) {
                        excerpt.clear();
                        excerpt.add(e);
                        throw new RepositoryException();
                    }
                    if (excerpt.isEmpty()) {
                        excerpt.add(e);
                    }
                }
            });
        } catch (RepositoryException e) {
            if (excerpt.isEmpty()) {
                throw e;
            }
        }
        if (excerpt.isEmpty()) {
            return new GSSearchResultExcerpt("");
        }
        return excerpt.get(0);
    }

    private static String getFirstSpan(String excerpt) {
        int start = excerpt.indexOf("<span>");
        if (start != -1) {
            int end = excerpt.indexOf("</span>", start);
            if (end != -1) {
                return excerpt.substring(start + "<span>".length(), end);
            }
        }
        return excerpt;
    }

    private static GSSearchResultExcerpt createGSSearchResultExcerpt(Property property, String relPath, Row row, final int maxLength) throws RepositoryException {
        if ((property.isMultiple()) || (property.getLength() == 0L)) {
            return null;
        }
        Value v = row.getValue("rep:excerpt(" + relPath + ")");
        if (v == null) {
            return null;
        }
        final int[] numHighlights = {0};
        HTMLEditorKit.Parser parser = HTMLParser.getInstance();
        StringReader strReader = new StringReader(getFirstSpan(v.getString()));
        final StringBuffer text = new StringBuffer();
        try {
            parser.parse(strReader, new HTMLEditorKit.ParserCallback() {
                private boolean tagOpened = false;
                private int tagOpenedPos = 0;
                private boolean highlighted = false;
                private boolean complete = false;

                @Override
                public void handleText(char[] data, int pos) {
                    if (this.complete) {
                        return;
                    }
                    stripTagsAndAppend(new String(data));
                    if ((!this.highlighted) && (text.length() > getMaxLength())) {
                        if (numHighlights[0] > 0) {
                            if (this.tagOpened) {
                                text.setLength(this.tagOpenedPos);
                                this.tagOpened = false;
                            }
                            if (text.length() > getMaxLength()) {
                                for (int i = getMaxLength(); i >= 0; i--) {
                                    if (Character.isWhitespace(text.charAt(i))) {
                                        text.setLength(i + 1);
                                        text.append("...");
                                        break;
                                    }
                                }
                                this.complete = true;
                            }
                        } else {
                            if (this.tagOpened) {
                                text.setLength(this.tagOpenedPos + 1);
                            }
                            for (int i = text.length() - getMaxLength() / 3; i >= 0; i--) {
                                if (Character.isWhitespace(text.charAt(i))) {
                                    text.delete(0, i + 1);
                                    if (this.tagOpenedPos > i + 1) {
                                        this.tagOpenedPos -= i + 1;
                                        break;
                                    }
                                    this.tagOpenedPos = 0;
                                    break;
                                }
                            }
                        }
                    }
                }

                @Override
                public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
                    if (this.complete) {
                        return;
                    }
                    if (t == GSSearchResultExcerpt.HIGHLIGHT_TAG) {
                        text.append("<");
                        text.append(t);
                        text.append(">");
                        this.highlighted = true;
                        numHighlights[0] += 1;
                    }
                }

                @Override
                public void handleEndTag(HTML.Tag t, int pos) {
                    if (this.complete) {
                        return;
                    }
                    if (t == GSSearchResultExcerpt.HIGHLIGHT_TAG) {
                        text.append("</");
                        text.append(t);
                        text.append(">");
                        this.highlighted = false;
                    }
                }

                private int getMaxLength() {
                    return maxLength + numHighlights[0] * GSSearchResultExcerpt.HIGHLIGHT_OFFSET;
                }

                private void stripTagsAndAppend(String s) {
                    for (int i = 0; i < s.length(); i++) {
                        char c = s.charAt(i);
                        switch (c) {
                            case '<':
                                if (!this.tagOpened) {
                                    this.tagOpenedPos = text.length();
                                }
                                this.tagOpened = true;
                                text.append(c);
                                break;
                            case '>':
                                text.setLength(this.tagOpenedPos);
                                this.tagOpened = false;
                                break;
                            default:
                                text.append(c);
                        }
                    }
                }
            }, true);
        } catch (IOException e) {
            throw new RepositoryException(e);
        }
        return new GSSearchResultExcerpt(text.toString(), numHighlights[0] > 0);
    }

    public String getText() {
        return this.text;
    }

    public boolean hasHighlights() {
        return this.hasHighlights;
    }

    private static final class HTMLParser extends HTMLEditorKit {
        private static final long serialVersionUID = 383121295616881915L;

        public static Parser getInstance() {
            return new Parser() {
                public void parse(Reader r, ParserCallback cb, boolean ignoreCharSet) throws IOException {
                    new DocumentParser(DTDEx.INSTANCE).parse(r, cb, ignoreCharSet);
                }
            };
        }
    }

    private static final class DTDEx extends DTD {
        static final DTD INSTANCE = ParserDelegatorEx.createDTD();

        private DTDEx(String str) {
            super(str);
        }

        private static final class ParserDelegatorEx extends ParserDelegator {
            private static final long serialVersionUID = -7151679094537195031L;

            static DTD createDTD() {
                DTD dtd = new DTDEx(null);
                ParserDelegator.createDTD(dtd, "html32");
                dtd.defEntity("apos", 65536, 39);
                return dtd;
            }
        }
    }

}