package chapters.main.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import chapters.base.BaseActivity
import chapters.base.NetRequest
import chapters.main.reg.MainLoginActivity
import chapters.network.NetApiClient
import com.tempry.R

class SplashActivity : BaseActivity(), NetRequest {


    override fun setLayoutRes(): Int {
        return R.layout.activity_splash
    }

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, SplashActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeRequest()

    }

    override fun makeRequest() {
        NetApiClient.checkToken()
                .subscribe({
                    if(it){
                        NetApiClient.getMyUserInfo()
                                ?.subscribe({MainActivity.launch(this)}
                                        ,{it.printStackTrace()})
                    } else {
                        MainLoginActivity.launch(this)
                    }
                },{
                    it.printStackTrace()
                    MainLoginActivity.launch(this)
                })
    }

}