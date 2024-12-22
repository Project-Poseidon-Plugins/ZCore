package org.betamc.core.commands

import org.betamc.core.config.Property
import org.betamc.core.player.PlayerMap
import org.betamc.core.util.format
import org.betamc.core.util.formatError
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.commandapi.sendMessage

class CommandSetHome : Command(
    "sethome",
    listOf("sh"),
    "Sets a home at your current location.",
    "/sethome <name>",
    "bmc.sethome",
    true,
    maxArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        var homeName = "main"
        if (event.args.isNotEmpty()) {
            if (!event.args[0].matches("^[a-zA-Z0-9_-]+$".toRegex())) {
                sendMessage(event.sender, formatError("invalidHomeName"))
                return
            }
            homeName = event.args[0]
        }

        val bmcPlayer = PlayerMap.getPlayer(event.sender as Player)
        val limit = Property.MULTIPLE_HOMES.toUInt()
        val homeCount = bmcPlayer.getHomes().size

        if (!hasPermission(event.sender, "bmc.sethome.unlimited")) {
            if (!hasPermission(event.sender, "bmc.sethome.multiple")) {
                if (homeCount >= 1) {
                    sendMessage(event.sender, formatError("homeLimitSingle"))
                    return
                }
            } else if (homeCount >= limit) {
                sendMessage(event.sender, formatError("homeLimitMultiple",
                    "amount" to limit))
                return
            }
        }

        if (bmcPlayer.homeExists(homeName)) {
            sendMessage(event.sender, formatError("homeAlreadyExists"))
            return
        }

        bmcPlayer.addHome(homeName, (event.sender as Player).location)
        sendMessage(event.sender, format("homeSet", "home" to homeName))
    }
}