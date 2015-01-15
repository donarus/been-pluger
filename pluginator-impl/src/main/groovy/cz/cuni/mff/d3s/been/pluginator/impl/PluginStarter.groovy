package cz.cuni.mff.d3s.been.pluginator.impl

import cz.cuni.mff.d3s.been.pluginator.IPluginActivator
import cz.cuni.mff.d3s.been.pluginator.IPluginStarter

class PluginStarter implements IPluginStarter {

    @Override
    void start(Collection<IPluginActivator> activators) {
        activators*.start()
    }

}
