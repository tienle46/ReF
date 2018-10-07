package com.example.tienle.ref

import android.app.Fragment
import android.app.ListFragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.example.tienle.ref.Database.PlaceRepository
import com.example.tienle.ref.Local.PlaceDataSource
import com.example.tienle.ref.Local.PlaceDatabase
import com.example.tienle.ref.Model.Place
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_restaurent_list.*

/**
 * Created by tienle on 10/6/18.
 */

class RestaurantListFragment: Fragment() {

    lateinit var adapter:ArrayAdapter<*>
     var placeList: MutableList<Place> = ArrayList()

    private var compositeDisposable: CompositeDisposable?=null
    private var placeRepository:PlaceRepository?=null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView:View = inflater!!.inflate(R.layout.fragment_restaurent_list, container, false)
        compositeDisposable = CompositeDisposable()
        adapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, placeList)
        val listRestaurant:ListView = rootView.findViewById<ListView>(R.id.listRestaurant)
        listRestaurant!!.adapter = adapter

        val placeDatabase = PlaceDatabase.getInstance(activity)
        placeRepository = PlaceRepository.getInstance(PlaceDataSource.getInstance(placeDatabase.placeDAO()))

        loadData()

        return rootView
    }

    private fun loadData() {
        val disposable = placeRepository!!.allPlaces
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({places -> onGetAllPlaceSuccess(places)})
                {
                    throwable -> Toast.makeText(activity,""+throwable.message, Toast.LENGTH_LONG).show()
                }
        compositeDisposable!!.add(disposable)
    }

    private fun onGetAllPlaceSuccess(places: List<Place>) {
        placeList.clear()
        placeList.addAll(places)
        adapter.notifyDataSetChanged()

    }

//    override fun onDestroy() {
//        super.onDestroy()
//        compositeDisposable!!.clear()
//
//    }
}