package com.example.tienle.ref

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val apiKey: String = getString(R.string.weather_api_key)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnToMapActivity.setOnClickListener {
            val intent: Intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        val urlJSON = "http://api.openweathermap.org/data/2.5/weather?q=London&appid=${apiKey}&units=Imperial"
        getWeatherInfo().execute(urlJSON)

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
//            Toast.makeText(applicationContext,result,Toast.LENGTH_LONG)
            handleWeatherInfo(result)
        }

    }

    fun handleWeatherInfo(data: String?) {
        val weatherObject:JSONObject = JSONObject(data)
        val temp = weatherObject.getJSONObject("main").getString("temp")
        Toast.makeText(applicationContext,temp,Toast.LENGTH_LONG).show()

    }
}

