package com.project.pathfinder.Model

import com.google.gson.annotations.SerializedName

data class CurrentOpeningHours(

	@field:SerializedName("open_now")
	val openNow: Boolean? = null,

	@field:SerializedName("periods")
	val periods: List<PeriodsItem?>? = null,

	@field:SerializedName("weekday_text")
	val weekdayText: List<String?>? = null
)