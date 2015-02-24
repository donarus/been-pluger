package cz.cuni.mff.d3s.been.pluger.impl

import cz.cuni.mff.d3s.been.pluger.IPluginFilter
import cz.cuni.mff.d3s.been.pluger.IDependencyResolver
import cz.cuni.mff.d3s.been.pluger.IJarLoader
import cz.cuni.mff.d3s.been.pluger.IPluginActivatorLoader
import cz.cuni.mff.d3s.been.pluger.IPluginInitializer
import cz.cuni.mff.d3s.been.pluger.IPluginInjector
import cz.cuni.mff.d3s.been.pluger.IPluginLoader
import cz.cuni.mff.d3s.been.pluger.IServicePreregistrator
import cz.cuni.mff.d3s.been.pluger.IServiceRegistrator
import cz.cuni.mff.d3s.been.pluger.IServiceRegistry
import cz.cuni.mff.d3s.been.pluger.IPluginStarter
import cz.cuni.mff.d3s.been.pluger.IPluginUnpacker
import cz.cuni.mff.d3s.been.pluger.IPluginServiceActivator

import java.nio.file.Files
import java.nio.file.Path

class Pluger {

    public static final String WORKING_DIRECTORY_KEY = "working.directory"

    public static final String DEPENDENCIES_FINAL_KEY = "dependencies.final"

    private PlugerConfig plugerConfig

    private IServiceRegistry pluginRegistry

    private IPluginLoader pluginLoader

    private IPluginFilter pluginFilter

    private IPluginUnpacker pluginUnpacker

    private IDependencyResolver dependencyResolver

    private IJarLoader jarLoader

    private IPluginActivatorLoader pluginActivatorLoader

    private IPluginServiceActivator serviceRegistrator

    private IPluginInjector pluginInjector

    private IPluginInitializer pluginInitializer

    private IPluginStarter pluginStarter

    private List<IServicePreregistrator> servicePreregistrators = []

    private Pluger() {
        // use Pluger.create(config) instead !!!
    }

    public void start() {
        preRegisterServices(pluginRegistry)
        def allPlugins = pluginLoader.loadPlugins(plugerConfig)
        def allowedPlugins = pluginFilter.filter(plugerConfig, allPlugins)
        pluginUnpacker.unpack(plugerConfig, allowedPlugins)
        def resolvedDependencies = dependencyResolver.resolve(plugerConfig, allowedPlugins)
        def pluginClassloader = jarLoader.loadJars(resolvedDependencies)
        def pluginActivators = pluginActivatorLoader.loadActivators(allowedPlugins, pluginClassloader)
        serviceRegistrator.activateServices(pluginRegistry, pluginActivators)
        pluginInjector.injectServices(pluginRegistry)
        pluginInitializer.initialize(pluginActivators)
        pluginStarter.start(pluginActivators)
    }

    void preRegisterServices(IServiceRegistry registry) {
        servicePreregistrators.each {
            it.registerService(registry)
        }
    }

    public void addServicePreregistrator(IServicePreregistrator preregistrator) {
        servicePreregistrators << preregistrator
    }

    public static Pluger create(Map<String, Object> configuration) {
        Path workingDirectory = configuration.get(WORKING_DIRECTORY_KEY)
        Collection<String> finalDependencies = configuration.get(DEPENDENCIES_FINAL_KEY)

        def unpackedLibsDirectory = Files.createDirectories(workingDirectory.resolve('libs'))
        def pluginsDirectory = Files.createDirectories(workingDirectory.resolve('plugins'))
        def configDirectory = Files.createDirectories(workingDirectory.resolve('config'))
        def temporaryDirectory = Files.createDirectories(workingDirectory.resolve('tmp'))

        def disabledPluginsConfigFile = configDirectory.resolve('disabled-plugins.conf')
        def disabledPlugins = Files.isReadable(disabledPluginsConfigFile) ? Files.readAllLines(disabledPluginsConfigFile) : []


        new Pluger(
                plugerConfig: new PlugerConfig(
                        workingDirectory: workingDirectory.toAbsolutePath(),
                        unpackedLibsDirectory: unpackedLibsDirectory,
                        pluginsDirectory: pluginsDirectory.toAbsolutePath(),
                        configDirectory: configDirectory.toAbsolutePath(),
                        temporaryDirectory: temporaryDirectory.toAbsolutePath(),
                        disabledPluginsConfigFile: disabledPluginsConfigFile.toAbsolutePath(),
                        disabledPlugins: disabledPlugins,
                        finalDependencies: finalDependencies
                ),
                pluginRegistry: new PlugerRegistry(),
                pluginLoader: new PluginLoader(),
                pluginFilter: new PluginFilter(),
                pluginUnpacker: new PluginUnpacker(),
                dependencyResolver: new DependencyResolver(),
                jarLoader: new JarLoader(),
                pluginActivatorLoader: new PluginActivatorLoader(),
                serviceRegistrator: new ServiceRegistrator(),
                pluginInjector: new PluginInjector(),
                pluginInitializer: new PluginInitializer(),
                pluginStarter: new PluginStarter()
        )
    }

}
