package chapters.main.profile

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatEditText
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import chapters.base.BaseActivity
import chapters.base.NetRequest
import chapters.database.UserProvider
import chapters.extension.*
import chapters.main.reg.MainLoginActivity
import chapters.main.reg.NewPasswordActivity
import chapters.main.reg.mvp.MvpReg
import chapters.main.reg.mvp.PresenterReg
import chapters.network.NetApiClient
import chapters.ui.utils.GlideLoadCircle
import com.hbb20.CountryCodePicker
import com.jakewharton.rxbinding.widget.RxTextView
import com.tempry.R
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import filehelpers.FileClassUtils
import filehelpers.Sha512
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.app_bar_main.view.*
import rx.Observable
import rx.Subscription


class EditProfileActivity : BaseActivity(), NetRequest, MvpReg {


    private val presenter = PresenterReg(this)
    private var uri: Uri? = null
    private var s: MenuItem? = null
    private val subRequest: Subscription? = null
    private var subsValidate: Subscription? = null
    override fun setLayoutRes(): Int {
        return R.layout.activity_edit_profile
    }

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, EditProfileActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initClick()
    }

    private fun initView() {
        initToolBar(viewToolbar.toolbar, arrow = true)
        viewToolbar.toolbar.setNavigationIcon(R.drawable.ic_close)

        ivChangePhoto.setOnClickListener {
            openCameraOrGallery()
        }

        llchange.setOnClickListener {
            openCameraOrGallery()
        }

        ivProfile.setOnClickListener {
            openCameraOrGallery()
        }

        viewDateBirth.view().setOnClickListener {
            presenter.openCalendar(this)
        }

        btnChangeEmail.setOnClickListener {
            val v = layoutInflater.inflate(R.layout.layout_change, null)
            val etNew = v.findViewById(R.id.etNew) as AppCompatEditText
            val etOld = v.findViewById(R.id.etOld) as AppCompatEditText
            etOld.isEnabled=false
            val dialog = AlertDialog.Builder(this)
                    .setTitle("Change email")
                    .setView(v)
                    .setPositiveButton("Change", { d, d2 ->
                        NetApiClient.editProfileEmail(etNew.text.toString())
                                .subscribe({
                                    finish()
                                },{it.printStackTrace()})
                    })
                    .setNegativeButton("Cancel", { d, d2 ->
                        d.dismiss()
                    })
                    .create()
            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(this, R.color.colorAccent))

                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            }
            dialog.show()

            val email = UserProvider.getMyProfile()?.email
            etOld.setText(email)

            val obsOld = RxTextView.textChanges(etOld)
                    .map { it.toString() == email }
                    .distinctUntilChanged()
            val obsNew = RxTextView.textChanges(etNew)
                    .map { it.toString().isEmail() && it.toString() != email }
                    .distinctUntilChanged()
            Observable.combineLatest(obsNew, obsOld, {
                new, old ->
                Log.d("OBS_FF_"," new "+new+" old "+old)
                new && old
            })
                    .distinctUntilChanged()
                    .subscribe {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = it
                    }


        }

        btnChangePhone.setOnClickListener {
            val v = layoutInflater.inflate(R.layout.layout_change_phone, null)
            val etNew = v.findViewById(R.id.etPhone) as AppCompatEditText
            val etOld = v.findViewById(R.id.etOld) as AppCompatEditText
            val picker=v.findViewById(R.id.viewCodePicker) as CountryCodePicker
            etOld.isEnabled=false
            etOld.setText(UserProvider.getMyProfile()?.phoneCode+UserProvider.getMyProfile()?.phoneNumber)
            val dialog = AlertDialog.Builder(this)
                    .setTitle("Change number")
                    .setView(v)
                    .setPositiveButton("Change", { d, d2 ->
                        NetApiClient.editProfilePhone(etNew.text.toString(),picker)
                                .subscribe({
                                    finish()
                                },{it.printStackTrace()})
                    })
                    .setNegativeButton("Cancel", { d, d2 ->
                        d.dismiss()
                    })
                    .create()
            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(this, R.color.colorAccent))

                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            }
            dialog.show()

            val phoneOld = etOld.text.toString()

            val obsOld = RxTextView.textChanges(etOld)
                    .map { it.toString() == phoneOld }
                    .distinctUntilChanged()
            val obsNew = RxTextView.textChanges(etNew)
                    .map { it.toString() != phoneOld }
                    .distinctUntilChanged()
            Observable.combineLatest(obsNew, obsOld, {
                new, old ->
                Log.d("OBS_FF_"," new "+new+" old "+old)
                new && old
            })
                    .distinctUntilChanged()
                    .subscribe {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = it
                    }
        }


        val user = UserProvider.getMyProfile()
        val obsName = RxTextView.textChanges(viewName.getEditText())
                .map { it.isNotEmpty() }
                .distinctUntilChanged()
        val obsSurName = RxTextView.textChanges(viewSurName.getEditText())
                .map { it.isNotEmpty() }
                .distinctUntilChanged()
        subsValidate = Observable.combineLatest(obsName, obsSurName, {
            name, surname ->
            name && surname
        }).distinctUntilChanged()
                .subscribe {
                    s?.isVisible = it
                    /*if (it) *//*ivDone.show()*//* else *//*ivDone.invis(*//*) */
                }
        viewName.setBlackText(user?.firstName)
        viewSurName.setBlackText(user?.lastName)
        viewDateBirth.setBlackText(user?.birthday)
        viewInfoUser.setBlackText(user?.description)
        GlideLoadCircle(ivProfile, user?.photo)
    }

    private fun initClick() {
        btnChangePassword.setOnClickListener {
            NewPasswordActivity.launch(this)
        }
        btnTerminate.setOnClickListener {
            showDialogTerminate()
        }
    }

    private fun showDialogTerminate() {
        val input = layoutInflater.inflate(R.layout.view_terminate, null)
        val et = input.findViewById(R.id.etPassword) as AppCompatEditText

        val dialog = AlertDialog.Builder(this)
                .setTitle("Terminate account")
                .setView(input)
                .setCancelable(false)
                .setPositiveButton("Change") { p0, p1 ->
                    if (et.text.length < 8) {
                        Toast.makeText(this, "Min length 8 symbols", Toast.LENGTH_SHORT).show()
                    } else {
                        NetApiClient.terminateAccount(Sha512.getHashCodeFromString(et.text.toString()))
                                .doOnSubscribe { showProgressDialog() }
                                .doOnUnsubscribe { cancelProgressDialog() }
                                .subscribe({
                                    if (it.response == "wrong") {
                                        Toast.makeText(this, "Password not correct", Toast.LENGTH_SHORT).show()
                                    } else {
                                        MainLoginActivity.launch(this)
                                    }
                                }, {})
                    }
                }.setNegativeButton("Cancel") { p0, p1 -> }
                .create()
        dialog.setOnShowListener {
            dialog.getButton(Dialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.orange))
            dialog.getButton(Dialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.orange))
        }
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_done, menu)
        s = menu?.findItem(R.id.done)
        s?.setOnMenuItemClickListener {
            makeRequest()
            false
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun makeRequest() {
        NetApiClient.editProfile(viewName.getEditText().text.toString(),
                viewSurName.getEditText().text.toString(),
                viewDateBirth.getEditText().text.toString(), viewInfoUser.getEditText().text.toString()
                , uri, this)
                .doOnSubscribe { showProgressDialog() }
                .doOnUnsubscribe { cancelProgressDialog() }
                .flatMap { NetApiClient.getMyUserInfo() }
                .subscribe({
                    finish()
                }, { it.printStackTrace() })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_IMAGE) {
            uri = data?.data
            if (uri != null) {
                uri = data?.data
                CropImage.activity(uri!!)
                        .setFixAspectRatio(true)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this)
            }
        } else if (requestCode == PHOTO_CAMERA) {
            uri = filePathPhoto
            if (uri != null) {
                CropImage.activity(uri!!)
                        .setFixAspectRatio(true)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this)
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val bitmap = createBitmap(this, result.uri)
                uri = Uri.fromFile(FileClassUtils.getFile(this, bitmap))
                GlideLoadCircle(ivProfile, uri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (REQ_PER_STORAGE == requestCode) {
            if (grantResults[0] == 0) {
                openGallery()
            }
        }
        if (REQ_PER_CAMERA == requestCode) {
            if (grantResults[0] == 0) {
                startCamera()
            }
        }

    }

    override fun onError(it: Throwable?) {

    }

    override fun setDate(date: String?) {
        viewDateBirth.getEditText().text = date
    }

    override fun finish() {
        subsValidate?.unsubscribe()
        subRequest?.unsubscribe()
        super.finish()
    }
}