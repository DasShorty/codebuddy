package de.dasshorty.codebuddy.twitch

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential
import com.github.twitch4j.TwitchClient
import com.github.twitch4j.TwitchClientBuilder
import com.github.twitch4j.events.ChannelGoLiveEvent
import com.github.twitch4j.events.ChannelGoOfflineEvent
import com.github.twitch4j.helix.domain.Stream
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel
import net.dv8tion.jda.api.interactions.components.buttons.Button
import java.awt.Color
import java.time.Instant
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class TwitchBot(private val guild: Guild) {

    private val client: TwitchClient = TwitchClientBuilder
        .builder()
        .withDefaultAuthToken(OAuth2Credential("twitch", System.getenv("TWITCH_ACCESS_TOKEN")))
        .withEnableHelix(true)
        .withEnableChat(true)
        .build()


    private lateinit var announcementId: String
    private lateinit var schedule: ScheduledFuture<*>

    init {
        this.loadTwitchChannel()
        this.loadEvents()
    }

    private fun getNewsChannel(): NewsChannel? {
        return guild.getNewsChannelById("1209124564584632440")
    }

    private fun loadEvents() {

        this.client.eventManager.onEvent(ChannelGoOfflineEvent::class.java) {

            val channel = this.getNewsChannel()
            if (channel == null) {
                println("Channel is null | id not valid")
                return@onEvent
            }

            this.schedule.cancel(true)

            channel.history.getMessageById(this.announcementId)?.delete()?.queue()
            this.announcementId = ""

        }

        this.client.eventManager.onEvent(ChannelGoLiveEvent::class.java) { event ->

            println("Stream started from DasShorty")

            val stream = event.stream
            val channelName = event.channel.name

            val url = "https://twitch.tv/$channelName"

            val channel = this.getNewsChannel()

            if (channel == null) {
                println("Channel is null | id not valid")
                return@onEvent
            }

            channel.sendMessage("<@&1209123147412930571>")
                .addEmbeds(
                    EmbedBuilder()
                        .setAuthor(channelName, url)
                        .setTitle(stream.title)
                        .setDescription("$channelName ist Live gegangen! Heute wird ${stream.gameName} gestreamt.")
                        .setThumbnail(stream.thumbnailUrl)
                        .setTimestamp(Instant.now())
                        .setColor(Color.decode("#a970ff"))
                        .setFooter("Twitch", "https://cdn.dasshorty.de/twitch.png")
                        .build()
                ).addActionRow(Button.link(url, "Auf Twitch anschauen")).flatMap(Message::crosspost).queue {
                    this.announcementId = it.id
                    println("Announcement has been send into channel")
                    this.scheduleUpdate(it.id, stream)
                }
        }
    }

    private fun scheduleUpdate(messageId: String, stream: Stream) {

        this.schedule = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({

            this.updateThumbnail(messageId, stream)

        }, 0, 10, TimeUnit.SECONDS)

    }

    private fun updateThumbnail(messageId: String, stream: Stream) {

        val channel = this.getNewsChannel()!!
        val messageById = channel.history.getMessageById(messageId)!!

        val embed = messageById.embeds[0]

        val messageEmbed = EmbedBuilder(embed)
            .setThumbnail(stream.thumbnailUrl)
            .build()

        messageById.editMessageEmbeds(messageEmbed).queue()

    }

    private fun loadTwitchChannel() {

        this.client.chat.joinChannel("dasshortyy")
        this.client.clientHelper.enableStreamEventListener("dasshortyy")
        this.client.chat.sendMessage("dasshortyy", "Der CodeBuddy ist dem Chat beigetreten!")


    }

}