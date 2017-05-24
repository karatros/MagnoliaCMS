package com.restcrudmanager.actions;

import info.magnolia.ui.api.action.ConfiguredActionDefinition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The definition for VoucherListPresenter.
 * @author isilanes
 */
public class OpenCreateActionDefinition extends ConfiguredActionDefinition {

	
	private String dialogName;
	private String nodeType;
	
    private final Logger log = LoggerFactory.getLogger(OpenCreateActionDefinition.class);

	/**
	 * Constructor
	 */
    public OpenCreateActionDefinition() {
    	log.debug("ENTERING ACTION CONSTRUCTOR");
        
    	setImplementationClass(OpenCreateDialogAction.class);
    	log.debug("EXITING ACTION CONSTRUCTOR");
     
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
     * Getter for nodeType
     * @return
     */
    public String getNodeType() {
    	log.debug("Getting nodeType: "+nodeType);
        return nodeType;
    }

    /**
     * Setter for dialog
     * @param dialogName
     */
    public void setDialogName(String dialogName) {
    	log.debug("Setting dialog: "+dialogName);
    	
        this.dialogName = dialogName;
    }

    /**
     * Setter for nodeType
     * @param nodeType
     */
    public void setNodeType(String nodeType) {
    	
    	log.debug("Setting nodeType: "+nodeType);
        this.nodeType = nodeType;
    }

}