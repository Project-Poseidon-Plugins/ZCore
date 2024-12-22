package org.betamc.core.commands

import org.betamc.core.data.SpawnData
import org.betamc.core.util.format
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.sendMessage

class CommandSetSpawn : Command(
    "setspawn",
    description = "Sets the world spawn to your current location.",
    usage = "/setspawn [none]",
    permission = "bmc.setspawn",
    isPlayerOnly = true,
    maxArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        val loc = player.location
        if (event.args.size == 1 && event.args[0].equals("none", true)) {
            SpawnData.removeSpawn(loc.world.name)
            sendMessage(player, format("spawnReset", "world" to loc.world.name))

        } else {
            SpawnData.setSpawn(loc.world.name, loc)
            sendMessage(event.sender, format("spawnSet",
                "world" to loc.world.name,
                "coordinates" to "${loc.blockX}, ${loc.blockY}, ${loc.blockZ}"))
        }
    }
}