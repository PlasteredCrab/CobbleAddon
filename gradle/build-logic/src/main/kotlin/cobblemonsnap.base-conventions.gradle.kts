plugins {
    id("java")
    id("java-library")
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("org.cadixdev.licenser")
    kotlin("jvm")
}

group = rootProject.group
version = rootProject.version
description = rootProject.description

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven("https://maven.impactdev.net/repository/development")
    maven("https://maven.parchmentmc.org")
}

architectury {
    minecraft = project.property("mc_version").toString()
}

license {
    header(rootProject.file("HEADER"))
}

loom {
    silentMojangMappingsLicense()
    accessWidenerPath = project(":common").file("src/main/resources/cobblemonsnap.common.accesswidener")
}

dependencies {
    minecraft("net.minecraft:minecraft:${rootProject.property("mc_version")}")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.21:2024.07.28@zip")
    })
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(21)
        options.compilerArgs.add("-Xlint:-processing,-classfile,-serial")
    }
}
