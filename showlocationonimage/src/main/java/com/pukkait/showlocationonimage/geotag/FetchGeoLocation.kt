package com.pukkait.showlocationonimage.geotag

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.pukkait.showlocationonimage.R
import com.pukkait.showlocationonimage.helper.HelperClass
import java.io.IOException
import java.util.Locale

class FetchGeoLocation(private val mContext: Activity) : Service(), LocationListener {
    private var locationManager: LocationManager? = null
    private var isGPSTrackingEnabled: Boolean = false
    private var location: Location? = null
    private var latitude = 0.0
    private var longitude = 0.0

    init {
        getLocation()
    }

    fun getLocation(): Location? {
        try {
            locationManager = mContext.getSystemService(LOCATION_SERVICE) as LocationManager
            val isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled =
                locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isNetworkEnabled) {
                showSettingsAlert()
            } else if (!isGPSEnabled) {
                HelperClass.askPermissionDialog(mContext)
            } else {
                val canGetLocation = true
                isGPSTrackingEnabled = true

                if (ActivityCompat.checkSelfPermission(
                        mContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        mContext, Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        mContext,
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ),
                        200
                    )
                } else {
                    locationManager!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                    )
                }

                if (locationManager != null) {
                    location =
                        locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    updateGPSCoordinates()
                }
                if (location == null) {
                    locationManager!!.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                    )
                    if (locationManager != null) {
                        location =
                            locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        updateGPSCoordinates()
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(mContext, " ${e.printStackTrace()}", Toast.LENGTH_LONG)
                .show()
        }

        return location
    }

    fun updateGPSCoordinates() {
        if (location != null) {
            latitude = location!!.latitude
            longitude = location!!.longitude
        }
    }

    fun getLatitude(): Double {
        if (location != null) {
            latitude = location!!.latitude
        }
        return latitude
    }

    fun getLongitude(): Double {
        if (location != null) {
            longitude = location!!.longitude
        }
        return longitude
    }

    private fun showSettingsAlert() {
        try {
            val alertDialog = AlertDialog.Builder(mContext)
            alertDialog.setTitle(R.string.GPSAlertDialogTitle)
            alertDialog.setMessage(R.string.GPSAlertDialogMessage)
            alertDialog.setPositiveButton(R.string.settings) { dialog, which ->
                val intent =
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                dialog.dismiss()
                mContext.startActivity(intent)
            }
            alertDialog.setNegativeButton(R.string.cancel) { dialog, which ->
                dialog.cancel()
                mContext.finishAffinity()
            }
            alertDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getGeocoderAddress(context: Context): List<Address>? {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(latitude, longitude, 1)
    }

    override fun onLocationChanged(location: Location) {
        location.speed
    }

    override fun onProviderDisabled(provider: String) {
    }

    override fun onProviderEnabled(provider: String) {
        isGPSTrackingEnabled = true
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 //10 metters
        private const val MIN_TIME_BW_UPDATES = (1000 * 60 // 1 minute
                ).toLong()
    }
}
