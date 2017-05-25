package com.restcrudmanager.utils;

import info.magnolia.cms.util.QueryUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.ui.api.app.AppContext;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.vaadin.overlay.MessageStyleTypeEnum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.net.ssl.HttpsURLConnection;

import org.apache.jackrabbit.JcrConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.restcrudmanager.base.keys.FullRestCRUDManagerKeys;
import com.restcrudmanager.exception.ConnectionProblemException;

/**
 * Util methods
 *
 * @author isilanes
 */
public class Utils {

    private static Logger log = LoggerFactory.getLogger(Utils.class);
    
    /**
     * Constructor
     */
    private Utils(){
    	//Nothing to do here
    }

    /**
     * Get correct protocol to put into the connection
     * @param url
     * @return
     * @throws IOException
     */
    private static HttpURLConnection getProtocol(URL url) throws IOException{
    	//Check if is http or https
        if ("http".equalsIgnoreCase(url.getProtocol())) {
            return (HttpURLConnection) url.openConnection();
        } else {
        	return (HttpsURLConnection) url.openConnection();
        }
    }
    
    /**
     * Put headers into connection
     * @param conn
     * @param headers
     * @return
     */
    private static HttpURLConnection putHeadersIntoConnection(HttpURLConnection conn, Map<String, String> headers){
    	//Check headers
        if (!headers.isEmpty()) {
            Iterator<String> keyList = headers.keySet().iterator();
            while (keyList.hasNext()) {
                String key = keyList.next();
                String value = headers.get(key);
                conn.setRequestProperty(key, value);
            }
        }
        return conn;
    }
    
    /**
     * Method to get from service
     * 
     * @param url
     * @param httpMethod
     * @param headers
     * @param uiContext
     * @return
     */
    public static JSONArray getAPIBackResponseJSON(URL url, String httpMethod, Map<String, String> headers, UiContext uiContext) {
        JSONArray result = new JSONArray();
        HttpURLConnection conn = null;

        log.debug("Calling Rest-Service: '"+url+"' method:"+httpMethod+" headers: "+headers.toString());
        
        try {
        	conn = getProtocol(url);

            conn.setRequestMethod(httpMethod);
            
            conn = putHeadersIntoConnection( conn, headers);
            
            log.debug("ResponseCode: "+conn.getResponseCode());
            //Check if response is correct
            if (conn.getResponseCode() != 200) {
            	//Response is not correct. Some problem occurrs. Convert response to stream to iterate over it
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                //Read response line by line
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                log.debug("Response: "+sb.toString());
                //Convert response to JSON
                JSONObject responseJSON = new JSONObject(sb.toString());

                uiContext.openNotification(MessageStyleTypeEnum.ERROR, true, responseJSON.getString(FullRestCRUDManagerKeys.FIELD_DESCRIPTION));
            }else{
            	//Response is correct. Convert response to stream to iterate over it
            	BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                //Read response line by line
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                //Convert response to JSON
                result = new JSONArray(sb.toString());
                
            	log.debug("Response: "+sb.toString());
            }
            conn.disconnect();

        } catch ( IOException e) {
        	//Url not good
            log.error("getAPIBackResponseJSON: " + e);
            if(conn!=null){
            	conn.disconnect();
            }
        } 
        //Return response
        return result;
    }

    public static Date convertStringToDate(String dateStr) {
        Date result = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            result = formatter.parse(dateStr);
        } catch (ParseException e) {
            log.error("convertStringToDate: " + e);
        }

        return result;
    }

    public static boolean putAPIBackResponseJSON(URL url, JSONObject input, String method, Map<String, String> headers, UiContext uiContext) throws URISyntaxException {
        log.debug("Calling " + url.toURI().toString() + " service with method " + method);
        log.debug("input object:  " + input.toString(2));
        String parsedJSON = input.toString();
        HttpURLConnection conn = null;

        try {

        	conn = getProtocol(url);

            conn.setRequestMethod(method);

            conn = putHeadersIntoConnection( conn, headers);

            conn.setDoOutput(true);
            conn.setDoInput(true);

            try (OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream())) {
                wr.write(parsedJSON);
                wr.close();
            }

            if (conn.getResponseCode() != 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                JSONObject responseJSON = new JSONObject(sb.toString());

                if (responseJSON.getString(FullRestCRUDManagerKeys.FIELD_DESCRIPTION).matches("Country code: (.*) already exists")) {
                    uiContext.openNotification(MessageStyleTypeEnum.ERROR, true, responseJSON.getString(FullRestCRUDManagerKeys.FIELD_DESCRIPTION));
                }
            }

            conn.disconnect();
            log.debug("Successful connection");
        } catch (IOException e) {
            log.error("putAPIBackResponseJSON: " + e);
            if(conn!=null){
            	conn.disconnect();
            }
        } 
        return true;
    }

    public static boolean delAPIBackResponseJSON(URL url, boolean mayHaveRelatedItems, Map<String, String> headers) 
    throws ConnectionProblemException {
        HttpURLConnection conn = null;

        try {
            log.debug("DEL Calling " + url.toString() + " service with method DEL");

            conn = getProtocol(url);

            conn.setRequestMethod("DELETE");

            conn = putHeadersIntoConnection( conn, headers);

            conn.setDoOutput(true);
            conn.setDoInput(true);

            try (OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream())) {
                wr.close();
            }

            if (conn.getResponseCode() != 200) {
                log.debug("ERROR IN SERVICE: " + conn.getResponseMessage());
                if ((conn.getResponseCode() == 400) && ("BAD REQUEST".equalsIgnoreCase(conn.getResponseMessage())) && (mayHaveRelatedItems)) {
                    //Esto significa que no podemos borrarlo ya que tenemos destinations hijas asociadas.
                } else {
                    throw new ConnectionProblemException("Failed : HTTP error code : " + conn.getResponseCode());
                }
            }

            conn.disconnect();
            log.debug("Successful connection");
        } catch (IOException e) {
            log.error("putAPIBackResponseJSON: " + e);
            if(conn!=null){
            	conn.disconnect();
            }
        } 
        return true;
    }

    /**
     * Get the path from a given app into RestCRUDManager module
     * @param appContext
     * @return
     */
    public static String getAppPath(AppContext appContext){
    	return FullRestCRUDManagerKeys.CTE_MODULEPATH + appContext.getName();
    }
    
    /**
     * Return the service configuration node from a given app into RestCRUDManager module
     * @param appContext
     * @return
     */
    public static String getServiceConfigNodePath(AppContext appContext){
    	return getAppPath(appContext)+ FullRestCRUDManagerKeys.CTE_SERVICECONFIG_NODE;
    }
    
    public static String getFieldsNodeConfigNodePath(AppContext appContext){
    	return getAppPath(appContext)+ FullRestCRUDManagerKeys.CTE_FIELDSCORRESPONDENCY_NODE;
    }
    
    /**
     * Return the "headers for the service" node from a given app into RestCRUDManager module
     * @param appContext
     * @return
     */
    public static String getServiceHeadersConfigNodePath(AppContext appContext){
    	return getAppPath(appContext)+ FullRestCRUDManagerKeys.CTE_URLSERVICEHEADERSPATH;
    }
    
    /**
     * Return a map with all the headers to be used in the service call
     * @param appContext
     * @return
     * @throws RepositoryException
     */
    public static Map<String, String> getHeadersFromConfig(AppContext appContext) throws RepositoryException {

        Map<String, String> headers = new HashMap<>();

        Session session = MgnlContext.getJCRSession(FullRestCRUDManagerKeys.CTE_CONFIGWORKSPACE);
        Node serviceHeadersNode = session.getNode(Utils.getServiceHeadersConfigNodePath(appContext));

        PropertyIterator propertiesIterator = serviceHeadersNode.getProperties();

        while (propertiesIterator.hasNext()) {
            javax.jcr.Property property = propertiesIterator.nextProperty();

            String keyOfProperty = property.getPath().split(FullRestCRUDManagerKeys.CTE_SLASH)[property.getPath().split(FullRestCRUDManagerKeys.CTE_SLASH).length - 1];
            String valueOfProperty = property.getValue().getString();

            if (!keyOfProperty.contains(FullRestCRUDManagerKeys.CTE_COLON)) {
                headers.put(keyOfProperty, valueOfProperty);
            }

            log.debug(FullRestCRUDManagerKeys.LOGGERTEXT_NAMEPROPERTY + keyOfProperty);
            log.debug(FullRestCRUDManagerKeys.LOGGERTEXT_VALUEPROPERTY + valueOfProperty);
        }

        return headers;

    }
    
    /**
     * Get node that represents a field from config
     * 
     * @param appContext
     * @param id
     * @return
     * @throws RepositoryException
     */
    public static Node getFieldNodeFromConfig(AppContext appContext,String id) throws RepositoryException {
    	
    	Node fieldNode =  null;

        //Querying to retrieve the node that represent the field
        String queryStr = "SELECT * FROM [" + JcrConstants.NT_BASE + "] as t "
        				+ " WHERE ISDESCENDANTNODE(t, '" + Utils.getFieldsNodeConfigNodePath(appContext) + "') AND name(t) = '" + id + "' ";
        //Get an iterator with all the nodes 
        NodeIterator nodeIt = QueryUtil.search(FullRestCRUDManagerKeys.CTE_CONFIGWORKSPACE, queryStr);
        if (nodeIt.hasNext()) {
        	//Get the node
        	fieldNode = nodeIt.nextNode();
        }
        //Return the node
        return fieldNode;
    }
    
    /**
     * Method to retrieve from configuration all the correspondency between JSON fields and ITEM fields
     * 
     * @param appContext
     * @return
     * @throws RepositoryException
     */
    public static Map<String, String> getFieldsCorrespondencyFromConfig(AppContext appContext) throws RepositoryException {

    	Map<String,String> fromJSONToJCRKeys = new HashMap<>();

        //Querying to retrieve all fields of the node Fields
        String queryStr = "SELECT * FROM [" + JcrConstants.NT_BASE + "] as t "
        				+ " WHERE ISDESCENDANTNODE(t, '" + Utils.getFieldsNodeConfigNodePath(appContext) + "') AND t.[jcr:primaryType] = 'mgnl:contentNode' ";
        //Get an iterator with all the nodes 
        NodeIterator nodeIt = QueryUtil.search(FullRestCRUDManagerKeys.CTE_CONFIGWORKSPACE, queryStr);
        if (nodeIt.hasNext()) {
        	while(nodeIt.hasNext()){
        		//Get a node. That node represents a field
        		Node fieldNode = nodeIt.nextNode();
        		//Get the name of the node and insert it as key
        		String keyOfProperty = fieldNode.getName();
        		String valueOfProperty = fieldNode.getProperty("jsonField").getString();
        		fromJSONToJCRKeys.put(keyOfProperty, valueOfProperty);
        	}
        }

        return fromJSONToJCRKeys;

    }
    
    /**
     * Get the primary field
     * @param appContext
     * @return
     */
    public static String getPrimaryField(AppContext appContext){
        
    	String id = "";
    	//Querying to retrieve all fields of the node Fields
        String queryStr = "SELECT * FROM [" + JcrConstants.NT_BASE + "] as t "
        				+ " WHERE ISDESCENDANTNODE(t, '" + Utils.getFieldsNodeConfigNodePath(appContext) + "') AND t.[jcr:primaryType] = 'mgnl:contentNode' ";
        //Get an iterator with all the nodes 
        NodeIterator nodeIt;
		try {
			nodeIt = QueryUtil.search(FullRestCRUDManagerKeys.CTE_CONFIGWORKSPACE, queryStr);
			if (nodeIt.hasNext()) {
            	while(nodeIt.hasNext()){
            		//Get a node. That node represents a field
            		Node fieldNode = nodeIt.nextNode();
            		if(
            			(fieldNode.hasProperty("primary")&&
            			(fieldNode.getProperty("primary").getBoolean()))
            		){
            				id = fieldNode.getName();
            		}
            	}
            }
		} catch (RepositoryException e) {
			log.error("BasicItem.getIDs: " + e);
		}
    	return id;
    }
    
    /**
     * Method to retrieve from configuration all the correspondency between JSON fields and ITEM fields
     * 
     * @param appContext
     * @return
     * @throws RepositoryException
     */
    public static Map<String,Object> getItemFieldsFromConfig(AppContext appContext) throws RepositoryException {

    	Map<String,Object> mapOfProperties = new HashMap<>();

        //Querying to retrieve all fields of the node Fields
        String queryStr = "SELECT * FROM [" + JcrConstants.NT_BASE + "] as t "
        				+ " WHERE ISDESCENDANTNODE(t, '" + Utils.getFieldsNodeConfigNodePath(appContext) + "') AND t.[jcr:primaryType] = 'mgnl:contentNode' ";
        //Get an iterator with all the nodes 
        NodeIterator nodeIt = QueryUtil.search(FullRestCRUDManagerKeys.CTE_CONFIGWORKSPACE, queryStr);
        if (nodeIt.hasNext()) {
        	while(nodeIt.hasNext()){
        		//Get a node. That node represents a field
        		Node fieldNode = nodeIt.nextNode();
        		//Get the name of the node and insert it as key
        		String keyOfProperty = fieldNode.getName();
        		String valueOfProperty = "";
        		if(
            			(fieldNode.hasProperty("primary")&&
            			(fieldNode.getProperty("primary").getBoolean()))
            		){
        			valueOfProperty = FullRestCRUDManagerKeys.DEFAULT_ID;
            	}
        		
        		
        		mapOfProperties.put(keyOfProperty, valueOfProperty);
        	}
        }

        return mapOfProperties;

    }
    
    /**
     * Method to retrieve from configuration all the correspondency between JSON fields and ITEM fields
     * 
     * @param appContext
     * @return
     * @throws RepositoryException
     */
    public static Map<String,Object> getItemFieldsFromConfigForCreate(AppContext appContext) throws RepositoryException {

    	Map<String,Object> mapOfProperties = new HashMap<>();

        //Querying to retrieve all fields of the node Fields
        String queryStr = "SELECT * FROM [" + JcrConstants.NT_BASE + "] as t "
        				+ " WHERE ISDESCENDANTNODE(t, '" + Utils.getFieldsNodeConfigNodePath(appContext) + "') AND t.[jcr:primaryType] = 'mgnl:contentNode' ";
        //Get an iterator with all the nodes 
        NodeIterator nodeIt = QueryUtil.search(FullRestCRUDManagerKeys.CTE_CONFIGWORKSPACE, queryStr);
        if (nodeIt.hasNext()) {
        	while(nodeIt.hasNext()){
        		//Get a node. That node represents a field
        		Node fieldNode = nodeIt.nextNode();
        		//Get the name of the node and insert it as key
        		String keyOfProperty = fieldNode.getName();
        		String valueOfProperty = "";
        		if(
            			(fieldNode.hasProperty("primary")&&
            			(fieldNode.getProperty("primary").getBoolean()))
            		){
        			valueOfProperty = FullRestCRUDManagerKeys.DEFAULT_ID;
            	}
        		if(
        			(fieldNode.hasProperty("allowedInJsonMethod")&&
            		("".equals(fieldNode.getProperty("allowedInJsonMethod").getString())))
            	){
        			valueOfProperty = FullRestCRUDManagerKeys.DEFAULT_ID;
        		}
        		
        		mapOfProperties.put(keyOfProperty, valueOfProperty);
        	}
        }

        return mapOfProperties;

    }
    
    
    /**
     * 
     * @param appContext
     * @return
     * @throws RepositoryException 
     */
    public static String getTranslatorClassName(AppContext appContext) throws RepositoryException{
    	
    	String valueOfProperty = "";
    	//Querying to retrieve all fields of the node Fields
    	String queryStr = "SELECT * FROM [" + JcrConstants.NT_BASE + "] as t "
				+ " WHERE ISDESCENDANTNODE(t, '" + Utils.getAppPath(appContext) + "') and name(t) = 'serviceConfig'";
        //Get an iterator with all the nodes 
        NodeIterator nodeIt = QueryUtil.search(FullRestCRUDManagerKeys.CTE_CONFIGWORKSPACE, queryStr);
        if (nodeIt.hasNext()) {
        	while(nodeIt.hasNext()){
        		//Get a node. That node represents a field
        		Node fieldNode = nodeIt.nextNode();
        		if(
            			(fieldNode.hasProperty("jsonTranslatorClass"))
            	){
        			valueOfProperty = fieldNode.getProperty("jsonTranslatorClass").getString();
            	}
        	}
        }
    	
    	return valueOfProperty;
    }
    
    public static String getServiceClassName(AppContext appContext) throws RepositoryException{
    	
    	String valueOfProperty = "";
    	//Querying to retrieve all fields of the node Fields
    	String queryStr = "SELECT * FROM [" + JcrConstants.NT_BASE + "] as t "
				+ " WHERE ISDESCENDANTNODE(t, '" + Utils.getAppPath(appContext) + "') and name(t) = 'serviceConfig'";
        //Get an iterator with all the nodes 
        NodeIterator nodeIt = QueryUtil.search(FullRestCRUDManagerKeys.CTE_CONFIGWORKSPACE, queryStr);
        if (nodeIt.hasNext()) {
        	while(nodeIt.hasNext()){
        		//Get a node. That node represents a field
        		Node fieldNode = nodeIt.nextNode();
        		if(
            			(fieldNode.hasProperty("serviceClass"))
            	){
        			valueOfProperty = fieldNode.getProperty("serviceClass").getString();
            	}
        	}
        }
    	
    	return valueOfProperty;
    }
    
}
