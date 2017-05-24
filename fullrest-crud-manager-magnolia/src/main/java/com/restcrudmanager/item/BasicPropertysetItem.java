package com.restcrudmanager.item;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;

/**
 * An {@link com.vaadin.data.Item} implementation for the voucher-browser app.
 *
 * @author isilanes
 */
public class BasicPropertysetItem extends PropertysetItem implements Item {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(BasicPropertysetItem.class);
    /**
     * A constructor to use with a given {@link Language}.
     */
    public BasicPropertysetItem(Map<String,Object> mapOfProperties) {
    	
    	//Get entries of the map
    	Set<Entry<String, Object>> listOfEntries = mapOfProperties.entrySet();
    	for(Entry<String, Object> propertyOfTheItem : listOfEntries){
    		//Iterate over those entries
    		log.info(" Trying inserting value for: "+propertyOfTheItem.getKey());
    		if (
    			(propertyOfTheItem!=null)&&
    			(propertyOfTheItem.getValue()!=null)
    		){
    			log.info("Inserting register KEY( "+propertyOfTheItem.getKey()+" ) and VALUE( "+ propertyOfTheItem.getValue() +" )");
    			//If entry value is not null, insert it into properties
                addItemProperty(propertyOfTheItem.getKey(), new ObjectProperty(propertyOfTheItem.getValue()));
            }
    	}
    	
        log.info("Exiting LanguagePropertysetItem ");
    }
    
}

