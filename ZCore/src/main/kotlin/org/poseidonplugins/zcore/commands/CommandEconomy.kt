package org.poseidonplugins.zcore.commands

import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.api.Economy
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.InvalidUsageException
import org.poseidonplugins.zcore.util.*
import org.poseidonplugins.zcore.util.Utils.roundTo

class CommandEconomy : ZCoreCommand(
    "economy",
    listOf("eco"),
    "Modifies a player's balance.",
    "/economy <set|give|take> <player> <amount>",
    "zcore.economy",
    true,
    3,
    3
) {

    override fun execute(event: CommandEvent) {
        val uuid = Utils.getUUIDFromString(event.args[1])
        val name = User.from(uuid).name
        var amount = event.args[2].toDoubleOrNull()?.roundTo(2)
        assert(amount != null && amount >= 0, "invalidAmount")

        when (event.args[0].lowercase()) {
            "set" -> {
                Economy.setBalance(uuid, amount!!)
                event.sender.sendTl("setBalance", "user" to name, "amount" to Economy.formatBalance(amount))
            }
            "give" -> {
                Economy.addBalance(uuid, amount!!)
                event.sender.sendTl("gaveMoney", "user" to name, "amount" to Economy.formatBalance(amount))
            }
            "take" -> {
                if (!Economy.hasEnough(uuid, amount!!)) amount = Economy.getBalance(uuid)
                Economy.subtractBalance(uuid, amount)
                event.sender.sendTl("tookMoney", "user" to name, "amount" to Economy.formatBalance(amount))
            }
            else -> throw InvalidUsageException(this)
        }
    }
}