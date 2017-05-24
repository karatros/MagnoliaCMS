package com.restcrudmanager.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.ui.api.action.ConfiguredActionDefinition;

/**
 * The definition for VoucherListPresenter.
 * @author isilanes
 */
public class OpenDeleteActionDefinition extends ConfiguredActionDefinition {

	
	private String dialogName;
	private String nodeType;
	
    private final Logger log = LoggerFactory.getLogger(OpenDeleteActionDefinition.class);

	/**
	 * Constructor
	 */
    public OpenDeleteActionDefinition() {
    	log.debug("ENTERING DELETION CONSTRUCTOR");
    	
        setImplementationClass(OpenDeleteDialogAction.class);
    	log.debug("EXITING DELETION CONSTRUCTOR");
     
    }
    /**
     * Getter for dialog
     * @return
     */
    public String getDialogName() {
    	log.debug("Getting dialog: "+dialogName);
    	
        return dialogName;
    }
    /**
     * setter for dialog
     * @param dialogName
     */
    public void setDialogName(String dialogName) {
    	
    	log.debug("Setting dialog: "+dialogName);
        this.dialogName = dialogName;
    }

    /**
     * getter for nodeType
     * @return
     */
    public String getNodeType() {
    	log.debug("Getting nodeType: "+nodeType);
        return nodeType;
        
    }
    /**
     * setter for nodeType
     * @param nodeType
     */
    public void setNodeType(String nodeType) {
    	log.debug("Setting nodeType: "+nodeType);
        this.nodeType = nodeType;
    }

}