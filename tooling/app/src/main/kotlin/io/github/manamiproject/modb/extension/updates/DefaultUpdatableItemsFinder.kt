package io.github.manamiproject.modb.extension.updates

import io.github.manamiproject.modb.extension.config.Config
import io.github.manamiproject.modb.extension.filename
import java.net.URI
import kotlin.io.path.deleteIfExists

/**
 * Checks the anime-offline-database for entries that have changed.
 * @since 1.0.0
 * @property appConfig Application specific configuration.
 */
class DefaultUpdatableItemsFinder(
    private val appConfig: Config,
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
            appConfig.dataDirectory().resolve(it).deleteIfExists()
        }

        return newOrUpdatedEntriesInDb.values.toSet()
    }
}