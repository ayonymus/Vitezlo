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
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.MapView
import io.ticofab.androidgpxparser.parser.GPXParser
import org.szvsszke.vitezlo2018.data.CheckpointListMapper
import org.szvsszke.vitezlo2018.data.repository.checkpoint.CheckpointRepository
import org.szvsszke.vitezlo2018.framework.localdata.checkpoint.CheckpointLoader
import org.szvsszke.vitezlo2018.framework.localdata.checkpoint.GpxCheckpointMapper
import org.szvsszke.vitezlo2018.map.MapDecorator
import org.szvsszke.vitezlo2018.map.MapPreferences
import org.szvsszke.vitezlo2018.map.data.DescriptionsCache
import org.szvsszke.vitezlo2018.map.model.TrackDescription
import org.szvsszke.vitezlo2018.map.overlay.InfoBox
import org.szvsszke.vitezlo2018.map.overlay.MapControlBox
import org.szvsszke.vitezlo2018.map.overlay.MapControlBox.MapControlListener
import org.szvsszke.vitezlo2018.presentation.map.MapViewModel
import org.szvsszke.vitezlo2018.usecase.CheckpointState
import org.szvsszke.vitezlo2018.usecase.GetCheckpoints
import timber.log.Timber

/**
 * This fragment is responsible for loading data and displaying it
 * overlaying a map.
 */
class MapFragment : Fragment(), MapControlListener {

    // TODO remove lateinit
    private lateinit var viewModel: MapViewModel

    // TODO this should be injected to ViewModelFactory
    private lateinit var getCheckpoints: GetCheckpoints

    private lateinit var mapDecorator: MapDecorator

    private lateinit var mapView: MapView

    private var mapPrefs: MapPreferences? = null

    // TODO use synthetic functions
    private var mInfoContainer: RelativeLayout? = null
    private var mControlContainer: LinearLayout? = null

    private var mInfoBox: InfoBox? = null

    private var mControlBox: MapControlBox? = null

    private var mDescriptions: DescriptionsCache? = null

    //view variables
    private var isBoxExpanded = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instantiateObjects()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val inflatedView = inflater.inflate(R.layout.fragment_map, container,
                false)

        App.getComponent().inject(this)

        mapPrefs = MapPreferences(
                PreferenceManager.getDefaultSharedPreferences(activity))

        mapView = inflatedView.findViewById(R.id.mapView) as MapView
        mapView.onCreate(savedInstanceState)

        mapDecorator = MapDecorator(activity, mapView, mapPrefs)

        mDescriptions = DescriptionsCache(activity)

        mInfoContainer = inflatedView.findViewById<View>(R.id.infoBoxHolder) as RelativeLayout

        if (mapPrefs!!.isInfoboxEnabled) {
            setupInfobox(inflater)
        } else {
            mInfoContainer!!.visibility = View.GONE
        }

        showTrackInfo()

        mControlContainer = inflatedView.findViewById<View>(
                R.id.controlBoxHolder) as LinearLayout
        if (mapPrefs!!.isControlBoxEnabled) {
            // setup control box
            mControlBox = MapControlBox(mapPrefs)
            mControlBox!!.onCreateView(inflater, mControlContainer)
            mControlBox!!.setListener(this)


            mControlBox!!.enableUserHikeButton(false)

        } else {
            mControlContainer!!.visibility = View.GONE
        }
        return inflatedView
    }

    // TODO these should be injected
    private fun instantiateObjects() {

        val checkpointLoader = CheckpointLoader(context!!.assets,
                GPXParser(),
                GpxCheckpointMapper())
        val checkpointRepository = CheckpointRepository(checkpointLoader)

        getCheckpoints = GetCheckpoints(checkpointRepository, CheckpointListMapper())

        viewModel = ViewModelProviders.of(this)
                .get(MapViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        mDescriptions!!.getDescription(mapPrefs!!.selectedTrackIndex)?.let { description ->
            mapDecorator.decorate(description)
            showCheckpoint(description)
        }
    }

    private fun showCheckpoint(description: TrackDescription) {
        viewModel.getCheckpoints(getCheckpoints, description.checkPointIDs).observe(this,
                Observer<CheckpointState> { result ->
                    when (result) {
                        is CheckpointState.Data -> mapDecorator.drawCheckpoints(result.data)
                        else -> Timber.e("Could not get errors")
                    }
                })
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

    private fun setCurrentTrack(trackNr: Int) {
        mapPrefs!!.selectedTrackIndex = trackNr
        val descriptions = mDescriptions!!.acquireData()
        if (descriptions != null) {
            mDescriptions!!.getDescription(trackNr)?.let { it ->
                updateViews(it)
            }
        } else {
            mDescriptions!!.setDataLoadedListener { loadedDescriptions ->
                loadedDescriptions[trackNr]?.let { updateViews(it) }
            }
        }
    }

    private fun updateViews(description: TrackDescription) {
        mapDecorator.decorate(description)
        showCheckpoint(description)
        activity?.title = description.name
        mInfoBox?.setTitle(description.name)
    }

    private fun setupInfobox(inflater: LayoutInflater) {
        mInfoBox = InfoBox(activity)
        mInfoBox!!.onCreateView(inflater, mInfoContainer)
        val expandCollapse = OnClickListener {
            isBoxExpanded = !isBoxExpanded
            mInfoBox!!.expandInfoBox(isBoxExpanded)
        }
        mInfoBox!!.setOnClickListenerForContainer(expandCollapse)
        mInfoBox!!.lockSpinner(mapPrefs!!.isInfoboxLocked)
    }

    private fun setTrackSpinner() {
        if (mDescriptions!!.acquireData() != null) {
            mInfoBox!!.setupSpinner(mDescriptions!!.names,
                    TrackSpinnerListener(),
                    mapPrefs!!.selectedTrackIndex)
        } else {
            mDescriptions!!.setDataLoadedListener {
                mInfoBox!!.setupSpinner(mDescriptions!!.names,
                        TrackSpinnerListener(),
                        mapPrefs!!.selectedTrackIndex)
            }
        }
    }

    override fun displayPreferenceChanged() {
        mDescriptions!!.getDescription(mapPrefs!!.selectedTrackIndex)?.let { description ->
            mapDecorator.decorate(description)
            showCheckpoint(description)
        }

        showTrackInfo()
        mapDecorator.removeUserPath()
    }

    private fun showTrackInfo() {
        setTrackSpinner()
        updateTrackInfo()
    }

    private fun updateTrackInfo() {
        //setCurrentTrack(mapPrefs.getSelectedTrackIndex());
        val desc = mDescriptions!!.getDescription(
                mapPrefs!!.selectedTrackIndex)

        if (desc != null) {
            mInfoBox!!.addItems(desc.name,
                    resources.getStringArray(R.array.hike_info),
                    desc.publicData)
        } else {
            mDescriptions!!.setDataLoadedListener {
                val desc = mDescriptions!!.getDescription(
                        mapPrefs!!.selectedTrackIndex)
                mInfoBox!!.addItems(desc!!.name,
                        resources.getStringArray(R.array.hike_info),
                        desc.publicData)
            }
        }
    }

    private inner class TrackSpinnerListener : OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View,
                                    position: Int, id: Long) {
            setCurrentTrack(position)
            updateTrackInfo()
        }

        override fun onNothingSelected(parent: AdapterView<*>) {}
    }

}