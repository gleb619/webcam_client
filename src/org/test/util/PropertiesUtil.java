package org.test.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.test.data.types.InformationStore;

public class PropertiesUtil {

	private InformationStore informationStore;
	private Boolean result;

	public PropertiesUtil(InformationStore informationStore) {
		super();
		this.informationStore = informationStore;
	}

	public InformationStore getInformationStore() {
		return informationStore;
	}

	public void setInformationStore(InformationStore informationStore) {
		this.informationStore = informationStore;
	}

	public Boolean getResult() {
		return result;
	}

	public void setResult(Boolean result) {
		this.result = result;
	}

	public PropertiesUtil init(String path, Boolean defaultSettings) {
		setResult(readProperties(loadProperties(path, defaultSettings)));
		
		return this;
	}

	private Properties loadProperties(String path, Boolean defaultSettings) {
		Properties prop = new Properties();
		try {
			InputStream input = null;

			try {
				input = new FileInputStream(path);
				prop.load(input);

			} catch (IOException ex) {
				System.err.println("Launcher.loadProperties()#ERROR: "
						+ ex.getMessage());
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						System.err.println("Launcher.loadProperties()#ERROR: "
								+ e.getMessage());
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Launcher.loadProperties()#ERROR: "
					+ e.getMessage());
		}

		return prop;
	}

	private Boolean readProperties(Properties properties) {
		if (properties.size() == 0) {
			System.err.println("Config file is empty, will work with default settings");
			return false;
		}
		
		properties.forEach((_key, value) -> {
			if (_key.equals("key")) {
				informationStore.setKey((String) value);
			} else if (_key.equals("urlTemplate")) {
				informationStore.setUrlTemplate((String) value);
			} else if (_key.equals("heartbeat")) {
				informationStore.setHeartbeat((String) value);
			} else if (_key.equals("heartbeatPort")) {
				informationStore.setHeartbeatPort(new Integer(readInteger((String) value)));
			} else if (_key.equals("path")) {
				informationStore.setPath(checkPath((String) value));
				FileUtil.checkDirExist(informationStore.getPath());
			} else if (_key.equals("allowDebug")) {
				informationStore.setAllowDebug(new Boolean(readBoolean((String) value)));
			} else if (_key.equals("captureWidth")) {
				informationStore.setCaptureWidth(new Integer(readInteger((String) value)));
			} else if (_key.equals("captureHeight")) {
				informationStore.setCaptureHeight(new Integer(readInteger((String) value)));
			} else if (_key.equals("frequency")) {
				informationStore.setFrequency(new Integer(readInteger((String) value)));
			} else if (_key.equals("compressLevel")) {
				informationStore.setCompressLevel(new Integer(readInteger((String) value)));
			} else if (_key.equals("showPane")) {
				informationStore.setShowPane(new Boolean(readBoolean((String) value)));
			} else if (_key.equals("showLogPane")) {
				informationStore.setShowLogPane(new Boolean(readBoolean((String) value)));
			}
		});

		return true;
	}

	private Integer readInteger(String value) {
		value = value.replaceAll("[^0-9]", "").replaceAll("\\s+", "");
		return new Integer(value);
	}

	private Boolean readBoolean(String value) {
		value = value.replaceAll("[0-9]", "").replaceAll("\\s+", "");
		return new Boolean(value);
	}
	
	private String checkPath(String value){
		if (!value.endsWith("\\") && !value.endsWith("/")) {
			value += "/";
		}
		
		return value;
	}

}
