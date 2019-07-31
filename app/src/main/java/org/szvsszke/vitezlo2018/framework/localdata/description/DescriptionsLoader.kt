package org.szvsszke.vitezlo2018.framework.localdata.description

import android.content.res.AssetManager
import org.szvsszke.vitezlo2018.data.repository.DataSource
import org.szvsszke.vitezlo2018.domain.Loading
import org.szvsszke.vitezlo2018.domain.entity.Description
import org.szvsszke.vitezlo2018.map.data.FilePath
import timber.log.Timber
import javax.inject.Inject

class DescriptionsLoader @Inject constructor(private val assets: AssetManager,
                                             private val descriptionsXmlParser: DescriptionsXmlParser
): DataSource<List<Description>> {

    override fun getData(): Loading<List<Description>> {
        return try {
            Loading.Success(descriptionsXmlParser.parseHikeDescriptions(
                    assets.open(FilePath.FILE_HIKE_DESCRIPTIONS_XML)))
        } catch (exception: Exception) {
            Timber.e(exception)
            Loading.Failure(exception)
        }
    }
}
