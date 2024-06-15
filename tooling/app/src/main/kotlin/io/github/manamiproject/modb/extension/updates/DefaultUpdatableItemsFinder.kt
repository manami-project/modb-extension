package io.github.manamiproject.modb.extension.updates

import io.github.manamiproject.modb.extension.config.Config
import io.github.manamiproject.modb.extension.filename
import java.net.URI
import kotlin.io.path.deleteIfExists

/**
 * @since 1.0.0
 * @property config
 */
class DefaultUpdatableItemsFinder(
    private val config: Config
): UpdatableItemsFinder {

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
            config.dataDirectory().resolve(it).deleteIfExists()
        }

        return newOrUpdatedEntriesInDb.values.toSet()
    }
}