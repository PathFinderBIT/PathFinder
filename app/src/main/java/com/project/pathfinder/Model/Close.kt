package com.project.pathfinder.Model

import com.google.gson.annotations.SerializedName

data class Close(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("time")
	val time: String? = null,

	@field:SerializedName("day")
	val day: Int? = null,

	@field:SerializedName("truncated")
	val truncated: Boolean? = null
)