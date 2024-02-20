package de.dasshorty.codebuddy.voice

import com.mongodb.client.model.Filters
import de.dasshorty.codebuddy.database.DefaultSubscriber
import de.dasshorty.codebuddy.database.MongoConnection
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.concrete.Category
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.TimeUnit

class VoiceManager : ListenerAdapter() {

    fun cleanUpDb(guild: Guild) {

        MongoConnection.database.getCollection("temp-voice", TempVoice::class.java).find()
            .subscribe(DefaultSubscriber(100L) {

                if (it == null)
                    return@DefaultSubscriber

                val voiceChannelById = guild.getVoiceChannelById(it.channelId)

                if (voiceChannelById != null && voiceChannelById.members.isEmpty()) {
                    deleteVoiceChannel(voiceChannelById)
                    MongoConnection.database.getCollection("temp-voice", TempVoice::class.java)
                        .deleteOne(Filters.eq("channelId", voiceChannelById))
                        .subscribe(DefaultSubscriber(1L) {})
                }

            })

    }

    override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {

        if (event.channelLeft != null)
            handleChannelLeft(event.channelLeft as VoiceChannel, event.member)

        if (event.channelJoined == null)
            return

        if (event.channelJoined!!.id != "1209564907696029696")
            return

        createVoiceChannel(event.member)
    }

    private fun deleteVoiceChannel(channel: VoiceChannel) {
        if (channel.parentCategory!!.channels.size == 1)
            channel.parentCategory!!.delete().queueAfter(1, TimeUnit.SECONDS)

        channel.delete().queue()
    }

    private fun handleChannelLeft(channel: VoiceChannel, member: Member) {

        val defaultSubscriber = DefaultSubscriber<TempVoice>(1L) { tempVoice ->

            if (tempVoice == null)
                return@DefaultSubscriber

            if (tempVoice.channelId != channel.id)
                return@DefaultSubscriber

            if (channel.members.isNotEmpty())
                return@DefaultSubscriber

            deleteVoiceChannel(channel)

            MongoConnection.database.getCollection("temp-voice").deleteOne(Filters.eq("channelOwner", member.id))
                .subscribe(DefaultSubscriber(1L) {
                })
        }
        MongoConnection.database.getCollection("temp-voice", TempVoice::class.java)
            .find(Filters.eq("channelOwner", member.id)).limit(1).first()
            .subscribe(defaultSubscriber)

    }

    private fun createCategory(guild: Guild): Category {
        return guild.createCategory("🔊|Voice").setPosition(2).complete()
    }

    private fun getCategoryOrNull(guild: Guild): Category? {
        val categoriesByName = guild.getCategoriesByName("🔊|Voice", false)
        if (categoriesByName.isEmpty())
            return null

        return categoriesByName[0]
    }

    private fun createVoiceChannel(member: Member) {

        var category = getCategoryOrNull(member.guild)
        if (category == null)
            category = createCategory(member.guild)

        category.createVoiceChannel("🔊 ${member.effectiveName}").queue {
            moveMember(it, member)
            MongoConnection.database.getCollection("temp-voice", TempVoice::class.java)
                .insertOne(TempVoice(member.id, it.id)).subscribe(DefaultSubscriber(1L) {})
        }

    }

    private fun moveMember(newChannel: VoiceChannel, member: Member) {
        if (member.voiceState == null || !member.voiceState?.inAudioChannel()!!)
            return
        member.guild.moveVoiceMember(member, newChannel).queue()
    }

}