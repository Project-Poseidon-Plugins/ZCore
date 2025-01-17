package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.*

class CommandHome : Command(
    "home",
    listOf("h"),
    "Teleports you to your specified home.",
    "/home <name>",
    "zcore.home",
    true,
    1,
    1,
    Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        var zPlayer = PlayerMap.getPlayer(player)
        var homeName = event.args[0]

        if (":" in homeName) {
            assert(hasPermission(event.sender, "zcore.home.others"), "noPermission")
            val strings = event.args[0].split(":", limit = 2)
            assert(strings[1].isNotEmpty(), "noHomeSpecified")

            val uuid = Utils.getUUIDFromUsername(strings[0])
            zPlayer = PlayerMap.getPlayer(uuid)
            homeName = strings[1]
        }

        assert(zPlayer.homeExists(homeName), "homeNotFound")
        val location = zPlayer.getHome(homeName)
        player.teleport(location)

        val finalName = zPlayer.getFinalHomeName(homeName)
        if (player.uniqueId == zPlayer.uuid) {
            event.sender.sendTl("teleportedToHome", "home" to finalName)
        } else {
            event.sender.sendTl("teleportedToHomeOther", "user" to zPlayer.name, "home" to finalName)
        }
    }
}