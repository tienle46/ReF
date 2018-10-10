package com.example.tienle.ref.Common

/**
 * Created by tienle on 10/10/18.
 */

object Common{
    val API_KEY = "e73d5e58219955c23e3be2f74a70050c"
    val API_LINK = "http://api.openweathermap.org/data/2.5/weather"

    fun apiRequest(lat:String,lon:String):String {
        var sb = StringBuilder(API_LINK)
        sb.append("?lat=${lat}&lon=${lon}&APPID=${API_KEY}&units=metric")
        return sb.toString()
    }

    fun getImage(icon:String):String {
        return "http://api.openweathermap.org/img/w/${icon}.png"
    }
}