package com.restcrudmanager.jsontranslator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.restcrudmanager.base.JsonTranslator;
import com.restcrudmanager.base.keys.FullRestCRUDManagerKeys;
import com.restcrudmanager.item.BasicPropertysetItem;
import com.vaadin.data.Item;

//TODO esta clase hay que hacerla modificable a través de la config
public class BasicJsonTranslator implements JsonTranslator {

    private static final Logger log = LoggerFactory.getLogger(BasicJsonTranslator.class);

	public LinkedHashMap<String, Item> getMap(JSONArray jsonArray){
		
		List<String> ids = new ArrayList<>();
		Map<String,Object> mapOfProperties = new HashMap<>();
		LinkedHashMap<String, Item> partialItemsMap = new LinkedHashMap<>();
		
		for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            log.debug("Itering over collection with obj "+json.toString());
            
            if (!json.isNull(FullRestCRUDManagerKeys.FIELD_ID)) {
                String idLanguage = String.valueOf(json.getInt(FullRestCRUDManagerKeys.FIELD_ID));
                log.debug("Exits code?: {}", !json.isNull(FullRestCRUDManagerKeys.FIELD_ID));
                if (!json.isNull(FullRestCRUDManagerKeys.FIELD_ID)) {
                	log.debug("Inserting idLanguage into lang with value {}", String.valueOf(json.getInt(FullRestCRUDManagerKeys.FIELD_ID)));
                	mapOfProperties.put(FullRestCRUDManagerKeys.FIELD_ID, String.valueOf(json.getInt(FullRestCRUDManagerKeys.FIELD_ID)));
                }
                log.debug("Exits code?: {}", !json.isNull(FullRestCRUDManagerKeys.FIELD_CODE));
                if (!json.isNull(FullRestCRUDManagerKeys.FIELD_CODE)) {
                	log.debug("Inserting code into lang with value {}", json.getString(FullRestCRUDManagerKeys.FIELD_CODE));
                    mapOfProperties.put(FullRestCRUDManagerKeys.FIELD_CODE, json.getString(FullRestCRUDManagerKeys.FIELD_CODE));
                }
                if (!json.isNull(FullRestCRUDManagerKeys.FIELD_TTOO)) {
                	log.debug("Inserting ttoo values into lang");
                	JSONObject ttooOBJ = json.getJSONObject(FullRestCRUDManagerKeys.FIELD_TTOO);
                	log.debug("Exits idTtoo?: {}", !json.isNull(FullRestCRUDManagerKeys.FIELD_TTOOID));
                    if (!ttooOBJ.isNull(FullRestCRUDManagerKeys.FIELD_TTOOID)) {
                    	log.debug("Inserting ttooId into lang with value {}", ttooOBJ.getInt(FullRestCRUDManagerKeys.FIELD_TTOOID));
                    	mapOfProperties.put(FullRestCRUDManagerKeys.FIELD_TTOOID, String.valueOf(ttooOBJ.getInt(FullRestCRUDManagerKeys.FIELD_TTOOID)));
                    }
                    log.debug("Exits TtooName?: {}", !json.isNull(FullRestCRUDManagerKeys.FIELD_NAME));
                    if (!ttooOBJ.isNull(FullRestCRUDManagerKeys.FIELD_NAME)) {
                    	log.debug("Inserting ttooName into lang with value {}", ttooOBJ.getString(FullRestCRUDManagerKeys.FIELD_NAME));
                    	mapOfProperties.put(FullRestCRUDManagerKeys.FIELD_NAME, ttooOBJ.getString(FullRestCRUDManagerKeys.FIELD_NAME));
                    }
                }
                
                
            	String id = (String)mapOfProperties.get(FullRestCRUDManagerKeys.FIELD_ID);
                log.debug("Is {} into IDs  {} ", id, ids.toString());
                if (!ids.contains(id)) {
                    ids.add(id);
                    Item item = new BasicPropertysetItem(mapOfProperties);
                    log.debug("Adding item to result list {} IDs in {} ms", id, item.toString());
                    partialItemsMap.put(id, item);
                }
                
            }
        }
		
		
		return partialItemsMap;
	}
}
