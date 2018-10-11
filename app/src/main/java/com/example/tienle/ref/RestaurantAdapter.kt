package com.example.tienle.ref

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.tienle.ref.model.PlaceWithImage

/**
* Created by tienle on 10/11/18.
*/
class RestaurantAdapter(context: Context,
                        private val dataSource: ArrayList<PlaceWithImage>): BaseAdapter() {
    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private lateinit var resImage: ImageView
    private lateinit var resNameTextView: TextView
    private lateinit var resAddressTextView: TextView
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.layout_listitem, parent, false)
        resNameTextView = rowView.findViewById(R.id.resNameText)
        resAddressTextView = rowView.findViewById(R.id.resAddressText)
        resImage = rowView.findViewById(R.id.resImage)
        val restaurant = getItem(position) as PlaceWithImage
        resNameTextView.text = restaurant.name
        resAddressTextView.text = restaurant.address
        resImage.setImageDrawable(restaurant.image)

        return rowView
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }
}