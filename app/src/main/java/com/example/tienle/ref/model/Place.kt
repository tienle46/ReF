package com.example.tienle.ref.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import io.reactivex.annotations.NonNull
import java.io.Serializable

/**
* Created by tienle on 10/6/18.
*/

@Entity(tableName = "places")
class Place: Serializable{
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name ="ID")
    var id:Int =0

    @NonNull
    @ColumnInfo(name ="Name")
    lateinit var name:String

    @NonNull
    @ColumnInfo(name ="Address")
    lateinit var address:String

    @NonNull
    @ColumnInfo(name ="Path")
    lateinit var path:String

    @NonNull
    @ColumnInfo(name ="Category")
    var category:Int = 1

    @NonNull
    @ColumnInfo(name ="Lattitude")
    var lattitude:Float = 0F

    @NonNull
    @ColumnInfo(name ="Longtitude")
    var longtitude:Float = 0F

    override fun toString(): String {
        return StringBuilder(name).
                append("\n").
                append(address).
                toString()
    }
}
