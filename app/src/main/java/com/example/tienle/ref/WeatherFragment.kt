@file:Suppress("DEPRECATION")

package com.example.tienle.ref

import android.annotation.SuppressLint
import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.tienle.ref.Common.Common
import com.example.tienle.ref.Common.Helper
import com.example.tienle.ref.Model.OpenWeatherMap
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_weather.*

/**
* Created by tienle on 10/7/18.
*/
class WeatherFragment: Fragment(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private var mPermissionGranted: Boolean = false
    private lateinit var chooseResBtn:Button
    private lateinit var tempTextView: TextView
    private lateinit var placeTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var windTextView: TextView
    private lateinit var weatherIconImgView: ImageView
    private var mGoogleApiClient:GoogleApiClient? = null
    private var mLocationRequest: LocationRequest?=null
    internal var openWeatherMap = OpenWeatherMap()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView:View = inflater!!.inflate(R.layout.fragment_weather, container, false)
        chooseResBtn = rootView.findViewById(R.id.chooseResBtn)
        tempTextView = rootView.findViewById(R.id.tempTextView)
        placeTextView = rootView.findViewById(R.id.placeTextView)
        humidityTextView = rootView.findViewById(R.id.humidityTextView)
        windTextView = rootView.findViewById(R.id.windTextView)
        weatherIconImgView = rootView.findViewById(R.id.weatherIconImgView)

        chooseResBtn.setOnClickListener {
            val intent = Intent(activity, MapsActivity::class.java)
            startActivity(intent)
        }
        buildGoogleApiClient()



        val fragmentManager: FragmentManager = fragmentManager

        chooseResBtn.setOnClickListener {
            val fragmentTransaction:FragmentTransaction = fragmentManager.beginTransaction()
            val restaurantListFragment = RestaurantListFragment()
            fragmentTransaction.replace(R.id.content, restaurantListFragment)
            fragmentTransaction.commit()
        }
        return rootView
    }

    @SuppressLint("SetTextI18n")
    fun handleWeatherInfo(data: OpenWeatherMap?) {
        var temp = convertTempFtoC(data!!.main!!.temp) + "°C"
        placeTextView.text = "Welcome to \n"+data!!.name!!
        tempTextView.text = temp
        humidityTextView.text = data.main!!.humidity.toString() + "%"
        windTextView.text = data.wind!!.speed.toString() +"\nkm/h"
        Picasso.get().load(Common.getImage(data.weather!![0].icon!!)).into(weatherIconImgView);

    }

    private fun locationPermission() {
        if (ContextCompat.checkSelfPermission(activity,android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mPermissionGranted = true
            mGoogleApiClient!!.connect()
        } else {
            mGoogleApiClient!!.disconnect()
            val permission = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissions( permission,0)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mPermissionGranted = false
        when(requestCode) {
            0 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPermissionGranted = true
                    mGoogleApiClient!!.connect()
                }
            }
        }
    }

    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
    }


    private fun convertTempFtoC(input:Double) : String {
        val output = (input-32.0)/1.8
        return output.toInt().toString()
    }

    override fun onConnected(p0: Bundle?) {
        locationPermission()

        if(mPermissionGranted) {
            createLocationRequest()

        }
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 10000
        mLocationRequest!!.fastestInterval = 5000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        try {
            if(mPermissionGranted) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this)
            }
        } catch (e:SecurityException) {
            Log.e("Error:", e.message)
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        mGoogleApiClient!!.connect()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.e("Error:", p0.errorMessage)
    }

    override fun onLocationChanged(p0: Location?) {
        GetWeatherInfo().execute(Common.apiRequest(p0!!.latitude.toString(), p0!!.longitude.toString()))
    }

    override fun onStart() {
        super.onStart()
        if(mGoogleApiClient != null) {
            mGoogleApiClient!!.connect()
        }
    }

    override fun onDestroy() {
        mGoogleApiClient!!.disconnect()
        super.onDestroy()
    }

    inner class GetWeatherInfo(): AsyncTask<String, Void, String>() {
        private var pd = ProgressDialog(activity)
        override fun doInBackground(vararg params: String?): String {
            var stream: String?
            var urlString = params[0]

            val http = Helper()
            stream = http.getHttpData(urlString)
            return stream

        }

        override fun onPreExecute() {
            pd.setTitle("Loading")
            pd.show()
            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(result!!.contains("Error: Not found city"))
                pd.dismiss()

            val gson = Gson()
            val mType = object:TypeToken<OpenWeatherMap>(){}.type

            openWeatherMap = gson.fromJson<OpenWeatherMap>(result,mType)
            handleWeatherInfo(openWeatherMap)
            pd.dismiss()
        }

    }
}