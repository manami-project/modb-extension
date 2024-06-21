package io.github.manamiproject.modb.extension.synopsis

import io.github.manamiproject.modb.core.extensions.EMPTY

/**
 * Return for trying to retrieve a synopsis.
 * @since 1.0.0
 */
public sealed class SynopsisReturnValue

/**
 * Indicates that no synopsis has been found.
 * @since 1.0.0
 */
public data object SynopsisNotFound: SynopsisReturnValue()

/**
 * Synopsis created by an LLM.
 * @since 1.0.0
 * @property text Synopsis.
 * @property author Author
 */
public data class Synopsis(
    val text: String = EMPTY,
    val author: String = EMPTY,
): SynopsisReturnValue()