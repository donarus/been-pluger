package cz.cuni.mff.d3s.been.pluginator;

import java.util.Collection;

public interface IPluginStarter {

    void start(Collection<IPluginActivator> activators);

}
