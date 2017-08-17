package chapters.ui.utils

import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.AppCompatImageView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tempry.R
import jp.wasabeef.glide.transformations.CropCircleTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation


fun GlideLoadCircle(imageView: AppCompatImageView, url: String?, error: Int = R.drawable.logo) {

    val placeholder = AppCompatResources.getDrawable(imageView.context,error)
    Glide.with(imageView.context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(false)
            .bitmapTransform(CropCircleTransformation(imageView.context))
            .placeholder(placeholder)
            .error(placeholder)
            .into(imageView)
}

fun GlideLoadCircle(imageView: AppCompatImageView, uri: Uri?) {
    val placeholder = AppCompatResources.getDrawable(imageView.context,R.drawable.logo)
    Glide.with(imageView.context)
            .load(uri)
            .bitmapTransform(CropCircleTransformation(imageView.context))
            .error(placeholder)
            .into(imageView)
}

fun GlideLoadImage(imageView: AppCompatImageView, uri: Uri?) {
    val placeholder = AppCompatResources.getDrawable(imageView.context,R.drawable.logo)
    Glide.with(imageView.context)
            .load(uri)
            .error(placeholder)
            .into(imageView)
}

fun GlideLoadImage(imageView: AppCompatImageView, uri: String?) {
    val placeholder = AppCompatResources.getDrawable(imageView.context,R.drawable.logo)
    Glide.with(imageView.context)
            .load(uri)
            .error(placeholder)
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.RESULT)
            //.placeholder(placeholder)
            .into(imageView)
}

fun GlideLoadImage(imageView: ImageView, uri: String?) {
    val placeholder = AppCompatResources.getDrawable(imageView.context,R.drawable.logo)
    Glide.with(imageView.context)
            .load(uri)
            .error(placeholder)
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.RESULT)
            .placeholder(placeholder)
            .into(imageView)
}

fun GlideLoadImage(imageView: AppCompatImageView, uri: Bitmap?) {
    Glide.with(imageView.context)
            .load(uri)
            .into(imageView)
}


fun GlideLoadRoundedCorners(imageView: AppCompatImageView, url: String?, isRounded: Boolean = true, error: Int = R.drawable.logo) {
    var rounded = 0
    if (isRounded) {
        rounded =8
    }
    val placeholder = AppCompatResources.getDrawable(imageView.context,error)
    Glide.with(imageView.context)
            .load(url)
            .bitmapTransform(RoundedCornersTransformation(imageView.context, rounded, 0, RoundedCornersTransformation.CornerType.ALL))
            .error(placeholder)
            .into(imageView)
}

fun GlideLoadRoundedCorners(imageView: AppCompatImageView, url: Uri?, isRounded: Boolean = true, error: Int = R.drawable.logo) {
    var rounded = 0
    if (isRounded) {
        rounded = 8
    }

    val placeholder = AppCompatResources.getDrawable(imageView.context,error)

    Glide.with(imageView.context)
            .load(url)
            .bitmapTransform(RoundedCornersTransformation(imageView.context, rounded, 0, RoundedCornersTransformation.CornerType.ALL))
            .error(placeholder)
            .into(imageView)
}


