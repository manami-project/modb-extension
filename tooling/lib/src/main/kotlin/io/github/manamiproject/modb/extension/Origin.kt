package io.github.manamiproject.modb.extension

import io.github.manamiproject.modb.core.extensions.Directory
import io.github.manamiproject.modb.core.extensions.RegularFile
import io.github.manamiproject.modb.core.extensions.directoryExists
import io.github.manamiproject.modb.core.extensions.neitherNullNorBlank
import java.net.URI

/**
 * Defines the source of Data.
 * @since 1.0.0
 */
public sealed interface Origin<T> {

    /**
     * Resolve the filename to a base.
     * Example: If you have a directory. You resolve the filename against the directory creating an absolute path.
     * @since 1.0.0
     * @param filename Filename including suffix.
     * @return Object of defined type [T]
     */
    public fun resolve(filename: String): T
}

/**
 * Loads data from github repo.
 * @since 1.0.0
 */
public data object ModbExtensionRepoOrigin: Origin<URI> {

    private val uri = URI("https://raw.githubusercontent.com/manami-project/modb-extension/main/data")

    override fun resolve(filename: String): URI {
        require(filename.neitherNullNorBlank()) { "Filename must not be blank." }
        return URI("$uri/$filename")
    }
}

/**
 * File which can be accessed via HTTP/HTTPS.
 * @since 1.0.0
 * @property uri Host and path without filename.
 */
@JvmInline
public value class UriOrigin(private val uri: URI) : Origin<URI> {

    override fun resolve(filename: String): URI {
        require(filename.neitherNullNorBlank()) { "Filename must not be blank." }
        require(uri.scheme in setOf("https", "http")) { "Protocol must be http or https." }

        val uriString = uri.toString()

        val resolved = if (uriString.endsWith('/')) {
            "$uriString$filename"
        } else {
            "$uriString/$filename"
        }

        return URI(resolved)
    }
}

/**
 * Local file.
 * @since 1.0.0
 * @property directory Local directory in which the files resides.
 */
@JvmInline
public value class LocalFileOrigin(private val directory: Directory): Origin<RegularFile> {

    init {
        require(directory.directoryExists()) { "Path [${directory.toAbsolutePath()}] doesn't exist or is not a directory." }
    }

    override fun resolve(filename: String): RegularFile {
        require(filename.neitherNullNorBlank()) { "Filename must not be blank." }
        return directory.resolve(filename)
    }
}
