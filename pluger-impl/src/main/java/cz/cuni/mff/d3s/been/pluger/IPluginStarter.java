package cz.cuni.mff.d3s.been.pluger;

import java.util.Collection;

public interface IPluginStarter {

    void start(Collection<IPluginActivator> activators);

}
