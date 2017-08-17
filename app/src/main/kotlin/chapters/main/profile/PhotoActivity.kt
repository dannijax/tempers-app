package chapters.main.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import chapters.base.BaseActivity
import chapters.database.SharedSetting
import chapters.extension.show
import chapters.network.pojo.PostItem
import chapters.ui.utils.GlideLoadRoundedCorners
import com.tempry.R
import kotlinx.android.synthetic.main.activity_photo.*

class PhotoActivity : BaseActivity() {


    var y=0
    var action=MotionEvent.ACTION_DOWN
    override fun setLayoutRes(): Int {

        return R.layout.activity_photo
    }

    companion object {
        fun launch(context: Context, photo: String?, postItem: PostItem?) {
            val intent = Intent(context, PhotoActivity::class.java)
            intent.putExtra("photo", photo)
            intent.putExtra("post",postItem)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        initToolBar(viewToolbar,arrow = true)
        viewToolbar.setNavigationIcon(R.drawable.ic_close)
        val photo = intent.getStringExtra("photo")
        GlideLoadRoundedCorners(ivFullPhoto, photo)
        ivFullPhoto.setOnTouchListener { v, event ->
            if(event.action==MotionEvent.ACTION_UP){

                if((event.y.toInt()-y)>220 && !ivFullPhoto.isZoomable){
                    finish()
                    y=0
                }else{
                    y=0
                    action=MotionEvent.ACTION_DOWN
                }
            }
            if(action!=MotionEvent.ACTION_MOVE){
                y=event.y.toInt()
                action=event.action
            }
            false
        }
        val item=intent.getSerializableExtra("post") as PostItem?
        item?.let {
            viewBottom.show()
            viewBottom.bind(it)
        }
    }
}