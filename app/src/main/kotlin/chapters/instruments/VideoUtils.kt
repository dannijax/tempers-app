package chapters.instruments

import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import chapters.main.main.NewPostActivity.Companion.APP_DIR
import chapters.main.main.NewPostActivity.Companion.COMPRESSED_VIDEOS_DIR
import chapters.main.main.NewPostActivity.Companion.TEMP_DIR
import com.yovenny.videocompress.MediaController
import rx.Observable
import rx.schedulers.Schedulers
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class VideoUtils {
    companion object {

        fun createFile(path: String): Observable<File> {
            try2CreateCompressDir()
            val outPath = "" + Environment.getExternalStorageDirectory() + File.separator + APP_DIR +
                    COMPRESSED_VIDEOS_DIR +
                    "VIDEO_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date()) + ".mp4"
            return Observable.create<File> { subsriber ->
                VideoCompressor(path, outPath, object : okCompter {
                    override fun compress() {
                        if (!subsriber.isUnsubscribed) {
                            subsriber.onNext(File(outPath))
                            subsriber.onCompleted()
                        }
                    }

                }).execute()
            }.observeOn(Schedulers.io())
        }

        fun try2CreateCompressDir() {
            var f = File(Environment.getExternalStorageDirectory(), File.separator + APP_DIR)
            f.mkdirs()
            f = File(Environment.getExternalStorageDirectory(), File.separator + APP_DIR + COMPRESSED_VIDEOS_DIR)
            f.mkdirs()
            f = File(Environment.getExternalStorageDirectory(), File.separator + APP_DIR + TEMP_DIR)
            f.mkdirs()
        }
    }

    class VideoCompressor(val inPath: String, val outPath: String, val okCompter: okCompter)
        : AsyncTask<String, Void, Boolean>() {

        override fun onPreExecute() {
            super.onPreExecute();
            Log.d("VIDEO", "Start video compression")
        }

        override fun doInBackground(vararg params: String?): Boolean {
            val con = MediaController.getInstance().convertVideo(inPath, outPath)
            return con

        }

        override fun onPostExecute(compresed: Boolean) {
            super.onPostExecute(compresed);
            Log.d("VIDEO", "Compression successfully! " + compresed.toString() + " thread" + Thread.currentThread().name);

            if (compresed) {
                okCompter.compress()
            }
        }


    }

    interface okCompter {
        fun compress()
    }

}