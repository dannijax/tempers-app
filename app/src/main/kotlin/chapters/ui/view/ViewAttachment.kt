package chapters.ui.view

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import chapters.extension.hide
import chapters.extension.show
import chapters.ui.utils.GlideLoadImage
import com.tempry.R
import kotlinx.android.synthetic.main.view_attachment.view.*


class ViewAttachment @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var func: (() -> (Unit))? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_attachment, this)
    }

    fun setImage(uri:Uri?){
        uri?.let {
            show()
            GlideLoadImage(ivImage,uri)
        }
    }


    fun deleteImage(function: () -> Unit) {
        this.func=function
        ivClose.setOnClickListener {
            hide()
            function.invoke()
        }
    }


}