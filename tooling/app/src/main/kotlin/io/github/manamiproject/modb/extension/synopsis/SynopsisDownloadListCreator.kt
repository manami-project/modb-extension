package io.github.manamiproject.modb.extension.synopsis

import java.net.URI
import java.time.Period

/**
 * @since 1.0.0
 */
interface SynopsisDownloadListCreator {

    /**
     * @since 1.0.0
     * @param redownloadEntriesOlderThan
     * @return
     */
    suspend fun createDownloadList(redownloadEntriesOlderThan: Period = Period.ofMonths(6)): Set<HashSet<URI>>
}