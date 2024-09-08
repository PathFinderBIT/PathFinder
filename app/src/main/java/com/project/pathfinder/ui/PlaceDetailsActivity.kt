package com.project.pathfinder.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.WindowInsetsAnimation
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.pathfinder.Model.PlaceDetailResponse
import com.project.pathfinder.R
import com.project.pathfinder.Remote.RetrofitClient
import com.project.pathfinder.databinding.ActivityPlaceDetailsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PlaceDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaceDetailsBinding
   var phoneNumber :String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }

        val bundle: Bundle = intent.extras!!
        val placeId = bundle.getString("placeId")
        val photoReference = bundle.getString("photoReference")
        val placeName = bundle.getString("placeName")
        val openStatus = bundle.getBoolean("openStatus")
        val rateNumber = bundle.getDouble("rateNumber")
        val currentLat = bundle.getDouble("currentLat")
        val currentLng = bundle.getDouble("currentLng")
        val destinationLat = bundle.getDouble("destinationLat")
        val destinationLng = bundle.getDouble("destinationLng")
        Log.e("PlaceID: ",placeId.toString())
        fetchPlaceDetails(placeId.toString())

        val photoUrl = StringBuilder("https://maps.googleapis.com/maps/api/place/photo")
        photoUrl.append("?maxwidth=400")
        photoUrl.append("&photo_reference=$photoReference")
        photoUrl.append("&key=${getString(R.string.browser_key)}")

        Glide.with(this).load(photoUrl.toString()).into(binding.thumbnailImage)

        binding.placeName.text = placeName
        if (openStatus == true) {
            binding.openStatus.text = "Open Now"
            binding.openStatusIcon.setColorFilter(ContextCompat.getColor(applicationContext, R.color.green))
        } else {
            binding.openStatus.text = "Closed"
            binding.openStatusIcon.setColorFilter(ContextCompat.getColor(applicationContext, R.color.red_500))
        }

        binding.rateNumber.text = "$rateNumber / 5.0"
        startHalalVotesSnapshot(placeId)

        binding.voteHalalButton.setOnClickListener { showCallOrMessageDialog(phoneNumber) }
        binding.navigationButton.setOnClickListener { openNavigationOnMaps(currentLat, currentLng, destinationLat, destinationLng) }
    }

    private fun openNavigationOnMaps(currentLat: Double, currentLng: Double, destinationLat: Double, destinationLng: Double) {
        val uri = "http://maps.google.com/maps?f=d&hl=en&saddr=${currentLat},${currentLng}&daddr=${destinationLat},${destinationLng}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(Intent.createChooser(intent, "Select an application"))
    }


    private fun startHalalVotesSnapshot(placeId: String?) {
        val db = Firebase.firestore
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        db.collection("halalPlaces")
            .document(placeId!!)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("SNAPSHOT_ERROR", "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("CURRENT_DATA", "${snapshot.data}")
                    val votersId = snapshot.data!!["votersId"] as ArrayList<String>
                    val halalNumber = votersId.size
                    binding.halalNumber.text = "$halalNumber Halal Votes"

                    if (userId in votersId) {
                        binding.voteHalalButton.text = "Halal Voted!"
                        binding.voteHalalButton.setTextColor(ContextCompat.getColor(applicationContext, R.color.turquoise))
                    }
                } else {
                    Log.d("CURRENT_DATA_EMPTY", "null")
                }
            }
    }

    private fun userVotePlaceAsHalal(placeId: String?, placeName: String?) {
        val db = Firebase.firestore
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        var currentVotersId: ArrayList<String> = ArrayList()

        db.collection("halalPlaces")
            .document(placeId!!)
            .get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    currentVotersId = document.data!!["votersId"] as ArrayList<String>
                }

                if (userId !in currentVotersId) {
                    currentVotersId.add(userId!!)

                    val data = hashMapOf(
                        "placeId" to placeId,
                        "placeName" to placeName,
                        "votersId" to currentVotersId
                    )

                    db.collection("halalPlaces")
                        .document(placeId)
                        .set(data)
                        .addOnSuccessListener {
                            Toast.makeText(applicationContext, "You voted halal restaurant!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }
    private fun showCallOrMessageDialog(number:String) {
        val options = arrayOf("Call", "Message")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose an action")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> makePhoneCall(number)  // Call option
                1 -> sendTextMessage(number)  // Message option
            }
        }
        builder.show()
    }

    private fun makePhoneCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivity(intent)
    }

    private fun sendTextMessage(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("sms:$phoneNumber")
        }
        startActivity(intent)
    }
    private fun fetchPlaceDetails(placeId: String) {
        val apiKey = getString(R.string.browser_key)
        val apiService = RetrofitClient.create()
        val call = apiService.getPlaceDetails(placeId, apiKey)

        call.enqueue(object : Callback<PlaceDetailResponse> {
            override fun onResponse(call: Call<PlaceDetailResponse>, response: Response<PlaceDetailResponse>) {
                if (response.isSuccessful) {
                    val placeDetail = response.body()?.result
                    placeDetail?.let {
                        phoneNumber = it.internationalPhoneNumber.toString()
//                        Log.d("Number1",phoneNumber)
//                        Log.d("Number2",it.formattedPhoneNumber.toString())
//                        Toast.makeText(
//                            this@PlaceDetailsActivity,
//                            "Details: ${it.name}, Address: ${it.formattedAddress}, Phone: ${it.formattedPhoneNumber}",
//                            Toast.LENGTH_LONG
                       // ).show()
                    }
                } else {
                    Toast.makeText(this@PlaceDetailsActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PlaceDetailResponse>, t: Throwable) {
                Toast.makeText(this@PlaceDetailsActivity, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
