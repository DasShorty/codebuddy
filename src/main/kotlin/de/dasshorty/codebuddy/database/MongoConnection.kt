package de.dasshorty.codebuddy.database

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.MongoDatabase


object MongoConnection {

    lateinit var database: MongoDatabase

    fun connect() {
        val connString = ConnectionString(
            System.getenv("MONGO_URL")
        )
        val settings = MongoClientSettings.builder()
            .applyConnectionString(connString)
            .retryWrites(true)
            .build()
        val client = MongoClients.create(settings)


        this.database = client.getDatabase("codebuddy")

        Runtime.getRuntime().addShutdownHook(Thread { client.close() })
    }
}