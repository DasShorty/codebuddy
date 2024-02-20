package de.dasshorty.codebuddy

import de.dasshorty.codebuddy.database.MongoConnection
import de.dasshorty.codebuddy.voice.VoiceManager
import de.dasshorty.codebuddy.welcome.WelcomeListener
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag

fun main() {

    MongoConnection.connect()

    val builder = JDABuilder.createDefault(System.getenv("BOT_TOKEN"))

    builder.enableIntents(
        GatewayIntent.GUILD_MEMBERS,
        GatewayIntent.GUILD_INVITES,
        GatewayIntent.DIRECT_MESSAGES,
        GatewayIntent.MESSAGE_CONTENT,
        GatewayIntent.GUILD_PRESENCES,
        GatewayIntent.GUILD_VOICE_STATES
    )
    builder.enableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.ONLINE_STATUS, CacheFlag.MEMBER_OVERRIDES)

    val voiceManager = VoiceManager()
    builder.addEventListeners(WelcomeListener(), voiceManager)

    val jda = builder.build().awaitReady()

    voiceManager.cleanUpDb(jda.guilds[0])
}