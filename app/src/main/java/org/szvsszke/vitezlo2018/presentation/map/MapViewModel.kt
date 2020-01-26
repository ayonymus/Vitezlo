package org.szvsszke.vitezlo2018.presentation.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.szvsszke.vitezlo2018.domain.preferences.InfoBoxStatus
import org.szvsszke.vitezlo2018.domain.preferences.MapStatus
import org.szvsszke.vitezlo2018.usecase.*
import javax.inject.Inject

/**
 * View model for the main map view
 *
 * This reflects the different long running calls for getting data to display.
 */
class MapViewModel @Inject constructor(private val getCheckpoints: GetCheckpoints,
                                       private val getSights: GetSights,
                                       private val getDescriptions: GetDescriptions,
                                       private val getTrack: GetTrack,
                                       private val getTouristPaths: GetTouristPaths,
                                       private val getMapStatus: GetMapStatus,
                                       private val saveMapStatus: SaveMapStatus,
                                       private val getInfoBoxStatus: GetInfoBoxStatus,
                                       private val saveInfoBoxStatus: SaveInfoBoxStatus,
                                       private val io: CoroutineDispatcher = Dispatchers.IO): ViewModel() {

    private var checkpointJob: Job? = null
    private var sightJob: Job? = null
    private var descriptionsJob: Job? = null
    private var trackJob: Job? = null
    private var touristPathJob: Job? = null

    private val checkpointState: MutableLiveData<CheckpointState> = MutableLiveData()
    private val sightsState: MutableLiveData<SightsState> = MutableLiveData()
    private val descriptionsState: MutableLiveData<DescriptionsState> = MutableLiveData()
    private val trackState: MutableLiveData<TrackState> = MutableLiveData()
    private val touristPathState: MutableLiveData<TouristPathState> = MutableLiveData()

    fun getCheckpoints(ids: List<String>): LiveData<CheckpointState> {
        checkpointJob?.cancel()
        checkpointJob = CoroutineScope(io).launch {
            checkpointState.postValue(getCheckpoints.invoke(ids))
        }
        return checkpointState
    }

    fun getSights(): LiveData<SightsState>   {
        sightJob?.cancel()
        sightJob = CoroutineScope(io).launch {
            sightsState.postValue(getSights.invoke())
        }
        return sightsState
    }

    fun getDescriptions(): LiveData<DescriptionsState>   {
        descriptionsJob?.cancel()
        descriptionsJob = CoroutineScope(io).launch {
            descriptionsState.postValue(getDescriptions.invoke())
        }
        return descriptionsState
    }

    fun getTrack(trackName: String): LiveData<TrackState> {
        trackJob?.cancel()
        trackJob = CoroutineScope(io).launch {
            trackState.postValue(getTrack.invoke(trackName))
        }
        return trackState
    }

    fun getTouristPath(): LiveData<TouristPathState> {
        touristPathJob?.cancel()
        touristPathJob = CoroutineScope(io).launch {
            touristPathState.postValue(getTouristPaths.invoke())
        }
        return touristPathState
    }

    fun getMapStatus() = getMapStatus.invoke()

    fun saveMapStatus(mapStatus: MapStatus) = saveMapStatus.invoke(mapStatus)

    fun getInfoBoxStatus(): InfoBoxStatus  = getInfoBoxStatus.invoke()

    fun saveInfoBoxStatus(newInfoBoxStatus: InfoBoxStatus) = saveInfoBoxStatus.invoke(newInfoBoxStatus)

    override fun onCleared() {
        checkpointJob?.cancel()
        sightJob?.cancel()
        // TODO rest of jobs
        super.onCleared()
    }

}
