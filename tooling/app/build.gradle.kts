plugins {
    alias(libs.plugins.kotlin.jvm)
    application
    jacoco
    alias(libs.plugins.coveralls.jacoco)
}

val githubUsername = "manami-project"

group = "io.github.manamiproject"

repositories {
    mavenCentral()
    maven {
        name = "modb-core"
        url = uri("https://maven.pkg.github.com/$githubUsername/modb-core")
        credentials {
            username = parameter("GH_USERNAME", githubUsername)
            password = parameter("GH_PACKAGES_READ_TOKEN")
        }
    }
    maven {
        name = "modb-test"
        url = uri("https://maven.pkg.github.com/$githubUsername/modb-test")
        credentials {
            username = parameter("GH_USERNAME", githubUsername)
            password = parameter("GH_PACKAGES_READ_TOKEN")
        }
    }
}

dependencies {
    implementation(project(":lib"))
    implementation(platform(kotlin("bom")))
    implementation(libs.modb.core)
    implementation(libs.modb.serde)
    implementation(libs.modb.anidb)
    implementation(libs.modb.anilist)
    implementation(libs.modb.animeplanet)
    implementation(libs.modb.anisearch)
    implementation(libs.modb.kitsu)
    implementation(libs.modb.livechart)
    implementation(libs.modb.mal)
    implementation(libs.modb.notify)
    implementation(libs.guava.jre)
    implementation(libs.logback.classic)
    implementation(libs.commons.text)
    implementation(libs.aws.bedrock.runtime)

    testImplementation(libs.modb.test)
}

kotlin {
    jvmToolchain(JavaVersion.VERSION_21.toString().toInt())
}

application {
    mainClass = "io.github.manamiproject.modb.extension.MainKt"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_21.toString()
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    reports.html.required.set(false)
    reports.junitXml.required.set(true)
    maxParallelForks = Runtime.getRuntime().availableProcessors()
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val javaDoc by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(sourceSets.main.get().allSource)
}

coverallsJacoco {
    reportPath = "${layout.buildDirectory}/reports/jacoco/test/jacocoFullReport.xml"
}

tasks.jacocoTestReport {
    reports {
        html.required.set(false)
        xml.required.set(true)
        xml.outputLocation.set(file("${layout.buildDirectory}/reports/jacoco/test/jacocoFullReport.xml"))
    }
    dependsOn(allprojects.map { it.tasks.named<Test>("test") })
}

fun parameter(name: String, default: String = ""): String {
    val env = System.getenv(name) ?: ""
    if (env.isNotBlank()) {
        return env
    }

    val property = project.findProperty(name) as String? ?: ""
    if (property.isNotEmpty()) {
        return property
    }

    return default
}