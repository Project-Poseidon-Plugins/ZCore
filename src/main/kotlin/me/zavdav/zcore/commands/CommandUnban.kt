package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Punishments
import me.zavdav.zcore.user.User
import me.zavdav.zcore.user.UserMap
import me.zavdav.zcore.util.Utils
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.sendTl
import org.poseidonplugins.commandapi.CommandEvent

class CommandUnban : ZCoreCommand(
    "unban",
    listOf("pardon"),
    "Unbans a player from the server.",
    "/unban <player>",
    "zcore.unban",
    minArgs = 1,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val uuid = Utils.getUUIDFromString(event.args[0])
        val name = if (UserMap.isUserKnown(uuid)) User.from(uuid).name else uuid
        assert(Punishments.isBanned(uuid), "userNotBanned", "user" to name)

        Punishments.unban(uuid)
        event.sender.sendTl("unbannedPlayer", "user" to name)
    }
}