package com.subra.aem.rjs.core.component.models;

public interface Heading {

	String PN_DESIGN_DEFAULT_TYPE = "type";

	String PN_TITLE_LINK_DISABLED = "linkDisabled";

	default String getHeading() {
		throw new UnsupportedOperationException();
	}

	default String getType() {
		throw new UnsupportedOperationException();
	}

	default String getLinkURL() {
		throw new UnsupportedOperationException();
	}

	default boolean isLinkDisabled() {
		throw new UnsupportedOperationException();
	}

}
