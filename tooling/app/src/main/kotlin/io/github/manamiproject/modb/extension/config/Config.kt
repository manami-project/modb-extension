package io.github.manamiproject.modb.extension.config

import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.extensions.Directory
import java.net.URI
import java.time.Clock

/**
 * Contains all configurable and directly derived properties.
 * @since 1.0.0
 */
interface Config {

    /**
     * Returns the directory in which the `*.json` files are being saved.
     * @since 1.0.0
     * @return Path to the JSON files created by the app.
     */
    fun dataDirectory(): Directory

    /**
     * Returns the directory in which the raw files of the meta data providers are temporarily saved.
     * @since 1.0.0
     * @return Directory in which a folder for each meta data providers reside.
     */
    fun rawFilesDirectory(): Directory

    /**
     * Returns the directory in which the raw files of a specific meta data provider are temporarily saved.
     * @since 1.0.0
     * @param metaDataProviderConfig Identifies which meta data provider is selected.
     * @return Directory with the raw files for a specific meta data provider.
     */
    fun rawFilesDirectory(metaDataProviderConfig: MetaDataProviderConfig): Directory

    /**
     * Source of the anime data set.
     * Expected is the format defined [here](https://github.com/manami-project/anime-offline-database).
     * @since 1.0.0
     * @return [URI] pointing to the anime data set.
     */
    fun animeDataSet(): URI

    /**
     * Clock being used whenever dates and timestamps are created.
     * Default is system default zone.
     * @since 1.0.0
     * @return Instance of [Clock] as basis for any type of date instances.
     */
    fun clock(): Clock = Clock.systemDefaultZone()
}