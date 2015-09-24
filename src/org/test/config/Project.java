package org.test.config;

public enum Project {

	DATA("data/"),
	PHOTOS("photos/"),
	MANIFEST("60c9f40c");
	
	private final String value;
	
	private Project(String value) {
		this.value = value;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String value() {
		return value;
	}
	
}
