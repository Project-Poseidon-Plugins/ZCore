package me.zavdav.zcore.data

import org.bukkit.inventory.ItemStack
import java.math.BigDecimal
import java.util.UUID

interface Punishment {
    val issuer: UUID?
    val timeIssued: Long
    val duration: Long?
    val reason: String
    var pardoned: Boolean
}

class Mute(
    val uuid: UUID,
    override val issuer: UUID?,
    override val timeIssued: Long,
    override val duration: Long?,
    override val reason: String,
    override var pardoned: Boolean
) : Punishment

class Ban(
    val uuid: UUID,
    override val issuer: UUID?,
    override val timeIssued: Long,
    override val duration: Long?,
    override val reason: String,
    override var pardoned: Boolean
) : Punishment

class IPBan(
    val ip: String,
    override val issuer: UUID?,
    override val timeIssued: Long,
    override val duration: Long?,
    override val reason: String,
    override var pardoned: Boolean,
    var uuids: List<UUID>
) : Punishment

class Kit(
    val name: String,
    val items: Array<out ItemStack>,
    val cost: BigDecimal,
    val cooldown: Int
)