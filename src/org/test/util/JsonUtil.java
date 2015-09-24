package org.test.util;

import org.apache.http.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class JsonUtil {

	private static Gson gson = new GsonBuilder().serializeNulls().create();

	public static <T> T readJson(String text, Class<T> clazz) throws JsonSyntaxException, IllegalStateException {
		if(text.matches(".*\\<[^>]+>.*")){
			throw new ParseException("Unparseable data: \n\n" + text + "\n\n");
		}
		
		return gson.fromJson(text, clazz);
	}
	
}
