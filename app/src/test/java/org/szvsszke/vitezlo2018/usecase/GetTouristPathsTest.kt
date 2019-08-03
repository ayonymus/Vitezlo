package org.szvsszke.vitezlo2018.usecase

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import org.szvsszke.vitezlo2018.domain.Loading
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

    private val getSights = GetTouristPaths(repo)

    @Test
    fun `given a list of ids when checkpoints called then return data`() {
        val result = getSights.invoke()
        verify(repo).getData()
        assertEquals(TouristPathState.Data(tracks), result)
    }

    @Test
    fun `given a list of ids when checkpoints call errors then return error`() {
        given { repo.getData() }.willReturn(Loading.Failure())

        val result = getSights.invoke()
        verify(repo).getData()
        assertEquals(TouristPathState.Error, result)
    }
}
