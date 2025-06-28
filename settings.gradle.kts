rootProject.name = "cobblemonsnap"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()

        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net/")
    }

    includeBuild("gradle/build-logic")
}

listOf(
    "common",
    "neoforge",
    "fabric"
).forEach { include(it) }
