package com.restcrudmanager.workbench;

import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;
import info.magnolia.ui.workbench.list.ListPresenter;
import info.magnolia.ui.workbench.list.ListView;

import javax.inject.Inject;

import com.restcrudmanager.contentconnector.ServiceBrowserContentConnector;
import com.vaadin.data.Container;

/**
 * A {@link ListPresenter} to host VoucherItems.
 * @author isilanes
 */
public class RestCrudManagerListPresenter extends ListPresenter {

	/**
	 * Constructor
	 * @param view
	 * @param componentProvider
	 * @param contentConnector
	 */
    @Inject
    public RestCrudManagerListPresenter(ListView view, ComponentProvider componentProvider, ContentConnector contentConnector) {
        super(view, componentProvider);
        this.contentConnector = contentConnector;
    }

    /**
     * Initialize container
     */
    @Override
    protected Container initializeContainer() {
        return ((ServiceBrowserContentConnector)contentConnector).getContainer();
    }
}