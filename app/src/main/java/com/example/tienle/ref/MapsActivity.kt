package com.example.tienle.ref

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView


class MapsActivity : AppCompatActivity() {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        Mapbox.getInstance(this, getString(R.string.map_box_access_token))
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
    }
}

