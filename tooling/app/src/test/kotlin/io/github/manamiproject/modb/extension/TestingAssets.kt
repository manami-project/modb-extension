package io.github.manamiproject.modb.extension

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.config.ConfigRegistry
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
    override fun animeDataSet(): URI = shouldNotBeInvoked()
    override fun clock(): Clock = shouldNotBeInvoked()
}
