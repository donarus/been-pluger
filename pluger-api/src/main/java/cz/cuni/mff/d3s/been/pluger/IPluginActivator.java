package cz.cuni.mff.d3s.been.pluger;

import java.util.Map;

public interface IPluginActivator {
    void configure(Map<String, String> configuration);
    void activate(IServiceRegistrator registry);
    void initialize();
    void start();
    void notifyStarted();
}
