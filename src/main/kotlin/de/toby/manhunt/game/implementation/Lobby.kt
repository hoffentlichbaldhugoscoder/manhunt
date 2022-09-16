package de.toby.manhunt.game.implementation

import de.toby.manhunt.Manager
import de.toby.manhunt.config.implementation.Settings
import de.toby.manhunt.game.Game
import de.toby.manhunt.game.Phase
import de.toby.manhunt.scoreboard.implementation.mainScoreboard
import de.toby.manhunt.team.TeamManager.getTeam
import de.toby.manhunt.team.TeamManager.leaveTeam
import de.toby.manhunt.team.TeamManager.setTeam
import de.toby.manhunt.team.implementation.hunterTeam
import de.toby.manhunt.team.implementation.runnerTeam
import de.toby.manhunt.ui.SettingsUI
import de.toby.manhunt.ui.TeamUI
import de.toby.manhunt.user.UserState
import de.toby.manhunt.user.user
import de.toby.manhunt.util.PlayerHider.show
import de.toby.manhunt.util.formatToMinutes
import de.toby.manhunt.util.reset
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.bukkit.actionBar
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.extensions.pluginManager
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.entity.*
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class Lobby : Phase() {

    init {
        val world = Bukkit.getWorld("lobby_map") ?: WorldCreator("lobby_map").generateStructures(false).type(WorldType.FLAT).createWorld()!!
        world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0)
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)

        event<PlayerJoinEvent> { event ->
            val player = event.player

            player.reset()
            player.show()
            player.setTeam(hunterTeam)
            player.user().board?.layout(mainScoreboard)
            player.user().state = UserState.PLAYING
            player.teleport(Location(world, 0.0, world.getHighestBlockAt(0, 0).y.toDouble() + 1, 0.0))

            idle = !canStart()
            onlinePlayers.forEach { updateScoreboard(it) }

            equipHotBar(player)
        }

        event<PlayerQuitEvent> { event ->
            val player = event.player
            player.leaveTeam()
            player.user().state = null

            idle = !canStart()
            onlinePlayers.forEach { updateScoreboard(it) }
        }

        event<PlayerDropItemEvent> {
            it.isCancelled = true
        }

        event<PlayerInteractEvent> {
            it.isCancelled = true
        }

        event<EntityPickupItemEvent> {
            it.isCancelled = true
        }

        event<EntitySpawnEvent> {
            it.isCancelled = true
        }

        event<EntityDamageEvent> {
            it.isCancelled = true
        }

        event<FoodLevelChangeEvent> {
            it.isCancelled = true
        }

        event<EntityTargetEvent> {
            it.isCancelled = true
        }

        event<InventoryClickEvent> {
            it.isCancelled = true
        }

        onlinePlayers.forEach {
            pluginManager.callEvent(PlayerJoinEvent(it, null))
        }

        idle = !canStart()
    }

    override fun run() {
        onlinePlayers.forEach { updateScoreboard(it) }
        if (!idle) {
            when (countdown()) {
                2, 3, 4, 5, 10, 30 -> broadcast("${ChatColor.YELLOW}The game starts in ${countdown()} seconds")
                1 -> broadcast("${ChatColor.YELLOW}The game starts in one second")
                0 -> {
                    broadcast("${ChatColor.YELLOW}The game has started")

                    Game.current = Invincibility(Manager.world)
                }
            }
        } else {
            val text = if (runnerTeam.player().isEmpty()) "Not enough runners are online"
            else if (hunterTeam.player().size < Settings.requiredHunter) "Not enough hunters are online"
            else if (!Settings.autoStart) "The start is only done via /start" else null

            if (text == null) return
            onlinePlayers.forEach { it.actionBar("${ChatColor.RED}$text") }
        }
    }

    private fun countdown() = 30 - time

    fun canStart(): Boolean {
        return if (runnerTeam.player().isEmpty()) false
        else if (hunterTeam.player().size < Settings.requiredHunter) false
        else Settings.autoStart
    }

    fun updateScoreboard(player: Player) {
        player.user().board?.run {
            if (!idle) update(6, "${ChatColor.YELLOW}${ChatColor.BOLD}Start: ${ChatColor.WHITE}${countdown().formatToMinutes()}")
            else update(6, "${ChatColor.YELLOW}${ChatColor.BOLD}Start: ${ChatColor.WHITE}Paused")
            update(4, "Team: ${player.getTeam()?.color}${player.getTeam()?.name}")
            update(2, "Hunter: ${ChatColor.GRAY}${hunterTeam.player().size}")
            update(1, "Runner: ${ChatColor.GRAY}${runnerTeam.player().size}")
        }
    }

    private fun equipHotBar(player: Player) {
        player.inventory.setItem(4, TeamUI.item)
        player.inventory.setItem(8, SettingsUI.item)
    }
}