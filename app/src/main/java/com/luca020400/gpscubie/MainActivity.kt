package com.luca020400.gpscubie

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

@SuppressLint("MissingPermission", "HardwareIds")
class MainActivity : AppCompatActivity(), LocationListener {
    private val gpsService by lazy {
        val retrofit = Retrofit.Builder()
                .baseUrl("http://luca020400.duckdns.org:3333")
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        retrofit.create(GPSService::class.java)
    }

    private val locationManager by lazy {
        getSystemService(LocationManager::class.java)
    }

    private val uuid by lazy {
        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }

    private val formatter by lazy {
        SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ITALY)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderEnabled(provider: String?) {}

    override fun onProviderDisabled(provider: String?) {}

    override fun onLocationChanged(location: Location) {
        sendGPSData(location)
    }

    private val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    private val myPermission = 100

    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun sendGPSData(location: Location) = with(location) {
        val gpsData = GPSData(
                uuid,
                latitude,
                longitude,
                altitude,
                formatter.format(Date(time)),
                provider
        )
        gpsService.postGPS(gpsData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("TAG", "Sent successfully")
                }, {
                    Log.e("TAG", "Error while sending GPS data", it)
                })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, myPermission)
            return
        }

        send_gps.setOnClickListener {
            val updateTime = 10 * 100L // In millisecond
            val distance = 10f // In meters
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, updateTime, distance, this)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, updateTime, distance, this)
        }
    }

    override fun onPause() {
        super.onPause()
        locationManager.removeUpdates(this)
    }
}
