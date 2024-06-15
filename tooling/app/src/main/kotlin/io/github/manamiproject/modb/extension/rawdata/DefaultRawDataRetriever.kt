package io.github.manamiproject.modb.extension.rawdata

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.core.extensions.readFile
import io.github.manamiproject.modb.core.extensions.regularFileExists
import io.github.manamiproject.modb.core.extensions.writeToFile
import io.github.manamiproject.modb.extension.config.Config

class DefaultRawDataRetriever(
    private val appConfig: Config,
    private val config: MetaDataProviderConfig,
    private val downloader: Downloader,
) : RawDataRetriever {

    override suspend fun retrieveRawData(id: AnimeId): String {
        val rawFile = appConfig.rawFilesDirectory(config).resolve("$id.${config.fileSuffix()}")

        if (!rawFile.regularFileExists()) {
            downloader.download(id).writeToFile(rawFile)
        }

        return rawFile.readFile()
    }
}