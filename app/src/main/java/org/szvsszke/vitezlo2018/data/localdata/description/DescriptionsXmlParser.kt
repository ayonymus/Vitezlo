package org.szvsszke.vitezlo2018.data.localdata.description

import org.szvsszke.vitezlo2018.domain.entity.Description
import timber.log.Timber
import java.io.InputStream
import java.util.ArrayList
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Parses description xml.
 * Fixme: should use json/gson/moshi instead
 */
class DescriptionsXmlParser {

    fun parseHikeDescriptions(stream: InputStream): List<Description> {
        val docBuilderFactory = DocumentBuilderFactory.newInstance()
        val tracks = ArrayList<Description>()

        // access file
        try {
            val documentBuilder = docBuilderFactory.newDocumentBuilder()

            val doc = documentBuilder.parse(stream)
            val rootElement = doc.documentElement
            val nodeList = rootElement.getElementsByTagName("track")

            for (i in 0 until nodeList.length) {

                val node = nodeList.item(i)
                val attributes = node.attributes

                val name = attributes.getNamedItem("name").textContent
                val length = attributes.getNamedItem("length").textContent
                val routeFile = attributes.getNamedItem("route").textContent
                val checkpoints = attributes.getNamedItem("checkpoints").textContent
                val starting = attributes.getNamedItem("starting").textContent
                val entryFee = attributes.getNamedItem("entryFee").textContent
                val other = attributes.getNamedItem("other").textContent
                val date = attributes.getNamedItem("date").textContent
                val levelTime = attributes.getNamedItem("leveltime").textContent
                val cps = checkpoints.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                val track = Description(name, routeFile, starting, entryFee, other, cps, date, length, levelTime)

                tracks.add(track)
            }
        } catch (parserException: Exception) {
            Timber.e(parserException)
        } finally {
            stream.close()
        }

        return tracks
    }
}
