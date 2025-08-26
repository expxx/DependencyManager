package dev.expx.dependencies.resolver.lib

import java.nio.file.Path


/**
 * @author PaperMC
 */
interface LibraryStore {
    fun addLibrary(var1: Path)
}