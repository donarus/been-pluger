package cz.cuni.mff.d3s.been.pluger.impl

import cz.cuni.mff.d3s.been.pluger.IBaseServiceRegistrator
import cz.cuni.mff.d3s.been.pluger.IPluginConfigurer
import cz.cuni.mff.d3s.been.pluger.IPluginFilter
import cz.cuni.mff.d3s.been.pluger.IDependencyResolver
import cz.cuni.mff.d3s.been.pluger.IPluginActivator
import cz.cuni.mff.d3s.been.pluger.IPluginActivatorLoader
import cz.cuni.mff.d3s.been.pluger.IPluginInitializer
import cz.cuni.mff.d3s.been.pluger.IPluginInjector
import cz.cuni.mff.d3s.been.pluger.IPluginLoader
import cz.cuni.mff.d3s.been.pluger.IPluginStartedNotifier
import cz.cuni.mff.d3s.been.pluger.IServicePreregistrator
import cz.cuni.mff.d3s.been.pluger.IServiceRegistrator
import cz.cuni.mff.d3s.been.pluger.IServiceRegistry
import cz.cuni.mff.d3s.been.pluger.IPluginStarter
import cz.cuni.mff.d3s.been.pluger.IPluginUnpacker
import cz.cuni.mff.d3s.been.pluger.IPluginServiceActivator
import cz.cuni.mff.d3s.been.pluger.PlugerException
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

class PlugerTest extends Specification {

    @Rule
    private TemporaryFolder tmpFolder

    def 'creating pluger fails when working dir not set'() {
        given:
            def config = [:]
            def pluginsConfiguration = [:]

        when:
            Pluger.create(config, pluginsConfiguration)

        then:
            PlugerException ex = thrown()
            assert ex.message == "Invalid configuration: 'working.directory' must be configured"
    }

    def 'pluger defaults config contains expected values'() {
        given:
            def workingDir = tmpFolder.newFolder().toPath()
            def config = [
                    (Pluger.WORKING_DIRECTORY_KEY): workingDir
            ]
            def pluginsConfiguration = [:]

        when:
            def pluger = Pluger.create(config, pluginsConfiguration)

        then:
            assert pluger.plugerConfig.workingDirectory == workingDir
            assert pluger.plugerConfig.finalDependencies == []
    }

    def 'create pluger'() {
        given:
            def workingDir = tmpFolder.newFolder().toPath()
            def config = [
                    (Pluger.WORKING_DIRECTORY_KEY): workingDir
            ]
            def pluginsConfiguration = ["plugin": "configuration"]

        when:
            def pluger = Pluger.create(config, pluginsConfiguration)

        then:
            assert pluger instanceof Pluger
            assert pluger.plugerConfig.workingDirectory == workingDir
            assert pluger.plugerConfig.configDirectory == workingDir.resolve('config')
            assert Files.isDirectory(pluger.plugerConfig.configDirectory)
            assert pluger.plugerConfig.pluginsWorkingDirectory == workingDir.resolve('plugins-wrk')
            assert Files.isDirectory(pluger.plugerConfig.pluginsWorkingDirectory)
            assert pluger.plugerConfig.pluginsDirectory == workingDir.resolve('plugins')
            assert Files.isDirectory(pluger.plugerConfig.pluginsDirectory)
            assert pluger.plugerConfig.temporaryDirectory == workingDir.resolve('tmp')
            assert Files.isDirectory(pluger.plugerConfig.temporaryDirectory)
            assert pluger.plugerConfig.unpackedLibsDirectory == workingDir.resolve('libs')
            assert Files.isDirectory(pluger.plugerConfig.unpackedLibsDirectory)
            assert pluger.plugerConfig.disabledPluginsConfigFile == workingDir.resolve('config').resolve('disabled-plugins.conf')
            assert !Files.exists(pluger.plugerConfig.disabledPluginsConfigFile)
            assert pluger.plugerConfig.disabledPlugins == []

            assert pluger.pluginsConfiguration == pluginsConfiguration
            assert pluger.servicePreregistrators == []
            assert pluger.baseServiceRegistrator instanceof BaseServiceRegistrator
            assert pluger.serviceRegistry instanceof ServiceRegistry
            assert pluger.pluginLoader instanceof PluginLoader
            assert pluger.pluginFilter instanceof PluginFilter
            assert pluger.pluginUnpacker instanceof PluginUnpacker
            assert pluger.dependencyResolver instanceof DependencyResolver
            assert pluger.jarLoader instanceof JarLoader
            assert pluger.pluginActivatorLoader instanceof PluginActivatorLoader
            assert pluger.serviceRegistrator instanceof ServiceActivator
            assert pluger.pluginInjector instanceof PluginInjector
            assert pluger.pluginInitializer instanceof PluginInitializer
            assert pluger.pluginStarter instanceof PluginStarter
            assert pluger.pluginStartedNotifier instanceof PluginStartedNotifier
    }

    def 'content of tmp dir is deleted on each startup'() {
        given:
            def workingDir = tmpFolder.newFolder().toPath()
            def config = [
                    (Pluger.WORKING_DIRECTORY_KEY) : workingDir,
                    (Pluger.DEPENDENCIES_FINAL_KEY): []
            ]
            def pluginsConfiguration = [:]
            def tmpDirectory = workingDir.resolve("tmp")
            Files.createDirectories(tmpDirectory)
            def testFile = tmpDirectory.resolve("test.jar")

            testFile.write("LOREM IPSUM DOLOR SIT AMET")

        when:
            Pluger.create(config, pluginsConfiguration)

        then:
            assert testFile.toFile().exists() == false
    }

    def 'content of libs dir is deleted when this is specified in configuration'() {
        given:
            def workingDir = tmpFolder.newFolder().toPath()
            def config = [
                    (Pluger.WORKING_DIRECTORY_KEY) : workingDir,
                    (Pluger.DEPENDENCIES_FINAL_KEY): [],
                    (Pluger.CLEAR_LIB_DIR_KEY)     : shouldBeDeleted
            ]
            def pluginsConfiguration = [:]
            def libsDir = workingDir.resolve("libs")
            Files.createDirectories(libsDir)
            def testFile = libsDir.resolve("test.jar")

            testFile.write("LOREM IPSUM DOLOR SIT AMET")

        when:
            Pluger.create(config, pluginsConfiguration)

        then:
            assert testFile.toFile().exists() == exists

        where:
            shouldBeDeleted | exists
            true            | false
            false           | true
    }


    def 'phases are ordered correctly'() {
        given:
            def pluginsConfiguration = ["fake": "plugin configuration"]
            def config = Mock(PlugerConfig)
            def baseServiceRegistrator = Mock(IBaseServiceRegistrator)
            def serviceRegistry = Mock(IServiceRegistry)
            def pluginLoader = Mock(IPluginLoader)
            def pluginFilter = Mock(IPluginFilter)
            def pluginUnpacker = Mock(IPluginUnpacker)
            def dependencyResolver = Mock(IDependencyResolver)
            def jarLoader = Mock(JarLoader)
            def pluginActivatorLoader = Mock(IPluginActivatorLoader)
            def pluginConfigurer = Mock(IPluginConfigurer)
            def serviceRegistrator = Mock(IPluginServiceActivator)
            def pluginInjector = Mock(IPluginInjector)
            def pluginInitializer = Mock(IPluginInitializer)
            def pluginStarter = Mock(IPluginStarter)
            def pluginStartedNotifier = Mock(IPluginStartedNotifier)

            def pluger = new Pluger(
                    pluginsConfiguration: pluginsConfiguration,
                    plugerConfig: config,
                    baseServiceRegistrator: baseServiceRegistrator,
                    serviceRegistry: serviceRegistry,
                    pluginLoader: pluginLoader,
                    pluginFilter: pluginFilter,
                    pluginUnpacker: pluginUnpacker,
                    dependencyResolver: dependencyResolver,
                    jarLoader: jarLoader,
                    pluginActivatorLoader: pluginActivatorLoader,
                    serviceRegistrator: serviceRegistrator,
                    pluginConfigurer: pluginConfigurer,
                    pluginInjector: pluginInjector,
                    pluginInitializer: pluginInitializer,
                    pluginStarter: pluginStarter,
                    pluginStartedNotifier: pluginStartedNotifier
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
            1 * baseServiceRegistrator.register(config, serviceRegistry, jarLoaderClassLoader)

        then:
            1 * serviceRegistry.registerService(preregisteredService)

        then:
            1 * pluginActivatorLoader.loadActivators(selectedPlugins, jarLoaderClassLoader) >> activators

        then:
            1 * pluginConfigurer.configurePlugins(pluginsConfiguration, activators)

        then:
            1 * serviceRegistrator.activateServices(serviceRegistry, activators)

        then:
            1 * pluginInjector.injectServices(serviceRegistry)

        then:
            1 * pluginInitializer.initialize(activators)

        then:
            1 * pluginStarter.start(activators)

        then:
            1 * pluginStartedNotifier.notifyStarted(activators)

    }

}
