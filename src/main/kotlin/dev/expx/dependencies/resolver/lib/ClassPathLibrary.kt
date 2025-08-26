package dev.expx.dependencies.resolver.lib

/**
 * @author PaperMC
 */
interface ClassPathLibrary {
    @Throws(LibraryLoadingException::class)
    fun register(var1: LibraryStore)
}