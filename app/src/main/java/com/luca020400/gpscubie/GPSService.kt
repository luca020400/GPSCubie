package com.luca020400.gpscubie

import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface GPSService {
    @POST("/send_gps")
    fun postGPS(@Body data: GPSData): Observable<Response<Void>>

    @GET("/get_gps")
    fun getGPS(): Observable<GPSDataList>
}