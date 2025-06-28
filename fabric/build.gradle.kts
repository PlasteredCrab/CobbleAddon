plugins {
    id("cobblemonsnap.platform-conventions")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

dependencies {
    implementation(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    "developmentFabric"(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }

    modImplementation(libs.fabric.loader)
    modImplementation(libs.cobblemon.fabric)
    modApi(libs.fabric.api)
    modApi(libs.bundles.fabric)

    listOf(
        libs.bundles.kotlin,
    ).forEach {
        runtimeOnly(it)
    }

}

tasks.withType<ProcessResources> {
    filesMatching("fabric.mod.json") {
        expand(
            "id" to properties["mod_id"],
            "version" to properties["mod_version"],
            "description" to properties["mod_description"],
            "name" to properties["display_name"]
        )
    }
}
