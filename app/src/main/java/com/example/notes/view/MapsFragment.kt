package com.example.notes.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.notes.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_maps.*
import java.util.*

class MapsFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        const val MY_PERMISSION_FINE_LOCATION = 101
        var addFragment = ""
        var updeta: Int = 0
        val bundle = Bundle()
    }


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener { marker ->
            if (marker.isInfoWindowShown) {
                marker.hideInfoWindow()
            } else {
                marker.showInfoWindow()
            }
            true
        }
        setUpMap()
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSION_FINE_LOCATION)
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)
              if (addFragment == "1" && AddFragment.updateLat!=0.0) {
                    placeMarkerOnMap(LatLng(AddFragment.updateLat, AddFragment.updateLng))
                    mMap.addMarker(MarkerOptions().position(LatLng(AddFragment.updateLat, AddFragment.updateLng))
                            .title("Location"))
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(AddFragment.updateLat, AddFragment.updateLng), 5f))
                }
                else if(AddFragment.updateLat==0.0 ) {
                    placeMarkerOnMap(currentLatLong)
                    mMap.addMarker(MarkerOptions().position(LatLng(location.latitude, location.longitude))
                            .title("Location"))
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 5f))
                }
            }
        }
    }

    private fun placeMarkerOnMap(currentLatLong: LatLng) {
        val address: String
        val lat = currentLatLong.latitude
        val lng = currentLatLong.longitude
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(lat, lng, 1)
        if (addresses.isNotEmpty()) {
            address = addresses[0].getAddressLine(0)
            val markerOptions = MarkerOptions().position(currentLatLong)
            markerOptions.title("Location")
            mMap.setOnMapClickListener {
                mMap.clear()
                mMap.addMarker(MarkerOptions().position(LatLng(it.latitude, it.longitude))
                        .title("Location"))
                placeMarkerOnMap(LatLng(it.latitude, it.longitude))
            }
                mapFlatingActionButton.setOnClickListener {
                    updeta=1
//                    bundle.putString("mapsArg", address)
                    AddFragment.mapsAddress=address
//                    findNavController().navigate(R.id.addFragment, bundle)
//                    MAIN.navController.navigate(R.id.action_mapsFragment_to_addFragment, bundle)
                    activity?.onBackPressed()
            }
        } else {
            Toast.makeText(requireContext(), "Ако Ягон шахар гирид.Илтимос) ", Toast.LENGTH_SHORT).show()
        }
    }
}

