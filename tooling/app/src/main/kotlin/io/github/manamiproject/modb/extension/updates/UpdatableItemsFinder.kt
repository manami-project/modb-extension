package io.github.manamiproject.modb.extension.updates

import java.net.URI

/**
 * @since 1.0.0
 */
interface UpdatableItemsFinder {

    /**
     * @since 1.0.0
     * @param sourcesFromDb
     * @param existingFileNames
     * @return
     */
    suspend fun findNewDbEntries(sourcesFromDb: Set<HashSet<URI>>, existingFileNames: Set<String>): Set<HashSet<URI>>
}