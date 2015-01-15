package cz.cuni.mff.d3s.been.pluginator.impl

import cz.cuni.mff.d3s.been.pluginator.IPluginActivator
import cz.cuni.mff.d3s.been.pluginator.IPluginInitializer

class PluginInitializer implements IPluginInitializer{

    @Override
    void initialize(Collection<IPluginActivator> activators) {
        activators.each {
            it.initialize()
        }
    }

}
