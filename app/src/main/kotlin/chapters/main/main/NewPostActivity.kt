package chapters.main.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.widget.Toast
import chapters.base.BaseActivity
import chapters.base.NetRequest
import chapters.extension.*
import chapters.network.NetApiClient
import chapters.network.pojo.Post.UserPost
import chapters.network.pojo.PostItem
import chapters.network.pojo.User
import chapters.ui.adapter.TagPostAdapter
import chapters.utils.location.RxLocation
import com.tempry.R
import filehelpers.FileClassUtils
import kotlinx.android.synthetic.main.actiivty_new_post.*
import kotlinx.android.synthetic.main.app_bar_main.view.*
import kotlinx.android.synthetic.main.view_attachment.view.*
import rx.android.schedulers.AndroidSchedulers


class NewPostActivity : BaseActivity(), NetRequest {

    private var isVideo: Boolean? = null
    private var uri: Uri? = null
    private lateinit var adapter: TagPostAdapter
    private var post: PostItem? = null

    override fun setLayoutRes(): Int {
        return R.layout.actiivty_new_post
    }

    companion object {
        fun launch(context: Context, firstPost: PostItem?) {
            val intent = Intent(context, NewPostActivity::class.java)
            intent.putExtra("post", firstPost)
            context.startActivity(intent)
        }

        val APP_DIR = "VideoCompressor"

        val COMPRESSED_VIDEOS_DIR = "/Compressed Videos/"

        val TEMP_DIR = "/Temp/"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        post = intent.getSerializableExtra("post") as PostItem?
        initView()
        initRv()
    }

    private fun initRv() {
        adapter = TagPostAdapter(this)
        rvTag.initGrid(this, 2, GridLayoutManager.VERTICAL)
        rvTag.adapter = adapter
    }


    private fun initView() {
        initToolBar(viewToolbar.toolbar, arrow = true)
        etMessage.background = null
        viewAttach.deleteImage {
            uri = null
        }
        btnCreate.setOnClickListener {
            if (uri != null || etMessage.text.isNotEmpty() || adapter.list.isNotEmpty()) {
                makeRequest()
            } else {
                showSnackBar("Attach file , enter message or add tag")
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_new_post, menu)
        val s = menu?.findItem(R.id.action_pick_file)
        s?.setOnMenuItemClickListener {
            createDialogChoice()
            false
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_IMAGE && resultCode== Activity.RESULT_OK) {
            uri = data?.data
            viewAttach.setImage(uri)
        } else if (requestCode == PHOTO_CAMERA && resultCode== Activity.RESULT_OK) {
            uri = filePathPhoto
            viewAttach.setImage(uri)
        } else if (requestCode == ADD_TAG_POST && resultCode == Activity.RESULT_OK) {
            val tagItem = data?.getSerializableExtra("tagItem") as ArrayList<User>
            val list = adapter.list
            list.addAll(tagItem)
            val newList = list.distinctBy { it.validId }
            adapter.list.clear()
            adapter.list.addAll(newList)
            adapter.notifyDataSetChanged()
        }
    }

    private fun createDialogChoice() {
        val dialog = AlertDialog.Builder(this)
                .setTitle("Type attachment")
                .setSingleChoiceItems(arrayOf("Video", "Image", "Tag`s"), -1) { dialog, which ->
                    dialog.dismiss()

                    if (which == 0) {
                        isVideo = true
                        openCameraOrGallery(MediaStore.ACTION_VIDEO_CAPTURE)
                        //openGallery(MediaStore.ACTION_VIDEO_CAPTURE)
                    }
                    if (which == 1) {
                        isVideo = false
                        openCameraOrGallery(MediaStore.ACTION_IMAGE_CAPTURE)
                    } else if (which == 2) {
                        FindUserActivity.launch(this)
                    }
                }
                .create().show()
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

    private fun createArrayUser(): List<UserPost> {
        return adapter.list.map { UserPost(it.validId.toString()) }
    }

    override fun makeRequest() {
        if (post?.text == etMessage.text.toString() && isVideo==null) {
            Toast.makeText(this,"It looks like you already shared that ",Toast.LENGTH_LONG).show()
            return
        }
        var bitmap: Bitmap? = null
        try {
            viewAttach.ivImage.buildDrawingCache(true)
            bitmap = viewAttach.ivImage.getDrawingCache(true)
        } catch(e: Exception) {
            e.printStackTrace()
        }
        showProgressDialog()
        showToast(RxLocation.subLoc.value.toString())
        NetApiClient.createPost(etMessage.text.toString(), isVideo, uri, this, bitmap
                , createArrayUser())
                .doOnUnsubscribe { cancelProgressDialog() }
                .doOnNext {
                    FileClassUtils.clearImageCache(this)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ finish() }, { it.printStackTrace() })

    }
}