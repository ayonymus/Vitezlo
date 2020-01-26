package org.szvsszke.vitezlo2018.usecase

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Test
import org.szvsszke.vitezlo2018.domain.preferences.InfoBoxPreferences
import org.szvsszke.vitezlo2018.domain.preferences.InfoBoxStatus
import kotlin.test.assertEquals

class GetInfoBoxStatusTest {

    private val infoBoxStatus = InfoBoxStatus(2, true, true)
    private val preferences = mock<InfoBoxPreferences> {
        on { getInfoBoxStatus() } doReturn infoBoxStatus
    }

    @Test
    fun `when invoked should get infoBox preferences`() {
        val getPreferences = GetInfoBoxStatus(preferences)
        val result = getPreferences()
        assertEquals(infoBoxStatus, result)
    }
}