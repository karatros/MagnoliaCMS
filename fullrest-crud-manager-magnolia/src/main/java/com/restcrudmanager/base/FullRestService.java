package com.restcrudmanager.base;

import info.magnolia.ui.api.app.AppContext;
import info.magnolia.ui.api.context.UiContext;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.jcr.RepositoryException;

import org.json.JSONArray;

import com.vaadin.data.Item;

public interface FullRestService {

	public String getUrl(AppContext appContext);
	public JSONArray getAll(UiContext uiContext,AppContext appContext);
	public boolean addItem(Item item,UiContext uiContext,AppContext appContext) throws URISyntaxException;
	public boolean editItem(Item item,UiContext uiContext,AppContext appContext) throws URISyntaxException;
	public boolean addOrEditItem(Item item, String mode,UiContext uiContext,AppContext appContext) throws URISyntaxException, MalformedURLException, RepositoryException;
	public boolean delItem(Item item,AppContext appContext) throws URISyntaxException;
}
