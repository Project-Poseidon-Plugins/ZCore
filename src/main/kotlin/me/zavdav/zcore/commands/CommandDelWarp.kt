package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.data.Warps
import me.zavdav.zcore.util.assertOrSend
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender

class CommandDelWarp : AbstractCommand(
    "delwarp",
    "Deletes the specified warp.",
    "/delwarp <name>",
    "zcore.delwarp",
    minArgs = 1,
    maxArgs = 1
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        var warpName = args[0]
        sender.assertOrSend("warpNotFound", warpName) { Warps.warpExists(warpName) }

        warpName = Warps.getWarpName(warpName)
        Warps.removeWarp(warpName)
        sender.sendTl("deletedWarp", warpName)
    }
}