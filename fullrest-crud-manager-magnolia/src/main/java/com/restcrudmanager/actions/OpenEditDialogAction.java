package com.restcrudmanager.actions;

import info.magnolia.event.EventBus;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.ui.api.action.AbstractAction;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.event.AdmincentralEventBus;
import info.magnolia.ui.api.event.ContentChangedEvent;
import info.magnolia.ui.dialog.formdialog.FormDialogPresenter;
import info.magnolia.ui.dialog.formdialog.FormDialogPresenterFactory;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.form.EditorValidator;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;
import info.magnolia.ui.vaadin.integration.contentconnector.SupportsCreation;
import info.magnolia.ui.vaadin.overlay.MessageStyleTypeEnum;
import info.magnolia.ui.workbench.list.ListPresenter;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;

/**
 * A {@link ListPresenter} to host VoucherItems.
 * @author isilanes
 */
public class OpenEditDialogAction extends AbstractAction<OpenCreateActionDefinition> {

	private final Item parentItem;
    private final FormDialogPresenterFactory formDialogPresenterFactory;
    private final UiContext uiContext;
    private final EventBus eventBus;
    private ContentConnector contentConnector;
    private final SimpleTranslator i18n;
    protected final EditorValidator validator;
    
    private final Logger log = LoggerFactory.getLogger(OpenEditDialogAction.class);

    /**
     * Constructor
     * @param definition
     * @param parentItem
     * @param formDialogPresenterFactory
     * @param uiContext
     * @param eventBus
     * @param contentConnector
     * @param i18n
     * @param validator
     */
	@Inject
    public OpenEditDialogAction(OpenCreateActionDefinition definition, Item parentItem, FormDialogPresenterFactory formDialogPresenterFactory, UiContext uiContext, @Named(AdmincentralEventBus.NAME) final EventBus eventBus, 
    		ContentConnector contentConnector, SimpleTranslator i18n, EditorValidator validator) {
        super(definition);
        log.debug("Entrando en LanguageEdit Constructor");
        log.debug("Valores");
        log.debug("ParentItem: "+parentItem.toString());
        //Parent item to use it 
        this.parentItem = parentItem;
        log.debug("formDialogPresenterFactory: "+formDialogPresenterFactory.toString());
        //Presenter for the dialog
        this.formDialogPresenterFactory = formDialogPresenterFactory;
        log.debug("uiContext: "+uiContext.toString());
        //UIcontext for notifications
        this.uiContext = uiContext;
        log.debug("eventBus: "+eventBus.toString());
        //EventBus for notification
        this.eventBus = eventBus;
        log.debug("contentConnector: "+contentConnector.toString());
        //ContentConnector of the service
        this.contentConnector = contentConnector;
        log.debug("i18n: "+i18n.toString());
        //Translator
        this.i18n = i18n;
        log.debug("Saliendo en LanguageEdit Constructor");
        //Validator
        this.validator = validator;

    }

	/**
	 * Execute mode
	 */
    @Override
    public void execute() throws ActionExecutionException {

    	log.debug("Executing CountryEditAction");
    	
    	//Get parentItem
        Object parentId = contentConnector.getItemId(parentItem);
        //Enable validation
        validator.showValidation(true);
        //Validates
        if (validator.isValid()) {
        	//If validation is correct check if contentConnector supports creation of elements
        	if (contentConnector instanceof SupportsCreation) {
        		//Get item
	            final Object itemId = ((SupportsCreation)contentConnector).getNewItemId(parentId, getDefinition().getNodeType());
	            //Get dialog
	            final String dialogName = getDefinition().getDialogName();
	            if(StringUtils.isBlank(dialogName)){
	            	//IF dialog doesn't exists send notification
	                uiContext.openNotification(MessageStyleTypeEnum.ERROR, false, i18n.translate("ui-framework.actions.no.dialog.definition", getDefinition().getName()));
	                return;

	            }
	            //Get presenter
	            final FormDialogPresenter formDialogPresenter = formDialogPresenterFactory.createFormDialogPresenter(dialogName);
	
	            if(formDialogPresenter == null){
	            	//If presenter doesn't exists send notification
	                uiContext.openNotification(MessageStyleTypeEnum.ERROR, false, i18n.translate("ui-framework.actions.dialog.not.registered", dialogName));
	                return;
	            }
	            
	            //Start presenter
	            formDialogPresenter.start(contentConnector.getItem(itemId), getDefinition().getDialogName(), uiContext, new EditorCallback() {
	
	                @Override
	                public void onSuccess(String actionName) {
	                	//If process go well send event to refresh table
	                    eventBus.fireEvent(new ContentChangedEvent(itemId, true));
	                    formDialogPresenter.closeDialog();
	                }
	
	                @Override
	                public void onCancel() {
	                	//Close dialog
	                    formDialogPresenter.closeDialog();
	                }
	            });
	        }
        } else {
        	//Not supported creation
            log.info("Validation error(s) occurred. No save performed.");
        }
        log.debug("Fin Executing LanguageEditAction");
    }
}