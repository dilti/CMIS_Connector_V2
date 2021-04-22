package com.lti.cmisconnector_v2.operations.post;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import com.boomi.connector.api.ObjectData;
import com.boomi.connector.api.OperationResponse;
import com.boomi.connector.api.PayloadUtil;
import com.boomi.connector.api.ResponseUtil;
import com.boomi.connector.api.UpdateRequest;
import com.boomi.connector.util.BaseConnection;
import com.boomi.connector.util.BaseUpdateOperation;
import com.boomi.util.IOUtil;
import com.lti.cmisconnector_v2.CMISConnection;
import com.lti.cmisconnector_v2.RepeatableInputStreamEntity;
import com.lti.cmisconnector_v2.client.RESTClient;

public class TestPostOperation extends BaseUpdateOperation {

	public TestPostOperation(BaseConnection<?> conn) {
		super(conn);
		// TODO Auto-generated constructor stub
	}

	private static final Logger logger = Logger.getLogger(TestPostOperation.class.getName());

	@Override
	protected void executeUpdate(UpdateRequest updateRequest, OperationResponse operationResponse) {
		RESTClient client = null;
		for (ObjectData data : updateRequest) {
			CloseableHttpResponse response = null;
			InputStream dataStream = null;
			try {
				// fetch the document data as a stream
				dataStream = data.getData();
				String baseURL = CMISConnection.getBASE_URL();
				String uri = baseURL + data.getDynamicOperationProperties().getProperty("path");
				URIBuilder newUri = new URIBuilder(uri);
				Map<String, String> reqParams = data.getDynamicOperationProperties().getCustomProperties("params");
				for (Map.Entry<String, String> e : reqParams.entrySet())
					newUri.addParameter(e.getKey(), e.getValue());
				URI finalUri = newUri.build();
				logger.log(Level.INFO, "The generate uri is " + finalUri);
				HttpUriRequest request = null;
				if (data.getDynamicOperationProperties().getCustomProperties("headers") != null
						&& data.getDynamicOperationProperties().getCustomProperties("headers").get("Content-Type")
								.equals("multipart/form-data")) {
					InputStream input = new FileInputStream(
							data.getDynamicOperationProperties().getProperty("filePath"));

					MultipartEntityBuilder builder = MultipartEntityBuilder.create();
					builder.setBoundary("------------abcdef---------");
					builder.setContentType(ContentType.MULTIPART_FORM_DATA);
					String metaData = IOUtils.toString(dataStream, StandardCharsets.UTF_8.name());
					builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
					builder.addTextBody("object", metaData, ContentType.APPLICATION_JSON);
					builder.addBinaryBody("content", input);

					BufferedHttpEntity entity = new BufferedHttpEntity(builder.build());
					request = RequestBuilder.create("POST").setUri(finalUri).setEntity(entity).build();
					logger.log(Level.INFO, "The generate reqquest content is "
							+ IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8.name()));

				} else {
					request = RequestBuilder.create("POST").setUri(finalUri)
							.setEntity(new RepeatableInputStreamEntity(dataStream, data.getDataSize()) {
							}).build();
				}
				Map<String, String> reqHeaders = data.getDynamicOperationProperties().getCustomProperties("headers");
				for (Map.Entry<String, String> e : reqHeaders.entrySet()) {
					if (e.getValue().equals("multipart/form-data")) {
						request.addHeader(e.getKey(), e.getValue() + ";boundary=------------abcdef---------");

					} else {
						request.addHeader(e.getKey(), e.getValue());
					}
				}

				client = getConnection().getRESTClient();
				response = client.executeRequest(request);
				int statusCode = response.getStatusLine().getStatusCode();

				if (statusCode >= 200 && statusCode < 300) {
					ResponseUtil.addSuccess(operationResponse, data, response.getStatusLine().getReasonPhrase(),
							PayloadUtil.toPayload(response.getEntity().getContent()));
				} else {
					ResponseUtil.addFailure(operationResponse, data,
							IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8.name()),
							PayloadUtil.toPayload(response.getEntity().getContent()));

				}
			} catch (Exception e) {
				// Exception occurred, add failure.
				ResponseUtil.addExceptionFailure(operationResponse, data, e);
			} finally {
				IOUtil.closeQuietly(response, dataStream, client);
			}
		}
	}

	protected String generateUri(String baseURL, String[] params) {
		String uri = baseURL + "/repositories";
		for (String s : params) {
			uri += "/" + getContext().getOperationProperties().getProperty(s);
		}
		return uri;
	}

//	abstract protected String[] getParamsForURL();

	public com.lti.cmisconnector_v2.CMISConnection getConnection() {
		return (com.lti.cmisconnector_v2.CMISConnection) super.getConnection();
	}
}
