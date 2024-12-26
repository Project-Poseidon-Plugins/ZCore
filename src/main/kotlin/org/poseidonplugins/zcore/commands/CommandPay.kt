package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.sendMessage
import org.poseidonplugins.zcore.api.Economy
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.Utils.roundTo
import org.poseidonplugins.zcore.util.format
import org.poseidonplugins.zcore.util.formatError

class CommandPay : Command(
    "pay",
    description = "Sends money to another player.",
    usage = "/pay <player/uuid> <amount>",
    permission = "zcore.pay",
    isPlayerOnly = true,
    minArgs = 2,
    maxArgs = 2,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val sender = PlayerMap.getPlayer(event.sender as Player)
        val receiver = PlayerMap.getPlayer(Utils.getUUIDFromString(event.args[0]))

        val amount = event.args[1].toDoubleOrNull()?.roundTo(2)
        if (amount == null || amount <= 0) {
            sendMessage(event.sender, formatError("invalidAmount"))
            return
        }
        Economy.transferBalance(sender.uuid, receiver.uuid, amount)
        sendMessage(event.sender, format("paidUser",
            "user" to receiver.name, "amount" to Economy.formatBalance(amount)))
        if (receiver.isOnline) sendMessage(receiver.onlinePlayer, format("receivedMoney",
            "player" to sender.name, "amount" to Economy.formatBalance(amount)))
    }
}