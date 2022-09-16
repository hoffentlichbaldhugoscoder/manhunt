package de.toby.manhunt.user

import de.toby.manhunt.scoreboard.Board
import net.axay.kspigot.runnables.KSpigotRunnable
import org.bukkit.entity.Player
import java.util.*

private val players = mutableMapOf<UUID, User>()

class User {
    var board: Board? = null
    var state: UserState? = null
    var task: KSpigotRunnable? = null
}

fun Player.user() = players.computeIfAbsent(uniqueId) { User() }