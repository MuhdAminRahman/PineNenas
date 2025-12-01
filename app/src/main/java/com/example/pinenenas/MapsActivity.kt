package com.example.pinenenas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.pinenenas.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.PlaceAutocomplete
import com.google.android.libraries.places.widget.PlaceAutocompleteActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var currentMarker: Marker? = null

    private var mode: String? = null
    private var initialLat: Double = 0.0
    private var initialLng: Double = 0.0

    private lateinit var placesClient: PlacesClient
    private lateinit var placeAutocompleteLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mode = intent.getStringExtra("MODE")
        initialLat = intent.getDoubleExtra("latitude", 3.1390) // Default to Kuala Lumpur
        initialLng = intent.getDoubleExtra("longitude", 101.6869)

        initializePlaces()

        // As per the latest documentation: Register the ActivityResultLauncher
        placeAutocompleteLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    // Get the prediction object
                    val prediction = PlaceAutocomplete.getPredictionFromIntent(intent)
                    // Fetch the full place details using the place ID from the prediction
                    fetchPlaceDetails(prediction)
                }
            } else if (result.resultCode == PlaceAutocompleteActivity.RESULT_ERROR) {
                val status = PlaceAutocomplete.getResultStatusFromIntent(result.data!!)
                Log.e("MapsActivity", "Place Autocomplete error: ${status?.statusMessage}")
                Toast.makeText(this, "Search error: ${status?.statusMessage}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonLaunchSearch.setOnClickListener {
            launchAutocomplete()
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initializePlaces() {
        try {
            // Correct and safe way to access application metadata
            val apiKey = BuildConfig.MAPS_API_KEY

            // Also check for placeholder value
            // Use the new initialization method
            Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)
            placesClient = Places.createClient(this)
        } catch (e: Exception) {
            Log.e("MapsActivity", "Cannot load API Key", e)
            Toast.makeText(this, "Error loading API Key.", Toast.LENGTH_LONG).show()
        }
    }

    private fun launchAutocomplete() {
        // Create a session token
        val sessionToken = AutocompleteSessionToken.newInstance()
        // Create the intent using the modern builder
        val autocompleteIntent = PlaceAutocomplete.createIntent(this) {
            // Pass the session token
            setAutocompleteSessionToken(sessionToken)
            // Optional: You can filter by country
            setCountries(listOf("MY")) // Example: Restrict search to Malaysia
        }
        // Launch the activity for a result
        placeAutocompleteLauncher.launch(autocompleteIntent)
    }

    private fun fetchPlaceDetails(prediction: AutocompletePrediction?) {
        // Define the fields you want to get for the place (we only need LatLng)
        val placeFields = listOf<Place.Field?>(
            Place.Field.ID,
            Place.Field.DISPLAY_NAME,
            Place.Field.ADDRESS_COMPONENTS,
            Place.Field.LOCATION
        )

        // Use a coroutine to make the network request
        lifecycleScope.launch {
            try {
                // Fetch the place details

                if(prediction != null){
                    val requestPlaceId = prediction.placeId
                    val request = FetchPlaceRequest.builder(requestPlaceId, placeFields)
                        .build()
                    val response = placesClient.fetchPlace(request)
                    val place = response.await().place
                    // If we get the LatLng, move the pin
                    place.location.let {
                        if (it != null) {
                            movePinAndCamera(it)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("MapsActivity", "Failed to fetch place details", e)
                Toast.makeText(this@MapsActivity, "Could not get place details.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val initialLocation = LatLng(initialLat, initialLng)

        when (mode) {
            "SELECT" -> setupSelectMode(initialLocation)
            "VIEW" -> setupViewMode(initialLocation)
            else -> mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 12f))
        }
    }

    private fun setupSelectMode(location: LatLng) {
        binding.buttonConfirmLocation.visibility = View.VISIBLE
        binding.buttonLaunchSearch.visibility = View.VISIBLE
        currentMarker = mMap.addMarker(MarkerOptions().position(location).title("Your Shop Location").draggable(true))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))

        mMap.setOnMapClickListener { latLng ->
            movePinAndCamera(latLng)
        }

        binding.buttonConfirmLocation.setOnClickListener {
            currentMarker?.let { marker ->
                val resultIntent = Intent().apply {
                    putExtra("latitude", marker.position.latitude)
                    putExtra("longitude", marker.position.longitude)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun setupViewMode(location: LatLng) {
        binding.buttonConfirmLocation.visibility = View.GONE
        binding.buttonLaunchSearch.visibility = View.GONE
        mMap.addMarker(MarkerOptions().position(location).title("Shop Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }
    private fun movePinAndCamera(latLng: LatLng) {
        currentMarker?.position = latLng
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }
}
