package com.luca020400.gpscubie

data class GPSData(
        val uuid: String,
        val latitude: Double,
        val longitude: Double,
        val altitude: Double,
        val time: String
)

data class GPSDataList(
        val data: List<GPSData>
)
