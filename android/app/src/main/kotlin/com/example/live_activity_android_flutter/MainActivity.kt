package com.example.live_activity_android_flutter


//class MainActivity : FlutterActivity() {
//    @Override
//    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
//        super.configureFlutterEngine(flutterEngine)
//        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
//            .setMethodCallHandler { call, result ->
//                if (call.method.equals("getNativeMessage")) {
//                    val message = nativeMessage
//                    result.success(message)
//                } else {
//                    result.notImplemented()
//                }
//            }
//    }
//
//    private val nativeMessage: String
//        private get() = "Hello from Android Native Code!"
//
//    companion object {
//        private const val CHANNEL = "com.example.method_channel_example/channel"
//    }
//}

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils.SimpleStringSplitter
import android.util.Log
import android.widget.Button
import androidx.lifecycle.*
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import org.greenrobot.eventbus.EventBus

class MainActivity : FlutterActivity() {

    private val CHANNEL = "DI"
    private var finalSeconds: Int = 0

    lateinit var buttonChangePosition: Button
    lateinit var buttonCheck: Button

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver())

    }
     @SuppressLint("CommitPrefEdits")
     @Override
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
            .setMethodCallHandler { call, result ->
                if (call.method.equals("checkLayoutPermission")) {
                    checkPermissions()
                }
                else if (call.method.equals("stopLiveActivity")) {
                    Log.e("shaimaa", "shaimaa")
                    //System.exit(0)
                }
                else if (call.method.equals("startLiveActivity")) {
                    val args  = call.arguments as Map<*, *>
                    val seconds: Int = args["elapsedSeconds"] as Int
                    Log.e("shaimaa", seconds.toString())
                }
                else if (call.method.equals("updateLiveActivity")) {
                    val args  = call.arguments as Map<*, *>
                    val seconds: Int = args["elapsedSeconds"] as Int
                    finalSeconds = seconds
                    EventBus.getDefault().post(EventPositionChanged(100, 0, finalSeconds))

//                    val sharedPreferences = applicationContext.getSharedPreferences("SECONDS", Context.MODE_PRIVATE)
//                    // Get SharedPreferences editor
//                    val editor = sharedPreferences.edit()
//                    editor.putInt("seconds", finalSeconds)
                    Log.e("shaimaaSharedPreferences", finalSeconds.toString())
                }
                else {
                    result.notImplemented()
                }
            }
    }
    private fun checkPermissions() {
        if (!Settings.canDrawOverlays(this)) {
            requestOverlayPermission()
        } else if (!isAccessibilitySettingsOn(this)) {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
    }

    private fun requestOverlayPermission() {
        val permissionIntent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")
        )
        permissionIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivityForResult(permissionIntent, 8)
    }

    private fun isAccessibilitySettingsOn(mContext: Context): Boolean {
        var accessibilityEnabled = 0
        val service = packageName + "/" + DynamicIslandService::class.java.canonicalName
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                mContext.applicationContext.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
        }
        val mStringColonSplitter = SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(
                mContext.applicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
                    if (accessibilityService.equals(service, ignoreCase = true)) {
                        return true
                    }
                }
            }
        }
        return false
    }
    inner class AppLifecycleObserver : LifecycleObserver {

        private var appInForeground = false

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onForeground() {
            EventBus().post(EventCheckBackground(false))
            // App is in the foreground
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onBackground() {
            appInForeground = false
            EventBus().post(EventCheckBackground(true))

        }
    }
}