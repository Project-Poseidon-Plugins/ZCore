package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.*
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.Utils.isSelf
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.sendTl

class CommandNick : ZCoreCommand(
    "nick",
    listOf("nickname"),
    "Changes your nickname.",
    "/nick [player] <nickname>",
    "zcore.nick",
    true,
    1,
    2
) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        var target = player
        var nickname = event.args[0]

        if (event.args.size == 2) {
            target = Utils.getPlayerFromUsername(event.args[0])
            nickname = event.args[1]
        }

        val isSelf = player.isSelf(target)
        assert(isSelf || hasPermission(event.sender, "zcore.nick.others"), "noPermission")
        val reset = nickname.equals("reset", true) || nickname.equals(target.name, true)
        if (hasPermission(target, "zcore.nick.color")) nickname = colorize(nickname)

        val user = User.from(target)
        if (reset) {
            user.resetNickname()
        } else {
            if (isSelf) charge(player)
            user.nickname = nickname
        }
        user.updateDisplayName()

        val rawNick = user.getNick()
        if (!isSelf) {
            if (reset) {
                event.sender.sendTl("resetNickOther", target)
            } else {
                event.sender.sendTl("setNickOther", target, "nickname" to rawNick)
            }
        }

        if (reset) {
            target.sendTl("resetNick")
        } else {
            target.sendTl("setNick", "nickname" to rawNick)
        }
    }
}