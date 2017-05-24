package com.restcrudmanager.ui.form.field.factory;

import info.magnolia.ui.api.app.AppContext;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.definition.SelectFieldDefinition;
import info.magnolia.ui.form.field.definition.SelectFieldOptionDefinition;
import info.magnolia.ui.form.field.factory.SelectFieldFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.jcr.RepositoryException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.restcrudmanager.base.keys.FullRestCRUDManagerKeys;
import com.restcrudmanager.ui.form.field.definition.RestSelectFieldDefinition;
import com.restcrudmanager.utils.Utils;
import com.vaadin.data.Item;
import com.vaadin.ui.AbstractSelect;

/**
 * Creates and initializes a selection field based on a field definition.
 *
 * @author isilanes
 */
public class RestSelectFieldFactory extends SelectFieldFactory<SelectFieldDefinition> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestSelectFieldFactory.class);
    protected final UiContext uiContext;
    private final AppContext appContext;
    private String serviceUrl;
	private String jsonFieldForValue;
	private String jsonFieldForName;
	private String jsonTypeForValue;
	private String jsonTypeForName;

    @Inject
    public RestSelectFieldFactory(RestSelectFieldDefinition definition, Item relatedFieldItem, UiContext uiContext, I18NAuthoringSupport i18nAuthoringSupport,
    		AppContext appContext) {
        super(definition, relatedFieldItem, uiContext, i18nAuthoringSupport);
        this.uiContext = uiContext;
        this.appContext = appContext;
        this.serviceUrl = definition.getServiceUrl();
        this.jsonFieldForValue = definition.getJsonFieldForValue();
        this.jsonFieldForName = definition.getJsonFieldForName();
        this.jsonTypeForValue = definition.getJsonTypeForValue();
        this.jsonTypeForName = definition.getJsonTypeForName();
    }

    @Override
    public List<SelectFieldOptionDefinition> getSelectFieldOptionDefinition() {
        ArrayList<SelectFieldOptionDefinition> optionList = new ArrayList<>();

        try {
        	
        	URL url = new URL(this.serviceUrl);
            JSONArray jsonArray = Utils.getAPIBackResponseJSON(url, FullRestCRUDManagerKeys.HTTP_MODE_GET, Utils.getHeadersFromConfig(appContext), uiContext);
            
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                if (!json.isNull(this.jsonFieldForName) && !json.isNull(this.jsonFieldForValue)) {
                	
                    SelectFieldOptionDefinition option = new SelectFieldOptionDefinition();
                    
                    try {
	                    //Inserting the name
	                    String stringClassOfName = this.jsonTypeForName;
	            		Class<?> classOfTheName = Class.forName(stringClassOfName);
					
	                    if(classOfTheName.isAssignableFrom(String.class)){
	                    	option.setName(json.getString(this.jsonFieldForName));
	                	}else{
	                		option.setName(String.valueOf(json.getInt(this.jsonFieldForName)));
	                	}
                    } catch (ClassNotFoundException e) {
                    	LOGGER.error("RESTSelectFieldFactory.getSelectFieldOptionDefinition.getName: " + e);
					}
                    try{
	                    //Inserting the label
	                    String stringClassOfLabel = this.jsonTypeForName;
	            		Class<?> classOfTheLabel = Class.forName(stringClassOfLabel);
	                    if(classOfTheLabel.isAssignableFrom(String.class)){
	                    	option.setLabel(json.getString(this.jsonFieldForName));
	                	}else{
	                		option.setLabel(String.valueOf(json.getInt(this.jsonFieldForName)));
	                	}
	                } catch (ClassNotFoundException e) {
	                	LOGGER.error("RESTSelectFieldFactory.getSelectFieldOptionDefinition.getLabel: " + e);
					}
	                try{
	                    //Inserting the value
	                    String stringClassOfValue = this.jsonTypeForValue;
	            		Class<?> classOfTheValue = Class.forName(stringClassOfValue);
	                    if(classOfTheValue.isAssignableFrom(String.class)){
	                    	option.setValue(json.getString(this.jsonFieldForValue));
	                	}else{
	                		option.setValue(String.valueOf(json.getInt(this.jsonFieldForValue)));
	                	}
	                } catch (ClassNotFoundException e) {
                    	LOGGER.error("RESTSelectFieldFactory.getSelectFieldOptionDefinition.getValue: " + e);
					}
                    
                    optionList.add(option);
                    
                }
            }

        } catch (MalformedURLException | RepositoryException e) {
            LOGGER.error("RESTSelectFieldFactory.getSelectFieldOptionDefinition: " + e);
		} 

        return optionList;
    }

    @Override
    protected Class<?> getDefaultFieldType() {
        return String.class;
    }

    @Override
    protected AbstractSelect createFieldComponent() {
        AbstractSelect newSelect = super.createFieldComponent();
        newSelect.setNullSelectionAllowed(true);

        return newSelect;
    }

}
