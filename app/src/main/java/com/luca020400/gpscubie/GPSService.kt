package com.luca020400.gpscubie

import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface GPSService {
    @POST("/send_gps")
    fun postGPS(@Body data: GPSData): Observable<Response<Void>>

    @GET("/get_gps")
    fun getGPS(@Query("limit") limit: Int): Observable<List<GPSData>>
}
