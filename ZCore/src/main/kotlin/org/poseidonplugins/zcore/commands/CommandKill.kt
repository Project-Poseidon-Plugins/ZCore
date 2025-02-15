package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.Utils.isSelf
import org.poseidonplugins.zcore.util.sendTl

class CommandKill : ZCoreCommand(
    "kill",
    description = "Kills a player.",
    usage = "/kill [player]",
    permission = "zcore.kill",
    isPlayerOnly = true,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        var target = event.sender as Player
        if (event.args.isNotEmpty()) {
            target = Utils.getPlayerFromUsername(event.args[0])
        }
        target.health = 0

        val isSelf = (event.sender as Player).isSelf(target)
        if (isSelf) {
            event.sender.sendTl("killed")
        } else {
            event.sender.sendTl("killedOther", target)
        }
    }
}