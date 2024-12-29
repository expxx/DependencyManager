package dev.expx.dependencymanager.resolver.lib;

/**
 * @author PaperMC
 */
public class LibraryLoadingException extends RuntimeException {
    public LibraryLoadingException(String s) {
        super(s);
    }

    public LibraryLoadingException(String s, Exception e) {
        super(s, e);
    }
}