package chapters.utils.location

import android.location.Location
import android.location.LocationListener
import android.os.Bundle


open class MyLocListener :LocationListener{
    override fun onLocationChanged(location: Location?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

}