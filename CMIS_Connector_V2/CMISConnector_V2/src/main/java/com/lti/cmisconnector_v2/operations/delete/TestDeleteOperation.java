package com.lti.cmisconnector_v2.operations.delete;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;

import com.boomi.connector.api.DeleteRequest;
import com.boomi.connector.api.ObjectIdData;
import com.boomi.connector.api.OperationResponse;
import com.boomi.connector.api.PayloadUtil;
import com.boomi.connector.api.ResponseUtil;
import com.boomi.connector.util.BaseConnection;
import com.boomi.connector.util.BaseDeleteOperation;
import com.boomi.util.IOUtil;
import com.lti.cmisconnector_v2.CMISConnection;
import com.lti.cmisconnector_v2.client.RESTClient;

public class TestDeleteOperation extends BaseDeleteOperation {

	private static final Logger logger = Logger.getLogger(TestDeleteOperation.class.getName());

	public TestDeleteOperation(BaseConnection<?> conn) {
		super(conn);
	}

	@Override
	protected void executeDelete(DeleteRequest request, OperationResponse response) {
		String baseURL = CMISConnection.getBASE_URL();
		CloseableHttpResponse resp = null;
		RESTClient client=null;
		

		for (ObjectIdData input : request) {
			try {
				String uri = baseURL+input.getDynamicOperationProperties().getProperty("path") + "/"
						+ input.getObjectId();
				URIBuilder uriBuilder=new URIBuilder(uri);
				Map<String, String> reqParams =input.getDynamicOperationProperties()
						.getCustomProperties("params");
				for (Map.Entry<String, String> e : reqParams.entrySet())
					uriBuilder.addParameter(e.getKey(), e.getValue());
				URI finalUri = null;
				try {
					finalUri = uriBuilder.build();
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// Logger to Print URL
				logger.log(Level.INFO, "The generated URL is  " + finalUri);
				HttpUriRequest httpRequest = RequestBuilder.delete(finalUri).build();
				
				Map<String, String> reqHeaders = input.getDynamicOperationProperties()
						.getCustomProperties("headers");
				for (Map.Entry<String, String> e : reqHeaders.entrySet()) {

					httpRequest.addHeader(e.getKey(), e.getValue());

				}
				client = getConnection().getRESTClient();
				resp = client.executeRequest(httpRequest);

				// Object found. Display Object
				logger.log(Level.INFO, "got rest client " + client.toString());

				int statusCode = resp.getStatusLine().getStatusCode();

				if (statusCode >= 200 && statusCode < 300) {
					ResponseUtil.addEmptySuccess(response, input, String.valueOf(statusCode));
				} else if (statusCode == 404) {
					ResponseUtil.addSuccess(response, input, String.valueOf(statusCode),
							PayloadUtil.toPayload(resp.getEntity().getContent()));
				} else {

					// Unsuccessful call, return status code of why
					ResponseUtil.addFailure(response, input, String.valueOf(statusCode),
							PayloadUtil.toPayload(resp.getEntity().getContent()));
				}

			} catch (Exception e) {
				// logger to check if the program entered the catch block
				logger.log(Level.INFO, "Object Not found: No Data" + resp.getStatusLine());
				ResponseUtil.addExceptionFailure(response, input, e);
			} finally {
				IOUtil.closeQuietly(resp,client);
			}
		}
	}
	public CMISConnection getConnection() {
		return (CMISConnection) super.getConnection();
	}
}
