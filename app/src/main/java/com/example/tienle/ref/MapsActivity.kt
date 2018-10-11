package com.example.tienle.ref

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.location.Location
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.tienle.ref.Model.Place
import com.example.tienle.ref.R.id.mapView
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationEngineListener, PermissionsListener {

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
    }

    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var originLocation: Location
    private lateinit var originPosition: Point
    private lateinit var destinationPosition: Point
    private lateinit var navigateButton: Button
    private lateinit var getRouteBtn: Button
    private var currentRoute: DirectionsRoute?=null

    private var locationEngine: LocationEngine? = null
    private var locationLayerPlugin: LocationLayerPlugin? = null
    private var destinationMarker: Marker? = null
    private var navigationMapRoute: NavigationMapRoute? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        Mapbox.getInstance(this, getString(R.string.map_box_access_token))
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        navigateButton = findViewById(R.id.navigateButton)
        getRouteBtn = findViewById(R.id.getRouteBtn)
        mapView.getMapAsync {mapboxMap ->
            map = mapboxMap
            val intent = intent
            val place: Place = intent.getSerializableExtra("clickedPlace") as Place
            enableLocation()
            destinationMarker = map.addMarker(MarkerOptions().position(LatLng(place!!.lattitude.toDouble(),place.longtitude.toDouble())))
            destinationPosition = Point.fromLngLat(place.longtitude.toDouble(),place.lattitude.toDouble())
            navigateButton.setOnClickListener {
                if (currentRoute == null) {
                    Toast.makeText(this, "Please get direction first", Toast.LENGTH_SHORT).show()
                } else {
                    val options = NavigationLauncherOptions.builder()
                            .directionsRoute(currentRoute)
                            .shouldSimulateRoute(true)
                            .build()
                    NavigationLauncher.startNavigation(this, options)
                }
            }
            getRouteBtn.setOnClickListener {
                getRoute(originPosition,destinationPosition)
            }
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


    @SuppressLint("MissingPermission")
    override fun onConnected() {
        locationEngine?.requestLocationUpdates()
    }

    override fun onLocationChanged(location: Location?) {
        location?.let{
            originLocation = location
            originPosition = Point.fromLngLat(originLocation.longitude,originLocation.latitude)
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
            originPosition = Point.fromLngLat(originLocation.longitude,originLocation.latitude)
            setCameraPosition(lastLocation)
        } else {
            locationEngine?.addLocationEngineListener(this)
        }

    }

    private fun getRoute(origin:Point, destination:Point) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken()!!)
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(object : Callback<DirectionsResponse> {
                    override fun onFailure(call: Call<DirectionsResponse>?, t: Throwable?) {
                        Log.e("failure", "Error:${t?.message}")
                    }

                    override fun onResponse(call: Call<DirectionsResponse>?, response: Response<DirectionsResponse>?) {

                        val routeResponse = response ?: return
                        val body =routeResponse.body() ?: return
                        if (body.routes().count() == 0 ) {
                            return
                        }
                        if (navigationMapRoute != null) {

                            navigationMapRoute?.removeRoute()

                        } else {

                            navigationMapRoute = NavigationMapRoute(null,mapView,map)
                        }
                        navigationMapRoute?.addRoute(body.routes().first())
                        currentRoute = body.routes().first()
                    }
                })

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

