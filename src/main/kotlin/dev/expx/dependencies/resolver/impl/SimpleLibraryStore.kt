package dev.expx.dependencies.resolver.impl

import dev.expx.dependencies.resolver.lib.LibraryStore
import java.nio.file.Path

/**
 * @author PaperMC
 */
class SimpleLibraryStore : LibraryStore {
    val paths: MutableList<Path?> = ArrayList()

    override fun addLibrary(var1: Path) {
        this.paths.add(var1)
    }
}