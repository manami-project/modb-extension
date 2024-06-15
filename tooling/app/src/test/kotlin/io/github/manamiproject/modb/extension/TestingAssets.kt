package io.github.manamiproject.modb.extension

import io.github.manamiproject.modb.core.config.*
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.core.extensions.Directory
import io.github.manamiproject.modb.extension.config.Config
import io.github.manamiproject.modb.test.shouldNotBeInvoked
import java.net.URI
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime

internal object TestDownloader: Downloader {
    override suspend fun download(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String = shouldNotBeInvoked()
}

internal object TestConfigRegistry: ConfigRegistry {
    override fun boolean(key: String): Boolean = shouldNotBeInvoked()
    override fun double(key: String): Double = shouldNotBeInvoked()
    override fun <T : Any> list(key: String): List<T> = shouldNotBeInvoked()
    override fun localDate(key: String): LocalDate = shouldNotBeInvoked()
    override fun localDateTime(key: String): LocalDateTime = shouldNotBeInvoked()
    override fun long(key: String): Long = shouldNotBeInvoked()
    override fun <T : Any> map(key: String): Map<String, T> = shouldNotBeInvoked()
    override fun offsetDateTime(key: String) = shouldNotBeInvoked()
    override fun string(key: String): String = shouldNotBeInvoked()
}

internal object TestConfig: Config {
    override fun dataDirectory(): Directory = shouldNotBeInvoked()
    override fun rawFilesDirectory(): Directory = shouldNotBeInvoked()
    override fun rawFilesDirectory(metaDataProviderConfig: MetaDataProviderConfig): Directory = shouldNotBeInvoked()
    override fun animeDataSet(): URI = shouldNotBeInvoked()
    override fun clock(): Clock = shouldNotBeInvoked()
}

internal object TestMetaDataProviderConfig: MetaDataProviderConfig {
    override fun fileSuffix(): FileSuffix = shouldNotBeInvoked()
    override fun hostname(): Hostname = shouldNotBeInvoked()
    override fun buildAnimeLink(id: AnimeId): URI = shouldNotBeInvoked()
    override fun buildDataDownloadLink(id: String): URI = shouldNotBeInvoked()
    override fun extractAnimeId(uri: URI): AnimeId = shouldNotBeInvoked()
    override fun isTestContext(): Boolean = true
}
