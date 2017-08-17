package chapters.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import chapters.base.BaseActivity
import chapters.base.NetRequest
import chapters.base.NetworkUtils
import chapters.network.NetApiClient
import chapters.network.pojo.SendSettingPojo
import chapters.network.pojo.SettingPojo
import com.tempry.R
import kotlinx.android.synthetic.main.activity_setting.*

import kotlinx.android.synthetic.main.app_bar_main.view.*

class SettingsActivity : BaseActivity(), NetRequest {


    override fun setLayoutRes(): Int {
        return R.layout.activity_setting
    }

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, SettingsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        makeRequest()
    }

    private fun initView() {
        initToolBar(viewToolbar.toolbar, arrow = true)
    }

    private fun initSettings(it: SettingPojo) {
        vsFollower.getSwitch()?.isChecked = it.follower == 1
        vsStar.getSwitch()?.isChecked = it.star == 1
        vsRetemp.getSwitch()?.isChecked = it.retemp == 1
        vsNewTag.getSwitch()?.isChecked = it.tag == 1
        vsTagStar.getSwitch()?.isChecked = it.tagStar == 1
        vsVanish.getSwitch()?.isChecked = it.vanish == 1
        vsJoin.getSwitch()?.isChecked = it.join == 1
        vsMessage.getSwitch()?.isChecked = it.message == 1
        vsRecomendation.getSwitch()?.isChecked = it.recomendation == 1

    }


    override fun makeRequest() {
        NetApiClient.getSettings()
                .doOnNext {initSettings(it) }
                .flatMap {  NetApiClient.getSettingsPrivacy()}
                .subscribe({
                    viewAllowMessage.setPos(it.follower)
                    viewAllowTags.setPos(it.star)
                    Log.d("MAKE_REQ_"," "+it.toString())
                },{it.printStackTrace()})
    }

    override fun finish() {
        val send = SendSettingPojo()
        send.class1=if(vsFollower.getSwitch()!!.isChecked) 1 else 0
        send.class2 =if(vsStar.getSwitch()!!.isChecked) 1 else 0
        send.class3=if(vsRetemp.getSwitch()!!.isChecked) 1 else 0
        send.class4=if(vsNewTag.getSwitch()!!.isChecked) 1 else 0
        send.class5=if(vsTagStar.getSwitch()!!.isChecked) 1 else 0
        send.class6=if(vsVanish.getSwitch()!!.isChecked) 1 else 0
        send.class7=if(vsJoin.getSwitch()!!.isChecked) 1 else 0
        send.class8=if(vsMessage.getSwitch()!!.isChecked) 1 else 0
        send.class9=if(vsRecomendation.getSwitch()!!.isChecked) 1 else 0

        NetApiClient.setNewSettings(send)
                .flatMap { NetApiClient.setSettingsPrivacy(viewAllowMessage.getBoolean()
                        ,viewAllowTags.getBoolean()) }
                .subscribe({}, { it.printStackTrace() })
                super.finish()
    }


}