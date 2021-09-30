package com.subra.aem.rjs.examples.documentlibrary.beans;

public class Asset implements Comparable<Asset> {

	private String name;
	private String imageType;
	private String modifiedBy;
	private String modifiedDate;
	private String path;
	private String title;
	private String type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public int compareTo(Asset other) {
		return name.compareTo(other.name);
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof Asset && ((Asset) object).getPath() == this.path;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
