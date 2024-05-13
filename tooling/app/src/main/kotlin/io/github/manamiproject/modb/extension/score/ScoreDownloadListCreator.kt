package io.github.manamiproject.modb.extension.score

import java.net.URI
import java.time.Period

/**
 * @since 1.0.0
 */
interface ScoreDownloadListCreator {

    /**
     * @since 1.0.0
     * @property redownloadEntriesOlderThan
     * @return
     */
    suspend fun createDownloadList(redownloadEntriesOlderThan: Period = Period.ofMonths(6)): Set<HashSet<URI>>
}