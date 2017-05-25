package com.restcrudmanager.container;

import info.magnolia.ui.api.app.AppContext;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.workbench.container.Refreshable;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jcr.RepositoryException;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.restcrudmanager.base.FullRestService;
import com.restcrudmanager.base.keys.FullRestCRUDManagerKeys;
import com.restcrudmanager.item.BasicItem;
import com.restcrudmanager.service.BaseServiceImpl;
import com.restcrudmanager.utils.Utils;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractContainer;

/**
 * A flat {@link Container} to host VoucherItems and Strings as IDs.
 *
 * @author isilanes
 */
public class ServiceFlatContainer extends AbstractContainer implements Container, Container.Indexed, Container.ItemSetChangeNotifier, Refreshable {

    private static final Logger log = LoggerFactory.getLogger(ServiceFlatContainer.class);
    private static final long serialVersionUID = 1905122041950251207L;

    private final List<ItemSetChangeListener> listeners = new LinkedList<>();
    private static Map<Object, Class> properties = new HashMap<>();
    private List<String> ids;
    private final LinkedHashMap<String, Item> itemsMap = new LinkedHashMap<>();
    private static UiContext uiContext;
    private FullRestService restService;
    private AppContext app;

    /**
     * Constructor
     * @param uiContext
     */
    @Inject
    public ServiceFlatContainer(UiContext uiContext,AppContext appContext) {
    	ServiceFlatContainer.uiContext = uiContext;
    	restService = new BaseServiceImpl();
    	app = appContext;
        configure();
    }

    /**
     * Refresh container
     */
    @Override
    public void refresh() {
    	log.debug("REFRESHING CONTAINER");
        removeAllItems();
    	log.debug("RELOADING CONTAINER");
        loadAll();
    	log.debug("EXIT REFRESH");
        notifyItemSetChange();
        log.debug("LANZADO NOTIFICACION");
    }

    /**
     * Load all elements
     */
    private void loadAll() {
    	//Init ids
        ids = new ArrayList<>();
        //Start operation time
        long start = System.currentTimeMillis();
        log.debug("Loading IDs in {}", this.getClass().getName());
        //Call service
        JSONArray jsonArray = restService.getAll(uiContext,app);

        log.debug("JSONArray value: {}", jsonArray.toString());
        log.debug("JSONArray length: {}", jsonArray.length());
        
        Class[] cArg = new Class[1];
        cArg[0] = JSONArray.class;
        JSONArray[] cArgValues = new JSONArray[1];
        cArgValues[0] = jsonArray;
        
        try {
        	Class<?> translatorClass = Class.forName(Utils.getTranslatorClassName(app));
			Method getMapMethod = translatorClass.getDeclaredMethod("getMap", cArg);
			LinkedHashMap<String, Item> partialItemMapList = (LinkedHashMap<String, Item>) getMapMethod.invoke(translatorClass.newInstance(), jsonArray); 
		
        
	        Iterator<Entry<String, Item>> itemMapIterator = partialItemMapList.entrySet().iterator();
	        
	        if(itemMapIterator.hasNext()){
	        	while(itemMapIterator.hasNext()){
	        		Entry<String, Item> node = itemMapIterator.next();
	        		String id = node.getKey();
	        		if (!ids.contains(id)) {
	                  ids.add(id);
	                  log.debug("Adding item to result list {} IDs in {} ms", id, node.getValue().toString());
	                  itemsMap.put(id, node.getValue());
	              }
	        	}
	        }
        
        } catch (NoSuchMethodException | SecurityException
				| ClassNotFoundException | IllegalAccessException | IllegalArgumentException 
				| InvocationTargetException | InstantiationException | RepositoryException e) {
        	log.error("ERROR in loadAll ", e);
		} 
        
        //Put duration of the operation into log
        log.debug("TOTALLY loaded {} IDs in {} ms", ids.size(), System.currentTimeMillis() - start);
    }
    

    /**
     * Get item
     */
    @Override
    public Item getItem(Object itemId) {
        return itemsMap.get(itemId);
    }
    
    /**
     * Get item ids
     */
    @Override
    public Collection<?> getItemIds() {
        if (ids == null) {
            loadAll();
        }
        return ids;
    }

    /**
     * get container property
     */
    @Override
    public Property getContainerProperty(Object itemId, Object propertyId) {
        Item item = getItem(itemId);
        if (item != null) {
            Property property = item.getItemProperty(propertyId);
            if (property != null) {
                return property;
            }
        }
        return null;
    }

    /**
     * Get type
     */
    @Override
    public Class<?> getType(Object propertyId) {
        return BasicItem.IDs.getIDs(app).get(propertyId);
    }

    /**
     * get size
     */
    @Override
    public int size() {
        return ids.size();
    }

    /**
     * Check if an itemId is contained
     */
    @Override
    public boolean containsId(Object itemId) {
        return ids.contains(itemId);
    }

    /**
     * Add an item
     */
    @Override
    public Item addItem(Object itemId){
    	//Not supported
    	
    	
        throw new UnsupportedOperationException(FullRestCRUDManagerKeys.ERROR_NOTSUPPORTED_TEXT);
    }

    
    @Override
    public Object addItem(){
    	//Not supported
        throw new UnsupportedOperationException(FullRestCRUDManagerKeys.ERROR_NOTSUPPORTED_TEXT);
    }

    /**
     * Remove an item
     */
    @Override
    public boolean removeItem(Object itemId){
    	//Not supported
    	
        throw new UnsupportedOperationException(FullRestCRUDManagerKeys.ERROR_NOTSUPPORTED_TEXT);
    }

    /**
     * Add property
     */
    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue){
        //Put property
    	properties.put(propertyId, type);
    	//Check if property is contained and returns result
        return properties.containsKey(propertyId);
    }

    /**
     * Remove property
     */
    @Override
    public boolean removeContainerProperty(Object propertyId){
    	//Remove
        properties.remove(propertyId);
        //Check if property is contained and returns the result
        return !properties.containsKey(propertyId);
    }

    /**
     * Remove all
     */
    @Override
    public boolean removeAllItems(){
        ids = null;
        itemsMap.clear();
        return itemsMap.size() == 0;
    }

    /**
     * Get preoperty ids contained
     */
    @Override
    public Collection<?> getContainerPropertyIds() {
        return BasicItem.IDs.getIDs(app).keySet();
    }

    /**
     * Configure container
     */
    private void configure() {
        Map<String, Class> props = BasicItem.IDs.getIDs(app);//TODO change for generic
        for (Entry<String, Class> entry : props.entrySet() ) {
            addContainerProperty(entry.getKey(), entry.getValue(), null);
            
        }
    }

    /**
     * Position of an id into array of ids
     */
    @Override
    public int indexOfId(Object itemId) {
        List<String> keys = new ArrayList<>(itemsMap.keySet());
        return keys.indexOf(itemId);
    }

    /**
     * Get the id of a given position
     */
    @Override
    public Object getIdByIndex(int index) {
        List<String> keys = new ArrayList<>(itemsMap.keySet());
        return keys.get(index);
    }
    
    /**
     * Get the N following items from a given position 
     */
    @Override
    public List<?> getItemIds(int startIndex, int numberOfItems) {
        if (itemsMap == null) {
            loadAll();
        }
        return new ArrayList<>(itemsMap.keySet());
    }

    /**
     * Add empty item to a certain position
     */
    @Override
    public Object addItemAt(int index){
        throw new UnsupportedOperationException(FullRestCRUDManagerKeys.ERROR_NOTSUPPORTED_TEXT);
    }

    /**
     * Add item to a certain position
     */
    @Override
    public Item addItemAt(int index, Object newItemId){
        throw new UnsupportedOperationException(FullRestCRUDManagerKeys.ERROR_NOTSUPPORTED_TEXT);
    }

    /**
     * Get the id of the next item 
     */
    @Override
    public Object nextItemId(Object itemId) {
        List<String> keys = new ArrayList<>(itemsMap.keySet());
        int index = keys.indexOf(itemId);
        if (index < keys.size() - 1) {
            return keys.get(index + 1);
        }
        return null;
    }

    /**
     * Get the id of the previous item 
     */
    @Override
    public Object prevItemId(Object itemId) {
        List<String> keys = new ArrayList<>(itemsMap.keySet());
        int index = keys.indexOf(itemId);
        if (index > 0) {
            return keys.get(index - 1);
        }
        return null;
    }

    /**
     * first item of the list
     */
    @Override
    public Object firstItemId() {
        List<String> keys = new ArrayList<>(itemsMap.keySet());
        return keys.get(0);
    }

    /**
     * Last item of the list
     */
    @Override
    public Object lastItemId() {
        List<String> keys = new ArrayList<>(itemsMap.keySet());
        return keys.get(keys.size() - 1);
    }

    /**
     * Check if an given ID is the first of the list
     */
    @Override
    public boolean isFirstId(Object itemId) {
        List<String> keys = new ArrayList<>(itemsMap.keySet());
        return keys.get(0).equals(itemId);
    }

    /**
     * Check if an given ID is the last of the list
     */
    @Override
    public boolean isLastId(Object itemId) {
        List<String> keys = new ArrayList<>(itemsMap.keySet());
        return keys.get(keys.size() - 1).equals(itemId);
    }

    /**
     * Adds empty item after certain item
     */
    @Override
    public Object addItemAfter(Object previousItemId){
        throw new UnsupportedOperationException(FullRestCRUDManagerKeys.ERROR_NOTSUPPORTED_TEXT);
    }

    /**
     * Adds item after certain item
     */
    @Override
    public Item addItemAfter(Object previousItemId, Object newItemId){
        throw new UnsupportedOperationException(FullRestCRUDManagerKeys.ERROR_NOTSUPPORTED_TEXT);
    }

    /**
     * Change ItemSetChangelistener
     */
    @Override
    public void addItemSetChangeListener(ItemSetChangeListener listener) {
       listeners.add(listener);
    }

    /**
     * Add ItemSetChangelistener
     */
    @Override
    public void addListener(ItemSetChangeListener listener) {
       listeners.add(listener);
    }

    /**
     * Remove ItemSetChangelistener
     */
    @Override
    public void removeItemSetChangeListener(ItemSetChangeListener listener) {
       listeners.remove(listener);
    }

    /**
     * Remove ItemSetChangelistener
     */
    @Override
    public void removeListener(ItemSetChangeListener listener) {
       listeners.remove(listener);
    }

    /**
     * Notify change
     */
    private void notifyItemSetChange() {
       final Object[] l = listeners.toArray();
       BaseItemSetChangeEvent event = new BaseItemSetChangeEvent(this);
       for (int i = 0; i < l.length; i++) {
    	   log.debug("VALOR LISTENER: "+((ItemSetChangeListener) l[i]).toString());
           ((ItemSetChangeListener) l[i]).containerItemSetChange(event);
       }
    }

   /**
    * A custom {@link EventObject}.
    */
   protected static class BaseItemSetChangeEvent extends EventObject implements Container.ItemSetChangeEvent, Serializable {
	   
	   	/**
	   	 * 
	   	 */
	   private static final long serialVersionUID = 1L;
	   /**
	    * Contructor
	    * @param source
	    */
	   protected BaseItemSetChangeEvent(Container source) {
		   super(source);
	   }
	   /**
	    * Get container
	    */
	   @Override
	   public Container getContainer() {
		   return (Container) getSource();
	   }
   }
}
