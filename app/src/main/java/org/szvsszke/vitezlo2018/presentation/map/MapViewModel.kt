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
import org.szvsszke.vitezlo2018.usecase.GetCheckpoints
import org.szvsszke.vitezlo2018.usecase.GetSights
import org.szvsszke.vitezlo2018.usecase.SightsState
import javax.inject.Inject

class MapViewModel @Inject constructor(private val getCheckpoints: GetCheckpoints,
                                       private val getSights: GetSights,
                                       private val io: CoroutineDispatcher = Dispatchers.IO): ViewModel() {

    private var checkpointJob: Job? = null
    private var sightJob: Job? = null

    private val checkpointState: MutableLiveData<CheckpointState> = MutableLiveData()

    private val sightsState: MutableLiveData<SightsState> = MutableLiveData()

    fun getCheckpoints(ids: Array<String>): LiveData<CheckpointState> {
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

    override fun onCleared() {
        checkpointJob?.cancel()
        sightJob?.cancel()
        super.onCleared()
    }

}
