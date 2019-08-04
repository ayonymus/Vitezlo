package org.szvsszke.vitezlo2018.domain

interface Preferences {

    fun areSightsEnabled(): Boolean

    fun areCheckPointsEnabled(): Boolean

    fun areTouristPathsEnabled(): Boolean
}
