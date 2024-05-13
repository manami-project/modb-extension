package io.github.manamiproject.modb.extension

import java.net.URI

/**
 * @since 1.0.0
 */
public interface FileAccessor {

    /**
     * @since 1.0.0
     * @param sources
     * @param origin
     * @return
     */
    public suspend fun loadEntry(sources: Collection<URI>, origin: Origin<*>): ExtensionDataReturnValue

    /**
     * @since 1.0.0
     * @param directory
     * @param extensionData
     * @return
     */
    public suspend fun saveEntry(directory: LocalFileOrigin, extensionData: ExtensionData)
}