package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.extension.*
import java.net.URI
import java.time.Clock
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

/**
 * Adds scores to [ExtensionData] files.
 * @since 1.0.0
 * @property directory Local directory on which the [ExtensionData] file is saved.
 * @property fileAccessor Reads the [ExtensionData] file.
 * @property clock Clock to determine lastUpdate.
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