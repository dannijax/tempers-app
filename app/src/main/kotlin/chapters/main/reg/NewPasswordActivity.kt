package chapters.main.reg

import android.content.Context
import android.content.Intent
import android.os.Bundle
import chapters.base.BaseActivity
import chapters.base.NetRequest
import chapters.base.NetworkUtils
import chapters.extension.animHideFast
import chapters.extension.animShowFast
import chapters.extension.cancelProgressDialog
import chapters.extension.showProgressDialog
import chapters.network.NetApiClient
import com.jakewharton.rxbinding.widget.RxTextView
import com.tempry.R
import kotlinx.android.synthetic.main.activity_new_password.*
import kotlinx.android.synthetic.main.app_bar_main.view.*
import rx.Observable
import rx.Subscription

class NewPasswordActivity : BaseActivity(), NetRequest {

    private var subValidate: Subscription? = null
    override fun setLayoutRes(): Int {
        return R.layout.activity_new_password
    }

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, NewPasswordActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        initToolBar(viewToolbar.toolbar, arrow = true)
        initRx()
        btnNewPassword.setOnClickListener {
            makeRequest()
        }
    }

    private fun initRx() {
        val old = RxTextView.textChanges(viewOldPassword.getEditText())
                .map { it.length > 7 }
                .distinctUntilChanged()
                .doOnNext { viewOldPassword.changeColorDrawable(it) }
        val new = RxTextView.textChanges(viewNewPassword.getEditText())
                .map { it.length > 7 }
                .distinctUntilChanged()
                .doOnNext { viewNewPassword.changeColorDrawable(it) }

        subValidate = Observable.combineLatest(old, new, {
            old, new ->
            old && new
        })
                .distinctUntilChanged()
                .subscribe {
                    if (it) btnNewPassword.animShowFast() else btnNewPassword.animHideFast()
                }
    }

    override fun makeRequest() {
        NetApiClient.changePassword(viewOldPassword.getEditText().text.toString(),
                viewNewPassword.getEditText().text.toString())
                .doOnSubscribe { showProgressDialog() }
                .doOnUnsubscribe { cancelProgressDialog() }
                .subscribe({

                }, { NetworkUtils.netError(it, this, this) })
    }

    override fun finish() {
        subValidate?.unsubscribe()
        super.finish()
    }
}