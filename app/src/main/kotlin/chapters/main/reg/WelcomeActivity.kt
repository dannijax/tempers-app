package chapters.main.reg

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import chapters.base.BaseActivity
import chapters.base.NetRequest
import chapters.base.NetworkUtils
import chapters.database.UserProvider
import chapters.extension.*
import chapters.main.main.MainActivity
import chapters.network.NetApiClient
import chapters.ui.adapter.FollowingAdapter
import com.tempry.R
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : BaseActivity(), NetRequest {

    private var adapter: FollowingAdapter? = null

    override fun setLayoutRes(): Int {
        return R.layout.activity_welcome
    }

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, WelcomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initClick()
    }

    private fun initClick() {
        tvSkip.setOnClickListener {
            MainActivity.launch(this)
        }
        btnGrantAccess.setOnClickListener {
            loadContacts()
        }
        val name = UserProvider.getMyProfile()?.firstName
        tvWelcome.text = getString(R.string.welcome_name).format(name)
    }

    private fun loadContacts() {
        if (checkPermision(Manifest.permission.READ_CONTACTS)) {
            makeRequest()
        } else {
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_CONTACTS), CONTACT_REQUEST)
        }
    }

    override fun makeRequest() {
        getAllContactsNumber()
                .doOnSubscribe { showProgressDialog() }
                .doOnUnsubscribe { cancelProgressDialog() }
                .flatMap {
                    NetApiClient.sendMyContacts(it)
                }
                .subscribe({
                    if (it.isNotEmpty()) {
                        FollowPeopleActivity.launch(this, it)
                    } else {
                        MainActivity.launch(this)
                    }
                }, {
                    it.printStackTrace()
                    MainActivity.launch(this) })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CONTACT_REQUEST) {
            if(checkPermision(Manifest.permission.READ_CONTACTS)){
                makeRequest()
            }else{
                MainActivity.launch(this)
            }
        }
    }
}