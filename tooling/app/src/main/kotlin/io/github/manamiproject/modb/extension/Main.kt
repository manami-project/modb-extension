package io.github.manamiproject.modb.extension

import io.github.manamiproject.modb.core.coroutines.CoroutineManager.runCoroutine
import io.github.manamiproject.modb.core.extensions.readFile
import io.github.manamiproject.modb.core.extractor.JsonDataExtractor
import io.github.manamiproject.modb.core.random
import io.github.manamiproject.modb.extension.config.AppConfig
import io.github.manamiproject.modb.extension.config.Config
import io.github.manamiproject.modb.extension.score.*
import io.github.manamiproject.modb.extension.synopsis.*
import io.github.manamiproject.modb.extension.updates.DefaultUpdatableItemsFinder
import io.github.manamiproject.modb.serde.json.AnimeListJsonStringDeserializer
import io.github.manamiproject.modb.serde.json.DefaultExternalResourceJsonDeserializer
import kotlinx.coroutines.delay
import java.net.URI
import kotlin.io.path.forEachDirectoryEntry

fun main() = runCoroutine {
    val appConfig = AppConfig()
    val sourcesFromDb = fetchSourcesFromDb(appConfig)
    val localFileOrigin = LocalFileOrigin(appConfig.dataDirectory())
    val existingFiles = fetchAllExistingFiles(appConfig)

    val newDbEntries = DefaultUpdatableItemsFinder(appConfig).findNewDbEntries(sourcesFromDb, existingFiles)
    val scoreDownloadListCreator = DefaultScoreDownloadListCreator(appConfig)
    val synopsisDownloadListCreator = DefaultSynopsisDownloadListCreator(appConfig)
    val downloadList = newDbEntries.union(scoreDownloadListCreator.createDownloadList())
        .union(synopsisDownloadListCreator.createDownloadList())

    val scoreCreator = DefaultScoreCreator(appConfig)
    val scoreWriter = DefaultScoreWriter(localFileOrigin)
    downloadList.forEachIndexed { index, sourcesBlock ->
        println("Downloading [${index + 1}/${downloadList.size}]")
        delay(random(1500, 2000))
        val scoreReturnValue = scoreCreator.createScore(sourcesBlock)
        if (scoreReturnValue !is ScoreNoteFound) {
            scoreWriter.saveOrUpdateScore(sourcesBlock, scoreReturnValue as Score)
        }
    }

    val synopsisCreator = DefaultSynopsisCreator(appConfig)
    val synopsisWriter = DefaultSynopsisWriter(localFileOrigin)
    downloadList.forEachIndexed { index, sourcesBlock ->
        println("Downloading [${index + 1}/${downloadList.size}]")
        delay(random(1500, 2000))
        val synopsisReturnValue = synopsisCreator.createSynopsis(sourcesBlock)
        if (synopsisReturnValue !is SynopsisNotFound) {
            synopsisWriter.saveOrUpdateSynopsis(sourcesBlock, synopsisReturnValue as Synopsis)
        }
    }

    check((fetchAllSourcesInFiles(appConfig) - sourcesFromDb).isEmpty()) { "All sources in files must exist in db at this point." }

    println("Done")
}

private suspend fun fetchSourcesFromDb(config: Config): Set<HashSet<URI>> {
    val deserializer = DefaultExternalResourceJsonDeserializer(deserializer = AnimeListJsonStringDeserializer())
    return deserializer.deserialize(config.animeDataSet().toURL()).data.map { it.sources }.toSet()
}

private suspend fun fetchAllSourcesInFiles(config: Config): Set<Set<URI>> {
    val list = mutableSetOf<Set<URI>>()
    config.dataDirectory().forEachDirectoryEntry("*.json") { file ->
        val extractionResult = JsonDataExtractor.extract(file.readFile(), mapOf(
            "sources" to "$.sources"
        ))
        list.add(extractionResult.listNotNull<URI>("sources") { URI(it) }.toSet())
    }
    return list
}

private fun fetchAllExistingFiles(config: Config): Set<String> {
    val ret = mutableSetOf<String>()
    config.dataDirectory().forEachDirectoryEntry("*.json") {
        ret.add(it.fileName.toString())
    }
    return ret
}