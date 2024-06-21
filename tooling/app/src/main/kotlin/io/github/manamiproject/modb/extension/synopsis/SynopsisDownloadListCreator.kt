package io.github.manamiproject.modb.extension.synopsis

import java.net.URI
import java.time.Period

/**
 * Creates a list of all anime for which a [Synopsis] needs to be created.
 * @since 1.0.0
 */
interface SynopsisDownloadListCreator {

    /**
     * Creates the download list and can also take an interval into account which defines the retention time of an entry.
     * Because synopsis might be added at a later time a redownload should be done in intervals
     * @since 1.0.0
     * @param redownloadEntriesOlderThan Defined the interval in which a score should be updated.
     * @return List of anime which for which a score has to be (re)created. The identifier of an anime is a list of [URI] as seen in "sources" property of anime-offline-database.
     */
    suspend fun createDownloadList(redownloadEntriesOlderThan: Period = Period.ofMonths(6)): Set<HashSet<URI>>
}