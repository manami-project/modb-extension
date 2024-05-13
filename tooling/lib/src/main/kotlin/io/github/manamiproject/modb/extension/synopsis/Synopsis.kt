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
 * @property coAuthors
 * @property hash
 * @property created
 * @property lastUpdate
 */
public data class Synopsis(
    val text: String = EMPTY,
    val author: String = EMPTY,
    val coAuthors: Set<String> = emptySet(),
    val hash: String,
    private val created: String = LocalDate.now().format(ISO_LOCAL_DATE),
    private val lastUpdate: String = LocalDate.now().format(ISO_LOCAL_DATE),
): SynopsisReturnValue() {

    init {
        require(hash.isNotBlank()) { "Property 'hash' must not be blank." }
        require(YEAR_REGEX.matches(created)) { "Property 'created' must be set and match ISO_LOCAL_DATE format." }
        require(YEAR_REGEX.matches(lastUpdate)) { "Property 'lastUpdate' must be set and match ISO_LOCAL_DATE format." }
        require(!lastUpdatedAt.isBefore(createdAt)) { "Propterty 'created' cannot have a date before property 'lastUpdate'." }
        require(coAuthors.all { it.isNotBlank() }) { "Property 'coAuthors' must not contain blank entries." }
    }

    /**
     * @since 1.0.0
     */
    val createdAt: LocalDate
        get() = LocalDate.parse(created)

    /**
     * @since 1.0.0
     */
    val lastUpdatedAt: LocalDate
        get() = LocalDate.parse(lastUpdate)

    private companion object {
        private val YEAR_REGEX = """^\d{4}-\d{2}-\d{2}$""".toRegex()
    }
}