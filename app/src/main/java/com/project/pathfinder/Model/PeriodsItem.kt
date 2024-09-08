package com.project.pathfinder.Model

import com.google.gson.annotations.SerializedName

data class PeriodsItem(

	@field:SerializedName("close")
	val close: Close? = null,

	@field:SerializedName("open")
	val open: Open? = null
)