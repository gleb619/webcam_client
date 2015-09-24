package org.test.util;

import org.test.data.types.InformationStore;
import org.test.data.types.Logger;
import org.test.data.types.LoginResponse;

public class LoginUtil {

	public static void loadKey(InformationStore informationStore) {
		String key = SecurityUtil.loadKey();
		if (key != null) {
			informationStore.setKey(key);
		}
		else {
			throw new NullPointerException("Can't find the key");
		}
	}
	
	public static Boolean login(String login, String password, InformationStore informationStore, Logger logger) {
		String key = SecurityUtil.hashSha256(login + password);
		
		System.out.println("LoginUtil.login()#b::url: " + informationStore.getUrlTemplate() + informationStore.getUrlLogin());
		
		String url = String.format(informationStore.getUrlTemplate() + informationStore.getUrlLogin(), key);
		
		System.out.println("LoginUtil.login()#key: " + key);
		System.out.println("LoginUtil.login()#url: " + url);
		
		try {
			LoginResponse loginResponse = JsonUtil.readJson(UploadUtil.post(url, logger, informationStore.getAllowDebug(), "").getResult(), LoginResponse.class);
			if (loginResponse.getSuccess()) {
				SecurityUtil.saveKey(key);
			}
			return loginResponse.getSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
}
