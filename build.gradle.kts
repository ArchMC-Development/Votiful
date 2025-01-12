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

publishing {
    publications{
        create<MavenPublication>("shadowJar") {
            from(components["shadow"])
        }
    }

    repositories {
        maven {
            name = "artifactory"
            url = uri("${property("artifactory_contextUrl")}/${property("artifactory_release")}")
            credentials {
                username = property("artifactory_user") as String
                password = property("artifactory_password") as String
            }
        }
    }
}

configure<ArtifactoryPluginConvention> {
    publish {
        contextUrl = property("artifactory_contextUrl") as String
        repository {
            repoKey = property("artifactory_release_local") as String
            username = property("artifactory_user") as String
            password = property("artifactory_password") as String
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