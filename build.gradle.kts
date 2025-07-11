plugins {
    id("java")
    id("dev.architectury.loom") version("1.10-SNAPSHOT")
    id("architectury-plugin") version("3.4-SNAPSHOT")
    kotlin("jvm") version ("2.0.0")
}

group = "com.cobblemon.eclipse"
version = "1.0.0-SNAPSHOT"

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    silentMojangMappingsLicense()
}

repositories {
    mavenCentral()
    maven(url = "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.impactdev.net/repository/development/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx/maven")
}

dependencies {
    minecraft("net.minecraft:minecraft:1.21.1")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:0.16.5")

    modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:0.104.0+1.21.1")
    modImplementation(fabricApi.module("fabric-command-api-v2", "0.104.0+1.21.1"))

    modImplementation("net.fabricmc:fabric-language-kotlin:1.12.3+kotlin.2.0.21")
    modImplementation("com.cobblemon:fabric:1.6.0+1.21.1-SNAPSHOT")


    //val cloudVersion = "2.0.0-beta.2"
    //modImplementation("org.incendo:cloud-fabric:$cloudVersion")
    //modRuntimeOnly("org.incendo:cloud-fabric:$cloudVersion")
    //include("org.incendo:cloud-fabric:$cloudVersion")

    //implementation("net.impactdev.impactor.api:economy:5.3.0")

    compileOnly("net.impactdev.impactor.api:economy:5.3.4")
    modRuntimeOnly(fileTree("libs") { include("*.jar") })

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}


tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand(project.properties)
    }
}