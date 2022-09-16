package de.toby.manhunt.game

object Game {
    var current: Phase? = null
        set(value) {
            field?.stop()
            field = value
        }
}