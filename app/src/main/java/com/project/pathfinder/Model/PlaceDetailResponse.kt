package com.project.pathfinder.Model

import com.google.gson.annotations.SerializedName

data class PlaceDetailResponse(

	@field:SerializedName("result")
	val result: Result? = null,

	@field:SerializedName("html_attributions")
	val htmlAttributions: List<Any?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)