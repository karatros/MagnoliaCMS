package com.tui.restcrudmanager.setup;

import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.BootstrapSingleResource;
import info.magnolia.module.delta.Delta;
import info.magnolia.module.delta.DeltaBuilder;
import info.magnolia.module.delta.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is optional and lets you manager the versions of your module, by
 * registering "deltas" to maintain the module's configuration, or other type of
 * content. If you don't need this, simply remove the reference to this class in
 * the module descriptor xml.
 */
public class RestCRUDManagerVersionHandler extends DefaultModuleVersionHandler {

    public RestCRUDManagerVersionHandler() {
        super();
        
        register(addTasksVersion100());
        
    }

    private Delta addTasksVersion100() {
    	return DeltaBuilder.update("1.0.0", "")
        		.addTask(new BootstrapSingleResource("Bootstrap01", "Install app base", "/mgnl-bootstrap/restcrudmanager/config.modules.restcrudmanager.apps.xml"))
				.addTask(new BootstrapSingleResource("Bootstrap02", "Install dialogs base", "/mgnl-bootstrap/restcrudmanager/config.modules.restcrudmanager.dialogs.xml"))
				.addTask(new BootstrapSingleResource("Bootstrap03", "Install admin-central configuration", "/mgnl-bootstrap/restcrudmanager/config.modules.ui-admincentral.config.appLauncherLayout.groups.edit.apps.templateApp.xml"))
				.addTask(new BootstrapSingleResource("Bootstrap04", "Install restSelectField fieldtype config", "/mgnl-bootstrap/restcrudmanager/config.modules.ui-framework.fieldTypes.restSelectField.xml"));

    }
    
    @Override
    protected List<Task> getExtraInstallTasks(InstallContext installContext) {
        final List<Task> tasks = new ArrayList<>();

        tasks.addAll(super.getExtraInstallTasks(installContext));

        return tasks;

    }
}
