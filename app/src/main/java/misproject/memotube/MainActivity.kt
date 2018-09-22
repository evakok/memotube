package misproject.memotube

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.app.Activity
import android.net.Uri
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.provider.OpenableColumns

class MainActivity: AppCompatActivity() {

    val READ_REQUEST_CODE = 42

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val DemoButton: Button = findViewById(R.id.scene2)
        DemoButton.setOnClickListener{
            val intent = Intent(this, VideoActivity :: class.java)
            intent.putExtra("uri", "DEMO")
            intent.putExtra("title", "DEMO")
            startActivity(intent)
        }

        val LocalButon: Button = findViewById(R.id.storage)
        LocalButon.setOnClickListener{fileSearch()}

    }

    fun fileSearch() {
        val local_intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        local_intent.addCategory(Intent.CATEGORY_OPENABLE)
        local_intent.type = "video/*"
        startActivityForResult(local_intent, READ_REQUEST_CODE)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int,
                                         resultData: Intent?) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            var videoUri: Uri? = null
            if (resultData != null) {
                videoUri = resultData.data
                val intent = Intent(this, VideoActivity :: class.java)
                val uri = videoUri.toString()
                val fullTitle = getFileName(videoUri)
                val title = fullTitle.removeRange(fullTitle.lastIndexOf('.'), fullTitle.length)
                intent.putExtra("uri", uri)
                intent.putExtra("title", title)
                startActivity(intent)
                Log.i(FragmentActivity.STORAGE_SERVICE, "Uri: " + videoUri!!.toString())
            }
        }
    }

    // Source: https://developer.android.com/guide/topics/providers/document-provider
    fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }
}