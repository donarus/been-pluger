package cz.cuni.mff.d3s.been.pluger.impl

import cz.cuni.mff.d3s.been.pluger.IJarLoader

import java.nio.file.Path

class JarLoader implements IJarLoader {

    @Override
    ClassLoader loadJars(Collection<Path> jars) {
        new URLClassLoader(jars*.toUri()*.toURL() as URL[])
    }

}
