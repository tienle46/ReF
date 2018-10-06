package com.example.tienle.ref.Model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import io.reactivex.annotations.NonNull

/**
 * Created by tienle on 10/6/18.
 */

@Entity(tableName = "places")
class Place{
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name ="id")
    var id:Int =0

    @ColumnInfo(name ="name")
    var name:String? = null

    @ColumnInfo(name ="address")
    var address:String? = null

    @ColumnInfo(name ="path")
    var path:String? = null

    @ColumnInfo(name ="category")
    var category:String? = null

    @ColumnInfo(name ="lattitude")
    var lattitude:Long = 0

    @ColumnInfo(name ="longtitude")
    var longtitude:Long = 0

    override fun toString(): String {
        return StringBuilder(name).
                append("\n").
                append(address).
                toString()
    }
}
