package com.example.agoraonetoonechatdemo.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.agoraonetoonechatdemo.R
import com.example.agoraonetoonechatdemo.databinding.ActivityMainBinding
import com.example.agoraonetoonechatdemo.util.Constants
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas

class MainActivity : AppCompatActivity() {
     private lateinit var binding: ActivityMainBinding
    private val myAppId = "0d4402400ac647f0b0caa401ace09067"
    private var mRtcEngine: RtcEngine? = null
    private val channelName = "video_call_channel"
    private val token = "007eJxTYFh7u/uVj7m0CHdN2iXOeTptB/S350j7nJfeX/xPNf/4pF8KDAYpJiYGRiYGBonJZibmaQZJBsmJiSYGhonJqQaWBmbmE59OymwIZGTY9T+VkZEBAkF8IYayzJTU/PjkxJyc+OSMxLy81BwGBgAfWCV+"
    private val uid = 0
    private var isJoined = false

    private var localSurfaceView: SurfaceView? = null

    private var remoteSurfaceView: SurfaceView? = null


    private val PERMISSION_REQ_ID = 22
    private val REQUESTED_PERMISSIONS = arrayOf<String>(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        if (checkSelfPermission()) {
            initializeAgoraEngine()
        } else {
            requestPermissions(REQUESTED_PERMISSIONS, PERMISSION_REQ_ID)
        }

        binding.JoinButton.setOnClickListener { joinChannel() }
        binding.LeaveButton.setOnClickListener { leaveChannel() }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQ_ID &&
            grantResults.isNotEmpty() &&
            grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        ) {
            initializeAgoraEngine()
        } else {
            showMessage("Permissions are required for video call")
        }
    }
    private fun checkSelfPermission(): Boolean {
        return REQUESTED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }
    fun showMessage(message: String?) {
        runOnUiThread {
            Toast.makeText(
                applicationContext,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private fun initializeAgoraEngine() {
        Log.e("AgoraJoin", "initializeAgoraEngine: ")
        try {
            val config = RtcEngineConfig().apply {
                mContext = this@MainActivity
                mAppId = myAppId
                mEventHandler = mRtcEventHandler
            }
            mRtcEngine = RtcEngine.create(config)
            mRtcEngine?.enableVideo()
            Log.e("AgoraJoin", "initializeAgoraEngine3232232: ")

        } catch (e: Exception) {
            showMessage("Error initializing Agora SDK: ${e.message}")
        }
    }
    private fun setupVideoSDKEngine() {
        try {
            val config = RtcEngineConfig().apply {
                mContext = this@MainActivity
                mAppId = myAppId
                mEventHandler = mRtcEventHandler
            }
            mRtcEngine = RtcEngine.create(config)
            mRtcEngine?.enableVideo()

        } catch (e: Exception) {
            showMessage(e.toString())
        }
    }
    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            showMessage("Remote user joined $uid")

            // Set the remote video view
            runOnUiThread { setupRemoteVideo(uid) }
        }

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            isJoined = true
            showMessage("Joined Channel $channel")
            Log.e("AgoraJoin", "onJoinChannelSuccess:$channel")
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            showMessage("Remote user offline $uid $reason")
            Log.e("AgoraJoin", "onJoinChannelSuccess:$uid")

            runOnUiThread { remoteSurfaceView!!.visibility = View.GONE }
        }
    }
    private fun setupRemoteVideo(uid: Int) {
        if (remoteSurfaceView == null) {
            remoteSurfaceView = SurfaceView(this)
            remoteSurfaceView?.setZOrderMediaOverlay(true)
            binding.remoteVideoViewContainer.addView(remoteSurfaceView)
        }
        mRtcEngine?.setupRemoteVideo(VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_FIT, uid))
        remoteSurfaceView?.visibility = View.VISIBLE
    }

    private fun setupLocalVideo() {
        if (localSurfaceView == null) {
            localSurfaceView = SurfaceView(this)
            binding.localVideoViewContainer.addView(localSurfaceView)
            mRtcEngine?.setupLocalVideo(VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0))
        }
        mRtcEngine?.startPreview()
    }




    private fun joinChannel() {
        if (!checkSelfPermission()) {
            showMessage("Permissions not granted")
            return
        }

        setupLocalVideo()  // good, keep this

        val options = ChannelMediaOptions().apply {
            channelProfile = io.agora.rtc2.Constants.CHANNEL_PROFILE_COMMUNICATION
            clientRoleType = io.agora.rtc2.Constants.CLIENT_ROLE_BROADCASTER
        }

        val joinResult = mRtcEngine?.joinChannel(token, channelName, uid, options) ?: -999  // -999 if engine null

        Log.e("AgoraJoin", "joinChannel returned: $joinResult")

        if (joinResult != 0) {
            val errorMsg = when (joinResult) {
                -1 -> "General failure (invalid params or state)"
                -2 -> "Invalid argument (check token/channel/uid)"
                -17 -> "Join rejected (already joining, or test account limit?)"  // common in free/test projects
                -5 -> "Refused (wrong state)"
                else -> "Error code $joinResult — check Agora docs"
            }
            showMessage("Join failed: $errorMsg (code $joinResult)")
            Log.e("AgoraJoin", "Detailed: $errorMsg")
        } else {
            Log.e("AgoraJoin", "joinChannel called successfully (async)")
        }
    }
    private fun leaveChannel() {
        if (!isJoined) {
            showMessage("Join a channel first")
            return
        }

        mRtcEngine?.leaveChannel()
        showMessage("Left the channel")

        localSurfaceView?.visibility = View.GONE
        remoteSurfaceView?.visibility = View.GONE
        isJoined = false
    }

    override fun onDestroy() {
        super.onDestroy()
        mRtcEngine?.stopPreview()
        mRtcEngine = null
    }
}