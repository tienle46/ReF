package com.example.tienle.ref

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
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
            val intent: Intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
        locationPermission()

        val urlJSON = "http://api.openweathermap.org/data/2.5/weather?q=London&appid=${apiKey}&units=Imperial"
        getWeatherInfo().execute(urlJSON)

        getUserLocation {
            Toast.makeText(applicationContext,"lat: ${it.latitude}",Toast.LENGTH_LONG).show()
        }

//        val userLocation = getUserLocation()
//
//        if(userLocation != null) {
//            Toast.makeText(applicationContext,"lat: ${userLocation.latitude}",Toast.LENGTH_LONG).show()
//        } else {
//            Toast.makeText(applicationContext,"null cmmr",Toast.LENGTH_LONG).show()
//        }
    }

    inner class getWeatherInfo: AsyncTask<String, Void, String?>() {
        override fun doInBackground(vararg params: String?): String? {
            var content: StringBuilder = StringBuilder()
            val url = URL(params[0])
            val urlConnection = url.openConnection()
            val inputStreamReader: InputStreamReader = InputStreamReader(urlConnection.inputStream)
            val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
            var line: String = ""
            try {
                do {
                    line = bufferedReader.readLine()
                    if(line != null ) {
                        content.append(line)
                    }
                } while (line !=null)

            } catch(e:Exception) {
                Log.d("dmm", e.toString())
            }
            return content.toString()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            handleWeatherInfo(result)
        }

    }

    fun handleWeatherInfo(data: String?) {
        val weatherObject:JSONObject = JSONObject(data)
        val temp = weatherObject.getJSONObject("main").getString("temp")
        //Toast.makeText(applicationContext,temp,Toast.LENGTH_LONG).show()

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
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPermissionGranted = true
                }
            }
        }
    }

    private fun getUserLocation(callback: (Location) -> Unit) {
        val mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        var mLastKnownLocation: Location
        try {
            if(mPermissionGranted) {
                val locationResult = mFusedLocationProviderClient.lastLocation
                Log.d("wtf", locationResult.toString())

                locationResult.addOnCompleteListener(this@MainActivity, OnCompleteListener {
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

}

