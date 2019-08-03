package org.szvsszke.vitezlo2018.framework.localdata.touristpath

import android.content.res.AssetManager
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import io.ticofab.androidgpxparser.parser.GPXParser
import io.ticofab.androidgpxparser.parser.domain.Gpx
import org.junit.Before
import org.junit.Test
import org.szvsszke.vitezlo2018.domain.Loading
import org.szvsszke.vitezlo2018.domain.entity.Track
import org.szvsszke.vitezlo2018.framework.localdata.touristpath.TouristPathLoader.Companion.PATH_TO_TOURIST_PATHS
import org.szvsszke.vitezlo2018.framework.localdata.track.GpxTrackMapper
import org.xmlpull.v1.XmlPullParserException
import java.io.InputStream
import kotlin.test.assertEquals

internal class TouristPathLoaderTest {

    private val trackName1 = "Rambo1"
    private val trackName2 = "Rambo2"
    private val trackName3 = "Rambo3"
    private val track1 = Track(trackName1, listOf(), trackName1)
    private val track2 = Track(trackName2, listOf(), trackName2)
    private val track3 = Track(trackName3, listOf(), trackName3)


    private val mockStream1 = mock<InputStream> { }
    private val mockStream2 = mock<InputStream> { }
    private val mockStream3 = mock<InputStream> { }

    private val assets = mock<AssetManager> {
        on { list(PATH_TO_TOURIST_PATHS) } doReturn arrayOf(trackName1, trackName2, trackName3)
        on { open("$PATH_TO_TOURIST_PATHS/$trackName1") } doReturn mockStream1
        on { open("$PATH_TO_TOURIST_PATHS/$trackName2") } doReturn mockStream2
        on { open("$PATH_TO_TOURIST_PATHS/$trackName3") } doReturn mockStream3

    }

    private val mockGpx1 = mock<Gpx> { }
    private val mockGpx2 = mock<Gpx> { }
    private val mockGpx3 = mock<Gpx> { }

    private val gpxParser = mock<GPXParser> {
        on { parse(mockStream1) } doReturn mockGpx1
        on { parse(mockStream2) } doReturn mockGpx2
        on { parse(mockStream3) } doReturn mockGpx3
    }

    private val gpxTrackMapper = mock<GpxTrackMapper> {
        on { mapToTrack(mockGpx1) } doReturn track1
        on { mapToTrack(mockGpx2) } doReturn track2
        on { mapToTrack(mockGpx3) } doReturn track3
    }

    private lateinit var loader: TouristPathLoader

    @Before
    fun setUp() {
        loader = TouristPathLoader(assets, gpxParser, gpxTrackMapper)
    }

    @Test
    fun `given a gpx file when data loaded then return a list of sights`() {
        val result = loader.getData()

        assertEquals(Loading.Success(listOf(track1, track2, track3)), result)
    }

    @Test
    fun `given a gpx file when an error happens then return Error object`() {
        val exception = XmlPullParserException("Olle")
        given(gpxParser.parse(any())).willThrow(exception)
        val result = loader.getData()

        assertEquals(Loading.Failure(exception), result)
    }
}
