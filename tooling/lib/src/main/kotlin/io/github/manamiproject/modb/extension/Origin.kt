package io.github.manamiproject.modb.extension

import io.github.manamiproject.modb.core.extensions.Directory
import io.github.manamiproject.modb.core.extensions.RegularFile
import io.github.manamiproject.modb.core.extensions.directoryExists
import java.net.URI

/**
 * @since 1.0.0
 */
public sealed interface Origin<T> {

    /**
     * @since 1.0.0
     * @param filename
     * @return
     */
    public fun resolve(filename: String): T
}

/**
 * @since 1.0.0
 */
public data object ModbExtensionRepoOrigin: Origin<URI> {

    private val uri = URI("https://github.com/manami-project/modb-extension/raw/master/data")

    override fun resolve(filename: String): URI {
        require(filename.isNotBlank()) { "Filename must not be blank." }
        return URI("$uri/$filename")
    }
}

/**
 * @since 1.0.0
 * @property uri
 */
@JvmInline
public value class UriOrigin(private val uri: URI) : Origin<URI> {

    override fun resolve(filename: String): URI {
        require(filename.isNotBlank()) { "Filename must not be blank." }

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
 * @since 1.0.0
 * @property directory
 */
@JvmInline
public value class LocalFileOrigin(private val directory: Directory): Origin<RegularFile> {

    init {
        require(directory.directoryExists()) { "Path [${directory.toAbsolutePath()}] doesn't exist or is not a directory." }
    }

    override fun resolve(filename: String): RegularFile {
        require(filename.isNotBlank()) { "Filename must not be blank." }
        return directory.resolve(filename)
    }
}
