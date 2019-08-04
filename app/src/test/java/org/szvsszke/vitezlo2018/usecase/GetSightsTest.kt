package org.szvsszke.vitezlo2018.usecase

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.Test
import org.szvsszke.vitezlo2018.data.repository.sight.SightRepository
import org.szvsszke.vitezlo2018.domain.Loading
import org.szvsszke.vitezlo2018.domain.Preferences
import org.szvsszke.vitezlo2018.domain.entity.Sight
import kotlin.test.assertEquals

internal class GetSightsTest {

    private val sight = mock<Sight> { }
    private val sights = listOf(sight)
    private val sightsResult = Loading.Success(sights)

    private val repo = mock<SightRepository> {
        on { getData() } doReturn sightsResult
    }
    private val preferences = mock<Preferences> {
        on { areSightsEnabled() } doReturn true
    }


    private val getSights = GetSights(repo, preferences)

    @Test
    fun `given sights in repo when called then return data`() {
        val result = getSights.invoke()
        verify(repo).getData()
        assertEquals(SightsState.Data(sights), result)
    }

    @Test
    fun `given a failure in repository then return error`() {
        given { repo.getData() }.willReturn(Loading.Failure())

        val result = getSights.invoke()
        verify(repo).getData()
        assertEquals(SightsState.Error, result)
    }

    @Test
    fun `given sights are disabled then return disabled`() {
        given { preferences.areSightsEnabled() }.willReturn(false)

        val result = getSights.invoke()
        verifyNoMoreInteractions(repo)
        assertEquals(SightsState.Disabled, result)
    }
}
