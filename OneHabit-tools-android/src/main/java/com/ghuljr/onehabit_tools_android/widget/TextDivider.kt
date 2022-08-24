package com.ghuljr.onehabit_tools_android.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.ghuljr.onehabit_tools_android.R
import com.ghuljr.onehabit_tools_android.databinding.WidgetTextDividerBinding

//TODO: add animations
class TextDivider(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs, 0, 0) {

    private val viewBind by lazy { WidgetTextDividerBinding.inflate(LayoutInflater.from(context), this, true) }

    private val paint: Paint by lazy { Paint().apply { color = layoutColor } }

    private val layoutColor: Int
    private val textPadding: Float

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.OneHabit_Widget_TextDivider, 0, 0)
            .apply {
                layoutColor = getColor(R.styleable.OneHabit_Widget_TextDivider_color, ContextCompat.getColor(context, R.color.primary_black))
                textPadding = getDimension(R.styleable.OneHabit_Widget_TextDivider_textPadding, 0f)

                viewBind.apply {
                    title.apply {
                        text = getString(R.styleable.OneHabit_Widget_TextDivider_android_text)
                        setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimension(R.styleable.OneHabit_Widget_TextDivider_textSize, resources.getDimension(R.dimen.default_text_size_large)))
                        setTextColor(ColorStateList.valueOf(layoutColor))
                    }

                }

                paint.apply {
                    strokeWidth = getDimension(R.styleable.OneHabit_Widget_TextDivider_strokeWidth, 0f)
                }
            }
    }


    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        val drawHeight = height / 2f
        val drawTo = (width - viewBind.title.width - textPadding) / 2f
        val drawFrom = drawTo + viewBind.title.width + textPadding

        canvas?.drawLine(0f, drawHeight, drawTo, drawHeight, paint)
        canvas?.drawLine(drawFrom, drawHeight, width.toFloat(), drawHeight, paint)
    }
}