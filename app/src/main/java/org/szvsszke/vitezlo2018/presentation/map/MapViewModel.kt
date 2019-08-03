package org.szvsszke.vitezlo2018.presentation.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.szvsszke.vitezlo2018.usecase.CheckpointState
import org.szvsszke.vitezlo2018.usecase.DescriptionsState
import org.szvsszke.vitezlo2018.usecase.GetCheckpoints
import org.szvsszke.vitezlo2018.usecase.GetDescriptions
import org.szvsszke.vitezlo2018.usecase.GetSights
import org.szvsszke.vitezlo2018.usecase.GetTrack
import org.szvsszke.vitezlo2018.usecase.SightsState
import org.szvsszke.vitezlo2018.usecase.TrackState
import javax.inject.Inject

/**
 * View model for the main map view
 */
class MapViewModel @Inject constructor(private val getCheckpoints: GetCheckpoints,
                                       private val getSights: GetSights,
                                       private val getDescriptions: GetDescriptions,
                                       private val getTrack: GetTrack,
                                       private val io: CoroutineDispatcher = Dispatchers.IO): ViewModel() {

    private var checkpointJob: Job? = null
    private var sightJob: Job? = null
    private var descriptionsJob: Job? = null
    private var trackJob: Job? = null

    private val checkpointState: MutableLiveData<CheckpointState> = MutableLiveData()
    private val sightsState: MutableLiveData<SightsState> = MutableLiveData()
    private val descriptionsState: MutableLiveData<DescriptionsState> = MutableLiveData()
    private val trackState: MutableLiveData<TrackState> = MutableLiveData()

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

    override fun onCleared() {
        checkpointJob?.cancel()
        sightJob?.cancel()
        super.onCleared()
    }

}
