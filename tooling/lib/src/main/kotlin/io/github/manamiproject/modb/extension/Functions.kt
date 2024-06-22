package io.github.manamiproject.modb.extension

import com.google.common.hash.Hashing
import io.github.manamiproject.modb.core.config.FileSuffix
import java.net.URI

/**
 * Transform an anime identifier to a filename.
 * @since 1.0.0
 * @param sources List of [URI] identifying an anime as seen in the "sources" array in anime-offline-database.
 * @param fileSuffix File suffix including a dot.
 * @return Filename including file suffix set in [fileSuffix].
 */
public fun filename(sources: Collection<URI>, fileSuffix: FileSuffix = ".json"): String {
    require(sources.isNotEmpty()) { "Sources must not be empty." }
    val input = sources.toSet().sorted().joinToString("\\0").toByteArray()
    val hash = Hashing.sipHash24().hashBytes(input).toString()
    return "$hash$fileSuffix"
}