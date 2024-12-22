package org.betamc.core.commands

import org.betamc.core.data.SpawnData
import org.betamc.core.util.Utils
import org.betamc.core.util.format
import org.betamc.core.util.formatError
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.sendMessage

class CommandSpawn : Command(
    "spawn",
    description = "Teleports you to the server spawn.",
    usage = "/spawn",
    permission = "bmc.spawn",
    isPlayerOnly = true,
    maxArgs = 0,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        var loc = player.world.spawnLocation
        loc.x = loc.blockX + 0.5
        loc.z = loc.blockZ + 0.5
        loc = SpawnData.getSpawn(player.world) ?: loc
        try {
            loc.y = Utils.getSafeHeight(loc).toDouble()
        } catch (e: Exception) {
            sendMessage(event.sender, formatError("unsafeDestination"))
            return
        }
        player.teleport(loc)
        sendMessage(player, format("teleportedToSpawn",
            "world" to loc.world.name))
    }
}