package com.lms.jobmaster

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng



class FragmentMap : Fragment() {

    lateinit var mapFragment: SupportMapFragment
    lateinit var googleMap : GoogleMap


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_map,container,false)


        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap = it

            val location = LatLng(13.03,77.00)
            googleMap.addMarker(MarkerOptions().position(location).title("This is sempiternal"))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,10f))
        }

        return root
    }

}