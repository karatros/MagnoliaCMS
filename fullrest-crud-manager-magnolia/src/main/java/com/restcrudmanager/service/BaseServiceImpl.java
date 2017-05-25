package com.restcrudmanager.service;


import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.ui.api.app.AppContext;
import info.magnolia.ui.api.context.UiContext;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.restcrudmanager.base.FullRestService;
import com.restcrudmanager.base.keys.FullRestCRUDManagerKeys;
import com.restcrudmanager.exception.ConnectionProblemException;
import com.restcrudmanager.item.BasicItem;
import com.restcrudmanager.utils.Utils;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * Creates and initializes a selection field based on a field definition.
 *
 * @author isilanes
 */
public class BaseServiceImpl implements FullRestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseServiceImpl.class);
    
    /**
     * Get url of the service from configuration
     */
    public String getUrl(AppContext appContext){
    	
    	Session session;
    	String urlOfService = "";
		try {
			session = MgnlContext.getJCRSession(FullRestCRUDManagerKeys.CTE_CONFIGWORKSPACE);
			String moduleNodeString = Utils.getServiceConfigNodePath(appContext);
			Node moduleNode = session.getNode(moduleNodeString); 
			urlOfService = PropertyUtil.getString(moduleNode, FullRestCRUDManagerKeys.CTE_URLSERVICEBASE_PROPERTY);
			LOGGER.debug(FullRestCRUDManagerKeys.LOGGERTEXT_VALUEPROPERTY+urlOfService);
						
		} catch (RepositoryException e) {
			LOGGER.error("ERROR EN LanguagesService.getUrl ",e);
		} 
    	
    	return urlOfService;
    }
    
    /**
     * Get all elements from the service
     */
    public JSONArray getAll(UiContext uiContext,AppContext appContext) {
        JSONArray jsonArray = new JSONArray();

        try {
        	LOGGER.debug(FullRestCRUDManagerKeys.LOGGERTEXT_CALLINGSERVICE);
        	URL url = new URL(getUrl(appContext));
        	
            jsonArray = Utils.getAPIBackResponseJSON(url, FullRestCRUDManagerKeys.HTTP_MODE_GET, Utils.getHeadersFromConfig(appContext), uiContext);
            LOGGER.debug("ANSWER: "+jsonArray.toString());

        } catch (MalformedURLException | RepositoryException e) {
            LOGGER.error(FullRestCRUDManagerKeys.LOGGERTEXT_ERRORSERVICE_GETALL,e);
		} 

        return jsonArray;
    }

    /**
     * Add an item to the REST service
     * 
     * @param item
     * @param uiContext
     * @param appContext
     * @return
     * @throws URISyntaxException
     */
    public boolean addItem(Item item,UiContext uiContext, AppContext appContext) throws URISyntaxException {

    	try {
    		addOrEditItem(item, FullRestCRUDManagerKeys.MODE_OPERATION_ADD, uiContext, appContext);
    	} catch (MalformedURLException | RepositoryException e) {
            LOGGER.error(FullRestCRUDManagerKeys.LOGGERTEXT_ERRORSERVICE_ADD + e);
		} 

        return true;
    }
    
    /**
     * Edit an item from the REST service
     * 
     * @param item
     * @param uiContext
     * @param appContext
     * @return
     * @throws URISyntaxException
     */
    public boolean editItem(Item item,UiContext uiContext, AppContext appContext) throws URISyntaxException {

    	try {
    		addOrEditItem(item, FullRestCRUDManagerKeys.MODE_OPERATION_EDIT, uiContext, appContext);
    	} catch (MalformedURLException | RepositoryException e) {
            LOGGER.error(FullRestCRUDManagerKeys.LOGGERTEXT_ERRORSERVICE_EDIT + e);
		} 
    	return true;
    }
    
    /**
     * Implementation method to add or edit items to the service
     * @param item
     * @param mode
     * @param uiContext
     * @param appContext
     * @return
     * @throws URISyntaxException
     */
    public boolean addOrEditItem(Item item, String mode,UiContext uiContext, AppContext appContext) 
    throws URISyntaxException, MalformedURLException , RepositoryException {

        JSONObject input = createAndPopulateProperties(mode, item, appContext);

        	
        	String httpMethod = "";
        	
        	if(mode.equals(FullRestCRUDManagerKeys.MODE_OPERATION_ADD)){
        		httpMethod = FullRestCRUDManagerKeys.HTTP_MODE_POST;
        	}else if(mode.equals(FullRestCRUDManagerKeys.MODE_OPERATION_EDIT)){
        		httpMethod = FullRestCRUDManagerKeys.HTTP_MODE_PUT;
        	}
            Utils.putAPIBackResponseJSON(new URL(getUrl(appContext)), input, httpMethod, 
            		Utils.getHeadersFromConfig(appContext), uiContext);


        return true;
    }
    
    /**
     * Insert proproperty from magnolia ITEM into JSON response with the correct fields
     * 
     * @param input
     * @param id
     * @param mode
     * @param item
     * @param fromJSONToJCRKeys
     * @param appContext
     * @return
     * @throws RepositoryException
     */
    private JSONObject insertPropertyIntoJSON(JSONObject input, String id, String mode,Item item, Map<String,String> fromJSONToJCRKeys, AppContext appContext) 
    throws RepositoryException{
    	
    	//Get the property
    	Property prop = item.getItemProperty(id);
    	//Get The correspondent node
    	Node fieldNode = Utils.getFieldNodeFromConfig( appContext,id);
    	
    	//Now we have the node, check if the node is allowed in the method we are using    	
    	if(
    		(fieldNode!=null)&&
    		(fieldNode.hasProperty("allowedInJsonMethod"))&&
    		(fieldNode.getProperty("allowedInJsonMethod").getString().toLowerCase().contains(mode.toLowerCase()))
    	){
    		//Field is allowed to be used in that mode. Insert it into response
    		String value = prop.getValue().toString();
    		input.put(fromJSONToJCRKeys.get(id), value);
    	}
    	
    	return input;
    }
    
    /**
     * Create a map to connect the JSON object that is going to be sended to the service and the ITEM of the magnolia context
     * 
     * @param mode
     * @param item
     * @param appContext
     * @return
     */
    private JSONObject createAndPopulateProperties(String mode,Item item, AppContext appContext){
    	
    	JSONObject input = new JSONObject();
        LOGGER.debug("Going to perform an action. Operational mode is: "+mode);

        try{
        	Map<String,String> fromJSONToJCRKeys = Utils.getFieldsCorrespondencyFromConfig(appContext);
        
        
        	Map<String, Class> props = BasicItem.IDs.getIDs(appContext);
	        for (String id : props.keySet()) {
	        	input = insertPropertyIntoJSON(input,id,mode,item,fromJSONToJCRKeys,appContext);
	        }
        }catch(RepositoryException e){
        	LOGGER.error(FullRestCRUDManagerKeys.LOGGERTEXT_ERRORCREATEPOPULATEPROPERTY, e);
        }
        
        return input;
    	
    }
    
    public boolean delItem(Item item, AppContext appContext) throws URISyntaxException {
    	
    	try {
	    	Property prop = item.getItemProperty(Utils.getPrimaryField(appContext)); 
	    	String value = prop.getValue().toString();
	    	
	    	URL url = new URL(getUrl(appContext)+FullRestCRUDManagerKeys.CTE_SLASH+value);
	        Utils.delAPIBackResponseJSON(url, true, Utils.getHeadersFromConfig(appContext));
    	} catch (MalformedURLException | RepositoryException | ConnectionProblemException e) {
            LOGGER.error(FullRestCRUDManagerKeys.LOGGERTEXT_ERRORSERVICE_DELETE + e);
		} 
		return true;
    	
    }
    
}
