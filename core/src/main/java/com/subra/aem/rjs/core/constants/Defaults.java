package com.subra.aem.rjs.core.constants;

/**
 * @author Raghava Joijode
 */
public enum Defaults {

	UTF_CHARSET("UTF-8"),

	DEFAULT_PAGE_DESC("Default page description goes here."),

	DEFAULT_SITE_NAME("Subra Group"),

	DEFAULT_IMAGE("/content/dam/subra/defaults/image.jpg"),

	EXTERNALIZER_SCHEMA_HTTPS("https"), EXTERNALIZER_SCHEMA_HTTP("http"),

	EXTERNALIZER_DOMAIN_PUBLISH("publish"), EXTERNALIZER_DOMAIN_AUTHOR("author"),

	TWITTER_HANDLE("@Raghava_joijode");

	private String value;
	
	private Defaults(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

}
