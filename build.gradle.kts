
plugins {
    kotlin("jvm") version "1.7.10"
}

group = "de.toby"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://libraries.minecraft.net")
}

dependencies {
    compileOnly("com.mojang:brigadier:1.0.18")
    compileOnly("org.spigotmc:spigot-api:1.19.1-R0.1-SNAPSHOT")
    implementation("net.axay:kspigot:1.19.0")
    implementation("net.kyori:adventure-api:4.11.0")
}

tasks {
    compileJava {
        options.release.set(17)
        options.encoding = "UTF-8"
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }
}