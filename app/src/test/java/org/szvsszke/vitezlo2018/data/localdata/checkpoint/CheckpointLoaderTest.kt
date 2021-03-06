package org.szvsszke.vitezlo2018.data.localdata.checkpoint

import android.content.res.AssetManager
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import io.ticofab.androidgpxparser.parser.GPXParser
import io.ticofab.androidgpxparser.parser.domain.Gpx
import org.junit.Before
import org.junit.Test
import org.szvsszke.vitezlo2018.domain.entity.Checkpoint
import org.szvsszke.vitezlo2018.domain.Loading
import org.szvsszke.vitezlo2018.data.localdata.checkpoint.CheckpointLoader.Companion.PATH_CHECKPOINTS_GPX
import org.xmlpull.v1.XmlPullParserException
import java.io.InputStream
import kotlin.test.assertEquals

internal class CheckpointLoaderTest {

    private val mockStream = mock<InputStream> { }
    private val assets = mock<AssetManager> {
        on { open(PATH_CHECKPOINTS_GPX) } doReturn mockStream
    }

    private val mockGpx = mock<Gpx> { }
    private val gpxParser = mock<GPXParser> {
        on { parse(mockStream) } doReturn mockGpx
    }

    private val checkpointMap = mapOf(Pair("id",
            Checkpoint("id", "one", 0, 1.0, 2.0)))
    private val gpxCheckpointMapper = mock<GpxCheckpointMapper> {
        on { mapToCheckPointMap(mockGpx) } doReturn checkpointMap
    }

    private lateinit var loader: CheckpointLoader

    @Before
    fun setUp() {
        loader = CheckpointLoader(assets, gpxParser, gpxCheckpointMapper)
    }

    @Test
    fun `given a gpx file when data loaded then return a map of checkpoints`() {
        val result = loader.getData()

        assertEquals(Loading.Success(checkpointMap), result)
    }

    @Test
    fun `given a gpx file when an error happens then return Error object`() {
        val error = XmlPullParserException("")
        given(gpxParser.parse(any())).willThrow(error)
        val result = loader.getData()

        assertEquals(Loading.Failure(error), result)
    }
}
