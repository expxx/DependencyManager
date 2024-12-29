package dev.expx.dependencymanager.resolver;

import dev.expx.dependencymanager.resolver.lib.ClassPathLibrary;
import dev.expx.dependencymanager.resolver.lib.LibraryStore;
import lombok.Getter;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.graph.DefaultDependencyNode;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.TransferEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Direct Maven Resolver
 *
 * @author someone on stackoverflow
 */
@SuppressWarnings("deprecation")
public class DirectMavenResolver implements ClassPathLibrary {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectMavenResolver.class);

    /**
     * A list of packages that should be
     * loaded from the Parent Classloader
     * instead of the Module Classloader
     */
    public static final List<String> SHARED_PACKAGES = new ArrayList<>();

    private final RepositorySystem repository;
    private final DefaultRepositorySystemSession session;
    @Getter
    private final HashMap<RemoteRepository, Class<?>> repositories = new HashMap<>();
    @Getter
    private final HashMap<Dependency, Class<?>> dependencies = new HashMap<>();

    /**
     * Creates a new Direct Maven Resolver
     */
    @SuppressWarnings("unchecked")
    public DirectMavenResolver() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();

        try {
            locator.addService(RepositoryConnectorFactory.class, (Class<? extends RepositoryConnectorFactory>)
                    Class.forName("org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory"));
            locator.addService(TransporterFactory.class, (Class<? extends TransporterFactory>)
                    Class.forName("org.eclipse.aether.transport.http.HttpTransporterFactory"));
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }

        this.repository = locator.getService(RepositorySystem.class);
        this.session = MavenRepositorySystemUtils.newSession();

        this.session.setChecksumPolicy(RepositoryPolicy.CHECKSUM_POLICY_FAIL);
        this.session.setLocalRepositoryManager(this.repository.newLocalRepositoryManager(this.session, new LocalRepository("libraries")));
        this.session.setTransferListener(new AbstractTransferListener() {
            @Override
            public void transferInitiated(TransferEvent event) {
                LOGGER.info("Downloading {}{}...", event.getResource().getRepositoryUrl(), event.getResource().getResourceName());
            }
        });
        this.session.setReadOnly();
    }

    /**
     * Register the libraries
     * @param store Library store
     */
    @Override
    public void register(LibraryStore store) {
        List<RemoteRepository> repos = this.repository.newResolutionRepositories(this.session, this.repositories.keySet().stream().toList());

        try {
            for (Dependency dependency : this.dependencies.keySet()) {
                DefaultDependencyNode node = new DefaultDependencyNode(dependency);
                node.setRepositories(repos);

                DependencyResult result = this.repository.resolveDependencies(
                        this.session, new DependencyRequest(node, null));

                for (ArtifactResult artifact : result.getArtifactResults()) {
                    File file = artifact.getArtifact().getFile();
                    store.addLibrary(file.toPath());
                }
            }
        } catch (DependencyResolutionException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    /**
     * Add a dependency to be loaded
     * at runtime
     * @param owningClass Owning class
     * @param dependency Dependency
     */
    public void addDependency(Class<?> owningClass, Dependency dependency) {
        this.dependencies.put(dependency, owningClass);
        addSharedPackage(dependency.getArtifact().getGroupId());
    }

    /**
     * Add a repository to be used
     * at runtime to load the dependencies
     * @param owningClass Owning class
     * @param remoteRepository Remote repository
     */
    public void addRepository(Class<?> owningClass, RemoteRepository remoteRepository) {
        this.repositories.put(remoteRepository, owningClass);
    }

    /**
     * Add a shared package
     * @param pkg Package
     */
    public static void addSharedPackage(String pkg) {
        SHARED_PACKAGES.add(pkg);
    }
}
