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
import android.widget.TextView
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

/**
 * Created by tienle on 10/7/18.
 */
class WeatherFragment: Fragment() {
    private var mPermissionGranted: Boolean = false
    private lateinit var btnToMapActivity:Button
    private lateinit var chooseResBtn:Button
    private lateinit var tempTextView: TextView
    private lateinit var placeTextView: TextView
    private lateinit var weatherTextView: TextView

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView:View = inflater!!.inflate(R.layout.fragment_weather, container, false)
        val apiKey: String = getString(R.string.weather_api_key)
        btnToMapActivity = rootView.findViewById(R.id.btnToMapActivity)
        chooseResBtn = rootView.findViewById(R.id.chooseResBtn)
        tempTextView = rootView.findViewById(R.id.tempTextView)
        placeTextView = rootView.findViewById(R.id.placeTextView)
        weatherTextView = rootView.findViewById(R.id.weatherTextView)

        btnToMapActivity.setOnClickListener {
            val intent = Intent(activity, MapsActivity::class.java)
            startActivity(intent)
        }
        locationPermission()

        getUserLocation {
            val userLocationLat = it.latitude
            val userLocationLng = it.longitude
            val urlWeather = "http://api.openweathermap.org/data/2.5/weather?lat=$userLocationLat&lon=$userLocationLng&appid=$apiKey&units=Imperial"
            GetWeatherInfo().execute(urlWeather)
        }

        val fragmentManager: FragmentManager = fragmentManager

        chooseResBtn.setOnClickListener {
            val fragmentTransaction:FragmentTransaction = fragmentManager.beginTransaction()
            val restaurantListFragment = RestaurantListFragment()
            fragmentTransaction.replace(R.id.content, restaurantListFragment)
            fragmentTransaction.commit()
        }
        return rootView
    }

    @Suppress("DEPRECATION")
    @SuppressLint("StaticFieldLeak")
    inner class GetWeatherInfo: AsyncTask<String, Void, String?>() {
        private val progressDialog = ProgressDialog(activity)
        @SuppressLint("LogNotTimber")
        override fun doInBackground(vararg params: String?): String? {
            val content = StringBuilder()
            val url = URL(params[0])
            val urlConnection = url.openConnection()
            val inputStreamReader = InputStreamReader(urlConnection.inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            var line: String
            try {
                do {
                    line = bufferedReader.readLine()
                    if(line != null ) {
                        content.append(line)
                    }
                } while (true)

            } catch(e:Exception) {
                Log.d("dmm", e.toString())
            }
            return content.toString()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            handleWeatherInfo(result)
            progressDialog.dismiss()
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog.setMessage("Loading")
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

    }

    @SuppressLint("SetTextI18n")
    fun handleWeatherInfo(data: String?) {
        val weatherObject = JSONObject(data)
        val temp = convertTempFtoC(weatherObject.getJSONObject("main").getLong("temp"))
        val placeName = weatherObject.getString("name")
        val weather = weatherObject.getJSONArray("weather").getJSONObject(0).getString("main")
        placeTextView.text = placeName
        tempTextView.text = "${temp}°C"
        weatherTextView.text = weather

    }

    private fun locationPermission() {
        if (ContextCompat.checkSelfPermission(activity,android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mPermissionGranted = true
        } else {
            val permission = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(activity, permission,0)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mPermissionGranted = false
        when(requestCode) {
            0 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPermissionGranted = true
                }
            }
        }
    }

    @SuppressLint("LogNotTimber")
    private fun getUserLocation(callback: (Location) -> Unit) {
        val mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
        var mLastKnownLocation: Location
        try {
            if(mPermissionGranted) {
                val locationResult = mFusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(activity, {
                    if(it.isSuccessful) {
                        mLastKnownLocation = it.result!!
                        callback(mLastKnownLocation)
                    }
                })
            }
        } catch (e: SecurityException) {
            Log.e("dmm", e.message)
        }
    }

    private fun convertTempFtoC(input:Long) : String {
        val output = (input-32.0)/1.8
        return output.toInt().toString()
    }
}