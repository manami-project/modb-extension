package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.extension.*
import java.net.URI
import java.time.Clock
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

/**
 * @since 1.0.0
 * @property directory
 * @property fileAccessor
 * @property clock
 */
public class DefaultScoreWriter(
    private val directory: LocalFileOrigin,
    private val fileAccessor: FileAccessor = DefaultFileAccessor(),
    private val clock: Clock = Clock.systemDefaultZone(),
): ScoreWriter {

    override suspend fun saveOrUpdateScore(sources: Collection<URI>, score: Score) {
        require(sources.isNotEmpty()) { "Sources must not be empty." }

        val entry = when (val currentEntry = fileAccessor.loadEntry(sources, directory)) {
            is ExtensionData -> {
                currentEntry.copy(
                    score = score.copy(
                        hash = filename(sources, EMPTY),
                        lastUpdate = LocalDate.now(clock).format(ISO_LOCAL_DATE),
                    ),
                )
            }
            is ExtensionDataNotFound -> {
                ExtensionData(
                    sources = sources.toSet().sorted(),
                    score = score,
                )
            }
        }

        fileAccessor.saveEntry(directory, entry)
    }
}