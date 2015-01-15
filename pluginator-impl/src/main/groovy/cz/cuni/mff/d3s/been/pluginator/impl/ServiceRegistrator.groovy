package cz.cuni.mff.d3s.been.pluginator.impl

import cz.cuni.mff.d3s.been.pluginator.IPluginActivator
import cz.cuni.mff.d3s.been.pluginator.IServiceRegistry
import cz.cuni.mff.d3s.been.pluginator.IPluginServiceActivator

class ServiceRegistrator implements IPluginServiceActivator {

    @Override
    void activateServices(IServiceRegistry pluginRegistry, Collection<IPluginActivator> activators) {
        activators*.activate(pluginRegistry)
    }

}
