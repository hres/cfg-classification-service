package ca.gc.ip346.classification.model;

public class Ruleset {
	private String id;
	private String name;
	private Boolean isProd;
	private Boolean active;
	private Integer rulesetId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getIsProd() {
		return isProd;
	}

	public void setIsProd(Boolean isProd) {
		this.isProd = isProd;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Integer getRulesetId() {
		return rulesetId;
	}

	public void setRulesetId(Integer rulesetId) {
		this.rulesetId = rulesetId;
	}
}
