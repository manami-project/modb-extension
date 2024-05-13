package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.extension.*
import io.github.manamiproject.modb.extension.DefaultFileAccessor
import java.net.URI

/**
 * @since 1.0.0
 * @property origin
 * @property fileAccessor
 */
public class DefaultSynopsisReader(
    private val origin: Origin<*>,
    private val fileAccessor: FileAccessor = DefaultFileAccessor(),
) : SynopsisReader {

    override suspend fun findSynopsis(sources: Collection<URI>): SynopsisReturnValue {
        require(sources.isNotEmpty()) { "Sources must not be empty." }

        return when (val extensionData = fileAccessor.loadEntry(sources, origin)) {
            is ExtensionData -> extensionData.synopsis()
            is ExtensionDataNotFound -> SynopsisNotFound
        }
    }
}