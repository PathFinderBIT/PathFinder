package com.project.pathfinder.Model

import com.google.gson.annotations.SerializedName

data class PhotosItem(

	@field:SerializedName("photo_reference")
	val photoReference: String? = null,

	@field:SerializedName("width")
	val width: Int? = null,

	@field:SerializedName("html_attributions")
	val htmlAttributions: List<String?>? = null,

	@field:SerializedName("height")
	val height: Int? = null
)