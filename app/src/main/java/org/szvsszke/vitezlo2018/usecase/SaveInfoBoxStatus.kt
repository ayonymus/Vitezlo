package org.szvsszke.vitezlo2018.usecase

import org.szvsszke.vitezlo2018.domain.preferences.InfoBoxPreferences
import org.szvsszke.vitezlo2018.domain.preferences.InfoBoxStatus
import javax.inject.Inject

class SaveInfoBoxStatus @Inject constructor(private val preferences: InfoBoxPreferences) {

    operator fun invoke(infoBoxStatus: InfoBoxStatus) = preferences.setInfoBoxStatus(infoBoxStatus)
}
