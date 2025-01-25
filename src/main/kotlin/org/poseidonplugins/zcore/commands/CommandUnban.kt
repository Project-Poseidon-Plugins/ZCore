package org.poseidonplugins.zcore.commands

import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.data.BanData
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.user.UserMap
import org.poseidonplugins.zcore.util.*

class CommandUnban : ZCoreCommand(
    "unban",
    listOf("pardon"),
    "Unbans a player from the server.",
    "/unban <player/uuid>",
    "zcore.unban",
    minArgs = 1,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val uuid = Utils.getUUIDFromString(event.args[0])
        val name = if (UserMap.isUserKnown(uuid)) User.from(uuid).name else uuid
        assert(BanData.isBanned(uuid), "userNotBanned")

        BanData.unban(uuid)
        event.sender.sendTl("userUnbanned", "user" to name)
    }
}