package misproject.memotube

import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import kotlinx.android.synthetic.main.activity_main.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.widget.Toast
import android.text.method.Touch.onTouchEvent
import android.text.method.Touch.onTouchEvent
import android.util.Log
import android.text.method.Touch.onTouchEvent
import android.view.View.OnTouchListener
import com.simplify.ink.InkView
import kotlinx.android.synthetic.main.exo_controller.*
import misproject.memotube.R.id.exo_progress
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {

    private val TAG = "Debug"
    private var videoTitle = "videoTitle"
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
//    private val gestureDetector by lazy {
//        GestureDetector(this, TouchListener())
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

    private fun initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(
                DefaultRenderersFactory(this),
                DefaultTrackSelector(adaptiveTrackSelectionFactory),
                DefaultLoadControl()
        )
        val uri = Uri.parse(dashUrl)
        val mediaSource = buildMediaSource(uri)
        player.prepare(mediaSource)

        playerView.player = player
        player.seekTo(playbackPosition)
        player.playWhenReady = true

        exo_close.setOnClickListener(View.OnClickListener { this@MainActivity.finish() })

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
                // prevent player to resume whlie it wasn't before
                timestamp = player.getCurrentPosition()
                if(!isNoteMode) {
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

//    private inner class TouchListener : GestureDetector.SimpleOnGestureListener() {
//
//        override fun onSingleTapUp(e: MotionEvent): Boolean { //you can override onSingleTapConfirmed if you don't want doubleClick to fire it
//            Log.d(TAG, "onSingleTapUp: TAP DETECTED") //logged only upon click
//            return true
//        }
//
//    }
}