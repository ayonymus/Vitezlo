package org.szvsszke.vitezlo2018.usecase

import org.szvsszke.vitezlo2018.domain.preferences.MapPreferences
import javax.inject.Inject

class GetMapStatus @Inject constructor(private var mapPreferences: MapPreferences) {

    operator fun invoke() = mapPreferences.getMapStatus()

}
