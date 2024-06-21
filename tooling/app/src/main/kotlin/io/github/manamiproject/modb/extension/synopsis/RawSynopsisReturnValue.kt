package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.core.extensions.neitherNullNorBlank

/**
 * Return for trying to retrieve a raw synopsis.
 * @since 1.0.0
 */
sealed class RawSynopsisReturnValue

/**
 * Indicates that a raw synopsis was not found.
 * @since 1.0.0
 */
data object NoRawSynopsis: RawSynopsisReturnValue()

/**
 * Represents the synopsis as it is found on the site of the meta data provider.
 * @since 1.0.0
 * @property text The text describing the anime or its content.
 */
data class RawSynopsis(val text: String) : RawSynopsisReturnValue() {

    init {
        require(text.neitherNullNorBlank()) { "Text must not be blank." }
    }

    /**
     * Very simple word count option.
     * @since 1.0.0
     * @return Number of segments when splitting the text by whitespaces.
     */
    fun wordCount(): Int = text.split(" ").size
}