package chapters.main.reg

import android.content.Context
import android.content.Intent
import android.os.Bundle
import chapters.base.BaseActivity
import chapters.base.NetRequest
import chapters.base.NetworkUtils
import chapters.extension.validName
import chapters.main.main.MainActivity
import chapters.network.NetApiClient
import com.jakewharton.rxbinding.widget.RxTextView
import com.tempry.R
import filehelpers.Sha512
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.app_bar_main.view.*
import rx.Observable
import rx.Subscription

class LoginActivity : BaseActivity(), NetRequest {

    private var subs: Subscription? = null
    override fun setLayoutRes(): Int {
        return R.layout.activity_login
    }

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initClick()
        initRx()
    }

    private fun initView() {
        initToolBar(viewToolbar.toolbar, arrow = true)
        viewPassword.getEditText().setOnEditorActionListener { v, actionId, event ->
            if(btnLogin.isEnabled){
                makeRequest()
            }
            false
        }
    }

    private fun initClick() {
        btnLogin.setOnClickListener {
            makeRequest()
        }

        tvReg.setOnClickListener {
            RegistrationActivity.launch(this)
        }

        tvReset.setOnClickListener {
            PasswordResetActivity.launch(this)
        }
    }

    private fun initRx() {
        val name = RxTextView.textChanges(viewUserName.getEditText())
                .map { it.validName() }
                .doOnNext { viewUserName.changeColorDrawable(it) }
                .distinctUntilChanged()

        val password = RxTextView.textChanges(viewPassword.getEditText())
                .map { it.length > 5 }
                .doOnNext { viewPassword.changeColorDrawable(it) }
                .distinctUntilChanged()

        subs = Observable.combineLatest(name, password, {
            name, pass ->
            name && pass
        }).distinctUntilChanged()
                .subscribe { btnLogin.isEnabled = it }
    }

    override fun makeRequest() {
        val map = HashMap<String, Any>()
        map.put("username", viewUserName.getEditText().text)
        map.put("password", Sha512.getHashCodeFromString(viewPassword.getEditText().text.toString()))
        NetApiClient.login(map, this)
                ?.subscribe({
                    if (it) MainActivity.launch(this)
                }, { NetworkUtils.netError(it, this, this) })
    }

    override fun finish() {
        subs?.unsubscribe()
        super.finish()
    }

}