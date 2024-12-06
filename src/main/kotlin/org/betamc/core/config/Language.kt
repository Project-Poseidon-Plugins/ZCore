package org.betamc.core.config

enum class Language(var msg: String) {

    NO_PERMISSION("&cYou don't have permission to do that."),
    PLAYER_ONLY("&cOnly players can run this command."),
    PLAYER_NOT_FOUND("&cError: Could not find player &6%player%"),

    DELHOME_SUCCESS("&eDeleted %player% home &6%home%"),
    GOD_ENABLE("&eEnabled %player% god mode"),
    GOD_DISABLE("&eDisabled %player% god mode"),
    HEAL_SELF("&aYou have been healed"),
    HEAL_PLAYER("&aHealed %player%"),
    HELP_HEADER("&eHelp: Page &6%page% &eof &6%pages%"),
    HELP_COMMAND("&6%command%: &7%description%"),
    NO_MATCHING_RESULTS("&cError: No matching results"),
    HOME_TELEPORT("&eTeleported you to %player% home &6%name%"),
    HOME_NOT_SPECIFIED("&cError: No home specified"),
    HOME_DOES_NOT_EXIST("&cError: That home does not exist"),
    HOME_SUCCESS("&eTeleported you to %player% home &6%home%"),
    HOMES_HEADER("&eHomes: Page &6%page% &eof &6%pages%"),
    HOMES_ENTRY("&a%home%&6, "),
    KICK_MESSAGE_BROADCAST("&c%sender% kicked %player%: &f%message%"),
    KICK_DEFAULT_MESSAGE("Kicked from server"),
    LIST_HEADER("&eThere are &6%count% &eout of &6%max% &eplayers online."),
    LIST_PLAYERS("&7Online players: &f%list%"),
    PAGE_TOO_HIGH("&cError: Page number too high"),
    SETHOME_INVALID_NAME("&cError: Home name must only contains characters A-Z,0-9,_,-"),
    SETHOME_MAXIMUM("&cError: You cannot set more than %amount% homes"),
    SETHOME_HOME_EXISTS("&cError: There is already a home with this name"),
    SETHOME_SUCCESS("&eSet your home &6%home%"),
    SETSPAWN_SUCCESS("&eSet the spawn of world &6%world% &eto %coords%"),
    SETSPAWN_RESET("&eReset the spawn of world &6%world%"),
    SPAWN_SUCCESS("&eTeleported you to the world spawn"),
    TP_PLAYER("&eTeleported %player% to %target%"),
    TP_COORDINATES("&eTeleported %player% to %coords%"),
    TP_PARSE_ERROR("&cError: Could not parse \"%args%\" into coordinates"),
    TP_UNSAFE("&cError: Unsafe destination, specify y-coordinate explicitly");

    override fun toString(): String = msg
}