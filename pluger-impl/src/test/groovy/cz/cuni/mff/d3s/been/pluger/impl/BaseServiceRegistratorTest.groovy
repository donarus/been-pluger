package cz.cuni.mff.d3s.been.pluger.impl

import cz.cuni.mff.d3s.been.pluger.PlugerServiceConstants
import spock.lang.Specification

import java.nio.file.Paths

/**
 * Created by donarus on 3.3.15.
 */
class BaseServiceRegistratorTest extends Specification {

    def 'test base service registrator registers all base services correctly'() {
        given:
            def baseServiceRegistrator = new BaseServiceRegistrator()
            def pluginsWorkingDirectory = Paths.get("plugins","working", "dir")
            def temporaryDirectory = Paths.get("temporary", "dir")
            def programArgs = ["arg1", "arg2"].toArray()
            def plugerConfig = new PlugerConfig(
                    pluginsWorkingDirectory: pluginsWorkingDirectory,
                    temporaryDirectory: temporaryDirectory,
                    programArgs: programArgs
            )
            def serviceRegistry = Mock(ServiceRegistry)
            def pluginsClassLoader = Mock(ClassLoader)

        when:
            baseServiceRegistrator.register(plugerConfig, serviceRegistry, pluginsClassLoader)

        then:
            1 * serviceRegistry.registerService(PlugerServiceConstants.PLUGIN_CLASSLOADER, ClassLoader, pluginsClassLoader)
            1 * serviceRegistry.registerService(PlugerServiceConstants.PLUGINS_WORKING_DIRECTORY, File, pluginsWorkingDirectory.toFile())
            1 * serviceRegistry.registerService(PlugerServiceConstants.TMP_DIRECTORY, File, temporaryDirectory.toFile())
            1 * serviceRegistry.registerService(PlugerServiceConstants.PROGRAM_ARGUMENTS, String[], programArgs)
    }
}
