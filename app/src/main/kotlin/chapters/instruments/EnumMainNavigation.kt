package chapters.instruments

import android.util.Log
import com.tempry.R


enum class EnumMainNavigation(val navId: Int, val tabId: Int, var pos: Int,val strId:Int) {

    HOME(R.id.navHome, R.id.tabHome, 0,R.string.home),
    PROFILE(R.id.navProfile,R.id.tabProfile,1,R.string.profile),
    NOTIFICATIONS(R.id.navNotif,R.id.tabNotif,2,R.string.notif),
    SEARCH(R.id.navSearch,R.id.tabSearch,3,R.string.search),
    INBOX(R.id.navInbox,R.id.tabInbox,4,R.string.inbox),
    SETTING(R.id.navSetting,R.id.navSetting,6,R.string.action_settings),
    EXIT(R.id.navLogOut,R.id.navLogOut,5,R.string.logout);

    companion object{
         fun fromId(id:Int):Int{
             return values().find { it.navId==id || it.tabId==id }?.pos?:0
         }

        fun fromIdStr(id:Int):Int{
            return values().find { it.navId==id || it.tabId==id }?.strId?:0
        }

        fun fromPosStr(id:Int):Int{
            return values().find { it.pos==id }?.strId?:0
        }

        fun navIdFromPos(pos: Int): Int {
           return values().find { it.pos==pos }?.navId?:0
        }
    }
}