package cz.cuni.mff.d3s.been.pluginator.impl

import cz.cuni.mff.d3s.been.pluginator.DescriptorException

class DependencyDescriptor {

    String groupId

    String artifactId

    String version

    DependencyScope scope

    private DependencyDescriptor() {
        // should be instantiated only by create method
    }

    static DependencyDescriptor create(Map dependencyDescriptorMap) {
        def scopeStr = (dependencyDescriptorMap.remove('scope') as String)
        def scope = null
        if (scopeStr) {
            try {
                scope = DependencyScope.valueOf(scopeStr.toUpperCase())
            } catch (IllegalArgumentException e) {
                throw new DescriptorException("Cannot create dependency descriptor. Unknown scope: '${scopeStr}'.", e)
            }
        }


        def descriptor = new DependencyDescriptor(
                groupId: dependencyDescriptorMap.remove('groupId'),
                artifactId: dependencyDescriptorMap.remove('artifactId'),
                version: dependencyDescriptorMap.remove('version'),
                scope: scope
        )

        validate(descriptor)

        descriptor
    }

    private static validate(DependencyDescriptor descriptor) {
        def msgs = []
        if (!descriptor.groupId) {
            msgs << "missing 'groupId'"
        }
        if (!descriptor.artifactId) {
            msgs << "missing 'artifactId'"
        }
        if (!descriptor.version) {
            msgs << "missing 'version'"
        }
        if (!descriptor.scope) {
            msgs << "missing 'scope'"
        }
        if (msgs) {
            throw new DescriptorException("Dependency descriptor is not valid. Detected problems: ${msgs.join(", ")}.")
        }
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        DependencyDescriptor that = (DependencyDescriptor) o

        if (artifactId != that.artifactId) return false
        if (groupId != that.groupId) return false
        if (scope != that.scope) return false
        if (version != that.version) return false

        return true
    }

    int hashCode() {
        int result
        result = (groupId != null ? groupId.hashCode() : 0)
        result = 31 * result + (artifactId != null ? artifactId.hashCode() : 0)
        result = 31 * result + (version != null ? version.hashCode() : 0)
        result = 31 * result + (scope != null ? scope.hashCode() : 0)
        return result
    }

}