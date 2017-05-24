/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.restcrudmanager.actions;

import info.magnolia.event.EventBus;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.ui.api.action.AbstractAction;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.app.AppContext;
import info.magnolia.ui.api.app.AppEventBus;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.event.ContentChangedEvent;
import info.magnolia.ui.dialog.DialogPresenter;
import info.magnolia.ui.dialog.callback.DefaultEditorCallback;
import info.magnolia.ui.dialog.formdialog.FormDialogPresenter;
import info.magnolia.ui.dialog.formdialog.FormDialogPresenterFactory;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.framework.action.OpenCreateDialogActionDefinition;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;
import info.magnolia.ui.vaadin.overlay.MessageStyleTypeEnum;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.restcrudmanager.base.keys.FullRestCRUDManagerKeys;
import com.restcrudmanager.item.BasicPropertysetItem;
import com.restcrudmanager.utils.Utils;
import com.vaadin.data.Item;

/**
 * Opens a dialog for creating a new node in a tree.
 *
 * @see OpenCreateDialogActionDefinition
 * @author isilanes
 */
public class OpenDeleteDialogAction extends AbstractAction<OpenCreateActionDefinition> {

    private final Item parentItem;
    private final FormDialogPresenterFactory formDialogPresenterFactory;
    private final UiContext uiContext;
    private final EventBus eventBus;
    private ContentConnector contentConnector;
    private final SimpleTranslator i18n;
    private AppContext appContext;
    
    private static final Logger log = LoggerFactory.getLogger(OpenDeleteDialogAction.class);


    /**
     * Constructor
     * 
     * @param definition
     * @param parentItem
     * @param formDialogPresenterFactory
     * @param uiContext
     * @param eventBus
     * @param contentConnector
     * @param i18n
     */
    @Inject
    public OpenDeleteDialogAction(OpenCreateActionDefinition definition, Item parentItem, FormDialogPresenterFactory formDialogPresenterFactory, 
    		UiContext uiContext, @Named(AppEventBus.NAME) final EventBus eventBus, ContentConnector contentConnector, 
    		SimpleTranslator i18n, AppContext appContext) {
        super(definition);
        this.parentItem = parentItem;
        this.formDialogPresenterFactory = formDialogPresenterFactory;
        this.uiContext = uiContext;
        this.eventBus = eventBus;
        this.contentConnector = contentConnector;
        this.i18n = i18n;
        this.appContext = appContext;
    }

    /**
     * Execute method
     */
    @Override
    public void execute() throws ActionExecutionException {
    	
        final String dialogName = getDefinition().getDialogName();
        if (StringUtils.isBlank(dialogName)) {
        	//If there is not a name, send notification of error.
            uiContext.openNotification(MessageStyleTypeEnum.ERROR, false, i18n.translate("ui-framework.actions.no.dialog.definition", getDefinition().getName()));
            return;

        }

        //Create presenter for the dialog
        final FormDialogPresenter formDialogPresenter = formDialogPresenterFactory.createFormDialogPresenter(dialogName);

        if (formDialogPresenter == null) {
        	//If there is not a presenter, send notification of error.
            uiContext.openNotification(MessageStyleTypeEnum.ERROR, false, i18n.translate("ui-framework.actions.dialog.not.registered", dialogName));
            
            return;
        }

        //Create a default element to avoid problems
        //Create map of properties
        Map<String, Object> mapOfProperties = new HashMap<>();
		try {
			mapOfProperties = Utils.getItemFieldsFromConfig(appContext);
		} catch (RepositoryException e) {
			log.error("getItem: " + e);
		}
        
        final Item item = new BasicPropertysetItem(mapOfProperties);
        
        //Use that default element into the presenter
        formDialogPresenter.start(item, getDefinition().getDialogName(), uiContext, createEditorCallback(formDialogPresenter, item, eventBus));
    }
    
    /**
     * Create callback 
     * @param dialogPresenter
     * @param item
     * @param eventBus
     * @return
     */
    protected EditorCallback createEditorCallback(final DialogPresenter dialogPresenter, final Item item, final EventBus eventBus) {
        return new DefaultEditorCallback(dialogPresenter) {
            @Override
            public void onSuccess(String actionName) {
            	//If all goes well, send notification across the bus
            	log.debug("LANZAMOS EVENTO POR EL BUS PARA REINICIAR. EL ITEM ES: "+item.getItemProperty(FullRestCRUDManagerKeys.FIELD_PRIMARYKEY));
            	if(parentItem!=null){
            		log.debug("Parent Item: "+parentItem.toString());
            	}
                eventBus.fireEvent(new ContentChangedEvent(contentConnector.getItemId(item), true));
                super.onSuccess(actionName);
            }
        };
    }

    /**
     * Get the connector
     * @return
     */
    protected ContentConnector getContentConnector() {
        return contentConnector;
    }
}