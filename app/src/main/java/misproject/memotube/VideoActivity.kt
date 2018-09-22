package misproject.memotube

import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import android.view.View.OnTouchListener
import kotlinx.android.synthetic.main.activity_video.*
import kotlinx.android.synthetic.main.exo_controller.*
import java.io.File
import java.io.FileOutputStream
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Util


class VideoActivity : AppCompatActivity() {

    private lateinit var fragment : BookmarkFragment
    private var bookmarkShown = false
    private var fileUri = "DEMO"

    private val TAG = "Debug"
    private var videoTitle = "DEMO"
    private var timestamp: Long = 0
    private lateinit var emptyBitmap : Bitmap

    private var isNoteMode = false
    private var wasPlaying = true

    private lateinit var player: SimpleExoPlayer
    private var playbackPosition = 0L
    private val dashUrl = "http://rdmedia.bbc.co.uk/dash/ondemand/bbb/2/client_manifest-separate_init.mpd"
    private val bandwidthMeter by lazy {
        DefaultBandwidthMeter()
    }
    private val adaptiveTrackSelectionFactory by lazy {
        AdaptiveTrackSelection.Factory(bandwidthMeter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        // get video uri
        val bundle = intent.extras
        fileUri = bundle!!.getString("uri")
        videoTitle = bundle!!.getString("title")
        val fm = supportFragmentManager

        //if you added fragment via layout xml
        fragment = fm.findFragmentById(R.id.drawer) as BookmarkFragment
        fragment.setArguments(bundle)
        fragment.updateListview()
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onStop() {
        releasePlayer()
        super.onStop()
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory = DefaultHttpDataSourceFactory("ua", bandwidthMeter)
        val dashChunkSourceFactory = DefaultDashChunkSource.Factory(dataSourceFactory)
        return DashMediaSource(uri, dataSourceFactory, dashChunkSourceFactory, null, null)
    }

    private fun buildMediaSourceLocal(uri: Uri): MediaSource {

        val dataSpec = DataSpec(uri);
        val fileDataSource = FileDataSource();
        try {
            fileDataSource.open(dataSpec);
        } catch (e:FileDataSource.FileDataSourceException) {
            e.printStackTrace();
        }

        val factory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "idontknow"), DefaultBandwidthMeter())

        val videoSource = ExtractorMediaSource(uri,
                factory, DefaultExtractorsFactory(), null, null);

        return videoSource
    }

    private fun initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(
                DefaultRenderersFactory(this),
                DefaultTrackSelector(adaptiveTrackSelectionFactory),
                DefaultLoadControl()
        )

        if(fileUri == "DEMO") {
            val uri = Uri.parse(dashUrl)
            val mediaSource = buildMediaSource(uri)
            player.prepare(mediaSource)
        } else {
            val uri = Uri.parse(fileUri)
            val mediaSource = buildMediaSourceLocal(uri)
            player.prepare(mediaSource)
        }

        playerView.player = player
        player.seekTo(playbackPosition)
        player.playWhenReady = true

        exo_close.setOnClickListener(View.OnClickListener {
            toggleBookmarks()
        })

        addGestures()
    }

    private fun releasePlayer() {
        playbackPosition = player.getCurrentPosition()
        player.release()
    }

    private fun addGestures () {
        playerView.setOnTouchListener(OnTouchListener { _, event ->
            val pointerCount = event.pointerCount
            if (pointerCount > 1) { // pause with two finger pressed
                timestamp = player.getCurrentPosition()
                if(!isNoteMode) { // prevent player to resume whlie it wasn't before
                    if (player.getPlayWhenReady())
                        wasPlaying = true
                    else wasPlaying = false
                    emptyBitmap = drawView.getBitmap()  // to find out if the canvas is empty
                }
                player.setPlayWhenReady(false)
                isNoteMode = true

                drawView.visibility = View.VISIBLE
                playerView.hideController()

            } else {
                if(isNoteMode == true) {
                    if(wasPlaying)
                        player.setPlayWhenReady(true)
                    isNoteMode = false
                    drawView.visibility = View.INVISIBLE
                    var bitmap = drawView.getBitmap()
                    if(!bitmap.sameAs(emptyBitmap)) {
                        saveBitmap(bitmap, timestamp)
                        drawView.clearCanvas()
                        fragment.updateListview()
                    }
                }
            }
            false
        })
    }

    fun saveBitmap(bitmap: Bitmap, timestamp: Long) {
        val fileName = "Memotube/" + videoTitle +  timestamp.toString() + ".png"
        val file = File(Environment.getExternalStorageDirectory(), fileName)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(file))
    }

    fun toggleBookmarks() {
        if(bookmarkShown) {
            exo_close.setBackgroundResource(R.drawable.bookmarkwhite)
            exo_close.setAlpha(0.8F)
        } else {
            exo_close.setBackgroundResource(R.drawable.bookmarkred)
            exo_close.setAlpha(1.0F)
        }
        bookmarkShown = !bookmarkShown
    }

}