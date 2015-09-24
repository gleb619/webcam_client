package org.test.converter;

import org.apache.http.HttpResponse;

public class HttpResponseConverter {

	public static Boolean convert(HttpResponse httpResponse) {
		if (httpResponse != null) {
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return true;
			}
		}
		
		return false;
	}
	
}
