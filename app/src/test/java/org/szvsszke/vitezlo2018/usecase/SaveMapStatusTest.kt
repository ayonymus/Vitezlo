package org.szvsszke.vitezlo2018.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import org.szvsszke.vitezlo2018.domain.entity.Point
import org.szvsszke.vitezlo2018.domain.preferences.MapPreferences
import org.szvsszke.vitezlo2018.domain.preferences.MapStatus

class SaveMapStatusTest {

    private val mapStatus = MapStatus(3, Point(12.0, 12.0), 1F)
    private val mapPreferences = mock<MapPreferences> { }

    @Test
    fun `when invoked then return MapStatus from preferences`() {
        val saveMapStatus = SaveMapStatus(mapPreferences)
        saveMapStatus(mapStatus)
        verify(mapPreferences).setMapStatus(mapStatus)
    }
}