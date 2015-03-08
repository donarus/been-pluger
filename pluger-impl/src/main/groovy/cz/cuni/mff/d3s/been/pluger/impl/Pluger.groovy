package cz.cuni.mff.d3s.been.pluger.impl

import cz.cuni.mff.d3s.been.pluger.*

import java.nio.file.Files
import java.nio.file.Path

class Pluger {

    // FIXME add logging
    // FIXME add exception handling
    // FIXME add documentation + examples

    public static final String WORKING_DIRECTORY_KEY = "working.directory"

    public static final String DEPENDENCIES_FINAL_KEY = "dependencies.final"

    public static final String CLEAR_LIB_DIR_KEY = "clear.libs"

    public static final String PROGRAM_ARGS = "program.args"

    private PlugerConfig plugerConfig

    private IServiceRegistry serviceRegistry

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

    private IPluginStartedNotifier pluginStartedNotifier

    private IBaseServiceRegistrator baseServiceRegistrator

    private List<IServicePreregistrator> servicePreregistrators = []

    private Pluger() {
        // use Pluger.create(config) instead !!!
    }

    public void start() {
        def allPlugins = pluginLoader.loadPlugins(plugerConfig)
        def allowedPlugins = pluginFilter.filter(plugerConfig, allPlugins)
        pluginUnpacker.unpack(plugerConfig, allowedPlugins)
        def resolvedDependencies = dependencyResolver.resolve(plugerConfig, allowedPlugins)
        def pluginClassloader = jarLoader.loadJars(resolvedDependencies)
        baseServiceRegistrator.register(plugerConfig, serviceRegistry, pluginClassloader)
        preRegisterServices(serviceRegistry) // FIXME move to separate interface/class + unit test
        def pluginActivators = pluginActivatorLoader.loadActivators(allowedPlugins, pluginClassloader)
        serviceRegistrator.activateServices(serviceRegistry, pluginActivators)
        pluginInjector.injectServices(serviceRegistry)
        pluginInitializer.initialize(pluginActivators)
        pluginStarter.start(pluginActivators)
        pluginStartedNotifier.notifyStarted(pluginActivators)
    }

    private void preRegisterServices(IServiceRegistry registry) {
        // FIXME move to separate interface/class + unit test
        servicePreregistrators.each {
            it.registerService(registry)
        }
    }

    public void addServicePreregistrator(IServicePreregistrator preregistrator) {
        servicePreregistrators << preregistrator
    }

    public static Pluger create(Map<String, Object> configuration) throws PlugerException{
        Path workingDirectory = configuration.get(WORKING_DIRECTORY_KEY)
        if(workingDirectory == null) {
            throw new PlugerException("Invalid configuration: '${WORKING_DIRECTORY_KEY}' must be configured")
        }

        Collection<String> finalDependencies = configuration.get(DEPENDENCIES_FINAL_KEY, [])
        boolean clearLibDir = configuration.get(CLEAR_LIB_DIR_KEY, false)
        String[] programArgs = configuration.get(PROGRAM_ARGS, [])

        // FIXME move creating and directory creating to separate class
        def unpackedLibsDirectory = workingDirectory.resolve('libs')
        if (clearLibDir) {
            unpackedLibsDirectory.deleteDir()
        }
        Files.createDirectories(unpackedLibsDirectory)

        def temporaryDirectory = workingDirectory.resolve('tmp')
        temporaryDirectory.deleteDir()
        Files.createDirectories(temporaryDirectory)

        def pluginsWorkingDirectory = Files.createDirectories(workingDirectory.resolve('plugins-wrk'))

        def pluginsDirectory = Files.createDirectories(workingDirectory.resolve('plugins'))

        def configDirectory = Files.createDirectories(workingDirectory.resolve('config'))

        def disabledPluginsConfigFile = configDirectory.resolve('disabled-plugins.conf')

        def disabledPlugins = Files.isReadable(disabledPluginsConfigFile) ? Files.readAllLines(disabledPluginsConfigFile) : []

        new Pluger(
                plugerConfig: new PlugerConfig(
                        workingDirectory: workingDirectory.toAbsolutePath(),
                        pluginsWorkingDirectory: pluginsWorkingDirectory.toAbsolutePath(),
                        unpackedLibsDirectory: unpackedLibsDirectory,
                        pluginsDirectory: pluginsDirectory.toAbsolutePath(),
                        configDirectory: configDirectory.toAbsolutePath(),
                        temporaryDirectory: temporaryDirectory.toAbsolutePath(),
                        disabledPluginsConfigFile: disabledPluginsConfigFile.toAbsolutePath(),
                        disabledPlugins: disabledPlugins,
                        finalDependencies: finalDependencies,
                        programArgs: programArgs
                ),
                serviceRegistry: new ServiceRegistry(),
                pluginLoader: new PluginLoader(),
                pluginFilter: new PluginFilter(),
                pluginUnpacker: new PluginUnpacker(),
                dependencyResolver: new DependencyResolver(),
                jarLoader: new JarLoader(),
                pluginActivatorLoader: new PluginActivatorLoader(),
                serviceRegistrator: new ServiceActivator(),
                pluginInjector: new PluginInjector(),
                pluginInitializer: new PluginInitializer(),
                pluginStarter: new PluginStarter(),
                pluginStartedNotifier: new PluginStartedNotifier(),
                baseServiceRegistrator: new BaseServiceRegistrator()
        )
    }

}
