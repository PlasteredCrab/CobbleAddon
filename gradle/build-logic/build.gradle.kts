plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    maven("https://maven.architectury.dev/")
    maven("https://maven.fabricmc.net/")
    maven("https://maven.minecraftforge.net/")
}

dependencies {
    implementation(libs.bundles.kotlin)

    implementation(libs.loom)
    implementation(libs.architectury)
    implementation(libs.kotlin)
    implementation(libs.licenser)
}
