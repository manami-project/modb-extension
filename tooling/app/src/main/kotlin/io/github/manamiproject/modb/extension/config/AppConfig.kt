package io.github.manamiproject.modb.extension.config

import io.github.manamiproject.modb.core.config.ConfigRegistry
import io.github.manamiproject.modb.core.config.DefaultConfigRegistry
import io.github.manamiproject.modb.core.config.StringPropertyDelegate
import io.github.manamiproject.modb.core.extensions.Directory
import io.github.manamiproject.modb.core.extensions.directoryExists
import java.net.URI
import kotlin.io.path.Path

/**
 * Implementation of [Config] which contains all necessary properties.
 * @since 1.0.0
 * @param configRegistry Implementation of [ConfigRegistry] used for populating properties. Uses [DefaultConfigRegistry] by default.
 */
class AppConfig(
    configRegistry: ConfigRegistry = DefaultConfigRegistry,
) : Config {

    private val dataDirectory: String by StringPropertyDelegate(
        namespace = NAMESPACE,
        configRegistry = configRegistry,
    )

    private val animeDataset: String by StringPropertyDelegate(
        namespace = NAMESPACE,
        configRegistry = configRegistry,
        default = "https://raw.githubusercontent.com/manami-project/anime-offline-database/master/anime-offline-database.zip"
    )

    override fun dataDirectory(): Directory {
        val path = Path(dataDirectory)
        check(path.directoryExists()) { "Given value for dataDirectory [$dataDirectory] doesn't exist or is not a directory." }
        return path
    }

    override fun animeDataSet(): URI = URI(animeDataset)

    companion object {
        private const val NAMESPACE = "modb.extension.config"
    }
}