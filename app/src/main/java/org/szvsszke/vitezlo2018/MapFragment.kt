package org.szvsszke.vitezlo2018

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_map_re.*
import kotlinx.android.synthetic.main.info_box_view.*
import org.szvsszke.vitezlo2018.domain.entity.Description
import org.szvsszke.vitezlo2018.domain.entity.Point
import org.szvsszke.vitezlo2018.domain.preferences.InfoBoxStatus
import org.szvsszke.vitezlo2018.map.MapDecorator
import org.szvsszke.vitezlo2018.presentation.listitems.InfoBoxItem
import org.szvsszke.vitezlo2018.presentation.map.MapViewModel
import org.szvsszke.vitezlo2018.presentation.listitems.SimpleExpandableItem
import org.szvsszke.vitezlo2018.presentation.map.SpinnerListener
import org.szvsszke.vitezlo2018.usecase.*
import timber.log.Timber
import javax.inject.Inject

/**
 * This fragment is responsible for loading data and displaying it
 * overlaying a map.
 * TODO refactor properly once spaghetti is sorted out
 */
class MapFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var mapDecorator: MapDecorator

    @Inject
    lateinit var findBounds: FindBounds

    private lateinit var viewModel: MapViewModel

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    private var expandableGroup = ExpandableGroup(SimpleExpandableItem(R.string.description))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.getComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map_re, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)

        recycler_infoBox.adapter = groupAdapter
        recycler_infoBox.layoutManager = LinearLayoutManager(context)

        groupAdapter.clear()
        groupAdapter.add(expandableGroup)

        mapDecorator.init(activity, mapView) // TODO

        viewModel = ViewModelProvider(this, viewModelFactory)
                    .get(MapViewModel::class.java)

        imageView_mapTypeSwitch.setOnClickListener { mapDecorator.switchMapType() }
        imageView_infoBoxCollapse.apply {
            setOnClickListener {
                val oldInfoStatus = viewModel.getInfoBoxStatus()
                val newInfoBoxStatus = oldInfoStatus.copy(isExtended = !oldInfoStatus.isExtended)
                viewModel.saveInfoBoxStatus(newInfoBoxStatus)
                expandableGroup.onToggleExpanded()
                if (newInfoBoxStatus.isExtended) {
                    setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_action_collapse))
                } else {
                    setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.ic_action_expand))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        mapDecorator.mapStatus = viewModel.getMapStatus()
        viewModel.getDescriptions().observe(this,
                Observer { result ->
                    when(result) {
                        is DescriptionsState.Data -> onDescriptionsReady(
                                result.descriptions, viewModel.getInfoBoxStatus())
                        else -> Timber.e("Could not get descriptions")
                    }
                })
        showSights()
        showTouristPaths()
    }

    override fun onPause() {
        viewModel.saveMapStatus(mapDecorator.mapStatus)
        mapView.onPause()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mapView.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        mapView.onLowMemory()
        super.onLowMemory()
    }
    private fun onDescriptionsReady(descriptions: List<Description>,
                                    infoBoxStatus: InfoBoxStatus) {
        Timber.v(descriptions.toString())
        Timber.v(infoBoxStatus.toString())

        val hikeNames = descriptions.map { it.name }
        setupTrackSpinner(hikeNames, infoBoxStatus, descriptions)

        showCheckpoint(descriptions[infoBoxStatus.selectedTrackIndex])
        showTrack(descriptions[infoBoxStatus.selectedTrackIndex].routeFileName)
    }

    private fun setupTrackSpinner(hikeNames: List<String>, infoBoxStatus: InfoBoxStatus,
                                  descriptions: List<Description>) {
        context?.let { context ->
            val adapter = ArrayAdapter<String>(context, R.layout.info_box_spinner_item,
                    R.id.textViewTrackName, hikeNames)

            spinner_infoBoxHikeSpinner.apply {
                setAdapter(adapter)
                setSelection(infoBoxStatus.selectedTrackIndex)
                onItemSelectedListener = SpinnerListener { position ->
                    updateContent(descriptions[position])
                }
            }
        }
        showSpinnerOrTitle(hikeNames, infoBoxStatus)
        imageView_infoBoxLock.setOnClickListener {
            val oldInfoStatus = viewModel.getInfoBoxStatus()
            val newInfoBoxStatus = oldInfoStatus.copy(isLocked = !oldInfoStatus.isLocked)
            viewModel.saveInfoBoxStatus(newInfoBoxStatus)
            showSpinnerOrTitle(hikeNames, newInfoBoxStatus)
        }
    }

    private fun updateContent(description: Description) {
        showTrack(description.routeFileName)
        showCheckpoint(description)
        updateDescription(description)
    }

    private fun showSpinnerOrTitle(hikeNames: List<String>, infoBoxStatus: InfoBoxStatus) {
        if(infoBoxStatus.isLocked) {
            textView_infoBoxTitle.visibility = View.VISIBLE
            textView_infoBoxTitle.text = hikeNames[infoBoxStatus.selectedTrackIndex]
            spinner_infoBoxHikeSpinner.visibility = View.GONE
            context?.let { context ->
                imageView_infoBoxLock.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_menu_lock))
            }
        } else {
            textView_infoBoxTitle.visibility = View.GONE
            spinner_infoBoxHikeSpinner.visibility = View.VISIBLE
            spinner_infoBoxHikeSpinner.setSelection(infoBoxStatus.selectedTrackIndex)
            context?.let { context ->
                imageView_infoBoxLock.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_menu_unlock))
            }
        }
    }

    private fun updateDescription(description: Description) {
        groupAdapter.remove(expandableGroup)
        expandableGroup = ExpandableGroup(SimpleExpandableItem(R.string.description))
        expandableGroup.addAll(listOf(
                InfoBoxItem(R.string.length, description.length),
                InfoBoxItem(R.string.leveltime, description.levelTime),
                InfoBoxItem(R.string.starting, description.starting),
                InfoBoxItem(R.string.fee, description.entryFee)
        ))
        if(description.other.isNotBlank()) {
            expandableGroup.add(InfoBoxItem(R.string.other, description.other))
        }
        groupAdapter.add(expandableGroup)
        groupAdapter.notifyDataSetChanged()
    }

    private fun showTrack(trackName: String) {
        viewModel.getTrack(trackName).observe(this,
                Observer { result ->
                    when(result) {
                        is TrackState.Data -> processTrack(result.data.points)
                        else -> Timber.e("Could not get track")
                    }
                })
    }

    private fun showCheckpoint(description: Description) {
        viewModel.getCheckpoints(description.checkPointIDs).observe(this,
                Observer<CheckpointState> { result ->
                    when (result) {
                        is CheckpointState.Data -> mapDecorator.markCheckpoints(result.data)
                        is CheckpointState.Disabled -> mapDecorator.hideCheckpoints()
                        else -> Timber.e("Could not get checkpoints")
                    }
                })
    }

    private fun showSights() {
        viewModel.getSights().observe(this,
                Observer { result ->
                    when(result) {
                        is SightsState.Data -> mapDecorator.markSights(result.data)
                        is SightsState.Disabled -> mapDecorator.hideSights()
                        else -> Timber.e("Could not get sights")
                    }
                })
    }

    private fun showTouristPaths() {
        viewModel.getTouristPath().observe(this,
                Observer { result ->
                    when(result) {
                        is TouristPathState.Data -> mapDecorator.displayTouristPaths(result.data)
                        is TouristPathState.Disabled -> mapDecorator.hideTouristPaths()
                        else -> Timber.e("Could not get tourist paths")
                    }
                })
    }

    // TODO move to a utility class
    private fun processTrack(track: List<Point>) {
        val bounds = findBounds(track)
        val start = LatLng(bounds.first.latitude, bounds.first.longitude)
        val end = LatLng(bounds.second.latitude, bounds.second.longitude)
        mapDecorator.markTrack(track.map { LatLng(it.latitude, it.longitude) }, start, end)
    }



}
