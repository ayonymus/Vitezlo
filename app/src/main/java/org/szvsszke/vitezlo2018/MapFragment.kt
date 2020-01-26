package org.szvsszke.vitezlo2018

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import org.szvsszke.vitezlo2018.domain.entity.Description
import org.szvsszke.vitezlo2018.domain.entity.Point
import org.szvsszke.vitezlo2018.map.MapDecorator
import org.szvsszke.vitezlo2018.map.overlay.InfoBox
import org.szvsszke.vitezlo2018.presentation.map.MapViewModel
import org.szvsszke.vitezlo2018.usecase.*
import timber.log.Timber
import javax.inject.Inject

/**
 * This fragment is responsible for loading data and displaying it
 * overlaying a map.
 */
class MapFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var mapDecorator: MapDecorator

    @Inject
    lateinit var findBounds: FindBounds

    // TODO synthetic access
    private lateinit var mapView: MapView
    private lateinit var infoBoxHolder: RelativeLayout
    private lateinit var mapTypeSwitch: View

    private lateinit var viewModel: MapViewModel

    private lateinit var mInfoBox: InfoBox

    //view variables
    private var isBoxExpanded = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.getComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val inflatedView = inflater.inflate(R.layout.fragment_map, container, false)

        mapView = inflatedView.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapDecorator.init(activity, mapView)

        infoBoxHolder = inflatedView.findViewById(R.id.infoBoxHolder)
        setupInfoBox(inflater)

        mapTypeSwitch = inflatedView.findViewById(R.id.imageViewMapTypeSwitch)
        mapTypeSwitch.setOnClickListener { mapDecorator.switchMapType() }

        return inflatedView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)
                .get(MapViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        mapDecorator.mapStatus = viewModel.getMapStatus()
        viewModel.getDescriptions().observe(this,
                Observer { result ->
                    when(result) {
                        is DescriptionsState.Data -> onDescriptionsReady(result.data)
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

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mapView.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        mapView.onLowMemory()
        super.onLowMemory()
    }

    private fun onDescriptionsReady(descriptions: List<Description>) {
        Timber.v(descriptions.toString())
        setupTrackSpinner(descriptions)
        showCheckpoint(descriptions[0])
        showTrack(descriptions[0].routeFileName)
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

    private fun setupTrackSpinner(descriptions: List<Description>) {
        mInfoBox.setupSpinner(descriptions.map { it.name },
                TrackSpinnerListener { position ->
                    updateViews(descriptions[position])
                    updateInfoDescription(descriptions[position])
                }, 0)
    }

    private fun updateViews(description: Description) {
        activity?.title = description.name
        mInfoBox.setTitle(description.name)
        showTrack(description.routeFileName)
        showCheckpoint(description)
        showSights()
    }

    // TODO remove somehow
    private fun setupInfoBox(inflater: LayoutInflater) {
        mInfoBox = InfoBox(activity)
        mInfoBox.onCreateView(inflater, infoBoxHolder)
        val expandCollapse = OnClickListener {
            isBoxExpanded = !isBoxExpanded
            mInfoBox.expandInfoBox(isBoxExpanded)
        }
        mInfoBox.setOnClickListenerForContainer(expandCollapse)
        // todo
        //mInfoBox.lockSpinner(mapPrefs!!.isInfoboxLocked)
    }

    private fun updateInfoDescription(description: Description) {
        mInfoBox.addItems(description.name, resources.getStringArray(R.array.hike_info),
                description.publicData)
    }

    private class TrackSpinnerListener(private val action: (position: Int) -> Unit) : OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View,
                                    position: Int, id: Long) {
            action.invoke(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>) = Unit
    }

}
