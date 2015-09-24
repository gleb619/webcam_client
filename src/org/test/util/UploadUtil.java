package org.test.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.test.data.types.LoadingResponse;
import org.test.data.types.Logger;

public class UploadUtil implements Callable<LoadingResponse> {

	private final String url;
	private final Logger logger;
	private final Boolean debug;
	private String filepath;
	private String body;
	private Boolean uploadFile;
	
	public final String CONTENT_TYPE_HTML = "text/plain;charset=utf-8";
	public final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";
	public final String CONTENT_TYPE_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
	
	private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2382.0 Safari/537.36";
	private final String ACCEPT_LANGUAGE = "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4";

	public UploadUtil(String filepath, String url, Logger logger, Boolean debug) {
		super();
		this.filepath = filepath;
		this.url = url;
		this.logger = logger;
		this.debug = debug;
		this.uploadFile = true;
	}
	
	public UploadUtil(String url, Logger logger, Boolean debug, String body) {
		super();
		this.url = url;
		this.logger = logger;
		this.debug = debug;
		this.body = body;
	}

	@Override
	public LoadingResponse call() throws Exception {
		return init();
	}

	public LoadingResponse init() throws Exception {
		if (uploadFile) {
			return uploadFile();
		}
		
		return sendPost();
	}

	private LoadingResponse uploadFile() throws IOException, ClientProtocolException {
		logger.debug("UploadUtil.init()");
		HttpClient httpclient = HttpClientBuilder.create().build();

		HttpPost httppost = new HttpPost(url);
		File file = new File(filepath);
		if (!file.exists()) {
			throw new IOException("File not found");
		}

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addPart("file", new FileBody(new File(filepath)));

		httppost.setEntity(builder.build());
		logger.debug("UploadUtil.init()#Create post#httppost: " + httppost);

		file.deleteOnExit();

		return new LoadingResponse(httpclient.execute(httppost));
	}
	
	public LoadingResponse sendPost() throws Exception {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = sendPostStringEntity(url, body, CONTENT_TYPE_JSON);
		String log = "";
		
		HttpResponse response = client.execute(post);
		Integer responseCode = response.getStatusLine().getStatusCode();

		log += "\n\tResponse Code " + responseCode + " from request to URL : " + url;
		if (debug) {
			log += "\n\tData : " + body;
		}
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = bufferedReader.readLine()) != null) {
			result.append(line);
		}

		if (debug) {
			String body = result.toString();
			if (body.length() > 500) {
				body = body.substring(0, 500) + "...";
			}
		
			log += "\n\tResponse : " + body;
		}
		
		return new LoadingResponse(result.toString(), responseCode, log, response);
	}
	
	public HttpPost sendPostUrlEncoded(String url, List<NameValuePair> postParams, String contentType) throws Exception {
		HttpPost post = new HttpPost(url);
		
		post.setHeader("User-Agent", USER_AGENT);
		post.setHeader("Accept", "*/*");
		post.setHeader("Accept-Language", ACCEPT_LANGUAGE);
		post.setHeader("Connection", "keep-alive");
		post.setHeader("Content-Type", contentType);

		post.setEntity(new UrlEncodedFormEntity(postParams));
		
		return post;
	}
	
	public HttpPost sendPostStringEntity(String url, String data, String contentType) throws Exception {
		HttpPost post = new HttpPost(url);

		post.setHeader("User-Agent", USER_AGENT);
		post.setHeader("Accept", "*/*");
		post.setHeader("Accept-Language", ACCEPT_LANGUAGE);
		post.setHeader("Connection", "keep-alive");
		post.setHeader("Content-Type", contentType);

		StringEntity entity = new StringEntity(data);
		entity.setContentType(contentType);
		
		post.setEntity(entity);

		return post;
	}
	
	public static LoadingResponse post(String url, Logger logger, Boolean debug, String body) throws Exception {
		UploadUtil uploadUtil = new UploadUtil(url, logger, debug, body);
		return uploadUtil.sendPost();
	}
	
	public static LoadingResponse upload(String path, String url, Logger logger, Boolean debug
			, ExecutorService executor) {
		File file = new File(path);
		LoadingResponse result = null;
		logger.log("UploadUtil.upload()#file size: " + (file.length() / 1024) + " kilobytes");
		UploadUtil uploadUtil = new UploadUtil(path, url, logger, debug);
		Future<LoadingResponse> task = executor.submit(uploadUtil);
		try {
			logger.debug("UploadUtil.upload()#Prepare for get result");
			result = task.get();
			logger.debug("UploadUtil.upload()#Responce code: " + result.getResponse().getStatusLine().getStatusCode());
			logger.debug("UploadUtil.upload()#Body; " + result.getResponse().getEntity().toString());
			
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
