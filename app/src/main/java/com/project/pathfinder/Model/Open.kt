package com.project.pathfinder.Model

import com.google.gson.annotations.SerializedName

data class Open(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("time")
	val time: String? = null,

	@field:SerializedName("day")
	val day: Int? = null
)