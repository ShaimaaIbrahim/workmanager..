package com.example.live_activity_android_flutter

import LaunchFlutterWorker
import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.ImageView
import android.widget.TextView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class DynamicIslandService : AccessibilityService() {

    lateinit var floatingView: View
    private val eventBus = EventBus.getDefault()
    lateinit var windowManager: WindowManager
    lateinit var params: WindowManager.LayoutParams
    var y = 0
    var x = 0
    var seconds = 0
    var inBackground: Boolean = false

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
    }

    override fun onInterrupt() {
    }

    override fun onCreate() {
        super.onCreate()
        eventBus.register(this)

        val preferences = getSharedPreferences(Constants.MY_DATA, MODE_PRIVATE)
        y = preferences.getInt(Constants.Y_KEY, 0)
        x = preferences.getInt(Constants.X_KEY, 0)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        showTheIsland()
    }

    @SuppressLint("SetTextI18n")
    private fun showTheIsland() {
        floatingView = LayoutInflater.from(this).inflate(R.layout.view_dynamic_island, null)

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL

        params.x = x
        params.y = 100

     if(inBackground){
         windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
         windowManager.addView(floatingView, params)
     }
        val open = floatingView.findViewById<ImageView>(R.id.imageView_open)
        val secondsText = floatingView.findViewById<TextView>(R.id.seconds_text)

        open.setOnClickListener {
            val workRequest = OneTimeWorkRequestBuilder<LaunchFlutterWorker>().build()
            WorkManager.getInstance(this).enqueue(workRequest)

            secondsText.text = "" + seconds
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: EventPositionChanged) {
        params.y = event.y
        params.x = event.x
        windowManager.updateViewLayout(floatingView, params)
    }
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onChangeBackground(event: EventCheckBackground) {
      inBackground = event.inBackground
    }
}

