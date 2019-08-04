package org.szvsszke.vitezlo2018

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import org.szvsszke.vitezlo2018.map.MapDecorator
import org.szvsszke.vitezlo2018.map.MapPreferences
import org.szvsszke.vitezlo2018.domain.entity.Description
import org.szvsszke.vitezlo2018.domain.entity.Point
import org.szvsszke.vitezlo2018.map.overlay.InfoBox
import org.szvsszke.vitezlo2018.map.overlay.MapControlBox
import org.szvsszke.vitezlo2018.map.overlay.MapControlBox.MapControlListener
import org.szvsszke.vitezlo2018.presentation.map.MapViewModel
import org.szvsszke.vitezlo2018.usecase.CheckpointState
import org.szvsszke.vitezlo2018.usecase.DescriptionsState
import org.szvsszke.vitezlo2018.usecase.FindBounds
import org.szvsszke.vitezlo2018.usecase.SightsState
import org.szvsszke.vitezlo2018.usecase.TouristPathState
import org.szvsszke.vitezlo2018.usecase.TrackState
import timber.log.Timber
import javax.inject.Inject

/**
 * This fragment is responsible for loading data and displaying it
 * overlaying a map.
 */
class MapFragment : Fragment(), MapControlListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var mapDecorator: MapDecorator

    @Inject
    lateinit var findBounds: FindBounds

    private lateinit var viewModel: MapViewModel

    private lateinit var mapView: MapView

    private var mapPrefs: MapPreferences? = null

    // TODO use synthetic functions
    private var mInfoContainer: RelativeLayout? = null
    private var mControlContainer: LinearLayout? = null

    private lateinit var mInfoBox: InfoBox

    //view variables
    private var isBoxExpanded = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.getComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val inflatedView = inflater.inflate(R.layout.fragment_map, container,
                false)

        mapPrefs = MapPreferences(
                PreferenceManager.getDefaultSharedPreferences(activity))

        mapView = inflatedView.findViewById(R.id.mapView) as MapView
        mapView.onCreate(savedInstanceState)

        mapDecorator.init(activity, mapView, mapPrefs)

        mInfoContainer = inflatedView.findViewById<View>(R.id.infoBoxHolder) as RelativeLayout

        if (mapPrefs!!.isInfoboxEnabled) {
            setupInfoBox(inflater)
        } else {
            mInfoContainer!!.visibility = View.GONE
        }

        mControlContainer = inflatedView.findViewById<View>(
                R.id.controlBoxHolder) as LinearLayout
        if (mapPrefs!!.isControlBoxEnabled) {
            // setup control box
            MapControlBox(mapPrefs).apply {
                onCreateView(inflater, mControlContainer)
                setListener(this@MapFragment)
                enableUserHikeButton(false)
            }

        } else {
            mControlContainer!!.visibility = View.GONE
        }
        return inflatedView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MapViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
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
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        mapPrefs!!.isInfoboxLocked = mInfoBox!!.isSpinnerLocked
        mapPrefs!!.saveMapPreferences()
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
        mapDecorator.decorate()
        showCheckpoint(descriptions[0])
        showTrack(descriptions[0].routeFileName)
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
                        else -> Timber.e("Could not get sights")
                    }
                })
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

    private fun showTouristPaths() {
        viewModel.getTouristPath().observe(this,
                Observer { result ->
                    when(result) {
                        is TouristPathState.Data -> mapDecorator.displayTouristPaths(result.data)
                        else -> Timber.e("Could not get tourist paths")
                    }
                })
    }

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
        mapDecorator.decorate()
        showTrack(description.routeFileName)
        showCheckpoint(description)
        showSights()
    }

    // TODO remove somehow
    private fun setupInfoBox(inflater: LayoutInflater) {
        mInfoBox = InfoBox(activity)
        mInfoBox.onCreateView(inflater, mInfoContainer)
        val expandCollapse = OnClickListener {
            isBoxExpanded = !isBoxExpanded
            mInfoBox.expandInfoBox(isBoxExpanded)
        }
        mInfoBox.setOnClickListenerForContainer(expandCollapse)
        mInfoBox.lockSpinner(mapPrefs!!.isInfoboxLocked)
    }

    // TODO this is wrong
    override fun displayPreferenceChanged() {
        showSights()
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
