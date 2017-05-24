package com.restcrudmanager.contentconnector;

import info.magnolia.ui.api.app.AppContext;
import info.magnolia.ui.api.context.UiContext;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.restcrudmanager.base.keys.FullRestCRUDManagerKeys;
import com.restcrudmanager.container.ServiceFlatContainer;
import com.restcrudmanager.item.BasicItem;
import com.restcrudmanager.item.BasicPropertysetItem;
import com.restcrudmanager.utils.Utils;
import com.vaadin.data.Container;
import com.vaadin.data.Item;

/**
 * The implementation of a {@link info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector}
 * for the module voucher-browser which is carries its {@link Container}.
 * @author isilanes
 */
public class ServiceBrowserContentConnectorImpl implements ServiceBrowserContentConnector {

    private final ServiceFlatContainer container;
    private Object defaultItem;
    private AppContext appContext;
    
    private static Logger log = LoggerFactory.getLogger(ServiceBrowserContentConnectorImpl.class);

    @Inject
    public ServiceBrowserContentConnectorImpl( UiContext uiContext, AppContext appContext) {
        container = new ServiceFlatContainer( uiContext, appContext);
        this.appContext = appContext;
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public String getItemUrlFragment(Object itemId) {
        return canHandleItem(itemId) ? (String) itemId : null;
    }

    @Override
    public Object getItemIdByUrlFragment(String urlFragment) {
        return canHandleItem(urlFragment) ? urlFragment : null;
    }

    @Override
    public Object getDefaultItemId() {
    	defaultItem = FullRestCRUDManagerKeys.DEFAULT_ID; 
        return defaultItem;
    }

    @Override
    public Item getItem(Object itemId) {
    	if(FullRestCRUDManagerKeys.DEFAULT_ID.equals(itemId)){
    		
    		//Create a default element to avoid problems
            //Create map of properties
            Map<String, Object> mapOfProperties = new HashMap<>();
			try {
				mapOfProperties = Utils.getItemFieldsFromConfig(appContext);
			} catch (RepositoryException e) {
				log.error("getItem: " + e);
			}
    		
    		return new BasicPropertysetItem(mapOfProperties);
    	}else{
    		return canHandleItem(itemId) ? container.getItem(itemId) : null;
    	}
    }

    @Override
    public Object getItemId(Item item) {
        return item instanceof BasicPropertysetItem ? item.getItemProperty(BasicItem.IDs.getPrimaryField(appContext)).getValue() : null;
    }

    @Override
    public boolean canHandleItem(Object itemId) {
        return itemId instanceof String;
    }

}
