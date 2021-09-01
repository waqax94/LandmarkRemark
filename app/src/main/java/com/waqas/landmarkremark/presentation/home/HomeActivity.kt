package com.waqas.landmarkremark.presentation.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.waqas.landmarkremark.BuildConfig
import com.waqas.landmarkremark.R
import com.waqas.landmarkremark.databinding.ActivityHomeBinding
import com.waqas.landmarkremark.domain.home.entity.NoteEntity
import com.waqas.landmarkremark.infra.util.AppConstants
import com.waqas.landmarkremark.infra.util.SharedPrefs
import com.waqas.landmarkremark.presentation.home.utils.HomeUtils
import com.waqas.landmarkremark.presentation.home.viewmodels.HomeActivityState
import com.waqas.landmarkremark.presentation.home.viewmodels.HomeViewModel
import com.waqas.landmarkremark.presentation.usersetup.UserSetupActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var prefs: SharedPrefs
    private var currentLocation: Location? = null
    private lateinit var mMap : GoogleMap
    private val viewModel: HomeViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = SharedPrefs(applicationContext)
        observe()
        getAllNotes()
        filterNotes()
        initMap(savedInstanceState)
        showMyLocation()
        saveNotes()
        logout()
    }
    private fun getAllNotes() {
        viewModel.getAllNotes()
    }
    private fun observe() {
        viewModel.mState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .onEach { state -> handleState(state) }
                .launchIn(lifecycleScope)
    }

    private fun handleState(state: HomeActivityState){
        when(state) {
            is HomeActivityState.Init -> Unit
            is HomeActivityState.IsLoading -> handleLoading(state.isLoading)
            is HomeActivityState.SuccessResponse -> handleSuccessResponse(state.notes)
            is HomeActivityState.ErrorResponse -> handleErrorState(state.rawResponse)
            is HomeActivityState.AdditionSuccess -> handleAdditionSuccess()
            is HomeActivityState.AdditionError -> handleAdditionError()
        }
    }

    private fun handleLoading(set: Boolean){
        if(set){
            binding.homeProgress.visibility = View.VISIBLE
        }
        else {
            binding.homeProgress.visibility = View.GONE
        }

    }

    private fun handleSuccessResponse(notes: List<NoteEntity>){
        HomeUtils.setMarkers(notes,getCurrentUser(),mMap)
    }

    private fun handleErrorState(msg: String){
    }

    private fun handleAdditionSuccess() {
        Toast.makeText(this,getString(R.string.additon_successful),Toast.LENGTH_SHORT).show()
        getAllNotes()
    }

    private fun handleAdditionError(){
        getAllNotes()
    }

    private fun initMap(savedInstanceState: Bundle?) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        var mapViewBundle: Bundle? = null

        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(BuildConfig.MAPS_KEY)
        }

        binding.mapView.onCreate(mapViewBundle)

        binding.mapView.getMapAsync(this)
    }

    private fun showMyLocation(){
        binding.homeMyLocBtn.setOnClickListener {
            getLocation()
        }
    }

    private fun saveNotes(){
        binding.saveNotesHereButton.setOnClickListener {
            if(currentLocation != null){
                HomeUtils.saveDialogSetup(this,viewModel,currentLocation,getCurrentUser())
            }
            else {
                Toast.makeText(this, getString(R.string.no_location_available), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onResume() {
        super.onResume()
        getLocation()
        getAllNotes()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        var mapViewBundle = outState.getBundle(BuildConfig.MAPS_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(BuildConfig.MAPS_KEY, mapViewBundle)
        }
        binding.mapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if(currentLocation != null){
            val latLng = LatLng(currentLocation?.latitude!!, currentLocation?.longitude!!)
            HomeUtils.drawCurrentLocationMarker(this,mMap, latLng)
        }
        HomeUtils.setMarkers(viewModel.getCachedNotes(),getCurrentUser(),mMap)
    }

    private fun logout(){
        binding.homeLogoutBtn.setOnClickListener {
            resetUser()
            startActivity(Intent(this,UserSetupActivity::class.java))
            finish()
        }
    }


    private fun getLocation(){
        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    LOCATION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener {
            if(it != null){
                currentLocation = it
                onMapReady(mMap)
            }
        }
    }

    private fun filterNotes(){
        binding.homeShowNotesSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            HomeUtils.removeAllMarkers()
            if(isChecked){
                HomeUtils.setCurrentUserMarkers(viewModel.getCachedNotes(),getCurrentUser(),mMap)
            }
            else{
                HomeUtils.setMarkers(viewModel.getCachedNotes(),getCurrentUser(),mMap)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == LOCATION_REQUEST_CODE && grantResults.isNotEmpty()){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocation()
            }
            else{
                Toast.makeText(this@HomeActivity, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentUser(): String{
        return  prefs.get(AppConstants.USERNAME, String::class.java)
    }

    private fun resetUser(){
        prefs.clear(AppConstants.USERNAME)
    }

    companion object {
        private const val LOCATION_REQUEST_CODE = 111
        var markerList : ArrayList<Marker?> = ArrayList()
    }
}