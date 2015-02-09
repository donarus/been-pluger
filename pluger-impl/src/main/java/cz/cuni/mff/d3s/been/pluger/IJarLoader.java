package cz.cuni.mff.d3s.been.pluger;

import java.nio.file.Path;
import java.util.Collection;

public interface IJarLoader {

    ClassLoader loadJars(Collection<Path> jarPaths);

}
