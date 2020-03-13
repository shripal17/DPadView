package com.codertainment.dpadview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.min

/*
 * Created by Shripal Jain
 * on 12/03/2020
 */

class DPadView(context: Context, private val attrs: AttributeSet) : AppCompatImageView(context, attrs) {

  enum class TextStyle(val style: Int) {
    NORMAL(0),
    BOLD(1),
    ITALIC(2),
    UNDERLINE(4)
  }

  enum class CenterIconSizeMode(mode: Int) {
    WRAP(0),
    FIXED(1)
  }

  @ColorInt
  var normalDirectionColor: Int

  @ColorInt
  var pressedDirectionColor: Int

  @ColorInt
  var centerCircleNormalColor: Int

  @ColorInt
  var centerCirclePressedColor: Int

  var isCenterCircleEnabled = true
  var isCenterCirclePressEnabled = true

  var centerText: String? = null

  @DrawableRes
  var centerIcon: Int? = null

  var centerCircleRatio = 3.5f

  private var size = 0

  private var circleCenter = size.toFloat() / 2
  private var centerCircleRadius = size / centerCircleRatio
  var padding = 20f
  var centerIconSizeMode: CenterIconSizeMode
  var centerIconSize: Float

  @ColorInt
  var centerIconTint: Int
  var centerIconDrawable: Drawable? = null

  var centerTextSize: Float

  @ColorInt
  var centerTextColor: Int

  var centerTextStyle: Int

  private var textPaint = Paint()

  private val centerCirclePaint = Paint()
  private val upPaint = Paint()
  private val downPaint = Paint()
  private val leftPaint = Paint()
  private val rightPaint = Paint()
  private var upTouched = false
  private var downTouched = false
  private var leftTouched = false
  private var rightTouched = false
  private var centerCircleTouched = false
  private val halfPadding = padding / 2
  private var sweepAngle = 88f
  private var clipBoundsRect = Rect()
  private var textBoundsRect = Rect()
  private var centerIconRect = Rect()
  private var arcsRect = RectF()

  init {
    context.theme.obtainStyledAttributes(attrs, R.styleable.DPadView, 0, 0).apply {
      normalDirectionColor = getColor(R.styleable.DPadView_normalColor, context.resolveColorAttr(android.R.attr.textColorSecondary))
      pressedDirectionColor = getColor(R.styleable.DPadView_pressedColor, context.resolveColorAttr(R.attr.colorAccent))
      centerCircleNormalColor = getColor(R.styleable.DPadView_centerCircleNormalColor, context.resolveColorAttr(R.attr.colorPrimary))
      centerCirclePressedColor = getColor(R.styleable.DPadView_centerCirclePressedColor, context.resolveColorAttr(R.attr.colorPrimaryDark))
      isCenterCircleEnabled = getBoolean(R.styleable.DPadView_centerCircleEnabled, true)
      isCenterCirclePressEnabled = getBoolean(R.styleable.DPadView_centerCirclePressEnabled, true)
      centerText = getString(R.styleable.DPadView_centerText)
      centerIcon = getResourceId(R.styleable.DPadView_centerIcon, 0)
      centerCircleRatio = getFloat(R.styleable.DPadView_centerCircleRatio, 3.5f)
      padding = getDimension(R.styleable.DPadView_imagePadding, 20f)

      centerIconSizeMode = if (getInt(R.styleable.DPadView_centerIconSizeMode, 0) == 1) CenterIconSizeMode.FIXED else CenterIconSizeMode.WRAP
      centerIconSize = getDimension(R.styleable.DPadView_centerIconSize, 24f)
      centerIconTint = getColor(R.styleable.DPadView_centerIconTint, 0)

      centerTextSize = getDimension(R.styleable.DPadView_centerTextSize, 14f)
      val defaultTextColor = if (ColorUtils.calculateLuminance(centerCircleNormalColor) < 0.5f) {
        Color.WHITE
      } else {
        Color.BLACK
      }
      centerTextColor = getColor(R.styleable.DPadView_centerTextColor, defaultTextColor)
      centerTextStyle = getInteger(R.styleable.DPadView_centerTextStyle, 0)

      recycle()

      init()
    }
  }

  private fun init() {
    centerIconDrawable = if (centerIcon != null) {
      if (centerIcon != 0) {
        ContextCompat.getDrawable(context, centerIcon!!)
      } else null
    } else null

    if (centerIconDrawable != null) {
      if (centerIconTint != 0) {
        centerIconDrawable!!.colorFilter = PorterDuffColorFilter(centerIconTint, PorterDuff.Mode.SRC_ATOP)
      }
    }

    textPaint.apply {
      color = centerTextColor
      textSize = centerTextSize
      flags = if (centerTextStyle and 4 == 4) {
        Paint.UNDERLINE_TEXT_FLAG
      } else {
        0
      }
      val typeface = if (centerTextStyle and 1 == 1 && centerTextStyle and 2 == 2) {
        Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
      } else if (centerTextStyle and 1 == 1) {
        Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      } else if (centerTextStyle and 2 == 2) {
        Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
      } else {
        null
      }
      setTypeface(typeface)
    }

    upPaint.color = normalDirectionColor
    downPaint.color = normalDirectionColor
    rightPaint.color = normalDirectionColor
    leftPaint.color = normalDirectionColor

    centerCirclePaint.color = centerCircleNormalColor

    setPadding(padding.toInt(), padding.toInt(), padding.toInt(), padding.toInt())
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    size = min(measuredWidth, measuredHeight)
    circleCenter = size.toFloat() / 2
    setMeasuredDimension(size, size)
  }

  override fun onDraw(canvas: Canvas?) {
    if (canvas == null) return
    circleCenter = size.toFloat() / 2
    centerCircleRadius = size / centerCircleRatio

    canvas.getClipBounds(clipBoundsRect)

    arcsRect.set(clipBoundsRect.left.toFloat(), clipBoundsRect.top.toFloat(), clipBoundsRect.right.toFloat(), clipBoundsRect.bottom.toFloat())

    canvas.drawArc(arcsRect, 46f, sweepAngle, true, downPaint)
    canvas.drawArc(arcsRect, 136f, sweepAngle, true, leftPaint)
    canvas.drawArc(arcsRect, 226f, sweepAngle, true, upPaint)
    canvas.drawArc(arcsRect, 316f, sweepAngle, true, rightPaint)

    if (isCenterCircleEnabled) {
      canvas.drawCircle(circleCenter, circleCenter, centerCircleRadius, centerCirclePaint)
    }

    super.onDraw(canvas)

    centerIconDrawable?.let {
      if (centerIconSizeMode == CenterIconSizeMode.WRAP) {
        centerIconRect.set(
          circleCenter.toInt() - it.intrinsicWidth / 2, circleCenter.toInt() - it.intrinsicHeight / 2, circleCenter.toInt() + it.intrinsicWidth / 2,
          circleCenter.toInt() + it.intrinsicHeight / 2
        )
      } else {
        centerIconRect.set(
          circleCenter.toInt() - centerIconSize.toInt() / 2,
          circleCenter.toInt() - centerIconSize.toInt() / 2,
          circleCenter.toInt() + centerIconSize.toInt() / 2,
          circleCenter.toInt() + centerIconSize.toInt() / 2
        )
      }
      it.bounds = centerIconRect
      it.draw(canvas)
    }

    if (!centerText.isNullOrEmpty()) {
      textPaint.getTextBounds(centerText, 0, centerText!!.length, textBoundsRect)
      val textX = clipBoundsRect.width() / 2f - textBoundsRect.width() / 2f - textBoundsRect.left
      val textY = clipBoundsRect.height() / 2f + textBoundsRect.height() / 2f - textBoundsRect.bottom
      canvas.drawText(centerText!!, textX, textY, textPaint)
    }
  }

  fun reInit() {
    init()
    invalidate()
  }

  override fun onTouchEvent(event: MotionEvent?): Boolean {
    if (event == null) return false

    val isInCircle = isInCircle(event.x, event.y, circleCenter.toInt(), circleCenter - halfPadding)
    val isInCenterCircle = if (isCenterCircleEnabled && isCenterCirclePressEnabled) {
      isInCircle(event.x, event.y, circleCenter.toInt(), centerCircleRadius - halfPadding)
    } else false
    centerCircleTouched = false

    rightTouched = false
    leftTouched = false
    upTouched = false
    downTouched = false

    if (isInCircle) {
      val isTouched = event.isTouched

      if (isInCenterCircle) {
        centerCircleTouched = isTouched
      } else {
        var deg = Math.toDegrees(atan2(event.y - width / 2, event.x - width / 2).toDouble())
        if (deg < 0) {
          deg += 360
        }

        if (deg > 315 || deg <= 45) {
          rightTouched = isTouched
        } else if (deg > 45 && deg <= 135) {
          downTouched = isTouched
        } else if (deg > 135 && deg <= 225) {
          leftTouched = isTouched
        } else if (deg > 225 && deg < 315) {
          upTouched = isTouched
        }
      }
    }

    centerCirclePaint.color = if (!centerCircleTouched) centerCircleNormalColor else centerCirclePressedColor
    rightPaint.color = if (!rightTouched) normalDirectionColor else pressedDirectionColor
    downPaint.color = if (!downTouched) normalDirectionColor else pressedDirectionColor
    leftPaint.color = if (!leftTouched) normalDirectionColor else pressedDirectionColor
    upPaint.color = if (!upTouched) normalDirectionColor else pressedDirectionColor

    if (isHapticFeedbackEnabled && event.action == MotionEvent.ACTION_DOWN) {
      if (centerCircleTouched || rightTouched || leftTouched || upTouched || downTouched) {
        performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
      }
    }

    invalidate()

    return isInCircle
  }

  private val MotionEvent.isTouched
    get() = action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE

  private fun isInCircle(x: Float, y: Float, center: Int, radius: Float): Boolean {
    val dx = abs(x - center)
    if (dx > radius) return false

    val dy = abs(y - center)
    if (dy > radius) return false

    if (dx + dy <= radius) return true
    return dx * dx + dy * dy <= radius * radius
  }
}

@ColorInt
fun Context.resolveColorAttr(@AttrRes colorAttr: Int): Int {
  val resolvedAttr = resolveThemeAttr(colorAttr)
  // resourceId is used if it's a ColorStateList, and data if it's a color reference or a hex color
  val colorRes = if (resolvedAttr.resourceId != 0) resolvedAttr.resourceId else resolvedAttr.data
  return ContextCompat.getColor(this, colorRes)
}

fun Context.resolveThemeAttr(@AttrRes attrRes: Int): TypedValue {
  val theme = theme
  val typedValue = TypedValue()
  theme.resolveAttribute(attrRes, typedValue, true)
  return typedValue
}