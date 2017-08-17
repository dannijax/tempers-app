package chapters.main.push

import chapters.extension.toFeeBackDate
import chapters.network.pojo.NotificationsItem
import chapters.utils.eventbus.RxBus
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import java.util.*


class FirebaseMessaginService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage?) {
        val item: NotificationsItem
        try {
            val jo = JSONObject(JSONObject(p0?.data).getString("custom")).getJSONObject("a")
            val jo2=JSONObject(p0?.data)
            item = NotificationsItem().apply {

                if(jo.has("notificationClassID"))
                classId = jo.getInt("notificationClassID").toString()

                if(jo.has("name"))
                name = jo.getString("name")

                if(jo.has("version"))
                version = jo.getInt("version").toString()

                if(jo.has("userID"))
                userId = jo.getInt("userID").toString()

                if(jo2.has("alert"))
                content = jo2.getString("alert")

                if(jo.has("date")) {
                    date = jo.getString("date").toFeeBackDate()
                }

                if(date.isEmpty()){
                    date=Date().time.toFeeBackDate()
                }

            }
            if(item.classId!="8") {
                RxBus.send(item)
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }

        super.onMessageReceived(p0)
    }

    override fun onMessageSent(p0: String?) {
        super.onMessageSent(p0)

    }


    private fun notificationBuild() {

    }

}