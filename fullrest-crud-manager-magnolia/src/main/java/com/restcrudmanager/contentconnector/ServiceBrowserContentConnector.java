package com.restcrudmanager.contentconnector;

import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;

import com.vaadin.data.Container;

/**
 * A {@link ContentConnector} for the module voucher-browser which is cpipled with a {@link Container}.
 * @author isilanes
 */
public interface ServiceBrowserContentConnector extends ContentConnector {

    Container getContainer();

}
