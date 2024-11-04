plugins {
    kotlin("jvm") version "2.0.21"
    application
    id("com.github.johnrengelman.shadow") version ("8.1.1")
}

group = "de.dasshorty"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:5.2.0")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:5.2.0")
    implementation("org.mongodb:bson-kotlinx:5.2.0")
    implementation("com.github.twitch4j:twitch4j:1.22.0")
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