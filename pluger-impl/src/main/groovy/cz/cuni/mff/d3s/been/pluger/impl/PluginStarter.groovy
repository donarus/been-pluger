package cz.cuni.mff.d3s.been.pluger.impl

import cz.cuni.mff.d3s.been.pluger.IPluginActivator
import cz.cuni.mff.d3s.been.pluger.IPluginStarter

class PluginStarter implements IPluginStarter {

    @Override
    void start(Collection<IPluginActivator> activators) {
        activators*.start()
    }

}
