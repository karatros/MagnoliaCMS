package com.restcrudmanager.actions;

import info.magnolia.event.EventBus;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.ui.api.action.AbstractAction;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.app.AppContext;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.event.AdmincentralEventBus;
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

import com.restcrudmanager.item.BasicPropertysetItem;
import com.restcrudmanager.utils.Utils;
import com.vaadin.data.Item;

/**
 * Opens a dialog for creating a new node in a tree.
 *
 * @see OpenCreateDialogActionDefinition
 * @author isilanes
 */
public class OpenCreateDialogAction extends AbstractAction<OpenCreateActionDefinition> {

    private static final Logger log = LoggerFactory.getLogger(OpenCreateDialogAction.class);

    private final FormDialogPresenterFactory formDialogPresenterFactory;
    private final UiContext uiContext;
    private final EventBus eventBus;
    private final ContentConnector contentConnector;
    private final SimpleTranslator i18n;
    private AppContext appContext;

    /**
     * Constructor
     * 
     * @param definition
     * @param formDialogPresenterFactory
     * @param uiContext
     * @param eventBus
     * @param contentConnector
     * @param i18n
     */
    @Inject
    public OpenCreateDialogAction(OpenCreateActionDefinition definition, FormDialogPresenterFactory formDialogPresenterFactory,
            UiContext uiContext, @Named(AdmincentralEventBus.NAME) final EventBus eventBus, ContentConnector contentConnector, 
            SimpleTranslator i18n, AppContext appContext) {
        super(definition);
        this.formDialogPresenterFactory = formDialogPresenterFactory;
        this.uiContext = uiContext;
        this.eventBus = eventBus;
        this.contentConnector = contentConnector;
        this.i18n = i18n;
        this.appContext = appContext;
    }

    /**
     * Execute method of class
     */
    @Override
    public void execute() throws ActionExecutionException {

    	//Get dialog name
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
			mapOfProperties = Utils.getItemFieldsFromConfigForCreate(appContext);
			
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
                log.debug("LANZANDO EVENTO");
                eventBus.fireEvent(new ContentChangedEvent(contentConnector.getItemId(item), true));
                
                log.debug("LANZADO");
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
