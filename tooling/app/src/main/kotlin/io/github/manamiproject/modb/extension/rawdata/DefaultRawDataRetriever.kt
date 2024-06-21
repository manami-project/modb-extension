package io.github.manamiproject.modb.extension.rawdata

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.core.extensions.readFile
import io.github.manamiproject.modb.core.extensions.regularFileExists
import io.github.manamiproject.modb.core.extensions.writeToFile
import io.github.manamiproject.modb.extension.config.Config

/**
 * Retrieves raw data.
 * First it checks if the raw data is available in a local file.
 * If that is not the case it will download the data from the respective [MetaDataProviderConfig] and save it as a file.
 * @since 1.0.0
 * @property appConfig Application specific configuration.
 * @property metaDataProviderConfig Configuration for a specific meta data provider.
 * @property downloader A downloader that allows to download the raw data from the meta data provider. Should correspond with the [metaDataProviderConfig].
 */
class DefaultRawDataRetriever(
    private val appConfig: Config,
    private val metaDataProviderConfig: MetaDataProviderConfig,
    private val downloader: Downloader,
) : RawDataRetriever {

    override suspend fun retrieveRawData(id: AnimeId): String {
        val rawFile = appConfig.rawFilesDirectory(metaDataProviderConfig).resolve("$id.${metaDataProviderConfig.fileSuffix()}")

        if (!rawFile.regularFileExists()) {
            downloader.download(id).writeToFile(rawFile)
        }

        return rawFile.readFile()
    }
}