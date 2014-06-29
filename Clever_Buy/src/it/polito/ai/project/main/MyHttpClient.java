package it.polito.ai.project.main;

import com.loopj.android.http.*;

public class MyHttpClient {

	private static final String BASE_URL = "http://192.168.1.131:8080/supermarket/android";

	//private static final String BASE_URL = "http://87.10.96.38:8080/supermarket/android";


	private static AsyncHttpClient client = new AsyncHttpClient();
	
	public static void setCookieStore (PersistentCookieStore cookieStore) {
		client.setCookieStore(cookieStore);
	}
	
	public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void setBasicAuth(String username, String password) {
		client.setBasicAuth(username, password);
	}
	
	private static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}
}

