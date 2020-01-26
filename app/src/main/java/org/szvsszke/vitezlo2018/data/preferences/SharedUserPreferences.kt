package org.szvsszke.vitezlo2018.data.preferences

import android.content.SharedPreferences
import org.szvsszke.vitezlo2018.domain.preferences.UserPreferences
import javax.inject.Inject

class SharedUserPreferences @Inject constructor(private val sharedPreferences: SharedPreferences): UserPreferences {

    override fun areCheckPointsEnabled() = sharedPreferences.getBoolean(KEY_CHECK_POINTS_ENABLED, true)

    override fun areTouristPathsEnabled() = sharedPreferences.getBoolean(KEY_TOURIST_PATHS_ENABLED, true)

    override fun areSightsEnabled() = sharedPreferences.getBoolean(KEY_SIGHTS_ENABLED, true)

    companion object {
        private const val KEY_CHECK_POINTS_ENABLED = "key_checkpoints_enabled"
        private const val KEY_TOURIST_PATHS_ENABLED = "key_tourist_paths_enabled"
        private const val KEY_SIGHTS_ENABLED = "key_sights_enabled"
    }

}
