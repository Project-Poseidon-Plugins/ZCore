package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Economy
import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assertOrSend
import me.zavdav.zcore.util.getUUIDFromString
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.math.BigDecimal

class CommandPay : AbstractCommand(
    "pay",
    "Sends money to another player.",
    "/pay <player/uuid> <amount>",
    "zcore.pay",
    minArgs = 2,
    maxArgs = 2
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val sending = User.from(sender as Player)
        val receiving = User.from(getUUIDFromString(args[0]))

        val amount = args[1].toBigDecimalOrNull()
        sender.assertOrSend("invalidAmount", args[1]) { amount != null && amount > BigDecimal.ZERO }
        Economy.transferBalance(sending.uuid, receiving.uuid, amount!!)

        sender.sendTl("paidMoney", receiving.name, Economy.formatBalance(amount))
        if (receiving.isOnline) receiving.player.sendTl("receivedMoney",
            Economy.formatBalance(amount), sending.name)
    }
}