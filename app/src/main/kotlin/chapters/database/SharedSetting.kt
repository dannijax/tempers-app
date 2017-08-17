package chapters.database

import android.content.Context
import android.content.SharedPreferences
import chapters.extension.ID


object SharedSetting {

    private var sPref: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null


    fun initWithContext(context: Context) {
        sPref = context.getSharedPreferences("shared", Context.MODE_PRIVATE)
    }


    fun setUserToken(token: String) {
        editor = sPref!!.edit().apply {
            putString(ID, token)
            apply()
        }

    }

    fun getUserToken(): String {
        return sPref?.getString(ID,"") ?: ""
    }


    fun setUserId(token: Int) {
        editor = sPref!!.edit().apply {
            putInt("id2", token)
            apply()
        }

    }

    fun getUserId(): Int {
        return sPref?.getInt("id2",0) ?: 0
    }
}