package dev.expx.dependencymanager.resolver.impl;

import dev.expx.dependencymanager.resolver.lib.ClassPathLibrary;
import dev.expx.dependencymanager.resolver.lib.LibraryLoadingException;
import dev.expx.dependencymanager.resolver.lib.LibraryStore;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author PaperMC
 */
public class JarLibrary implements ClassPathLibrary {
    private final Path path;

    public JarLibrary(Path path) {
        this.path = path;
    }

    public void register(LibraryStore store) throws LibraryLoadingException {
        if (Files.notExists(this.path)) {
            throw new LibraryLoadingException("Could not find library at " + this.path);
        } else {
            store.addLibrary(this.path);
        }
    }
}
