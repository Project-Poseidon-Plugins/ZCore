package org.poseidonplugins.zcore.commands

import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.joinArgs
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.util.broadcastConfTl

class CommandBroadcast : ZCoreCommand(
    "broadcast",
    listOf("bc"),
    "Broadcasts a message to all players.",
    "/broadcast <message>",
    "zcore.broadcast",
    minArgs = 1
) {

    override fun execute(event: CommandEvent) {
        broadcastConfTl(Config.broadcastFormat, "message" to joinArgs(event.args, 0))
    }
}