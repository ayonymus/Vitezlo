package org.szvsszke.vitezlo2018.data.localdata.track

import android.content.res.AssetManager
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import io.ticofab.androidgpxparser.parser.GPXParser
import io.ticofab.androidgpxparser.parser.domain.Gpx
import org.junit.Before
import org.junit.Test
import org.szvsszke.vitezlo2018.domain.entity.Track
import org.xmlpull.v1.XmlPullParserException
import java.io.InputStream
import kotlin.test.assertEquals

internal class TrackLoaderTest {

    private val mockStream = mock<InputStream> { }
    private val assets = mock<AssetManager> {
        on { open(any()) } doReturn mockStream
    }

    private val mockGpx = mock<Gpx> { }
    private val gpxParser = mock<GPXParser> {
        on { parse(mockStream) } doReturn mockGpx
    }

    private val trackName = "Rambo 3"
    private val track = Track(trackName, listOf())

    private val gpxTrackMapper = mock<GpxTrackMapper> {
        on { mapToTrack(mockGpx) } doReturn track
    }

    private lateinit var loader: TrackLoader

    @Before
    fun setUp() {
        loader = TrackLoader(assets, gpxParser, gpxTrackMapper)
    }

    @Test
    fun `given a gpx file when data loaded then return a list of sights`() {
        val result = loader.getData(trackName)

        assertEquals(track, result)
    }

    @Test
    fun `given a gpx file when an error happens then return Error object`() {
        given(gpxParser.parse(any())).willThrow(XmlPullParserException("Olle"))
        val result = loader.getData(trackName)

        assertEquals(null, result)
    }
}
