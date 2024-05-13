package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.extension.*
import java.net.URI

/**
 * @since 1.0.0
 * @property origin
 * @property fileAccessor
 */
public class DefaultScoreReader(
    private val origin: Origin<*>,
    private val fileAccessor: FileAccessor = DefaultFileAccessor(),
): ScoreReader {

    override suspend fun findScore(sources: Collection<URI>): ScoreReturnValue {
        require(sources.isNotEmpty()) { "Sources must not be empty." }

        return when (val extensionData = fileAccessor.loadEntry(sources, origin)) {
            is ExtensionData -> extensionData.score()
            is ExtensionDataNotFound -> ScoreNoteFound
        }
    }
}