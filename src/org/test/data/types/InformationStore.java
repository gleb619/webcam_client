package org.test.data.types;

import org.test.util.FileUtil;

public class InformationStore {

	private String key = null;
	private String urlTemplate = "http://178.88.186.230:8080/webcam-server/";
	private String urlLogin = "api/%s/access";
	private String urlUpload = "api/%s/photo/upload";
	private String heartbeat = "178.88.186.230";
	private Integer heartbeatPort = 8080;
	private String path = "D:/TEMP/TEST2/";

	private Integer captureWidth = 600;
	private Integer captureHeight = 800;
	private Integer frequency = 5000;
	private Integer compressLevel = 95;
	
	private Boolean allowDebug = false;
	private Boolean showPane = false;
	private Boolean showLogPane = false;

	public InformationStore() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InformationStore(String key, String urlTemplate, String heartbeat,
			Integer heartbeatPort, String path, Integer captureWidth,
			Integer captureHeight, Integer frequency, Integer compressLevel,
			Boolean allowDebug, Boolean showPane, Boolean showLogPane) {
		super();
		this.key = key;
		this.urlTemplate = urlTemplate;
		this.heartbeat = heartbeat;
		this.heartbeatPort = heartbeatPort;
		this.path = path;
		this.captureWidth = captureWidth;
		this.captureHeight = captureHeight;
		this.frequency = frequency;
		this.compressLevel = compressLevel;
		this.allowDebug = allowDebug;
		this.showPane = showPane;
		this.showLogPane = showLogPane;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getUrlTemplate() {
		return urlTemplate;
	}

	public void setUrlTemplate(String urlTemplate) {
		this.urlTemplate = urlTemplate;
	}

	public String getUrlLogin() {
		return urlLogin;
	}

	public void setUrlLogin(String urlLogin) {
		this.urlLogin = urlLogin;
	}

	public String getUrlUpload() {
		return urlUpload;
	}

	public void setUrlUpload(String urlUpload) {
		this.urlUpload = urlUpload;
	}

	public String getHeartbeat() {
		return heartbeat;
	}

	public void setHeartbeat(String heartbeat) {
		this.heartbeat = heartbeat;
	}

	public Integer getHeartbeatPort() {
		return heartbeatPort;
	}

	public void setHeartbeatPort(Integer heartbeatPort) {
		this.heartbeatPort = heartbeatPort;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Boolean getAllowDebug() {
		return allowDebug;
	}

	public void setAllowDebug(Boolean allowDebug) {
		this.allowDebug = allowDebug;
	}

	public Integer getCaptureWidth() {
		return captureWidth;
	}

	public void setCaptureWidth(Integer captureWidth) {
		this.captureWidth = captureWidth;
	}

	public Integer getCaptureHeight() {
		return captureHeight;
	}

	public void setCaptureHeight(Integer captureHeight) {
		this.captureHeight = captureHeight;
	}

	public Integer getFrequency() {
		return frequency;
	}

	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

	public Integer getCompressLevel() {
		return compressLevel;
	}

	public void setCompressLevel(Integer compressLevel) {
		this.compressLevel = compressLevel;
	}

	public Boolean getShowPane() {
		return showPane;
	}

	public void setShowPane(Boolean showPane) {
		this.showPane = showPane;
	}

	public String getUrl() {
		return String.format(urlTemplate, key);
	}
	
	public Boolean getShowLogPane() {
		return showLogPane;
	}

	public void setShowLogPane(Boolean showLogPane) {
		this.showLogPane = showLogPane;
	}

	public InformationStore validate() {
		if (!urlTemplate.endsWith("/")) {
			urlTemplate += "/";
		}
		
		FileUtil.checkDirExist(getPath());
		
		return this;
	}
	
	@Override
	public String toString() {
		return String
				.format("InformationStore ["
						+ "\n\t key=%s"
						+ "\n\t urlTemplate=%s"
						+ "\n\t heartbeat=%s"
						+ "\n\t heartbeatPort=%s"
						+ "\n\t path=%s"
						+ "\n\t allowDebug=%s"
						+ "\n\t captureWidth=%s"
						+ "\n\t captureHeight=%s"
						+ "\n\t frequency=%s"
						+ "\n\t compressLevel=%s"
						+ "\n\t showPane=%s"
						+ "\n\t showLogPane=%s"
						+ "\n]",
						key.hashCode(), urlTemplate, heartbeat, heartbeatPort, path,
						allowDebug, captureWidth, captureHeight, frequency,
						compressLevel, showPane, showLogPane);
	}

}
