package com.codertainment.dpadview.sample

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
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
      dpad.modify {

        isHapticFeedbackEnabled = prefs.getBoolean("haptic_feedback", true)

        normalColor = prefs.getInt("normal_color", 0)
        pressedColor = prefs.getInt("pressed_color", 0)
        padding = getFloat("image_padding", 24f)

        isCenterCircleEnabled = prefs.getBoolean("center_circle", true)
        isCenterCirclePressEnabled = prefs.getBoolean("center_circle_press", true)

        centerCircleNormalColor = prefs.getInt("center_circle_normal_color", 0)
        centerCirclePressedColor = prefs.getInt("center_circle_pressed_color", 0)

        centerCircleRatio = getFloat("center_circle_ratio", 4f)

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

        centerIconSize = getFloat("center_icon_size", 48f)

        centerText = if (prefs.getBoolean("center_text_enabled", true)) {
          prefs.getString("center_text", "O K")
        } else {
          ""
        }

        centerIconTint = prefs.getInt("center_icon_tint", 0)

        centerTextSize = getFloat("center_text_size", 18f)

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
      }
    }

    apply.performClick()

    dpad.onDirectionPressListener = { direction, action ->
      val text = StringBuilder()
      val directionText = direction?.name ?: ""
      if (directionText.isNotEmpty()) {
        text.append("Direction:\t")
      }
      text.append(directionText)
      if (directionText.isNotEmpty()) {
        text.append("\nAction:\t")
        val actionText = when (action) {
          MotionEvent.ACTION_DOWN -> "Down"
          MotionEvent.ACTION_UP -> "Up"
          MotionEvent.ACTION_MOVE -> "Move"
          else -> action.toString()
        }
        text.append(actionText)
      }
      touched.text = text.toString()
    }

    dpad.onDirectionClickListener = {
      it?.let {
        Log.i("directionPress", it.name)
      }
    }

    dpad.setOnClickListener {
      Log.i("Click", "Done")
    }
  }

  private fun getFloat(key: String, defValue: Float): Float {
    return try {
      prefs.getString(key, "$defValue")?.toFloat() ?: defValue
    } catch (e: Exception) {
      e.printStackTrace()
      defValue
    }
  }
}
