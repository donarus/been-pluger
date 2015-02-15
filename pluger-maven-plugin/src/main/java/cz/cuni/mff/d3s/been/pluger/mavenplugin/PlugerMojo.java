package cz.cuni.mff.d3s.been.pluger.mavenplugin;

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
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.util.artifact.JavaScopes;

import java.util.Collection;
import java.util.List;

@Mojo(name = "pluginate", threadSafe = true)
public class PlugerMojo extends AbstractMojo {

    @Component
    private RepositorySystem repoSystem;

    @Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
    private RepositorySystemSession repoSession;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject mavenProject;


    public void execute() throws MojoExecutionException, MojoFailureException {

        String artifactCoords = String.format("%s:%s:%s",
                mavenProject.getGroupId(),
                mavenProject.getArtifactId(),
                mavenProject.getVersion());

        try {
            Artifact artifact = new DefaultArtifact(artifactCoords);
            Collection<ArtifactResult> dependencies = collectDependencies(artifact);
            for (ArtifactResult dependency : dependencies) {
                System.out.println(dependency.toString());
            }
        } catch (IllegalArgumentException | DependencyResolutionException e) {
            throw new MojoFailureException(e.getMessage(), e);
        }
    }

    public Collection<ArtifactResult> collectDependencies(Artifact artifact) throws DependencyResolutionException {
        Dependency dependency = new Dependency(artifact, JavaScopes.RUNTIME);
        CollectRequest collectRequest = new CollectRequest(dependency, null);

        DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, new DependencyFilter() {
            @Override
            public boolean accept(DependencyNode node, List<DependencyNode> parents) {
                return !node.getDependency().getScope().equals(JavaScopes.TEST) && !node.getDependency().isOptional();
            }
        });
        return repoSystem.resolveDependencies(repoSession, dependencyRequest).getArtifactResults();
    }


}
