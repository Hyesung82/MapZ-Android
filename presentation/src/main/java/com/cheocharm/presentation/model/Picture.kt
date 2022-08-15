package com.cheocharm.presentation.model

import android.net.Uri
import com.google.android.gms.maps.model.LatLng

data class Picture(val uri: Uri, val latLng: LatLng?, val address: String? = null) {

    fun getLocationString(): String {
        return address ?: latLng.toString()
    }
}
