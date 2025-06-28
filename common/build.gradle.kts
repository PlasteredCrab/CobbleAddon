plugins {
    id("cobblemonsnap.base-conventions")
}

architectury {
    common("neoforge", "fabric")
}

repositories {
    maven(url = "https://maven.neoforged.net/releases")
}

dependencies {
    implementation(libs.bundles.kotlin)
    modImplementation(libs.fabric.loader)
    modImplementation(libs.cobblemon)

    // Unit Testing
    testImplementation(libs.bundles.unitTesting)
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        setEvents(listOf("failed"))
        setExceptionFormat("full")
    }
}
