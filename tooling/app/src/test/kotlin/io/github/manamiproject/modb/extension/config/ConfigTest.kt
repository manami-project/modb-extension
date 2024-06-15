package io.github.manamiproject.modb.extension.config

import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.extensions.Directory
import io.github.manamiproject.modb.test.shouldNotBeInvoked
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.net.URI
import java.time.Clock

internal class ConfigTest {

    @Nested
    inner class ClockTests {

        @Test
        fun `default value is current system local zone`() {
            // given
            val systemDefaultZone = Clock.systemDefaultZone()
            val config = object : Config {
                override fun dataDirectory(): Directory = shouldNotBeInvoked()
                override fun rawFilesDirectory(): Directory = shouldNotBeInvoked()
                override fun rawFilesDirectory(metaDataProviderConfig: MetaDataProviderConfig): Directory = shouldNotBeInvoked()
                override fun animeDataSet(): URI = shouldNotBeInvoked()
            }

            // when
            val result = config.clock()

            // then
            assertThat(result).isEqualTo(systemDefaultZone)
        }
    }
}