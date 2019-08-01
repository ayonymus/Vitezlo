package org.szvsszke.vitezlo2018.domain.entity


data class Description(var name: String,
                       val routeFileName: String,
                       val starting: String,
                       val entryFee: String,
                       val other: String,
                       val checkPointIDs: Array<String>,
                       val date: String,
                       val length: String,
                       val levelTime: String) {

    /** @return an array of the public data of a Description object:
     * name, length, date, starting, entryFee, other
     */
    // TODO string appended in data
    val publicData: Array<String>
        get() = arrayOf("$length km", date, starting, entryFee, "$levelTime óra", other)
}
