package com.lti.cmisconnector_v2;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;

import com.boomi.connector.api.ConnectorContext;
import com.boomi.connector.util.BaseConnection;
import com.lti.cmisconnector_v2.client.*;

public class CMISConnection extends BaseConnection<ConnectorContext> {
	private static String USER_NAME = "username";
	private static String PASSWORD = "password";
	private static String BASE_URL = "url";


	public CMISConnection(ConnectorContext context) {
		super(context);
		USER_NAME = context.getConnectionProperties().getProperty("username");
		PASSWORD = context.getConnectionProperties().getProperty("password");
		BASE_URL = context.getConnectionProperties().getProperty("url");
		// TODO Auto-generated constructor stub
	}

	public static String getUSER_NAME() {
		return USER_NAME;
	}

	public static String getPASSWORD() {
		return PASSWORD;
	}

	public static String getBASE_URL() {
		return BASE_URL;
	}

	public RESTClient getRESTClient() {
		return new RESTClient(HttpClientBuilder.create().build(), getHttpContext());
	}

	private HttpClientContext getHttpContext() {
		HttpClientContext httpContext = HttpClientContext.create();
		httpContext.setCredentialsProvider(getCredentialsProvider());
		return httpContext;
	}

	private CredentialsProvider getCredentialsProvider() {
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(getUSER_NAME(), getPASSWORD()));
		return credsProvider;
	}

}
