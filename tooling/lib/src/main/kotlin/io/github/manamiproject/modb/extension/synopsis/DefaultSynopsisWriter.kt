package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.extension.*
import java.net.URI
import java.time.Clock
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

/**
 * Adds synopsis to [ExtensionData] files.
 * @since 1.0.0
 * @property directory Local directory on which the [ExtensionData] file is saved.
 * @property fileAccessor Reads the [ExtensionData] file.
 * @property clock Clock to determine lastUpdate.
 */
public class DefaultSynopsisWriter(
    private val directory: LocalFileOrigin,
    private val fileAccessor: FileAccessor = DefaultFileAccessor(),
    private val clock: Clock = Clock.systemDefaultZone(),
) : SynopsisWriter {

    override suspend fun saveOrUpdateSynopsis(sources: Collection<URI>, synopsis: Synopsis) {
        require(sources.isNotEmpty()) { "Sources must not be empty." }

        val entry = when (val currentEntry = fileAccessor.loadEntry(sources, directory)) {
            is ExtensionData -> {
                currentEntry.copy(
                    synopsis = synopsis,
                    lastUpdate = LocalDate.now(clock).format(ISO_LOCAL_DATE),
                )
            }
            is ExtensionDataNotFound -> {
                ExtensionData(
                    sources = sources.toSet().sorted(),
                    synopsis = synopsis,
                )
            }
        }

        fileAccessor.saveEntry(directory, entry)
    }
}