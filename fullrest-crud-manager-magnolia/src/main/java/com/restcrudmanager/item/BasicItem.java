package com.restcrudmanager.item;

import info.magnolia.cms.util.QueryUtil;
import info.magnolia.ui.api.app.AppContext;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.JcrConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.restcrudmanager.base.keys.FullRestCRUDManagerKeys;
import com.restcrudmanager.utils.Utils;
import com.vaadin.data.Item;

/**
 * The interface for an {@link com.vaadin.data.Item} for the voucher-browser.
 *
 * @author isilanes
 */
public interface BasicItem extends Item {

	

    /**
     * A static inner class which provides a static method to fetch the
     * properties map &lt;String, Class&gt; (propertyId, clazz).
     */
    public class IDs {

        private static final Map<String, Class> properties = new HashMap<>();
    	private static final Logger log = LoggerFactory.getLogger(IDs.class);


    	private IDs(){
        	//nothing to do here
        }
    	
        /**
         * Returns a map which contains the IDs and its types.
         */
        public static Map<String, Class> getIDs(AppContext appContext) {
        	
        	//Clear Map
        	properties.clear();
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
	            		String nameOfTheField = fieldNode.getName();
	            		String stringClassOfTheField = fieldNode.getProperty("itemClass").getString();
	            		Class<?> classOfTheField = Class.forName(stringClassOfTheField);
	            		properties.put(nameOfTheField, classOfTheField);
	            	}
	            }
			} catch (RepositoryException | ClassNotFoundException e) {
				log.error("BasicItem.getIDs: " + e);
			}
        	
            return properties;
        }
        
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
	            		String nameOfTheField = fieldNode.getName();
	            		String stringClassOfTheField = fieldNode.getProperty("itemClass").getString();
	            		Class<?> classOfTheField = Class.forName(stringClassOfTheField);
	            		properties.put(nameOfTheField, classOfTheField);
	            	}
	            }
			} catch (RepositoryException | ClassNotFoundException e) {
				log.error("BasicItem.getIDs: " + e);
			}
        	return id;
        }
    }

}
