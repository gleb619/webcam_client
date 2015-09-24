package org.test.config;

public enum Settings {

	DEBUG(true);
//	DEBUG(false);
	
	private final Boolean value;
	
	private Settings(Boolean value) {
		this.value = value;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Boolean getValue() {
		return value;
	}
}
