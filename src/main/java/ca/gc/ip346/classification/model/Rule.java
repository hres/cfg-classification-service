package ca.gc.ip346.classification.model;

public enum Rule {
	refamt     (0),
	fop        (1),
	shortcut   (2),
	thresholds (3),
	init       (4),
	tier       (5);

	private Integer code;

	/**
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	private Rule(Integer code) {
		this.code = code;
	}
}
