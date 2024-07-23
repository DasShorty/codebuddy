plugins {
    kotlin("jvm") version "1.9.22"
    application
    id("com.github.johnrengelman.shadow") version ("8.1.1")
}

group = "de.dasshorty"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-beta.20")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:4.11.0")
    implementation("org.mongodb:bson-kotlinx:5.1.2")
    implementation("com.github.twitch4j:twitch4j:1.17.0")
}

tasks {
    application {
        mainClass = "de.dasshorty.codebuddy.MainKt"
    }
    compileJava {
        options.encoding = "UTF-8"
    }
}


tasks.withType(Jar::class.java) {
    manifest {
        attributes["Manifest-Version"] = "1.0"
        attributes["Main-Class"] = "de.dasshorty.codebuddy.MainKt"
    }
}
kotlin {
    jvmToolchain(21)
}