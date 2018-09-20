package misproject.memotube

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.app.Activity
import android.net.Uri
import android.support.v4.app.FragmentActivity
import android.util.Log

class MainActivity: AppCompatActivity() {

    val READ_REQUEST_CODE = 42

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val DemoButton: Button = findViewById(R.id.scene2)
        DemoButton.setOnClickListener{
            val intent = Intent(this, VideoActivity :: class.java)
            intent.putExtra("uri", "DEMO")
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
                intent.putExtra("uri", uri)
                startActivity(intent)
                Log.i(FragmentActivity.STORAGE_SERVICE, "Uri: " + videoUri!!.toString())
            }
        }
    }

}