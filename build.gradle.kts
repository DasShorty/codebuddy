plugins {
    kotlin("jvm") version "2.1.10"
    application
    id("com.github.johnrengelman.shadow") version ("8.1.1")
}

group = "de.dasshorty"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:5.2.3")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:5.3.0")
    implementation("org.mongodb:bson-kotlinx:5.3.1")
    implementation("com.github.twitch4j:twitch4j:1.23.0")
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