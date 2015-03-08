package cz.cuni.mff.d3s.been.pluger;

import java.util.Collection;

public interface IPluginStartedNotifier {

    void notifyStarted(Collection<IPluginActivator> activators);

}
