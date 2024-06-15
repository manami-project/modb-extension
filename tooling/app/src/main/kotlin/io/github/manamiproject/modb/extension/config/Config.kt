package io.github.manamiproject.modb.extension.config

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