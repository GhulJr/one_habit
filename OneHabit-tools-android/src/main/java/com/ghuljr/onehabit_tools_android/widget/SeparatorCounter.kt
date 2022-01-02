package com.ghuljr.onehabit_tools_android.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.ghuljr.onehabit_tools_android.R
import com.ghuljr.onehabit_tools_android.databinding.WidgetSeparatorCounterBinding

/*TODO: enable errors (for instance out of scope) */
class SeparatorCounter(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private val viewBind by lazy { WidgetSeparatorCounterBinding.inflate(LayoutInflater.from(context), this, true) }

    var currentValue: Int = 0
        set(value) {
            field = value
            viewBind.currentValue.text = value.toString()
        }
    var maxValue: Int = 0
        set(value) {
            field = value
            viewBind.maxValue.text = value.toString()
        }
    var separator: String = "/"
        set(value) {
            field = value
            viewBind.separator.text = value
        }

    init {
        //TODO: support remaining attributes
        context.theme.obtainStyledAttributes(attrs, R.styleable.OneHabit_Widget_SeparatorCounter, 0, 0).apply {
            currentValue = getInt(R.styleable.OneHabit_Widget_SeparatorCounter_startValue, 0)
            maxValue = getInt(R.styleable.OneHabit_Widget_SeparatorCounter_maxValue, 1)
            separator = getString(R.styleable.OneHabit_Widget_SeparatorCounter_separator) ?: "/"
        }

        require(currentValue >= 0)
        require(maxValue >= 0)
    }
}