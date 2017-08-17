package chapters.utils.location

import android.content.Context
import android.content.Intent
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.util.Log
import chapters.extension.ON_GPS
import com.google.android.gms.maps.model.LatLng
import rx.Observable
import rx.subjects.BehaviorSubject

class RxLocation : MyLocListener() {

    companion object {
        var subLoc: BehaviorSubject<LatLng?> = BehaviorSubject.create()
        private var obs = subLoc.asObservable().filter { it?.latitude != 0.0 && it?.longitude != 0.0 }
        private var locationManager: LocationManager? = null
        private var locationListener: RxLocation? = null

        fun launchGps(context: Context) {
            Log.d("BBLF_"," okk ")
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val crit = Criteria()
            crit.accuracy = Criteria.ACCURACY_FINE
            locationListener = RxLocation()
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 20f, locationListener)
            val first = (locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER))
            subLoc.onNext(LatLng(first?.latitude ?: 0.0, first?.longitude ?: 0.0))
        }

        fun observLocation(): Observable<LatLng?> {
            return obs
        }

        fun disable() {
            locationManager?.removeUpdates(locationListener)
        }

        fun isProvided(): Boolean {
            return locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
        }

        fun requestProvided(act: AppCompatActivity, retry: Boolean = false) {
            if (!isProvided() && !retry) {
                act.startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), ON_GPS)
            } else if (retry) {
                act.startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), ON_GPS)
            }
        }

    }

    override fun onLocationChanged(location: Location?) {
        val loc = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
        subLoc.onNext(loc)
    }
}
