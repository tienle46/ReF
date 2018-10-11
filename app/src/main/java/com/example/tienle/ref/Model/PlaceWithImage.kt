package com.example.tienle.ref.Model

import android.graphics.drawable.Drawable

/**
 * Created by tienle on 10/11/18.
 */
class PlaceWithImage{
    var image:Drawable? =null
    var id:Int? = 0
    var name:String = ""
    var address:String =""

    constructor(id:Int, name: String, address:String,image:Drawable?) {
        this.id = id
        this.image = image
        this.name = name
        this.address = address
    }


}