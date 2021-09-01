package com.waqas.landmarkremark.presentation.home.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.location.Location
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.waqas.landmarkremark.R
import com.waqas.landmarkremark.domain.home.entity.NoteEntity
import com.waqas.landmarkremark.infra.util.AppConstants
import com.waqas.landmarkremark.presentation.common.Input
import com.waqas.landmarkremark.presentation.home.HomeActivity
import com.waqas.landmarkremark.presentation.home.viewmodels.HomeViewModel
import java.lang.Exception

object HomeUtils {
    fun drawCurrentLocationMarker(context: Context, googleMap: GoogleMap, latLng: LatLng){
        val options = MarkerOptions().position(latLng).
        title(context.getString(R.string.my_location_title)).
        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5.0F))
        googleMap.addMarker(options).showInfoWindow()

    }

    private fun drawCurrentUserMarker(googleMap: GoogleMap, latLng: LatLng, username: String, note: String){
        val options = MarkerOptions().position(latLng).
        title(username).
        snippet(note)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        HomeActivity.markerList.add(googleMap.addMarker(options))

    }

    private fun drawUserMarker(googleMap: GoogleMap, latLng: LatLng, username: String, note: String){
        val options = MarkerOptions().position(latLng).
        title(username).
        snippet(note)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        HomeActivity.markerList.add(googleMap.addMarker(options))
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

    fun removeAllMarkers(){
        for(marker in HomeActivity.markerList){
            marker?.isVisible = false
            marker?.remove()
        }
    }

    fun saveDialogSetup(activity: Activity, viewModel: HomeViewModel,location: Location?,username: String) {
        val saveDialogBuilder = AlertDialog.Builder(activity)
        val saveDialogView =
                LayoutInflater.from(activity)
                        .inflate(R.layout.save_note_dialog_layout, null)

        val saveDialog = saveDialogBuilder.setView(saveDialogView).create()
        saveDialog.setCancelable(false)
        saveDialogView.findViewById<MaterialButton>(R.id.save_note_btn).setOnClickListener {
            val noteText = saveDialogView.findViewById<TextInputEditText>(R.id.note_edit_text).text.toString()
            val noteEntity = NoteEntity(location!!.latitude,location.longitude,noteText,username)
            viewModel.addNote(noteEntity)
            saveDialog.dismiss()
        }
        saveDialogView.findViewById<MaterialButton>(R.id.cancel_note_btn).setOnClickListener {
            Input.hideKeybord(activity)
            saveDialog.dismiss()
        }

        saveDialog.show()
    }
}