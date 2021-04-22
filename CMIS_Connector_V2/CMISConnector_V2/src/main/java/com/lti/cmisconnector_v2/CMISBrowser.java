package com.lti.cmisconnector_v2;

import java.util.Collection;

import com.boomi.connector.api.BrowseContext;
import com.boomi.connector.api.ContentType;
import com.boomi.connector.api.ObjectDefinition;
import com.boomi.connector.api.ObjectDefinitionRole;
import com.boomi.connector.api.ObjectDefinitions;
import com.boomi.connector.api.ObjectType;
import com.boomi.connector.api.ObjectTypes;
import com.boomi.connector.util.BaseBrowser;

public class CMISBrowser extends BaseBrowser {

	protected CMISBrowser(BrowseContext context) {
		super(context);
	}

	@Override
	public ObjectTypes getObjectTypes() {
		ObjectTypes types = new ObjectTypes();
		ObjectType type = new ObjectType();
		type.setId("Profile");
		type.setLabel("Profile");
		types.getTypes().add(type);
		return types;
	}

	@Override
	public ObjectDefinitions getObjectDefinitions(String objectTypeId, Collection<ObjectDefinitionRole> roles) {
		ObjectDefinitions definitions = new ObjectDefinitions();
		definitions.getDefinitions().add(new ObjectDefinition().withInputType(ContentType.NONE)
					.withOutputType(ContentType.NONE).withElementName(""));
		return definitions;

	}

}
