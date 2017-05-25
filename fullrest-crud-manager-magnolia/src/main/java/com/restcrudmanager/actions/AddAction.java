package com.restcrudmanager.actions;

import info.magnolia.ui.api.action.AbstractAction;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.action.ConfiguredActionDefinition;
import info.magnolia.ui.api.app.AppContext;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.form.EditorValidator;

import java.net.URISyntaxException;

import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.restcrudmanager.base.FullRestService;
import com.restcrudmanager.utils.Utils;
import com.vaadin.data.Item;

/**
 * Saves file modifications on back.
 *
 * @param <T> definition type.
 * @author isilanes
 */
public class AddAction<T extends AddActionDefinition> extends AbstractAction<T> {

    private final Item item;

    private final EditorCallback callback;
    private FullRestService service;
    protected final EditorValidator validator;
    protected final UiContext uiContext;
    protected final AppContext appContext;

    private static final Logger log = LoggerFactory.getLogger(AddAction.class);

    /**
     * Constructor
     * @param definition
     * @param item
     * @param callback
     * @param validator
     * @param uiContext
     */
    public AddAction(T definition, Item item, EditorCallback callback, 
    		EditorValidator validator, UiContext uiContext, AppContext appContext) {
        super(definition);
        this.item = item;
        this.callback = callback; 
        this.validator = validator;
        this.uiContext = uiContext;
        this.appContext = appContext;

        Class<?> serviceClass;
		try {
			serviceClass = Class.forName(Utils.getServiceClassName(appContext));
			
			this.service = (FullRestService) serviceClass.newInstance();
		} catch (ClassNotFoundException | RepositoryException | InstantiationException | IllegalAccessException e) {
			log.error("Error en AddAction: ",e);
		}
//        translatorClass.newInstance()
//        this.service = (FullRestService) serviceClass.newInstance();
//        this.service = new BaseServiceImpl();//TODO Cambiar por llamada reflection
        
//        Class<?> translatorClass = Class.forName(Utils.getTranslatorClassName(app));
//		Method getMapMethod = translatorClass.getDeclaredMethod("getMap", cArg);
//		LinkedHashMap<String, Item> partialItemMapList = (LinkedHashMap<String, Item>) getMapMethod.invoke(translatorClass.newInstance(), jsonArray); 
        
    }

    /**
     * Execute method
     */
    @Override
    public void execute() throws ActionExecutionException {
        try {
        	// First Validate
            validator.showValidation(true); 
            //Check if is valid
            
            if (validator.isValid()) {
            	//If is valid we go to the service
	            service.addItem(item,uiContext, appContext); 
	            //IF all goes well, we send the callback
	            
	            callback.onSuccess("commit");
            } else {
            	
            	//If is not valid, send error to log
                log.info("Validation error(s) occurred. No save performed.");
            }
            
        } catch (URISyntaxException e) {
        	//Problems with the URL
            log.error("Error calling service to put results", e);
            
        }

    }
}
