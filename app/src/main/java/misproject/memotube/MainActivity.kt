package misproject.memotube

import android.graphics.Rect
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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






class MainActivity : AppCompatActivity(), Player.EventListener {
    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
    }

    override fun onSeekProcessed() {
    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
    }

    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
    }

    override fun onLoadingChanged(isLoading: Boolean) {
    }

    override fun onPositionDiscontinuity(reason: Int) {
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if(playbackState == Player.STATE_BUFFERING)
            progressBar.visibility = View.VISIBLE
        else if(playbackState == Player.STATE_READY)
            progressBar.visibility = View.INVISIBLE
    }

    private val TAG = "Debug"

    private var isNoteMode = false

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
        player.addListener(this)

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
                player.setPlayWhenReady(false)
                isNoteMode = true
                textView.visibility = View.VISIBLE
            } else {
                if(isNoteMode == true) {
                    player.setPlayWhenReady(true)   // shouldn't resume when it wasn't playing before TODO
                    isNoteMode = false
                    textView.visibility = View.INVISIBLE
                    // save the memo TODO
                    textView.clearCanvas()

                }
            }
            false
        })
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