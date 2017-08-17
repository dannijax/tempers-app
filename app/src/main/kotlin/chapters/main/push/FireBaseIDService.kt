package chapters.main.push

import android.util.Log
import chapters.network.NetApiClient
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.onesignal.OneSignal


class FireBaseIDService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        OneSignal.idsAvailable { userId, registrationId ->
            NetApiClient.registerOneSignal(userId)
                    .subscribe({}, { it.printStackTrace() })
        }
        super.onTokenRefresh()
    }
}