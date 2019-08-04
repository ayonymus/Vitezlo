package org.szvsszke.vitezlo2018.framework.preferences

import android.content.SharedPreferences
import org.szvsszke.vitezlo2018.domain.Preferences
import javax.inject.Inject

class UserPreferences @Inject constructor(private val sharedPreferences: SharedPreferences): Preferences {

    override fun areCheckPointsEnabled() = sharedPreferences.getBoolean(KEY_CHECK_POINTS_ENABLED, false)

    companion object {
        private const val KEY_CHECK_POINTS_ENABLED = "key_checkpoints_enabled"
    }

}
