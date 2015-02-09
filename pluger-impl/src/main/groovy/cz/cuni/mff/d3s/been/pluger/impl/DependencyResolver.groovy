package cz.cuni.mff.d3s.been.pluger.impl

import cz.cuni.mff.d3s.been.pluger.IDependencyResolver

import java.nio.file.Path

class DependencyResolver implements IDependencyResolver {

    @Override
    Collection<Path> resolve(PlugerConfig config, Collection<PluginDescriptor> pluginDescriptors) {
        def finalDependencies = config.finalDependencies.collect {
            def (String groupId, String artifactId, String version) = it.split(":")

            new DependencyDescriptor(
                    groupId: groupId,
                    artifactId: artifactId,
                    version: version
            )
        }

        def pluginDependencies = pluginDescriptors*.dependencies.flatten()
        pluginDependencies += pluginDescriptors.collect {
            new DependencyDescriptor(
                    groupId: it.groupId,
                    artifactId: it.artifactId,
                    version: it.version
            )
        }

        def requiredDependencies = pluginDependencies.findAll { dependency ->
            def finalDependency = finalDependencies.find {
                dependency.groupId == it.groupId && dependency.artifactId == it.artifactId
            }
            finalDependency == null
        }

        findRequiredDependenciesPaths(config.unpackedLibsDirectory, requiredDependencies)
    }

    private Collection<Path> findRequiredDependenciesPaths(Path unpackedLibsDirectory, Collection<DependencyDescriptor> dependencyDescriptors) {
        dependencyDescriptors = dependencyDescriptors.groupBy {
            "${it.groupId}:${it.artifactId}"
        }.values().each {
            def first = it.sort { it.version }.last()
            it.clear()
            it.add(first)
        }.flatten()


        dependencyDescriptors.collect { dependency ->
            def relativeJarPath = dependency.groupId.split("\\.") + dependency.artifactId + dependency.version + "${dependency.artifactId}-${dependency.version}.jar"
            def jarPath = unpackedLibsDirectory
            relativeJarPath.each {
                jarPath = jarPath.resolve(it)
            }
            jarPath
        }.unique()
    }

}
