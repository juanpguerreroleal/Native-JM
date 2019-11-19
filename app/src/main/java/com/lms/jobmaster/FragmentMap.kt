package com.lms.jobmaster

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.getbase.floatingactionbutton.FloatingActionButton
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class FragmentMap : Fragment(){


    lateinit var mapFragment: SupportMapFragment
    lateinit var googleMap : GoogleMap

    lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var db: FirebaseFirestore

    lateinit var latitud : String
    lateinit var longitud : String

    override fun onCreate(savedInstanceState: Bundle?) {

        latitud = ""
        longitud = ""

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_map,container,false)

        return view
    }

    override fun onStart() {


        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap = it

            getLocation()
        }

        super.onStart()
    }

    @SuppressLint("MissingPermission")
     fun getLocation() {

        locationManager = this.context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (hasGps || hasNetwork) {

            if (hasGps) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0F, object : LocationListener {
                    override fun onLocationChanged(location: Location?) {

                        if (location != null) {
                            locationGps = location

                            val latLng = LatLng(location.latitude, location.longitude)

                            latitud = location.latitude.toString()
                            longitud = location.longitude.toString()

                            //val markerOptions = MarkerOptions()
                            //markerOptions.position(latLng)
                            //markerOptions.title("Posición Actual")
                            //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                            //googleMap.addMarker(markerOptions)
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String?) {

                    }

                    override fun onProviderDisabled(provider: String?) {

                    }

                })

                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null)
                    locationGps = localGpsLocation
            }
            if (hasNetwork) {

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 0F, object :
                    LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationNetwork = location
                            val latLng = LatLng(location.latitude, location.longitude)

                            latitud = location.latitude.toString()
                            longitud = location.longitude.toString()

                            //val markerOptions = MarkerOptions()
                            //markerOptions.position(latLng)
                            //markerOptions.title("Posición Actual")
                            //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                            //googleMap.addMarker(markerOptions)
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String?) {

                    }

                    override fun onProviderDisabled(provider: String?) {

                    }

                })

                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation != null)
                    locationNetwork = localNetworkLocation
            }


        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    fun postDataWork() {

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        db = FirebaseFirestore.getInstance()

        var location = "$latitud,$longitud"

        val currentUser = auth.currentUser

        if(currentUser != null){



            val obj = hashMapOf(
                "expiration" to "2020",
                "location" to location,
                "owner_id" to currentUser.uid,
                "title" to "Soldador"
            )

            db.collection("Works").add(obj).addOnCompleteListener{
                Toast.makeText(activity,"Job added successfully",Toast.LENGTH_SHORT).show()
            }.addOnCanceledListener {
                Toast.makeText(activity,"Something went wrong...",Toast.LENGTH_SHORT).show()
            }

        }
    }


}