package dev.expx.dependencies.resolver.lib

/**
 * @author PaperMC
 */
class LibraryLoadingException : RuntimeException {
    constructor(s: String?) : super(s)

    constructor(s: String?, e: Exception?) : super(s, e)
}