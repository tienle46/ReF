package com.example.tienle.ref.Model

/**
 * Created by tienle on 10/10/18.
 */
class OpenWeatherMap{
    var coord:Coord?=null
    var weather:List<Weather>? = null
    var base:String? =null
    var main:Main? = null
    var wind:Wind? = null
    var rain:Rain? = null
    var cloud: Cloud? = null
    var dt:Int=0
    var sys:Sys?=null
    var id: Int=0
    var name:String?=null
    var cod:Int=0

    constructor() {}

    constructor(coord: Coord?, weather: List<Weather>?, base: String?, main: Main?, wind: Wind?, rain: Rain?, cloud: Cloud?, dt: Int, sys: Sys?, id: Int, name: String?, cod: Int) {
        this.coord = coord
        this.weather = weather
        this.base = base
        this.main = main
        this.wind = wind
        this.rain = rain
        this.cloud = cloud
        this.dt = dt
        this.sys = sys
        this.id = id
        this.name = name
        this.cod = cod
    }

}