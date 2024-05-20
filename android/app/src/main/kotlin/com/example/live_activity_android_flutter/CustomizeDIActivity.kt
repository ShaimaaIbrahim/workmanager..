package com.example.live_activity_android_flutter

import android.content.SharedPreferences.Editor
import android.os.Build
import android.os.Bundle
import android.view.Display
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import org.greenrobot.eventbus.EventBus

class CustomizeDIActivity : AppCompatActivity() {

    val eventBus: EventBus = EventBus.getDefault()
    lateinit var editor: Editor
    lateinit var seekBarY: SeekBar
    lateinit var seekBarX: SeekBar

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customize_diactivity)

        seekBarY = findViewById(R.id.seekBar_y)
        seekBarX = findViewById(R.id.seekBar_x)

        editor = getSharedPreferences(Constants.MY_DATA, MODE_PRIVATE).edit()

        val display: Display = windowManager.defaultDisplay

        initY(display.height)

        initX(display.width)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initX(width: Int) {
        seekBarX.max = width / 2
        seekBarX.min = -width / 2
        seekBarX.progress = 0
        seekBarX.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                eventBus.post(EventPositionChanged(seekBarY.progress, progress))
                editor.putInt(Constants.X_KEY, progress).apply()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initY(height: Int) {
        seekBarY.min = -height / 2 - 100
        seekBarY.max = height / 2
        seekBarY.progress = 0
        seekBarY.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                eventBus.post(EventPositionChanged(progress, seekBarX.progress))
                editor.putInt(Constants.Y_KEY, progress).apply()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
    }

    override fun onDestroy() {
        eventBus.unregister(this)
        super.onDestroy()
    }
}
