package org.szvsszke.vitezlo2018.usecase

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import org.junit.Test
import org.szvsszke.vitezlo2018.data.repository.track.TrackRepository
import org.szvsszke.vitezlo2018.domain.Loading
import org.szvsszke.vitezlo2018.domain.entity.Track
import kotlin.test.assertEquals

internal class GetTrackTest {

    private val track = mock<Track> { }
    private val trackResult = Loading.Success(track)

    private val trackName = "trackk"

    private val repo = mock<TrackRepository> {
        on { getData(trackName) } doReturn trackResult
    }

    private val getTrack = GetTrack(repo)

    @Test
    fun `given a list of ids when checkpoints called then return data`() {
        val result = getTrack.invoke(trackName)
        assertEquals(TrackState.Data(track), result)
    }

    @Test
    fun `given a list of ids when checkpoints call errors then return error`() {
        given { repo.getData(trackName) }.willReturn(Loading.Failure())

        val result = getTrack.invoke(trackName)
        assertEquals(TrackState.Error, result)
    }
}
