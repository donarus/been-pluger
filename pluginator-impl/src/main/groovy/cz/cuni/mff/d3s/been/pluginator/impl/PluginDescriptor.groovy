package cz.cuni.mff.d3s.been.pluginator.impl

import java.nio.file.Path

class PluginDescriptor {

    Path pluginPath

    String name

    String description

    String groupId

    String artifactId

    String version

    String activator

    List<DependencyDescriptor> dependencies

    List<DependencyDescriptor> pluginDependencies

    static PluginDescriptor create(Map pluginDescriptor, Path pluginPath) {
        new PluginDescriptor(
                pluginPath: pluginPath,
                name: pluginDescriptor.remove('name'),
                description: pluginDescriptor.remove('description'),
                groupId: pluginDescriptor.remove('groupId'),
                artifactId: pluginDescriptor.remove('artifactId'),
                version: pluginDescriptor.remove('version'),
                activator: pluginDescriptor.remove('activator'),
                dependencies: pluginDescriptor.remove('dependencies').collect {
                    DependencyDescriptor.create(it)
                },
                pluginDependencies: pluginDescriptor.remove('pluginDependencies').collect {
                    DependencyDescriptor.create(it)
                }
        )
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        PluginDescriptor that = (PluginDescriptor) o

        if (activator != that.activator) return false
        if (artifactId != that.artifactId) return false
        if (dependencies != that.dependencies) return false
        if (description != that.description) return false
        if (groupId != that.groupId) return false
        if (name != that.name) return false
        if (pluginDependencies != that.pluginDependencies) return false
        if (pluginPath != that.pluginPath) return false
        if (version != that.version) return false

        return true
    }

    int hashCode() {
        int result
        result = (pluginPath != null ? pluginPath.hashCode() : 0)
        result = 31 * result + (name != null ? name.hashCode() : 0)
        result = 31 * result + (description != null ? description.hashCode() : 0)
        result = 31 * result + (groupId != null ? groupId.hashCode() : 0)
        result = 31 * result + (artifactId != null ? artifactId.hashCode() : 0)
        result = 31 * result + (version != null ? version.hashCode() : 0)
        result = 31 * result + (activator != null ? activator.hashCode() : 0)
        result = 31 * result + (dependencies != null ? dependencies.hashCode() : 0)
        result = 31 * result + (pluginDependencies != null ? pluginDependencies.hashCode() : 0)
        return result
    }

}
