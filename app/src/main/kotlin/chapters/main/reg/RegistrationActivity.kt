package chapters.main.reg

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.telephony.TelephonyManager
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import chapters.base.BaseActivity
import chapters.base.NetRequest
import chapters.base.NetworkUtils
import chapters.extension.*
import chapters.extension.binding.countryChange
import chapters.main.reg.mvp.MvpReg
import chapters.main.reg.mvp.PresenterReg
import chapters.network.NetApiClient
import chapters.network.pojo.PhonePojo
import chapters.network.pojo.RegistrationBody
import chapters.ui.uiUtils.changeMaxLenth
import chapters.ui.uiUtils.filterEditText
import com.jakewharton.rxbinding.widget.RxCompoundButton
import com.jakewharton.rxbinding.widget.RxTextView
import com.tempry.R
import filehelpers.Sha512
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.app_bar_main.view.*
import rx.Observable
import rx.subscriptions.CompositeSubscription
import java.util.*

class RegistrationActivity : BaseActivity(), MvpReg, NetRequest {

    private val presenter = PresenterReg(this)
    private val compositeSubsribe = CompositeSubscription()
    private var countNumber = 0

    override fun setLayoutRes(): Int {
        return R.layout.activity_registration
    }

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, RegistrationActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initClick()
        initRx()
    }

    private fun initClick() {
        viewDateBirth.view().setOnClickListener {
            presenter.openCalendar(this)
        }
    }

    private fun initRx() {
        val country = viewCodePicker.countryChange()
                ?.doOnNext {
                    countNumber = it
                    //etPhone.changeMaxLenth(it)
                }
                ?.subscribe { tvPhoneInfo.text = getString(R.string.number_info).format(it) }
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        viewCodePicker.setCountryForNameCode("gb")
        val firstName = RxTextView.textChanges(viewFirstName.getEditText())
                .map { it.validName() }
                .doOnNext { viewFirstName.changeColorDrawable(it) }
                .distinctUntilChanged()
        val lastName = RxTextView.textChanges(viewLastName.getEditText())
                .map { it.validName() }
                .doOnNext { viewLastName.changeColorDrawable(it) }
                .distinctUntilChanged()

        val userName = RxTextView.textChanges(viewUserName.getEditText())
                .map { it.validName() }
                .doOnNext { viewUserName.changeColorDrawable(it) }
                .distinctUntilChanged()

        val email = RxTextView.textChanges(viewEmal.getEditText())
                .map { it.toString().isEmail() }
                .doOnNext {
                    viewConfirmEmail.getEditText().isEnabled = it
                    viewEmal.changeColorDrawable(it)
                    viewConfirmEmail.getEditText().text = viewConfirmEmail.getEditText().text
                }
                .distinctUntilChanged()

        val confirmEmail = RxTextView.textChanges(viewConfirmEmail.getEditText())
                .map { it.toString() == viewEmal.getEditText().text.toString() && it.toString().isEmail() }
                .doOnNext { viewConfirmEmail.changeColorDrawable(it) }
                .distinctUntilChanged()

        val password = RxTextView.textChanges(viewPassword.getEditText())
                .map { it.length > 6 }
                .doOnNext { viewPassword.changeColorDrawable(it) }
                .distinctUntilChanged()

        val birthDay = RxTextView.textChanges(viewDateBirth.getEditText())
                .map { it.validName() && it.toString() != DATE_PATTER }
                .distinctUntilChanged()

        val phone = RxTextView.textChanges(etPhone)
                .map {
                    it.length > 8
                }
                .distinctUntilChanged()
        val check = RxCompoundButton.checkedChanges(cbAgree)
                .distinctUntilChanged()

        val subsValidate = Observable.combineLatest(firstName, lastName, userName, email, confirmEmail,
                password, birthDay, phone, check, {
            firstName, lastName, userName, email, confirmEmail, password, birthDay, phone, check ->
            firstName && lastName && userName && email && confirmEmail && password && birthDay && phone
                    && check

        }).distinctUntilChanged()
                .subscribe {
                    btnReg.isEnabled = it
                    if (it) {
                        btnReg.setText(R.string.join_now)
                    } else {
                        btnReg.setText(R.string.not_validate)
                    }
                }

        compositeSubsribe.add(country)
        compositeSubsribe.add(subsValidate)
    }


    private fun initView() {
        etPhone.setBackgroundResource(R.color.transparent)
        etPhone.changeMaxLenth(16)
        viewUserName.getEditText().filterEditText("1234567890qwertyuiopasdfghjklzxcvbnm_QWERTYUIOPASDFGHJKLZXCVBNM", 32)
        etPhone.filterEditText("1234567890", 20, InputType.TYPE_CLASS_PHONE)
        viewEmal.getEditText().setSingleLine()
        viewDateBirth.getEditText().text = DATE_PATTER
        viewConfirmEmail.getEditText().setSingleLine()
        setAgreeTv()
        btnReg.setOnClickListener {
            makeRequest()
        }
        etPhone.setOnEditorActionListener { v, actionId, event ->
            if(btnReg.isEnabled){
                makeRequest()
            }
            false
        }

        viewPassword.getEditText().imeOptions=EditorInfo.IME_ACTION_DONE
        initToolBar(viewToolbar.toolbar, arrow = true)
    }

    private fun setAgreeTv() {
        val spanned = SpannableString(getString(R.string.reg_info2))
        spanned.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorAccent)), 42, 59, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spanned.setSpan(object : ClickableSpan() {
            override fun onClick(p0: View?) {
                val url = "https://www.tempry.com/privacy-policy"
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(i)
            }

        }, spanned.length - 14, spanned.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spanned.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorAccent)), spanned.length - 14, spanned.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spanned.setSpan(object : ClickableSpan() {
            override fun onClick(p0: View?) {
                val url = "https://www.tempry.com/terms-of-use"
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(i)
            }

        }, 42, 59, Spanned.SPAN_USER_SHIFT)
        tvRegInfo.movementMethod=LinkMovementMethod.getInstance()
        tvRegInfo.text = spanned
        tvRegInfo.setOnClickListener {

        }
    }

    override fun setDate(date: String?) {
        viewDateBirth.getEditText().text = date
    }

    override fun onError(it: Throwable?) {

    }

    override fun makeRequest() {
        val map = RegistrationBody()
        map.email=viewEmal.getEditText().text.toString()
        map.username=viewUserName.getEditText().text.toString()
        map.password=Sha512.getHashCodeFromString(viewPassword.getEditText().text.toString()).toString()
        map.firstname=viewFirstName.getEditText().text.toString()
        map.lastname= viewLastName.getEditText().text.toString()
        map.birthday= viewDateBirth.getEditText().text.toString()
        map.name= map.firstname + " " + map.lastname
        map.phone.code=viewCodePicker.selectedCountryCodeWithPlus
        map.phone.countryCode=viewCodePicker.selectedCountryNameCode
        map.phone.number=etPhone.text.toString()
        NetApiClient.registration(map, this)
                .doOnSubscribe { showProgressDialog() }
                .doOnUnsubscribe { cancelProgressDialog() }
                .subscribe({
                    if (it) WelcomeActivity.launch(this)
                }, { NetworkUtils.netError(it, this,
                        this,getString(R.string.user_has_reg)) })

    }

    override fun finish() {
        compositeSubsribe.unsubscribe()
        super.finish()
    }
}
