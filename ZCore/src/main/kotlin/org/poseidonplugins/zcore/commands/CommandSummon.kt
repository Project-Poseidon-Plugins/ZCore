package org.poseidonplugins.zcore.commands

import org.bukkit.entity.CreatureType
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.sendTl

class CommandSummon : ZCoreCommand(
    "summon",
    listOf("spawnmob"),
    "Summons the specified mob at your cursor position.",
    "/summon <mob> [amount]",
    "zcore.summon",
    true,
    1,
    2
) {

    val types: Map<String, CreatureType> = CreatureType.entries.associate { it.name.lowercase() to it }

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        val type = event.args[0].lowercase()
        assert(type in types.keys, "mobNotFound")

        val creatureType = types[type]!!
        var amount = 1
        if (event.args.size == 2) {
            amount = (event.args[1].toIntOrNull() ?: 1).coerceIn(1..255)
        }

        val targetBlock = player.getLastTwoTargetBlocks(null, 100)[0]
        repeat(amount) {
            player.world.spawnCreature(targetBlock.location, creatureType)
        }
        player.sendTl("mobSummoned", "amount" to amount, "mob" to creatureType.name)
    }
}