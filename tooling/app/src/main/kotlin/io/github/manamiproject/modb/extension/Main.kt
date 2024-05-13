package io.github.manamiproject.modb.extension

import io.github.manamiproject.modb.core.coroutines.CoroutineManager.runCoroutine
import io.github.manamiproject.modb.core.extensions.Directory
import io.github.manamiproject.modb.core.extensions.directoryExists
import io.github.manamiproject.modb.core.extensions.readFile
import io.github.manamiproject.modb.core.extractor.JsonDataExtractor
import io.github.manamiproject.modb.core.random
import io.github.manamiproject.modb.extension.score.*
import io.github.manamiproject.modb.extension.synopsis.*
import io.github.manamiproject.modb.extension.updates.DefaultUpdatableItemsFinder
import io.github.manamiproject.modb.serde.json.AnimeListJsonStringDeserializer
import io.github.manamiproject.modb.serde.json.DefaultExternalResourceJsonDeserializer
import kotlinx.coroutines.delay
import java.net.URI
import kotlin.io.path.Path
import kotlin.io.path.forEachDirectoryEntry

fun main() = runCoroutine {
    val sourcesFromDb = fetchSourcesFromDb()
    val dataDirectory = Path(System.getenv("MODB_EXTENSION_DATA_DIR"))
    val localFileOrigin = LocalFileOrigin(dataDirectory)
    val existingFiles = fetchAllExistingFiles(dataDirectory)
    val newDbEntries = DefaultUpdatableItemsFinder(dataDirectory).findNewDbEntries(sourcesFromDb, existingFiles)

    val scoreCreator = DefaultScoreCreator()
    val scoreWriter = DefaultScoreWriter(localFileOrigin)
    val scoreDownloadListCreator = DefaultScoreDownloadListCreator(dataDirectory)
    val scoreDownloadList = newDbEntries.union(scoreDownloadListCreator.createDownloadList())
    scoreDownloadList.forEachIndexed { index, sourcesBlock ->
        println("Downloading [${index + 1}/${scoreDownloadList.size}]")
        delay(random(1500, 2000))
        val scoreReturnValue = scoreCreator.createScore(sourcesBlock)
        if (scoreReturnValue !is ScoreNoteFound) {
            scoreWriter.saveOrUpdateScore(sourcesBlock, scoreReturnValue as Score)
        }
    }

    val synopsisCreator = DefaultSynopsisCreator()
    val synopsisWriter = DefaultSynopsisWriter(localFileOrigin)
    val synopsisDownloadListCreator = DefaultSynopsisDownloadListCreator(dataDirectory)
    val synopsisDownloadList = newDbEntries.union(synopsisDownloadListCreator.createDownloadList())
    synopsisDownloadList.forEachIndexed { index, sourcesBlock ->
        println("Downloading [${index + 1}/${synopsisDownloadList.size}]")
        delay(random(1500, 2000))
        val synopsisReturnValue = synopsisCreator.createSynopsis(sourcesBlock)
        if (synopsisReturnValue !is SynopsisNotFound) {
            synopsisWriter.saveOrUpdateSynopsis(sourcesBlock, synopsisReturnValue as Synopsis)
        }
    }

    check((fetchAllSourcesInFiles(dataDirectory) - sourcesFromDb).isEmpty()) { "All sources in files must exist in db at this point." }

    println("Done")
}

private suspend fun fetchSourcesFromDb(): Set<HashSet<URI>> {
    val deserializer = DefaultExternalResourceJsonDeserializer(deserializer = AnimeListJsonStringDeserializer())
    val zipFile = URI("https://raw.githubusercontent.com/manami-project/anime-offline-database/master/anime-offline-database.zip")
    return deserializer.deserialize(zipFile.toURL()).data.map { it.sources }.toSet()
}

private suspend fun fetchAllSourcesInFiles(directory: Directory): Set<Set<URI>> {
    val list = mutableSetOf<Set<URI>>()
    directory.forEachDirectoryEntry("*.json") { file ->
        val extractionResult = JsonDataExtractor.extract(file.readFile(), mapOf(
            "sources" to "$.sources"
        ))
        list.add(extractionResult.listNotNull<URI>("sources") { URI(it) }.toSet())
    }
    return list
}

private fun fetchAllExistingFiles(directory: Directory): Set<String> {
    require(directory.directoryExists()) { "Data directory either doesn't exist or is not a directory." }
    val ret = mutableSetOf<String>()
    directory.forEachDirectoryEntry("*.json") {
        ret.add(it.fileName.toString())
    }
    return ret
}