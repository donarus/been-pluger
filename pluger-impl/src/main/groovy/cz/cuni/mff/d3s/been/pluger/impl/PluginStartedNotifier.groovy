package cz.cuni.mff.d3s.been.pluger.impl

import cz.cuni.mff.d3s.been.pluger.IPluginActivator
import cz.cuni.mff.d3s.been.pluger.IPluginStartedNotifier

/**
 * Created by donarus on 2.3.15.
 */
class PluginStartedNotifier implements IPluginStartedNotifier {

    @Override
    void notifyStarted(Collection<IPluginActivator> activators) {
        activators*.notifyStarted()
    }

}
