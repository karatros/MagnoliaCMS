package com.restcrudmanager.actions;

import info.magnolia.ui.api.action.ConfiguredActionDefinition;

/**
 * Definition of {@link SaveFileAction}.
 * @author isilanes
 */
public class AddActionDefinition extends ConfiguredActionDefinition {

    public AddActionDefinition() {
        setImplementationClass(AddAction.class);
    }
}
