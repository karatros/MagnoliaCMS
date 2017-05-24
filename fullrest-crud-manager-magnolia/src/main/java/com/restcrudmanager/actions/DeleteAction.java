package com.restcrudmanager.actions;

import info.magnolia.event.EventBus;
import info.magnolia.ui.api.action.AbstractAction;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.action.ConfiguredActionDefinition;
import info.magnolia.ui.api.app.AppContext;
import info.magnolia.ui.api.app.AppEventBus;
import info.magnolia.ui.api.event.ContentChangedEvent;
import info.magnolia.ui.form.EditorCallback;

import java.net.URISyntaxException;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.restcrudmanager.base.FullRestService;
import com.restcrudmanager.base.keys.FullRestCRUDManagerKeys;
import com.restcrudmanager.item.BasicItem;
import com.restcrudmanager.service.BaseServiceImpl;
import com.vaadin.data.Item;

/**
 * Saves file modifications on back.
 * @param <T> definition type.
 * @author isilanes
 */

public class DeleteAction<T extends ConfiguredActionDefinition> extends AbstractAction<T> {

    private Item item;

    private EditorCallback callback;
    
    private static final Logger log = LoggerFactory.getLogger(DeleteAction.class);

    private EventBus eventBus;
    
    private FullRestService service;
    private AppContext appContext;

    public DeleteAction(T definition, Item item, EditorCallback callback, @Named(AppEventBus.NAME) EventBus eventBus, AppContext appContext) {
        super(definition);
        this.item = item;
        this.callback = callback;
        this.eventBus = eventBus;
        this.service = new BaseServiceImpl();//TODO Cambiar por llamada reflection
        this.appContext = appContext;

    }

    @Override
    public void execute() throws ActionExecutionException {
        try {
        	service.delItem(item, appContext);
			log.debug("EXECUTE: LANZAMOS EVENTO POR EL BUS PARA REINICIAR. EL ITEM ES: "+
					item.getItemProperty(FullRestCRUDManagerKeys.FIELD_PRIMARYKEY)+" con bus "+ eventBus.getClass().getCanonicalName());
			eventBus.fireEvent(new ContentChangedEvent(item.getItemProperty(BasicItem.IDs.getPrimaryField(appContext)), true));
			callback.onSuccess("commit");
		} catch (URISyntaxException e) {
			log.error("Error calling service to put results",e);
		}
        
    }
}