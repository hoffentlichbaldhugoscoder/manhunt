package de.toby.manhunt.util

import de.toby.manhunt.util.PlayerHider.hide
import de.toby.manhunt.user.UserState
import de.toby.manhunt.user.user
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

fun Player.eliminate() {
    user().state = UserState.ELIMINATED
    hide()
    gameMode = GameMode.SPECTATOR
}

fun Player.hitCooldown(enabled: Boolean) {
    if (enabled) hitCooldown(4) else hitCooldown(100)
}

fun Player.hitCooldown(value: Int) {
    getAttribute(Attribute.GENERIC_ATTACK_SPEED)?.baseValue = value.toDouble()
}