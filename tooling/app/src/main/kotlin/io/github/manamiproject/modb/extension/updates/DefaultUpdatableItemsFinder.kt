package io.github.manamiproject.modb.extension.updates

import io.github.manamiproject.modb.core.extensions.Directory
import io.github.manamiproject.modb.core.extensions.directoryExists
import io.github.manamiproject.modb.extension.filename
import java.net.URI
import kotlin.io.path.deleteIfExists

/**
 * @since 1.0.0
 * @property directory
 */
class DefaultUpdatableItemsFinder(private val directory: Directory): UpdatableItemsFinder {

    init {
        require(directory.directoryExists()) { "Data directory either doesn't exist or is not a directory." }
    }

    override suspend fun findNewDbEntries(
        sourcesFromDb: Set<HashSet<URI>>,
        existingFileNames: Set<String>,
    ): Set<HashSet<URI>> {
        val expectedFilenameToDbSources = sourcesFromDb.associateBy { filename(it) }

        val newOrUpdatedEntriesInDb: Map<String, HashSet<URI>> = expectedFilenameToDbSources.toMutableMap().apply {
            existingFileNames.forEach { remove(it) }
        }

        existingFileNames.toMutableSet().apply {
            removeAll(expectedFilenameToDbSources.keys)
        }.forEach {
            directory.resolve(it).deleteIfExists()
        }

        return newOrUpdatedEntriesInDb.values.toSet()
    }
}