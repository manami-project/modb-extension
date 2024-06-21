package io.github.manamiproject.modb.extension.score

import io.github.manamiproject.modb.core.extensions.readFile
import io.github.manamiproject.modb.core.extractor.DataExtractor
import io.github.manamiproject.modb.core.extractor.JsonDataExtractor
import io.github.manamiproject.modb.core.json.Json
import io.github.manamiproject.modb.extension.ExtensionData
import io.github.manamiproject.modb.extension.config.Config
import java.net.URI
import java.time.LocalDate
import java.time.Period
import kotlin.io.path.forEachDirectoryEntry

/**
 * Creates a list all anime for which a [Score] needs to be created.
 * This can either be due to the entry being new, being updated or outdated.
 * @since 1.0.0
 * @property appConfig Application specific configuration.
 * @property extractor Extracts specific data from the raw data.
 */
class DefaultScoreDownloadListCreator(
    private val appConfig: Config,
    private val extractor: DataExtractor = JsonDataExtractor,
): ScoreDownloadListCreator {

    override suspend fun createDownloadList(redownloadEntriesOlderThan: Period): Set<HashSet<URI>> {
        val ret = mutableSetOf<HashSet<URI>>()

        appConfig.dataDirectory().forEachDirectoryEntry("*.json") { file ->
            val fileContent = file.readFile()
            val extensionData = Json.parseJson<ExtensionData>(fileContent)!!

            when (val score = extensionData.score()) {
                is Score -> {
                    if (isRedownloadNecessary(redownloadEntriesOlderThan, score.lastUpdatedAt)) {
                        ret.add(extensionData.sources.toHashSet())
                    }
                }
                is ScoreNotFound -> {
                    val data = extractor.extract(fileContent, mapOf(
                        "lastUpdate" to "$.score.lastUpdate",
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
        return LocalDate.now(appConfig.clock()).minus(redownloadEntriesOlderThan).isAfter(dateToCheck)
    }
}