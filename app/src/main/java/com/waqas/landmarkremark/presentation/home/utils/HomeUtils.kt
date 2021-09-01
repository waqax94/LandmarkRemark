package com.waqas.landmarkremark.presentation.home.utils

import android.content.Context
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.waqas.landmarkremark.R
import com.waqas.landmarkremark.domain.home.entity.NoteEntity

object HomeUtils {
    fun drawCurrentLocationMarker(context: Context, googleMap: GoogleMap, latLng: LatLng){
        val options = MarkerOptions().position(latLng).
        title(context.getString(R.string.my_location_title)).
        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0F))
        googleMap.addMarker(options).showInfoWindow()

    }

    fun drawCurrentUserMarker(googleMap: GoogleMap, latLng: LatLng, username: String,note: String){
        val options = MarkerOptions().position(latLng).
        title(username).
        snippet(note)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        googleMap.addMarker(options)
    }

    fun drawUserMarker(googleMap: GoogleMap, latLng: LatLng, username: String, note: String){
        val options = MarkerOptions().position(latLng).
        title(username).
        snippet(note)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        googleMap.addMarker(options)
    }

    fun setMarkers(notes: List<NoteEntity>, username: String, googleMap: GoogleMap){
        for(note in notes){
            if(note.userName == username){
                drawCurrentUserMarker(googleMap, getLatLng(note.latitude,note.longitude),note.userName, note.notes)
            }
            else{
                drawUserMarker(googleMap, getLatLng(note.latitude,note.longitude),note.userName, note.notes)
            }
        }
    }

    fun setCurrentUserMarkers(notes: List<NoteEntity>, username: String, googleMap: GoogleMap){
        for(note in notes){
            if(note.userName == username){
                drawCurrentUserMarker(googleMap, getLatLng(note.latitude,note.longitude),note.userName, note.notes)
            }
        }
    }

    fun getLatLng(lat: Double, lng: Double): LatLng{
        return LatLng(lat,lng)
    }
}