package com.project.pathfinder.Remote

import com.project.pathfinder.Model.PlaceDetailResponse
import com.project.pathfinder.Model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesApi {

    @GET("maps/api/place/nearbysearch/json")
    fun getNearbyPlaces(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("keyword") keyword: String,
        @Query("key") apiKey: String

    ): Call<PlaceResponse>

    @GET("maps/api/place/details/json")
    fun getPlaceDetails(
        @Query("placeid") placeId: String,
        @Query("key") apiKey: String
    ): Call<PlaceDetailResponse>
}
