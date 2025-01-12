import org.jfrog.gradle.plugin.artifactory.dsl.ArtifactoryPluginConvention

plugins {
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.5"
    id("com.jfrog.artifactory") version "5.2.5"
}

repositories {
    mavenLocal()
    mavenCentral()

    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    maven {
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }

    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    api(libs.bundles.topper)
    api(libs.bundles.hscore)
    api(libs.bundles.minelib)
    compileOnly(libs.bundles.nuvotifier)
    compileOnly(libs.spigot)
}

group = "me.hsgamer"
version = "1.0-SNAPSHOT"
description = "Votiful"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val artifactory_contextUrl = (project.findProperty("artifactory_contextUrl") ?: "") as String
val artifactory_release = (project.findProperty("artifactory_release") ?: "") as String
val artifactory_user = (project.findProperty("artifactory_user") ?: "") as String
val artifactory_password = (project.findProperty("artifactory_password") ?: "") as String
val artifactory_release_local = (project.findProperty("artifactory_release_local") ?: "") as String

publishing {
    publications{
        create<MavenPublication>("shadowJar") {
            from(components["shadow"])
        }
    }

    repositories {
        maven {
            name = "artifactory"
            url = uri("$artifactory_contextUrl/$artifactory_release")
            credentials {
                username = artifactory_user
                password = artifactory_password
            }
        }
    }
}

configure<ArtifactoryPluginConvention> {
    publish {
        contextUrl = artifactory_contextUrl
        repository {
            repoKey = artifactory_release_local
            username = artifactory_user
            password = artifactory_password
        }
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}

tasks.processResources {
    expand(mapOf("version" to version))
}

tasks.shadowJar {
    relocate("me.hsgamer.topper", "me.hsgamer.votiful.lib.topper")
    relocate("me.hsgamer.hscore", "me.hsgamer.votiful.lib.hscore")
    relocate("io.github.projectunified.minelib", "me.hsgamer.votiful.lib.minelib")

    archiveClassifier = ""
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}