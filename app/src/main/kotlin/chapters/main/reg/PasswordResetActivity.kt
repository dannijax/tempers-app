package chapters.main.reg

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import chapters.base.BaseActivity
import chapters.base.NetRequest
import chapters.base.NetworkUtils
import chapters.extension.*
import chapters.main.reg.mvp.MvpReg
import chapters.main.reg.mvp.PresenterReg
import chapters.network.NetApiClient
import com.jakewharton.rxbinding.widget.RxTextView
import com.tempry.R
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.app_bar_main.view.*
import rx.Observable
import rx.Subscription


class PasswordResetActivity : BaseActivity(), MvpReg, NetRequest {

    private var subs: Subscription? = null
    private val presenter = PresenterReg(this)
    override fun setLayoutRes(): Int {
        return R.layout.activity_reset_password
    }

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, PasswordResetActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initRx()

    }

    private fun initView() {
        initToolBar(viewToolbar.toolbar, arrow = true)
        viewDateBirth.getEditText().text = DATE_PATTER
        viewDateBirth.view().setOnClickListener {
            presenter.openCalendar(this)
        }
        btnReset.setOnClickListener {
            makeRequest()
        }
    }

    private fun initRx() {
        val birthDay = RxTextView.textChanges(viewDateBirth.getEditText())
                .map { it.toString() != DATE_PATTER }
                .doOnNext { viewDateBirth.changeColorDrawable(it) }
                .distinctUntilChanged()

        val email = RxTextView.textChanges(viewEmal.getEditText())
                .map { it.toString().isEmail() }
                .doOnNext { viewEmal.changeColorDrawable(it) }
                .distinctUntilChanged()

        Observable.combineLatest(birthDay, email, {
            birth, em ->
            birth && em
        }).distinctUntilChanged()
                .subscribe { btnReset.isEnabled = it }
    }

    override fun makeRequest() {
        NetApiClient.resetPassword(viewEmal.getEditText().text.toString()
                , viewDateBirth.getEditText().text.toString())
                .doOnSubscribe { showProgressDialog() }
                .doOnUnsubscribe { cancelProgressDialog() }
                .subscribe({
                    if (it.response.isNotEmpty()) {
                        if (it.response == "wrong") {
                            showSnackBar(R.string.wrong_reset)
                        } else if(it.response!="ok") {
                            showSnackBar(getString(R.string.dublicate_reset))
                        }else{
                            showSnackBar(R.string.password_reset)
                        }
                    } else {
                        showSnackBar(R.string.password_reset)
                    }

                }, { onError(it) })

    }

    override fun onError(it: Throwable?) {
        NetworkUtils.netError(it, this, this)
    }

    override fun setDate(date: String?) {
        viewDateBirth.getEditText().text = date
    }
}