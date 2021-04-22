package com.lti.cmisconnector_v2;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.boomi.connector.api.BrowseContext;
import com.boomi.connector.api.Browser;
import com.boomi.connector.api.Operation;
import com.boomi.connector.api.OperationContext;
import com.boomi.connector.util.BaseConnector;
import com.boomi.util.StringUtil;
import com.lti.cmisconnector_v2.operations.delete.TestDeleteOperation;
import com.lti.cmisconnector_v2.operations.get.GetContentFromDocumentOperation;
import com.lti.cmisconnector_v2.operations.get.TestGetOperation;
import com.lti.cmisconnector_v2.operations.post.TestPostOperation;

public class CMISConnector extends BaseConnector {


    private static final Logger logger = Logger.getLogger(CMISConnector.class.getName());

	public Browser createBrowser(BrowseContext context) {
		// TODO Auto-generated method stub
		return new CMISBrowser(context);
	}

	@Override
	protected Operation createGetOperation(OperationContext context) {
		 if (getCustomOperationType(context).equals("TestGet")) {
			 logger.log(Level.INFO, "TestGett Operation operation called from Connector");
	            return new TestGetOperation(createConnection(context));
	        }
		 if (getCustomOperationType(context).equals("GetDocumentContent")) {
			 logger.log(Level.INFO, "Get Document content Operation operation called from Connector");
	            return new GetContentFromDocumentOperation(createConnection(context));
	        }
		return super.createGetOperation(context);
	}
	
	protected Operation createUpdateOperation(OperationContext context) {
		if (getCustomOperationType(context).equals("TestPost")) {
			 logger.log(Level.INFO, "Test Psot Operation called from Connector");
	            return new TestPostOperation(createConnection(context));
	        }
		return super.createUpdateOperation(context);
	}
	@Override
	protected Operation createDeleteOperation(OperationContext context) {
		// TODO Auto-generated method stub
		if (getCustomOperationType(context).equals("TestDelete")) {
			 logger.log(Level.INFO, "TestDelete called from Connector");
	            return new TestDeleteOperation(createConnection(context));
	        }
		return super.createDeleteOperation(context);
	}
	 private String getCustomOperationType(BrowseContext context) {
	        return StringUtil.defaultIfBlank(context.getCustomOperationType(), "");
	    }
	 private CMISConnection createConnection(BrowseContext context) {
		return new CMISConnection(context); 
	 }

	
	
}
