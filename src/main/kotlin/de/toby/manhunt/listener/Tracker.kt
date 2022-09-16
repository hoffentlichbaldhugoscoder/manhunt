package de.toby.manhunt.listener

import de.toby.manhunt.team.TeamManager.getTeam
import de.toby.manhunt.team.implementation.runnerTeam
import de.toby.manhunt.user.UserState
import de.toby.manhunt.user.user
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.events.isRightClick
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.items.flag
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag

object Tracker {

    val item = itemStack(Material.COMPASS) {
        meta {
            name = literalText("${ChatColor.RED}Tracker")
            addEnchant(Enchantment.DURABILITY, 1, true)
            flag(ItemFlag.HIDE_ENCHANTS)
        }
    }

    fun enable() {
        listen<PlayerInteractEvent> {
            if (it.hand == EquipmentSlot.OFF_HAND) return@listen
            if (!it.action.isRightClick) return@listen
            if (!it.player.inventory.itemInMainHand.isSimilar(item)) return@listen

            val target = it.player.nearestTarget()
            if (target == null) it.player.sendMessage("${ChatColor.RED}There is noone nearby")
            else {
                it.player.compassTarget = target.location
                it.player.sendMessage("${ChatColor.WHITE}You are now tracking ${ChatColor.YELLOW}${target.displayName}")
            }
        }
    }

    private fun Player.nearestTarget() = onlinePlayers
        .filter { it.user().state == UserState.PLAYING }
        .filter { it.getTeam() == runnerTeam }
        .minByOrNull { location.distance(it.location) }
}