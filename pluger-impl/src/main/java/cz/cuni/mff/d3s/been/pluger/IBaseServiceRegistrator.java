package cz.cuni.mff.d3s.been.pluger;

import cz.cuni.mff.d3s.been.pluger.impl.PlugerConfig;

public interface IBaseServiceRegistrator {

    void register(PlugerConfig plugerConfig, IServiceRegistry registry, ClassLoader pluginClassLoader);

}
