package cz.cuni.mff.d3s.been.pluger.impl

import cz.cuni.mff.d3s.been.pluger.IPluginActivator
import cz.cuni.mff.d3s.been.pluger.IServiceRegistry
import cz.cuni.mff.d3s.been.pluger.IPluginServiceActivator

class ServiceRegistrator implements IPluginServiceActivator {

    @Override
    void activateServices(IServiceRegistry pluginRegistry, Collection<IPluginActivator> activators) {
        activators*.activate(pluginRegistry)
    }

}
