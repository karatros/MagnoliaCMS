package com.restcrudmanager.workbench;

import info.magnolia.ui.workbench.list.ListPresenterDefinition;

/**
 * The definition for VoucherListPresenter.
 * @author isilanes
 */
public class RestCrudManagerListPresenterDefinition extends ListPresenterDefinition {

    public RestCrudManagerListPresenterDefinition() {
        setImplementationClass(RestCrudManagerListPresenter.class);
    }

}