package de.toby.manhunt.config.implementation

import de.toby.manhunt.config.Properties

object Settings: Properties(name = "Settings") {
    var autoStart by value(true)
    var requiredHunter by value(1)
    var offlineTime by value<Long>(60 * 5)
    var graceDamage by value(false)
    var graceDuration by value(60)
    var hitCooldown by value(true)
    var damageMultiplier by value(50)
    var critMultiplier by value(60)
}