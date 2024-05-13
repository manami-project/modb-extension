package io.github.manamiproject.modb.extension.synopsis

/**
 * @since 1.0.0
 */
sealed class RawSynopsisReturnValue

/**
 * @since 1.0.0
 */
data object NoRawSynopsis: RawSynopsisReturnValue()

/**
 * @since 1.0.0
 * @property text
 */
data class RawSynopsis(val text: String) : RawSynopsisReturnValue() {

    init {
        require(text.isNotBlank()) { "Text must not be blank." }
    }

    /**
     * @since 1.0.0
     * @return
     */
    fun wordCount(): Int = text.split(" ").size
}