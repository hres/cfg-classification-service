package ca.gc.ip346.classification.model;

public class Ruleset {
	private String id;
	private String name;
	private Boolean isProd;
	private String location;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the isProd
	 */
	public Boolean getIsProd() {
		return isProd;
	}

	/**
	 * @param isProd the isProd to set
	 */
	public void setIsProd(Boolean isProd) {
		this.isProd = isProd;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
}
