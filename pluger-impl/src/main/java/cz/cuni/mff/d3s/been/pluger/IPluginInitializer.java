package cz.cuni.mff.d3s.been.pluger;

import java.util.Collection;

public interface IPluginInitializer {

    void initialize(Collection<IPluginActivator> activators);

}
