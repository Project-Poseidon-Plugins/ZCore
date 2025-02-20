package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Punishments
import me.zavdav.zcore.util.Utils
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.sendTl
import me.zavdav.zcore.util.tl
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.joinArgs
import java.time.LocalDateTime
import java.util.regex.Pattern

class CommandBanIP : ZCoreCommand(
    "banip",
    listOf("ipban"),
    "Bans an IP address from the server.",
    "/banip <player|ip> [duration] [reason]",
    "zcore.banip",
    minArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val ip =
            if (Utils.IPV4_PATTERN.matcher(event.args[0]).matches()) {
                event.args[0]
            } else {
                val player = Utils.getPlayerFromString(event.args[0])
                player.address.address.hostAddress
            }

        assert(event.sender !is Player || (event.sender as Player).address.address.hostAddress != ip, "cannotBanSelf")
        val subArgs = joinArgs(event.args , 1, event.args.size)
        val matcher = Pattern.compile("^${Utils.TIME_PATTERN.pattern()}").matcher(subArgs)
        val sb = StringBuilder()
        var end = 0

        while (matcher.find() && matcher.start() == end) {
            sb.append(subArgs, matcher.start(), matcher.end())
            end = matcher.end()
        }

        val duration = sb.toString()
        val reason = subArgs.substring(end)

        when (duration.length) {
            0 -> when (reason.length) {
                0 -> {
                    Punishments.banIP(ip)
                    event.sender.sendTl("bannedIp",
                        "ip" to ip,
                        "reason" to tl("banReason"))
                }
                else -> {
                    Punishments.banIP(ip, reason)
                    event.sender.sendTl("bannedIp", "ip" to ip, "reason" to reason)
                }
            }
            else -> when (reason.length) {
                0 -> {
                    val until = Utils.parseDateDiff(duration)
                    Punishments.banIP(ip, until)
                    event.sender.sendTl("tempBannedIp",
                        "ip" to ip,
                        "duration" to Utils.formatDateDiff(LocalDateTime.now(), until),
                        "reason" to tl("banReason"))
                }
                else -> {
                    val until = Utils.parseDateDiff(duration)
                    Punishments.banIP(ip, until, reason)
                    event.sender.sendTl("tempBannedIp",
                        "ip" to ip,
                        "duration" to Utils.formatDateDiff(LocalDateTime.now(), until),
                        "reason" to reason)
                }
            }
        }
    }
}