package com.restcrudmanager.ui.form.field.definition;

import info.magnolia.ui.form.field.definition.SelectFieldDefinition;

/**
 * Get API-Back data to display in the field
 *
 * @author isilanes
 */
public class RestSelectFieldDefinition extends SelectFieldDefinition {
	
	private String serviceUrl;
	private String jsonFieldForValue;
	private String jsonFieldForName;
	private String jsonTypeForValue;
	private String jsonTypeForName;
	
	public String getJsonTypeForName() {
		return jsonTypeForName;
	}

	public void setJsonTypeForName(String jsonTypeForName) {
		this.jsonTypeForName = jsonTypeForName;
	}

	public String getJsonFieldForValue() {
		return jsonFieldForValue;
	}

	public void setJsonFieldForValue(String jsonFieldForValue) {
		this.jsonFieldForValue = jsonFieldForValue;
	}

	public String getJsonFieldForName() {
		return jsonFieldForName;
	}

	public void setJsonFieldForName(String jsonFieldForName) {
		this.jsonFieldForName = jsonFieldForName;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public String getJsonTypeForValue() {
		return jsonTypeForValue;
	}

	public void setJsonTypeForValue(String jsonTypeForValue) {
		this.jsonTypeForValue = jsonTypeForValue;
	}
	
}
