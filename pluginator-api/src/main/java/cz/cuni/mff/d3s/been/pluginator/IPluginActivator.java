package cz.cuni.mff.d3s.been.pluginator;

public interface IPluginActivator {

    void activate(IServiceRegistrator registry);
    void initialize();
    void start();

}
