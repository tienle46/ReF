package com.example.tienle.ref

import android.annotation.SuppressLint
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationEngineListener, PermissionsListener {

    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var originLocation: Location
    private var locationEngine: LocationEngine? = null
    private var locationLayerPlugin: LocationLayerPlugin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        Mapbox.getInstance(this, getString(R.string.map_box_access_token))
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {mapboxMap ->
            map = mapboxMap
            enableLocation()
        }
    }

    private fun enableLocation() {
        if(PermissionsManager.areLocationPermissionsGranted(this)) {
            initLocationEngine()
            initLocationLayer()
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }
    }

    override fun onPermissionResult(granted: Boolean) {
        if(granted) {
            enableLocation()
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
    }

    @SuppressLint("MissingPermission")
    override fun onConnected() {
        locationEngine?.requestLocationUpdates()
    }

    override fun onLocationChanged(location: Location?) {
        location?.let{
            originLocation = location
            setCameraPosition(location)
        }
    }

    override fun onMapReady(mapboxMap: MapboxMap?) {

    }

    @SuppressLint("MissingPermission")
    private fun initLocationEngine() {
        locationEngine = LocationEngineProvider(this).obtainBestLocationEngineAvailable()
        locationEngine?.priority = LocationEnginePriority.HIGH_ACCURACY
        locationEngine?.activate()

        val lastLocation = locationEngine?.lastLocation
        if(lastLocation!= null) {
            originLocation = lastLocation
            setCameraPosition(lastLocation)
            Log.d("dmm","dmm")
        } else {
            locationEngine?.addLocationEngineListener(this)
        }

    }

    private fun setCameraPosition(location: Location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                LatLng(location.latitude, location.longitude),13.0))
    }

    @SuppressLint("MissingPermission")
    private fun initLocationLayer() {
        locationLayerPlugin = LocationLayerPlugin(mapView,map,locationEngine)
        locationLayerPlugin?.setLocationLayerEnabled(true)
        locationLayerPlugin?.cameraMode = CameraMode.TRACKING
        locationLayerPlugin?.renderMode = RenderMode.NORMAL

    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            locationEngine?.requestLocationUpdates()
        }
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        locationEngine?.removeLocationUpdates()
        locationLayerPlugin?.onStop()
        mapView.onStop()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        if (outState != null) {
            mapView.onSaveInstanceState(outState)
        }
    }
}

