plugins {
    id("cobblemonsnap.platform-conventions")
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

repositories {
    maven(url = "https://thedarkcolour.github.io/KotlinForForge/")
    maven(url = "https://maven.neoforged.net/releases")
}

dependencies {
    neoForge(libs.neoforge)
    implementation(libs.cobblemon.neoforge)
    implementation(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    implementation(libs.neo.kotlin.forge) {
        exclude(group = "net.neoforged.fancymodloader", module = "loader")
    }
    "developmentNeoForge"(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }

    testImplementation(project(":common", configuration = "namedElements"))
}
