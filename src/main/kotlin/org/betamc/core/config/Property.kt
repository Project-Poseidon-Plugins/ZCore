package org.betamc.core.config

enum class Property(val key: String, var value: Any) {

    AUTO_SAVE_TIME("auto-save-time", 300),
    BROADCAST_FORMAT("format.broadcast-format", "&f[&cBroadcast&f] &a%message%"),
    MULTIPLE_HOMES("multiple-homes", 10),
    HOMES_PER_PAGE("homes-per-page", 50);

    override fun toString(): String = value.toString()

    fun toInt(): Int = toString().toInt()

    fun toLong(): Long = toString().toLong()

    fun toDouble(): Double = toString().toDouble()

    fun toBoolean(): Boolean = toString().toBoolean()
}