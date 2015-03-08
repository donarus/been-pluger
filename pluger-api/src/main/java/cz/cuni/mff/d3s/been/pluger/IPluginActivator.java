package cz.cuni.mff.d3s.been.pluger;

public interface IPluginActivator {

    void activate(IServiceRegistrator registry);
    void initialize();
    void start();
    void notifyStarted();

}
