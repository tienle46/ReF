@file:Suppress("DEPRECATION")

package com.example.tienle.ref

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class MainActivity : AppCompatActivity() {


    private var mPermissionGranted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val apiKey: String = getString(R.string.weather_api_key)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnToMapActivity.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
        locationPermission()

        getUserLocation {
            val userLocationLat = it.latitude
            val userLocationLng = it.longitude
            val urlWeather = "http://api.openweathermap.org/data/2.5/weather?lat=$userLocationLat&lon=$userLocationLng&appid=$apiKey&units=Imperial"
            GetWeatherInfo().execute(urlWeather)
        }

    }

    @Suppress("DEPRECATION")
    @SuppressLint("StaticFieldLeak")
    inner class GetWeatherInfo: AsyncTask<String, Void, String?>() {
        private val progressDialog = ProgressDialog(this@MainActivity)
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
        if (ContextCompat.checkSelfPermission(applicationContext,android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mPermissionGranted = true
        } else {
            val permission = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(this, permission,0)
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
        val mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        var mLastKnownLocation: Location
        try {
            if(mPermissionGranted) {
                val locationResult = mFusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this, {
                    if(it.isSuccessful) {
                        mLastKnownLocation = it.result
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

