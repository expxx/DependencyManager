package dev.expx.dependencymanager.resolver.impl;


import dev.expx.dependencymanager.resolver.lib.LibraryStore;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author PaperMC
 */
public class SimpleLibraryStore implements LibraryStore {
    private final List<Path> paths = new ArrayList();

    public SimpleLibraryStore() {
    }

    public void addLibrary(Path library) {
        this.paths.add(library);
    }

    public List<Path> getPaths() {
        return this.paths;
    }
}
