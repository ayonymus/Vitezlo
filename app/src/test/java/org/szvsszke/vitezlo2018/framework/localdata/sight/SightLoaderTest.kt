package org.szvsszke.vitezlo2018.framework.localdata.sight

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
import org.szvsszke.vitezlo2018.domain.entity.Sight
import org.szvsszke.vitezlo2018.map.data.FilePath
import org.xmlpull.v1.XmlPullParserException
import java.io.InputStream
import kotlin.test.assertEquals

internal class SightLoaderTest {

    private val mockStream = mock<InputStream> { }
    private val assets = mock<AssetManager> {
        on { open(FilePath.FILE_SIGHTS_GPX) } doReturn mockStream
    }

    private val mockGpx = mock<Gpx> { }
    private val gpxParser = mock<GPXParser> {
        on { parse(mockStream) } doReturn mockGpx
    }

    private val sightList = listOf(Sight("Galoca", 1.0, 2.0, null))

    private val gpxSightMapper = mock<GpxSightMapper> {
        on { mapToSightList(mockGpx) } doReturn sightList
    }

    private lateinit var loader: SightLoader

    @Before
    fun setUp() {
        loader = SightLoader(assets, gpxParser, gpxSightMapper)
    }

    @Test
    fun `given a gpx file when data loaded then return a list of sights`() {
        val result = loader.getData()

        assertEquals(Loading.Success(sightList), result)
    }

    @Test
    fun `given a gpx file when an error happens then return Error object`() {
        given(gpxParser.parse(any())).willThrow(XmlPullParserException("Olle"))
        val result = loader.getData()

        assertEquals(Loading.Failure(), result)
    }
}
