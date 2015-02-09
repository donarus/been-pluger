package cz.cuni.mff.d3s.been.pluger.impl

import cz.cuni.mff.d3s.been.pluger.IPluginActivator
import cz.cuni.mff.d3s.been.pluger.IPluginInitializer

class PluginInitializer implements IPluginInitializer{

    @Override
    void initialize(Collection<IPluginActivator> activators) {
        activators.each {
            it.initialize()
        }
    }

}
