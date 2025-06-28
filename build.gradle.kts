plugins {
    id("cobblemonsnap.root-conventions")
}

version = "${project.property("mod_version")}+${project.property("mc_version")}"
val isSnapshot = project.property("snapshot")?.equals("true") ?: false

