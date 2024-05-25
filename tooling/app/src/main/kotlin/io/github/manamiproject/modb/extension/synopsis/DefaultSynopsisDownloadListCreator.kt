package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.core.extensions.Directory
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.extensions.directoryExists
import io.github.manamiproject.modb.core.extensions.readFile
import io.github.manamiproject.modb.core.extractor.DataExtractor
import io.github.manamiproject.modb.core.extractor.JsonDataExtractor
import io.github.manamiproject.modb.core.json.Json
import io.github.manamiproject.modb.extension.ExtensionData
import io.github.manamiproject.modb.extension.filename
import java.net.URI
import java.time.Clock
import java.time.LocalDate
import java.time.Period
import kotlin.io.path.forEachDirectoryEntry

/**
 * @since 1.0.0
 * @property directory
 * @property clock
 * @property extractor
 */
class DefaultSynopsisDownloadListCreator(
    private val directory: Directory,
    private val clock: Clock = Clock.systemDefaultZone(),
    private val extractor: DataExtractor = JsonDataExtractor,
): SynopsisDownloadListCreator {

    init {
        require(directory.directoryExists()) { "Data directory either doesn't exist or is not a directory." }
    }

    override suspend fun createDownloadList(redownloadEntriesOlderThan: Period): Set<HashSet<URI>> {
        val ret = mutableSetOf<HashSet<URI>>()

        directory.forEachDirectoryEntry("*.json") { file ->
            val fileContent = file.readFile()
            val extensionData = Json.parseJson<ExtensionData>(fileContent)!!
            when (val synopsis = extensionData.synopsis()) {
                is Synopsis -> {
                    if (isRedownloadNecessary(redownloadEntriesOlderThan, synopsis.lastUpdatedAt)) {
                        ret.add(extensionData.sources.toHashSet())
                    }
                }
                is SynopsisNotFound -> {
                    val data = extractor.extract(fileContent, mapOf(
                        "lastUpdate" to "$.synopsis.lastUpdate",
                    ))

                    if (data.notFound("lastUpdate") || isRedownloadNecessary(redownloadEntriesOlderThan, data.string("lastUpdate"))) {
                        ret.add(extensionData.sources.toHashSet())
                    }
                }
            }
        }

        return ret
    }

    private fun isRedownloadNecessary(redownloadEntriesOlderThan: Period, dateToCheck: String): Boolean {
        return isRedownloadNecessary(redownloadEntriesOlderThan, LocalDate.parse(dateToCheck))
    }

    private fun isRedownloadNecessary(redownloadEntriesOlderThan: Period, dateToCheck: LocalDate): Boolean {
        return LocalDate.now(clock).minus(redownloadEntriesOlderThan).isAfter(dateToCheck)
    }
}