package org.szvsszke.vitezlo2018.domain.preferences

/**
 * User settings go here
 */
interface UserPreferences {

    fun areSightsEnabled(): Boolean

    fun areCheckPointsEnabled(): Boolean

    fun areTouristPathsEnabled(): Boolean
}
