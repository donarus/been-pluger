package cz.cuni.mff.d3s.been.pluger.mavenplugin;

import cz.cuni.mff.d3s.been.pluger.impl.DependencyDescriptor;
import cz.cuni.mff.d3s.been.pluger.impl.DependencyScope;
import cz.cuni.mff.d3s.been.pluger.impl.PluginDescriptor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.resolution.*;
import org.eclipse.aether.util.artifact.JavaScopes;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Mojo(name = "pluginate", threadSafe = true)
public class PlugerMojo extends AbstractMojo {

    @Component
    private RepositorySystem repoSystem;

    @Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
    private RepositorySystemSession repoSession;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(required = true)
    private String activator;

    @Parameter(defaultValue = "${project.build.directory}", property = "pluger.plugin.output.dir")
    private String projectBuildDir;

    public void execute() throws MojoExecutionException, MojoFailureException {

        String artifactCoords = String.format("%s:%s:%s", project.getGroupId(), project.getArtifactId(), project.getVersion());
        Artifact resolvedPluginArtifact = resolveArtifact(new DefaultArtifact(artifactCoords));

        Map<Artifact, DependencyDescriptor> dependencies = collectDependencies(resolvedPluginArtifact);

        PluginDescriptor descriptor = new PluginDescriptor();
        descriptor.setName(project.getName());
        descriptor.setDescription(project.getDescription());
        descriptor.setGroupId(project.getGroupId());
        descriptor.setArtifactId(project.getArtifactId());
        descriptor.setVersion(project.getVersion());
        descriptor.setActivator(activator);
        descriptor.setDependencies(new ArrayList<>(dependencies.values()));

        String jsonDescriptor = descriptor.createJsonDescriptor();

        String pluginFileName = String.format("%s-%s.plugin", project.getArtifactId(), project.getVersion());
        File pluginDestDir = new File(projectBuildDir);
        if(!pluginDestDir.exists()) {
            pluginDestDir.mkdirs();
        }
        File pluginDestFile = new File(pluginDestDir, pluginFileName);
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(pluginDestFile))) {


            ZipEntry pluginJarEntry = new ZipEntry(String.format("%s-%s.jar", project.getArtifactId(), project.getVersion()));
            zip.putNextEntry(pluginJarEntry);
            writeToOutputStream(resolvedPluginArtifact.getFile(), zip);
            zip.closeEntry();

            ZipEntry descriptorEntry = new ZipEntry("plugin-descriptor.json");
            zip.putNextEntry(descriptorEntry);
            writeToOutputStream(jsonDescriptor, zip);
            zip.closeEntry();

            ZipEntry libEntry = new ZipEntry("lib/");
            zip.putNextEntry(libEntry);
            zip.closeEntry();
            
            List<String> existingPaths = new ArrayList<>();
            for (Artifact artifact : dependencies.keySet()) {
                StringBuilder partialPath = new StringBuilder("lib/");
                for (String part : artifact.getGroupId().split("\\.")) {
                    partialPath.append(part + "/");
                    String partialPathStr = partialPath.toString();
                    if(!existingPaths.contains(partialPathStr)) {
                        zip.putNextEntry(new ZipEntry(partialPathStr));
                        zip.closeEntry();
                        existingPaths.add(partialPathStr);
                    }
                }

                partialPath.append(artifact.getArtifactId() + "/");
                String partialPathStr = partialPath.toString();
                if(!existingPaths.contains(partialPathStr)) {
                    zip.putNextEntry(new ZipEntry(partialPathStr));
                    zip.closeEntry();
                    existingPaths.add(partialPathStr);
                }

                partialPath.append(artifact.getVersion() + "/");
                partialPathStr = partialPath.toString();
                if(!existingPaths.contains(partialPathStr)) {
                    zip.putNextEntry(new ZipEntry(partialPathStr));
                    zip.closeEntry();
                    existingPaths.add(partialPathStr);
                }

                ZipEntry dependencyJarEntry = new ZipEntry(partialPath + String.format("%s-%s.jar", artifact.getArtifactId(), artifact.getVersion()));
                zip.putNextEntry(dependencyJarEntry);
                writeToOutputStream(artifact.getFile(), zip);
                zip.closeEntry();
                
            }
        } catch (java.io.IOException e) {
            throw new MojoFailureException("Error while creating plugin archive");
        }
    }

    private void writeToOutputStream(File f, OutputStream out) throws IOException {
        try (FileInputStream in = new FileInputStream(f)) {
            byte[] b = new byte[1024];
            int count;

            while ((count = in.read(b)) > 0) {
                out.write(b, 0, count);
            }
        }
    }

    private void writeToOutputStream(String s, OutputStream out) throws IOException {
        out.write(s.getBytes());
    }

    public Map<Artifact, DependencyDescriptor> collectDependencies(Artifact artifact) throws MojoFailureException {
        boolean optional = true;

        Map<Artifact, DependencyDescriptor> optionalDescriptors = collectDependencies(artifact, optional);
        Map<Artifact, DependencyDescriptor> requiredDescriptors = collectDependencies(artifact, !optional);

        Map<Artifact, DependencyDescriptor> allDescriptors = new HashMap<>();
        allDescriptors.putAll(optionalDescriptors);
        allDescriptors.putAll(requiredDescriptors);

        return allDescriptors;
    }

    private Artifact resolveArtifact(Artifact artifact) throws MojoFailureException {
        ArtifactRequest request = new ArtifactRequest();
        request.setArtifact(artifact);
        try {
            return repoSystem.resolveArtifact(repoSession, request).getArtifact();
        } catch (ArtifactResolutionException e) {
            throw new MojoFailureException(e.getMessage(), e);
        }
    }

    private Map<Artifact, DependencyDescriptor> collectDependencies(final Artifact artifact, final boolean optional) throws MojoFailureException {
        try {
            Dependency dependency = new Dependency(artifact, JavaScopes.RUNTIME);
            CollectRequest collectRequest = new CollectRequest(dependency, null);

            DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, new DependencyFilter() {
                @Override
                public boolean accept(DependencyNode node, List<DependencyNode> parents) {
                    if (node.getDependency().getScope().equals(JavaScopes.TEST)) {
                        return false;
                    } else if (node.getDependency().getScope().equals(JavaScopes.PROVIDED)) {
                        return false;
                    } else if (node.getDependency().getScope().equals(JavaScopes.SYSTEM)) {
                        return false;
                    } else if (optional != node.getDependency().isOptional()) {
                        return false;
                    } else if (node.getArtifact().getGroupId().equals(artifact.getGroupId()) &&
                            node.getArtifact().getArtifactId().equals(artifact.getArtifactId()) &&
                            node.getArtifact().getVersion().equals(artifact.getVersion())) {
                        return false;
                    }

                    return true;
                }
            });
            Map<Artifact, DependencyDescriptor> dependencyMap = new HashMap<>();
            List<ArtifactResult> dependencies = repoSystem.resolveDependencies(repoSession, dependencyRequest).getArtifactResults();
            for (ArtifactResult artifactResult : dependencies) {
                DependencyDescriptor descriptor = new DependencyDescriptor();
                descriptor.setGroupId(artifactResult.getArtifact().getGroupId());
                descriptor.setArtifactId(artifactResult.getArtifact().getArtifactId());
                descriptor.setVersion(artifactResult.getArtifact().getVersion());
                descriptor.setScope(optional ? DependencyScope.OPTIONAL : DependencyScope.REQUIRED);
                dependencyMap.put(artifactResult.getArtifact(), descriptor);
            }
            return dependencyMap;
        } catch (IllegalArgumentException | DependencyResolutionException e) {
            throw new MojoFailureException(e.getMessage(), e);
        }

    }


}
