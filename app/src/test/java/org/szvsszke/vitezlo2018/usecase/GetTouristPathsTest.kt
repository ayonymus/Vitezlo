package org.szvsszke.vitezlo2018.usecase

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.Test
import org.szvsszke.vitezlo2018.domain.Loading
import org.szvsszke.vitezlo2018.domain.preferences.UserPreferences
import org.szvsszke.vitezlo2018.domain.Repository
import org.szvsszke.vitezlo2018.domain.entity.Track
import kotlin.test.assertEquals

internal class GetTouristPathsTest {

    private val track = mock<Track> { }
    private val tracks = listOf(track)
    private val trackResult = Loading.Success(tracks)

    private val repo = mock<Repository<List<Track>>> {
        on { getData() } doReturn trackResult
    }
    private val preferences = mock<UserPreferences> {
        on { areTouristPathsEnabled() } doReturn true
    }

    private val getTouristPaths = GetTouristPaths(repo, preferences)

    @Test
    fun `given repository has data when called then return data`() {
        val result = getTouristPaths.invoke()
        verify(repo).getData()
        assertEquals(TouristPathState.Data(tracks), result)
    }

    @Test
    fun `given a repository that fails then return error`() {
        given { repo.getData() }.willReturn(Loading.Failure())

        val result = getTouristPaths.invoke()
        verify(repo).getData()
        assertEquals(TouristPathState.Error, result)
    }

    @Test
    fun `given tourist paths are disabled then return disabled`() {
        given { preferences.areTouristPathsEnabled() }.willReturn(false)

        val result = getTouristPaths.invoke()
        verifyNoMoreInteractions(repo)
        assertEquals(TouristPathState.Disabled, result)
    }

}
