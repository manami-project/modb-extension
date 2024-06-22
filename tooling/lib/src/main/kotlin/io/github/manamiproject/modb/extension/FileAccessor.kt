package io.github.manamiproject.modb.extension

import java.net.URI

/**
 * Allows access to files containing data with this app.
 * @since 1.0.0
 */
public interface FileAccessor {

    /**
     * Loads the content of a file into an [ExtensionData] object.
     * @since 1.0.0
     * @param sources List of [URI] identifying an anime as seen in the "sources" array in anime-offline-database.
     * @param origin Where to load the file from.
     * @return Either the [ExtensionData] or [ExtensionDataNotFound] if a file doesn't exist.
     */
    public suspend fun loadEntry(sources: Collection<URI>, origin: Origin<*>): ExtensionDataReturnValue

    /**
     * Saves the data to a file.
     * @since 1.0.0
     * @param directory Local directory in which the files are being saved.
     * @param extensionData Data being saved.
     */
    public suspend fun saveEntry(directory: LocalFileOrigin, extensionData: ExtensionData)
}