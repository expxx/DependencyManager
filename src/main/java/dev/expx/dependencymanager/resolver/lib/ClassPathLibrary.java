package dev.expx.dependencymanager.resolver.lib;

/**
 * @author PaperMC
 */
public interface ClassPathLibrary {
    void register(LibraryStore var1) throws LibraryLoadingException;
}
