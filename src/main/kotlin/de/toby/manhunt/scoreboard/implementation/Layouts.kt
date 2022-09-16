package de.toby.manhunt.scoreboard.implementation

import de.toby.manhunt.scoreboard.scoreboard
import org.bukkit.ChatColor

val mainScoreboard = scoreboard("${ChatColor.AQUA}${ChatColor.BOLD}     MANHUNT     ") {
    +""
    +"${ChatColor.YELLOW}${ChatColor.BOLD}Time: ${ChatColor.WHITE}00:00"
    +""
    +"Team: N/A"
    +""
    +"Hunter: ${ChatColor.GRAY}0"
    +"Runner: ${ChatColor.GRAY}0"
    +""
}

val endScoreboard = scoreboard("${ChatColor.AQUA}${ChatColor.BOLD}     MANHUNT     ") {
    +""
    +"${ChatColor.YELLOW}${ChatColor.BOLD}Restart: ${ChatColor.WHITE}00"
    +""
    +"Winner: ${ChatColor.GREEN}"
    +""
}