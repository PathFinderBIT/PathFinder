package com.project.pathfinder

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.project.pathfinder.ui.EditProfileActivity
import com.project.pathfinder.ui.MapsActivity
import com.project.pathfinder.ui.NearbyActivity
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.project.pathfinder.auth.LoginActivity
import com.project.pathfinder.databinding.ActivityMainBinding
import java.util.Locale
import kotlin.properties.Delegates

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var user: FirebaseUser
    private var latitude by Delegates.notNull<Double>()
    private var longitude by Delegates.notNull<Double>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        if(auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        user = auth.currentUser!!
        retrieveAndDisplayPhoto()

        binding.displayName.text = user.displayName
        binding.logoutButton.setOnClickListener { logoutAccount() }

        fusedLocationProviderClient =  LocationServices.getFusedLocationProviderClient(this)
        fetchCurrentLocation()

        binding.currentLocation.isSelected = true

        binding.editProfileButton.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
            finish()
        }

        binding.pharmacyButton.setOnClickListener {
            val intent = Intent(this, NearbyActivity::class.java)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            intent.putExtra("typePlace", "pharmacy")
            startActivity(intent)
        }

        binding.searchButton.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        binding.hospitalButton.setOnClickListener {
            val intent = Intent(this, NearbyActivity::class.java)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            intent.putExtra("typePlace", "hospital")
            startActivity(intent)
        }

        binding.marketButton.setOnClickListener {
            val intent = Intent(this, NearbyActivity::class.java)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            intent.putExtra("typePlace", "market")
            startActivity(intent)
        }

        binding.hotelButton.setOnClickListener {
            val intent = Intent(this, NearbyActivity::class.java)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            intent.putExtra("typePlace", "hotel")
            startActivity(intent)
        }

        binding.shoppingButton.setOnClickListener {
            val intent = Intent(this, NearbyActivity::class.java)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            intent.putExtra("typePlace", "mall")
            startActivity(intent)
        }
    }

    private fun fetchCurrentLocation() {
        val task: Task<Location> = fusedLocationProviderClient.lastLocation
        val geocoder = Geocoder(this, Locale.getDefault())
        var addresses: List<Address>

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }

        task.addOnSuccessListener {
            if (it != null) {
                latitude = it.latitude
                longitude = it.longitude
                geocoder.getFromLocation(latitude, longitude, 1)!!.also { addresses = it }
                val city = addresses[0].locality
                val state = addresses[0].adminArea
                val country = addresses[0].countryName
                binding.currentLocation.text = "$city, $state, $country"
            }
        }
    }

    private fun retrieveAndDisplayPhoto() {
        val photoUri = user.photoUrl

        if (photoUri != null) {
            Glide.with(this).load(photoUri.toString()).into(binding.profileImage)
        }
    }

    private fun logoutAccount() {
        auth.signOut()
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        finish()
    }
}