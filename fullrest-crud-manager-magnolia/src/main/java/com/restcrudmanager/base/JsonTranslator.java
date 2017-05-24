package com.restcrudmanager.base;

import java.util.LinkedHashMap;

import org.json.JSONArray;

import com.vaadin.data.Item;

public interface JsonTranslator {

	public LinkedHashMap<String, Item> getMap(JSONArray jsonArray);
	
}
