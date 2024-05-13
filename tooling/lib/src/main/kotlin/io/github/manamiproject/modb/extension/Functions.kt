package io.github.manamiproject.modb.extension

import com.google.common.hash.Hashing
import io.github.manamiproject.modb.core.config.FileSuffix
import java.net.URI

/**
 * @since 1.0.0
 * @param sources
 * @param fileSuffix
 * @return Filename including file suffix set in [fileSuffix].
 */
public fun filename(sources: Collection<URI>, fileSuffix: FileSuffix = ".json"): String {
    require(sources.isNotEmpty()) { "Sources must not be empty." }
    val input = sources.toSet().sorted().joinToString("\\0").toByteArray()
    val hash = Hashing.sipHash24().hashBytes(input).toString()
    return "$hash$fileSuffix"
}