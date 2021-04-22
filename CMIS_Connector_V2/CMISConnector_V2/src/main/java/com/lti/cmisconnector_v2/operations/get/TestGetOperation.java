package com.lti.cmisconnector_v2.operations.get;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;

import com.boomi.connector.api.GetRequest;
import com.boomi.connector.api.ObjectIdData;
import com.boomi.connector.api.OperationResponse;
import com.boomi.connector.api.PayloadUtil;
import com.boomi.connector.api.ResponseUtil;
import com.boomi.connector.util.BaseConnection;
import com.boomi.connector.util.BaseGetOperation;
import com.boomi.util.IOUtil;
import com.lti.cmisconnector_v2.CMISConnection;
import com.lti.cmisconnector_v2.client.RESTClient;

public class TestGetOperation extends BaseGetOperation {

	private static final Logger logger = Logger.getLogger(TestGetOperation.class.getName());

	public TestGetOperation(BaseConnection<?> conn) {
		super(conn);
	}

	@Override
	protected void executeGet(GetRequest request, OperationResponse response) {

		ObjectIdData objectId = request.getObjectId();
		CloseableHttpResponse resp = null;
		String baseURL = CMISConnection.getBASE_URL();
		String uri = baseURL + request.getObjectId().getDynamicOperationProperties().getProperty("path") + "/"
				+ request.getObjectId().getObjectId();
		URIBuilder newUri = null;
		try {
			newUri = new URIBuilder(uri);
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Logger to see what the generated URL is
		logger.log(Level.INFO, "the generate url is: " + uri);
		Map<String, String> reqParams = request.getObjectId().getDynamicOperationProperties()
				.getCustomProperties("params");
		for (Map.Entry<String, String> e : reqParams.entrySet())
			newUri.addParameter(e.getKey(), e.getValue());
		URI finalUri = null;
		try {
			finalUri = newUri.build();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HttpUriRequest httpRequest = RequestBuilder.get(finalUri).build();
		Map<String, String> reqHeaders = request.getObjectId().getDynamicOperationProperties()
				.getCustomProperties("headers");
		for (Map.Entry<String, String> e : reqHeaders.entrySet()) {

			httpRequest.addHeader(e.getKey(), e.getValue());

		}
		RESTClient client = getConnection().getRESTClient();
		try {
			resp = client.executeRequest(httpRequest);
			logger.log(Level.INFO, "the generate response is: " + resp.getEntity().getContent());
			int statusCode = resp.getStatusLine().getStatusCode();
			if (statusCode >= 200 && statusCode < 300 || statusCode == 404) {
				ResponseUtil.addSuccess(response, objectId, resp.getStatusLine().getReasonPhrase(),
						PayloadUtil.toPayload(resp.getEntity().getContent()));
			} else {
				ResponseUtil.addFailure(response, objectId, String.valueOf(statusCode),
						PayloadUtil.toPayload(resp.getEntity().getContent()));
			}
		} catch (Exception e) {
			ResponseUtil.addExceptionFailure(response, objectId, e);
		} finally {
			IOUtil.closeQuietly(resp, client);
		}

	}

	public com.lti.cmisconnector_v2.CMISConnection getConnection() {
		return (com.lti.cmisconnector_v2.CMISConnection) super.getConnection();
	}
}
