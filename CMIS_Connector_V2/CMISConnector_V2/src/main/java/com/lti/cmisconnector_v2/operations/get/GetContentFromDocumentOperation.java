package com.lti.cmisconnector_v2.operations.get;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import com.boomi.connector.api.GetRequest;
import com.boomi.connector.api.ObjectIdData;
import com.boomi.connector.api.OperationResponse;
import com.boomi.connector.api.PayloadUtil;
import com.boomi.connector.api.ResponseUtil;
import com.boomi.connector.util.BaseConnection;
import com.boomi.connector.util.BaseGetOperation;
import com.boomi.util.IOUtil;
import com.lti.cmisconnector_v2.*;
import com.lti.cmisconnector_v2.client.RESTClient;

public class GetContentFromDocumentOperation extends BaseGetOperation {

	private static final Logger logger = Logger.getLogger(GetContentFromDocumentOperation.class.getName());

	public GetContentFromDocumentOperation(BaseConnection<?> conn) {
		super(conn);
	}

	@Override
	protected void executeGet(GetRequest request, OperationResponse response) {
		ObjectIdData objectId = request.getObjectId();
		CloseableHttpResponse resp = null;
		String baseURL = CMISConnection.getBASE_URL();

		String uri = baseURL + "/repositories/"
				+ request.getObjectId().getDynamicOperationProperties().getProperty("path")
				+ request.getObjectId().getObjectId() + "/contents/content";
		// Logger to see what the generated URL is
		logger.log(Level.INFO, "the generate uri is: " + uri);

		HttpUriRequest httpRequest = RequestBuilder.get(uri).build();
		RESTClient client =  getConnection().getRESTClient();
		try {

			resp = client.executeRequest(httpRequest);
			int statusCode = resp.getStatusLine().getStatusCode();
			if (!(statusCode >= 200 && statusCode < 300)) {
				if (statusCode == 404) {
					ResponseUtil.addSuccess(response, objectId, resp.getStatusLine().getReasonPhrase(),
							PayloadUtil.toPayload(resp.getEntity().getContent()));
				} else {
					ResponseUtil.addFailure(response, objectId, String.valueOf(statusCode),
							PayloadUtil.toPayload(resp.getEntity().getContent()));
				}
				IOUtil.closeQuietly(client);
				return;

			}

			BufferedReader bR = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
			String line = "";

			StringBuilder responseStrBuilder = new StringBuilder();
			while ((line = bR.readLine()) != null) {

				responseStrBuilder.append(line);
			}

			JSONObject result = new JSONObject(responseStrBuilder.toString());
			JSONArray ja = (JSONArray) result.get("links");

			JSONObject jsonObject = ja.getJSONObject(1);

			uri = jsonObject.getString("href");
			IOUtil.closeQuietly(resp);

			logger.log(Level.INFO, "the generate uri is: " + uri);
			int timeout = Integer.parseInt(getContext().getOperationProperties().getProperty("Timeout"));
			String FILE_NAME = getContext().getOperationProperties().getProperty("fileName");
			FileUtils.copyURLToFile(new URL(uri), new File(FILE_NAME), timeout, timeout);
			httpRequest = RequestBuilder.get(uri).build();
			resp = client.executeRequest(httpRequest);
			if (resp.getEntity().getContentLength() > 0) {
				// Object found. Display Object
				ResponseUtil.addResultWithHttpStatus(response, objectId, resp.getStatusLine().getStatusCode(),
						resp.getStatusLine().getReasonPhrase(), PayloadUtil.toPayload(resp.getEntity().getContent()));
			} else {
				// Object not found.
				ResponseUtil.addEmptyFailure(response, objectId,
						String.valueOf(resp.getStatusLine().getStatusCode()) + "Object Not found: No Data at" + uri);
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
