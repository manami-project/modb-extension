package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.core.extensions.EMPTY
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

/**
 * @since 1.0.0
 */
public sealed class SynopsisReturnValue

/**
 * @since 1.0.0
 */
public data object SynopsisNotFound: SynopsisReturnValue()

/**
 * @since 1.0.0
 * @property text
 * @property author
 * @property lastUpdate
 */
public data class Synopsis(
    val text: String = EMPTY,
    val author: String = EMPTY,
    private val lastUpdate: String = LocalDate.now().format(ISO_LOCAL_DATE),
): SynopsisReturnValue() {

    init {
        require(YEAR_REGEX.matches(lastUpdate)) { "Property 'lastUpdate' must be set and match ISO_LOCAL_DATE format." }
    }

    /**
     * @since 1.0.0
     */
    val lastUpdatedAt: LocalDate
        get() = LocalDate.parse(lastUpdate)

    private companion object {
        private val YEAR_REGEX = """^\d{4}-\d{2}-\d{2}$""".toRegex()
    }
}