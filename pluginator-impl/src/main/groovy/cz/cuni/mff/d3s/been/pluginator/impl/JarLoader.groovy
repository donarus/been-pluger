package cz.cuni.mff.d3s.been.pluginator.impl

import cz.cuni.mff.d3s.been.pluginator.IJarLoader

import java.nio.file.Path

class JarLoader implements IJarLoader {

    @Override
    ClassLoader loadJars(Collection<Path> jars) {
        new URLClassLoader(jars*.toUri()*.toURL() as URL[])
    }

}
