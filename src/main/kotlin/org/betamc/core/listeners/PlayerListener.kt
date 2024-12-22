package org.betamc.core.listeners

import org.betamc.core.config.Property
import org.betamc.core.data.BanData
import org.betamc.core.data.SpawnData
import org.betamc.core.player.PlayerMap
import org.betamc.core.util.Utils
import org.betamc.core.util.Utils.safeSubstring
import org.betamc.core.util.format
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.poseidonplugins.commandapi.hasPermission
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class PlayerListener : Listener {

    @EventHandler(priority = Event.Priority.Highest)
    fun onPlayerLogin(event: PlayerLoginEvent) {
        val bmcPlayer = PlayerMap.getPlayer(event.player)

        if (BanData.isIPBanned(event.address.hostAddress)) {
            val ip = event.address.hostAddress
            val ipBan = BanData.getIPBan(ip)!!
            if (!ipBan.uuids.contains(event.player.uniqueId)) ipBan.addUUID(event.player.uniqueId)
            when (ipBan.until == null) {
                true -> event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                    format(Property.IPBAN_PERMANENT, "reason" to ipBan.reason).safeSubstring(0, 99))
                false -> event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                    format(Property.IPBAN_TEMPORARY,
                        "datetime" to ipBan.until.truncatedTo(ChronoUnit.MINUTES),
                        "reason" to ipBan.reason).safeSubstring(0, 99))
            }
        } else if (bmcPlayer.isBanned) {
            val ban = BanData.getBan(event.player.uniqueId)!!
            when (ban.until == null) {
                true -> event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                    format(Property.BAN_PERMANENT, "reason" to ban.reason).safeSubstring(0, 99))
                false -> event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                    format(Property.BAN_TEMPORARY,
                        "datetime" to ban.until.truncatedTo(ChronoUnit.MINUTES),
                        "reason" to ban.reason).safeSubstring(0, 99))
            }
        }
    }

    @EventHandler(priority = Event.Priority.Highest)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val isFirstJoin = !PlayerMap.isPlayerKnown(event.player.uniqueId)
        val bmcPlayer = PlayerMap.getPlayer(event.player)
        val spawn = SpawnData.getSpawn(event.player.world)

        if (isFirstJoin) {
            bmcPlayer.firstJoin = LocalDateTime.now()
            if (spawn != null) event.player.teleport(spawn)
        }

        bmcPlayer.updateOnJoin(event.player.name)
        Utils.updateVanishedPlayers()

        if (Property.MOTD.toString().isNotEmpty() &&
            hasPermission(event.player, "bmc.motd")) {
            event.player.performCommand("motd")
        }
    }

    @EventHandler(priority = Event.Priority.Highest)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val bmcPlayer = PlayerMap.getPlayer(event.player)

        bmcPlayer.updateOnQuit()
        if (bmcPlayer.savedInventory != null) {
            event.player.inventory.contents = bmcPlayer.savedInventory
            bmcPlayer.savedInventory = null
        }
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Highest)
    fun onPlayerDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) return
        val player = event.entity as Player
        if (PlayerMap.getPlayer(player).isGod) {
            player.fireTicks = 0
            player.remainingAir = player.maximumAir
            event.isCancelled = true
        }
    }

    @EventHandler(priority = Event.Priority.Highest)
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        event.respawnLocation = SpawnData.getSpawn(event.respawnLocation.world) ?: return
    }
}