package chapters.database

import android.util.Log
import io.realm.Realm


class UserProvider {

    companion object {

        fun saveMyUser(user: UserRealm) {
            val realm = Realm.getDefaultInstance()
            realm?.beginTransaction()
            realm?.insertOrUpdate(user)
            realm?.commitTransaction()
            realm?.close()
        }

        fun getMyProfile(): UserRealm? {
            val realm = Realm.getDefaultInstance()
            val user = realm?.where(UserRealm::class.java)
                    ?.equalTo("userId",SharedSetting.getUserId())?.findFirst ()
            user?.let {
                val copy = realm.copyFromRealm(it)
                realm.close()
                return copy
            }
            realm.close()
            return user
        }
    }
}