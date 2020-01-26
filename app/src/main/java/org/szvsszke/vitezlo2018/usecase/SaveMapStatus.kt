package org.szvsszke.vitezlo2018.usecase

import org.szvsszke.vitezlo2018.domain.preferences.MapPreferences
import org.szvsszke.vitezlo2018.domain.preferences.MapStatus
import javax.inject.Inject

class SaveMapStatus @Inject constructor(private val mapPreferences: MapPreferences) {

    operator fun invoke(mapStatus: MapStatus) = mapPreferences.setMapStatus(mapStatus)

}
