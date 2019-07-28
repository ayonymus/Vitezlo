package org.szvsszke.vitezlo2018.presentation.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.szvsszke.vitezlo2018.usecase.GetCheckpoints
import org.szvsszke.vitezlo2018.usecase.CheckpointState

class MapViewModel: ViewModel() {

    private var checkpointJob: Job? = null

    private val checkpointState: MutableLiveData<CheckpointState> = MutableLiveData()

    fun getCheckpoints(getCheckpoints: GetCheckpoints,
                       ids: Array<String>): MutableLiveData<CheckpointState> {
        checkpointJob?.cancel()
        checkpointJob = CoroutineScope(Dispatchers.IO).launch {
            checkpointState.postValue(getCheckpoints.invoke(ids))
        }

        return checkpointState
    }

    override fun onCleared() {
        checkpointJob?.cancel()
        super.onCleared()
    }

}
