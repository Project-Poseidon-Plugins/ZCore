package org.betamc.core.commands

import org.betamc.core.config.Language
import org.betamc.core.player.PlayerMap
import org.betamc.core.util.Utils
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.sendMessage

class CommandInvSee : Command(
    "invsee",
    description = "Shows the contents of another player's inventory.",
    usage = "/invsee <player>",
    permission = "bmc.invsee",
    isPlayerOnly = true,
    maxArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        val bmcPlayer = PlayerMap.getPlayer(player)

        if (event.args.isNotEmpty()) {
            val target = Utils.getPlayerFromUsername(event.args[0])
            if (target == null) {
                sendMessage(event.sender, Utils.format(Language.PLAYER_NOT_FOUND, event.args[0]))
                return
            }
            if (bmcPlayer.savedInventory == null) bmcPlayer.savedInventory = player.inventory.contents
            player.inventory.contents = PlayerMap.getPlayer(target).savedInventory ?: target.inventory.contents
            sendMessage(event.sender, Utils.format(Language.INVSEE_SUCCESS, target.name))
        } else {
            if (bmcPlayer.savedInventory != null) {
                player.inventory.contents = bmcPlayer.savedInventory
                bmcPlayer.savedInventory = null
            }
            sendMessage(event.sender, Language.INVSEE_RESTORED)
        }
    }
}