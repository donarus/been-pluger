package cz.cuni.mff.d3s.been.pluginator;

import java.util.Collection;

public interface IPluginServiceActivator {

    void activateServices(IServiceRegistry pluginRegistry, Collection<IPluginActivator> activators);

}
