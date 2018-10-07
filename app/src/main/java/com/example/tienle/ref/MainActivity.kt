package com.example.tienle.ref

import android.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val weatherFragment = WeatherFragment()
        fragmentTransaction.add(R.id.content, weatherFragment)
        fragmentTransaction.commit()
    }
}

