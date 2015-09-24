package org.test.data.types;

import org.test.view.LogPane;

public interface Logger {

	public void log(String message);
	public void debug(String message);
	public void error(String message);
	public LogPane getLogPane();
}
