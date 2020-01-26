package org.szvsszke.vitezlo2018.usecase

import org.szvsszke.vitezlo2018.domain.preferences.InfoBoxPreferences
import javax.inject.Inject

class GetInfoBoxStatus @Inject constructor(private val infoBoxPreferences: InfoBoxPreferences) {

    operator fun invoke() = infoBoxPreferences.getInfoBoxStatus()
}