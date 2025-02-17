package org.poseidonplugins.zcore.user

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.data.Punishments
import org.poseidonplugins.zcore.data.UserData
import org.poseidonplugins.zcore.hooks.permissions.PermissionHandler
import org.poseidonplugins.zcore.util.broadcastTl
import org.poseidonplugins.zcore.util.formatString
import org.poseidonplugins.zcore.util.kick
import org.poseidonplugins.zcore.util.sendTl
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

class User private constructor(uuid: UUID) : UserData(uuid) {

    companion object {
        fun from(player: Player, register: Boolean = true): User =
            from(player.uniqueId, register)

        fun from(uuid: UUID, register: Boolean = true): User {
            if (!UserMap.isUserKnown(uuid)) UserMap.knownUsers.add(uuid)
            if (uuid in UserMap.userMap.keys) return UserMap.userMap[uuid]!!

            val user = User(uuid)
            if (register) UserMap.userMap[uuid] = user
            return user
        }
    }

    enum class TeleportType {
        TPA, TPAHERE
    }

    val name: String
        get() = when (isOnline) {
            true -> player.name
            false -> username
        }

    val isOnline: Boolean
        get() = uuid in Bukkit.getOnlinePlayers().map { player -> player.uniqueId }

    val player: Player
        get() = Bukkit.getOnlinePlayers().first { player -> player.uniqueId == uuid }

    val prefix: String
        get() = PermissionHandler.getPrefix(this)

    val suffix: String
        get() = PermissionHandler.getSuffix(this)

    var isAfk: Boolean = false

    var replyTo: Player? = null

    var tpRequest: Pair<Player, TeleportType>? = null

    var isInvSee: Boolean = false

    var savedInventory: Array<ItemStack>? = null

    var cachedPlayTime: Long = playTime

    fun getNick(): String {
        val sb = StringBuilder()
        if (player.isOp) sb.append(Config.operatorColor)
        if (nickname != username) sb.append(Config.nickPrefix)
        sb.append(nickname)
        return sb.toString()
    }

    fun getDisplayName(useNick: Boolean): String {
        val nickname = if (useNick) getNick() else username
        val displayName = formatString(Config.displayNameFormat,
            "prefix" to prefix, "nickname" to nickname, "suffix" to suffix)
        return "§f${displayName.trim()}§f"
    }

    fun updateDisplayName() {
        player.displayName = getDisplayName(true)
    }

    fun setInactive() {
        if (!isOnline) return
        isAfk = true
        broadcastTl("nowAfk", player)
    }

    fun updateActivity() {
        if (!isOnline) return
        if (isAfk) {
            isAfk = false
            broadcastTl("noLongerAfk", player)
        }
        lastSeen = LocalDateTime.now()
    }

    fun updatePlayTime() {
        playTime = cachedPlayTime + Duration.between(lastJoin, LocalDateTime.now()).toMillis()
    }

    fun checkIsAfk() {
        if (!isOnline) return

        if (!isAfk && Duration.between(lastSeen, LocalDateTime.now()).seconds >= Config.afkTime) {
            setInactive()
        }
        if (isAfk && Duration.between(lastSeen, LocalDateTime.now()).seconds >= Config.afkKickTime &&
            !hasPermission(player, "zcore.afk.kick.exempt"))
        {
            player.kick("afkKickReason")
        }
    }

    fun checkIsMuted(): Boolean {
        if (!isOnline) return false

        if (Punishments.isMuted(uuid)) {
            val mute = Punishments.getMute(uuid) ?: return false
            when (mute.until == null) {
                true -> player.sendTl("permaMuted", "reason" to mute.reason)
                false -> player.sendTl("tempMuted",
                    "datetime" to mute.until.truncatedTo(ChronoUnit.MINUTES),
                    "reason" to mute.reason
                )
            }
            return true
        }
        return false
    }

    fun checkKitCooldowns() {
        val kitCooldowns = kitCooldowns.toMutableMap()
        kitCooldowns.entries.removeIf { LocalDateTime.now().isAfter(it.value) }
        this.kitCooldowns = kitCooldowns
    }
}