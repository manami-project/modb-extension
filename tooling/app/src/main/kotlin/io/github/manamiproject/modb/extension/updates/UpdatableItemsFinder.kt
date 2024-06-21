package io.github.manamiproject.modb.extension.updates

import java.net.URI

/**
 * Checks the anime-offline-database for entries that have changed.
 * @since 1.0.0
 */
interface UpdatableItemsFinder {

    /**
     * Find all new items when comparing local data with anime-offline-datanase.
     * @since 1.0.0
     * @param sourcesFromDb All "sources" properties from anime-offline-database.
     * @param existingFileNames All existing files created by this app.
     * @return A list of animes which appear to be new for this app.
     */
    suspend fun findNewDbEntries(sourcesFromDb: Set<HashSet<URI>>, existingFileNames: Set<String>): Set<HashSet<URI>>
}