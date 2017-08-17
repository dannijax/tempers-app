package chapters.main.reg

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import chapters.base.BaseActivity
import chapters.database.Chat
import chapters.database.ChatMessage
import chapters.database.SharedSetting
import chapters.database.UserRealm
import com.tempry.R
import io.realm.Realm
import kotlinx.android.synthetic.main.acitivity_main_login.*

class MainLoginActivity : BaseActivity() {

    override fun setLayoutRes(): Int {
        return R.layout.acitivity_main_login
    }

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, MainLoginActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initClick()
        clearUser()
    }

    private fun clearUser() {
        val r = Realm.getDefaultInstance()
        r.beginTransaction()
        r.where(UserRealm::class.java).findAll().deleteAllFromRealm()
        r.where(Chat::class.java).findAll().deleteAllFromRealm()
        r.where(ChatMessage::class.java).findAll().deleteAllFromRealm()
        r.commitTransaction()
        SharedSetting.setUserId(0)
        SharedSetting.setUserToken("")
    }

    private fun initView(){
        tv1.text = Html.fromHtml(getString(R.string.info_1))
    }

    private fun initClick(){
        btnReg.setOnClickListener {
            RegistrationActivity.launch(this)
        }
        btnLogin.setOnClickListener {
            LoginActivity.launch(this)
        }
    }
}