package io.github.manamiproject.modb.extension.score

import java.net.URI
import java.time.Period

/**
 * Creates a list all anime for which a [Score] needs to be created.
 * @since 1.0.0
 */
interface ScoreDownloadListCreator {

    /**
     * Creates the download list and can also take an interval into account which defines the retention time of an entry.
     * Because scores will change over time a redownload should be done in intervals.
     * @since 1.0.0
     * @property redownloadEntriesOlderThan Defined the interval in which a score should be updated.
     * @return List of anime which for which a score has to be (re)created. The identifier of an anime is a list of [URI] as seen in "sources" property of anime-offline-database.
     */
    suspend fun createDownloadList(redownloadEntriesOlderThan: Period = Period.ofMonths(6)): Set<HashSet<URI>>
}