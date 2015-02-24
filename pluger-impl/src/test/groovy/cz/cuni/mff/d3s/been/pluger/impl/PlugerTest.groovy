package cz.cuni.mff.d3s.been.pluger.impl

import cz.cuni.mff.d3s.been.pluger.IPluginFilter
import cz.cuni.mff.d3s.been.pluger.IDependencyResolver
import cz.cuni.mff.d3s.been.pluger.IPluginActivator
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
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

class PlugerTest extends Specification {


    @Rule
    private TemporaryFolder tmpFolder

    def 'test create pluger'() {
        given:
            def workingDir = tmpFolder.newFolder().toPath()
            def config = [
                    (Pluger.WORKING_DIRECTORY_KEY) : workingDir,
                    (Pluger.DEPENDENCIES_FINAL_KEY): []
            ]

        when:
            def pluger = Pluger.create(config)

        then:
            assert pluger instanceof Pluger
            assert pluger.plugerConfig.workingDirectory == workingDir
            assert pluger.plugerConfig.configDirectory == workingDir.resolve('config')
            assert Files.isDirectory(pluger.@plugerConfig.configDirectory)
            assert pluger.plugerConfig.pluginsDirectory == workingDir.resolve('plugins')
            assert Files.isDirectory(pluger.@plugerConfig.pluginsDirectory)
            assert pluger.plugerConfig.temporaryDirectory == workingDir.resolve('tmp')
            assert Files.isDirectory(pluger.@plugerConfig.temporaryDirectory)
            assert pluger.plugerConfig.disabledPluginsConfigFile == workingDir.resolve('config').resolve('disabled-plugins.conf')
            assert !Files.exists(pluger.@plugerConfig.disabledPluginsConfigFile)
            assert pluger.plugerConfig.disabledPlugins == []

            assert pluger.servicePreregistrators == []
            assert pluger.pluginRegistry instanceof PlugerRegistry
            assert pluger.pluginLoader instanceof PluginLoader
            assert pluger.pluginFilter instanceof PluginFilter
            assert pluger.pluginUnpacker instanceof PluginUnpacker
            assert pluger.dependencyResolver instanceof DependencyResolver
            assert pluger.jarLoader instanceof JarLoader
            assert pluger.pluginActivatorLoader instanceof PluginActivatorLoader
            assert pluger.serviceRegistrator instanceof ServiceRegistrator
            assert pluger.pluginInjector instanceof PluginInjector
            assert pluger.pluginInitializer instanceof PluginInitializer
            assert pluger.pluginStarter instanceof PluginStarter
    }


    def 'phases are ordered correctly'() {
        given:
            def config = Mock(PlugerConfig)
            def pluginRegistry = Mock(IServiceRegistry)
            def pluginLoader = Mock(IPluginLoader)
            def pluginFilter = Mock(IPluginFilter)
            def pluginUnpacker = Mock(IPluginUnpacker)
            def dependencyResolver = Mock(IDependencyResolver)
            def jarLoader = Mock(JarLoader)
            def pluginActivatorLoader = Mock(IPluginActivatorLoader)
            def serviceRegistrator = Mock(IPluginServiceActivator)
            def pluginInjector = Mock(IPluginInjector)
            def pluginInitializer = Mock(IPluginInitializer)
            def pluginStarter = Mock(IPluginStarter)

            def pluger = new Pluger(
                    plugerConfig: config,
                    pluginRegistry: pluginRegistry,
                    pluginLoader: pluginLoader,
                    pluginFilter: pluginFilter,
                    pluginUnpacker: pluginUnpacker,
                    dependencyResolver: dependencyResolver,
                    jarLoader: jarLoader,
                    pluginActivatorLoader: pluginActivatorLoader,
                    serviceRegistrator: serviceRegistrator,
                    pluginInjector: pluginInjector,
                    pluginInitializer: pluginInitializer,
                    pluginStarter: pluginStarter
            )

            def allPlugins = [
                    new PluginDescriptor(name: 'descriptor1'),
                    new PluginDescriptor(name: 'descriptor2'),
                    new PluginDescriptor(name: 'descriptor3')
            ]

            def selectedPlugins = [
                    new PluginDescriptor(name: 'descriptor1'),
                    new PluginDescriptor(name: 'descriptor3')
            ]

            def resolvedDependencies = [
                    Paths.get('resolvedDependency1'),
                    Paths.get('resolvedDependency2')
            ]

            def jarLoaderClassLoader = Mock(ClassLoader)

            def activators = [
                    Mock(IPluginActivator)
            ]

            def preregisteredService = Mock(Object)
            pluger.addServicePreregistrator(new IServicePreregistrator() {
                @Override
                void registerService(IServiceRegistrator registry) {
                    registry.registerService(preregisteredService)
                }
            })

        when:
            pluger.start()

        then:
            1 * pluginRegistry.registerService(preregisteredService)

        then:
            1 * pluginLoader.loadPlugins(config) >> allPlugins

        then:
            1 * pluginFilter.filter(config, allPlugins) >> selectedPlugins

        then:
            1 * pluginUnpacker.unpack(config, selectedPlugins)

        then:
            1 * dependencyResolver.resolve(config, selectedPlugins) >> resolvedDependencies

        then:
            1 * jarLoader.loadJars(resolvedDependencies) >> jarLoaderClassLoader

        then:
            1 * pluginActivatorLoader.loadActivators(selectedPlugins, jarLoaderClassLoader) >> activators

        then:
            1 * serviceRegistrator.activateServices(pluginRegistry, activators)

        then:
            1 * pluginInjector.injectServices(pluginRegistry)

        then:
            1 * pluginInitializer.initialize(activators)

        then:
            1 * pluginStarter.start(activators)
    }

}
