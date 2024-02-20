package de.dasshorty.codebuddy.welcome

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.utils.FileUpload
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.geom.Ellipse2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.net.URL
import javax.imageio.ImageIO

class WelcomeListener : ListenerAdapter() {

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {

        val member = event.member

        val welcomeChannel = event.guild.getTextChannelById("1209571111394746438")!!

        val finalImage = encodeToByteArray(
            drawImage(member.effectiveName, member.effectiveAvatarUrl, event.guild.memberCount)
        )
        welcomeChannel.sendMessage(member.asMention).addFiles(FileUpload.fromData(finalImage, "welcome.png")).queue()

    }

    private fun encodeToByteArray(image: BufferedImage): ByteArray {
        val outputStream = ByteArrayOutputStream()
        ImageIO.write(image, "png", outputStream)
        return outputStream.toByteArray()
    }

    private fun drawImage(userName: String, avatarUrl: String, memberCount: Int): BufferedImage {
        val width = 1920
        val height = 1080

        // Create buffered image with desired width and height
        val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

        // Get graphics context from the image
        val graphics = bufferedImage.createGraphics()

        // Set rendering hints for better text quality
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)


        val avatar = ImageIO.read(URL(avatarUrl))

        // Hintergrund zeichnen
        graphics.color = Color.decode("#262626")
        graphics.fillRect(0, 0, width, height)

        // Set font and color for the text
        graphics.color = Color.WHITE
        val font = Font("Arial", Font.BOLD, 75)
        graphics.font = font

        // Draw first line of text
        val text1 = "Willkommen $userName!"
        val text1Width = graphics.fontMetrics.stringWidth(text1)
        graphics.drawString(text1, (width - text1Width) / 2, 700)

        // Draw second line of text
        val text2 = "$memberCount Mitglieder auf dem Discord"
        val text2Width = graphics.fontMetrics.stringWidth(text2)
        graphics.drawString(text2, (width - text2Width) / 2, 900)

        graphics.clip(Ellipse2D.Float(735f, 115f, 450f, 450f))
        graphics.drawImage(avatar, 735, 115, 450, 450, null)



        return bufferedImage
    }


}