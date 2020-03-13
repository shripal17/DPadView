package com.codertainment.dpadview.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codertainment.dpadview.DPadView
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.defaultSharedPreferences

class MainActivity : AppCompatActivity() {

  private val prefs by lazy {
    defaultSharedPreferences
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    apply.setOnClickListener {
      dpad.apply {

        isHapticFeedbackEnabled = prefs.getBoolean("haptic_feedback", true)

        normalDirectionColor = prefs.getInt("normal_color", 0)
        pressedDirectionColor = prefs.getInt("pressed_color", 0)
        padding = prefs.getString("image_padding", "24")?.toFloat() ?: 24f

        isCenterCircleEnabled = prefs.getBoolean("center_circle", true)
        isCenterCirclePressEnabled = prefs.getBoolean("center_circle_press", true)

        centerCircleNormalColor = prefs.getInt("center_circle_normal_color", 0)
        centerCirclePressedColor = prefs.getInt("center_circle_pressed_color", 0)

        centerCircleRatio = prefs.getString("center_circle_ratio", "4")?.toFloat() ?: 4f

        centerIcon = if (prefs.getBoolean("center_icon", true)) {
          R.drawable.ic_done_black
        } else {
          null
        }

        centerIconSizeMode = if (prefs.getBoolean("center_icon_fixed", true)) {
          DPadView.CenterIconSizeMode.FIXED
        } else {
          DPadView.CenterIconSizeMode.WRAP
        }

        centerIconSize = prefs.getString("center_icon_size", "48")?.toFloat() ?: 48f

        centerText = if (prefs.getBoolean("center_text_enabled", true)) {
          prefs.getString("center_text", "O K")
        } else {
          ""
        }

        centerIconTint = prefs.getInt("center_icon_color", 0)

        centerTextSize = prefs.getString("center_text_size", "18")?.toFloat() ?: 18f

        var style = 0
        if (prefs.getBoolean("center_text_bold", true)) {
          style = style or DPadView.TextStyle.BOLD.style
        }
        if (prefs.getBoolean("center_text_italic", true)) {
          style = style or DPadView.TextStyle.ITALIC.style
        }
        if (prefs.getBoolean("center_text_underline", true)) {
          style = style or DPadView.TextStyle.UNDERLINE.style
        }

        centerTextStyle = style

        centerTextColor = prefs.getInt("center_text_color", 0)
        reInit()
      }
    }

    apply.performClick()
  }
}
