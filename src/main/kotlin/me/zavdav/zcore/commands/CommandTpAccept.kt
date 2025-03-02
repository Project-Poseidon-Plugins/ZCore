package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Economy
import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.Delay
import me.zavdav.zcore.util.NoFundsException
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTpAccept : AbstractCommand(
    "tpaccept",
    "Accepts your current teleport request.",
    "/tpaccept",
    "zcore.tpaccept",
    maxArgs = 0,
    aliases = listOf("tpyes")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        val user = User.from(player)
        val request = user.tpRequest
        assert(request != null, "noTpRequest")

        val target = request!!.first
        val requestType = request.second
        val delay = Config.teleportDelay
        user.tpRequest = null
        player.sendTl("acceptedTpRequest", target)
        target.sendTl("otherAcceptedRequest", player)

        if (requestType == User.TeleportType.TPA) {
            if (delay > 0) {
                target.sendTl("commencingTeleport", "location" to player.name, "delay" to delay)
                target.sendTl("doNotMove")
            }
            Delay(target, delay) {
                try {
                    charge(target)
                    target.teleport(player)
                } catch (e: NoFundsException) {
                    for (message in e.messages) {
                        target.sendMessage(message)
                    }
                }
            }
        } else {
            if (delay > 0) {
                player.sendTl("commencingTeleport", "location" to target.name, "delay" to delay)
                player.sendTl("doNotMove")
            }
            Delay(player, delay) {
                try {
                    charge(target)
                    player.teleport(target)
                } catch (e: NoFundsException) {
                    for (message in e.messages) {
                        target.sendMessage(message)
                    }
                }
            }
        }
    }

    override fun charge(player: Player) {
        val cost = Config.getCommandCost(this)
        if (cost > 0.0 && !player.isAuthorized("$permission.charge.bypass")) {
            Economy.subtractBalance(player.uniqueId, cost)
            player.sendTl("tpRequestCharge", "amount" to Economy.formatBalance(cost))
        }
    }
}