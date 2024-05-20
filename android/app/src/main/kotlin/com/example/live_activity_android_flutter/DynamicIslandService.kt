package com.example.live_activity_android_flutter

import LaunchFlutterWorker
import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.graphics.PixelFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.ImageView
import android.widget.Toast
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import io.flutter.embedding.android.FlutterActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DynamicIslandService : AccessibilityService() {

    lateinit var floatingView: View
    val eventBus = EventBus.getDefault()
    lateinit var windowManager: WindowManager
    lateinit var params: WindowManager.LayoutParams
    var y = 0
    var x = 0

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
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

    private fun showTheIsland() {
        floatingView = LayoutInflater.from(this).inflate(R.layout.view_dynamic_island, null)

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.x = x
        params.y = y

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.addView(floatingView, params)

        val open = floatingView.findViewById<ImageView>(R.id.imageView_open)
        val notification = floatingView.findViewById<ImageView>(R.id.imageView_notification)

        open.setOnClickListener {
            Log.e("shaimaa", "shaimaa")

            val workRequest = OneTimeWorkRequestBuilder<LaunchFlutterWorker>().build()
            WorkManager.getInstance(this).enqueue(workRequest)

//            val intent = FlutterActivity
//                .withCachedEngine("my_engine_id")
//                .build(this)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            this.startActivity(intent)
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: EventPositionChanged) {
        params.y = event.y
        params.x = event.x
        windowManager.updateViewLayout(floatingView, params)
    }

}

