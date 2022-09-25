package com.ghuljr.onehabit_tools_android.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.ghuljr.onehabit_error.BaseError
import com.ghuljr.onehabit_error_android.extension.textForError
import com.ghuljr.onehabit_tools_android.R
import com.ghuljr.onehabit_tools_android.databinding.WidgetErrorBinding

class ErrorWidget @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    private val viewBind = WidgetErrorBinding.inflate(LayoutInflater.from(context), this, true)

    private val defaultIcon: Drawable
    private val defaultTitle: String
    private val defaultMessage: String
    private val displayContactSupport: Boolean
    private val displayRetry: Boolean
    private val retryButtonText: String

    init {
        val primaryColor = ContextCompat.getColor(context, R.color.primary_black)
        val customAttrs = context.theme.obtainStyledAttributes(attrs, R.styleable.ErrorWidget, 0, 0)

        defaultIcon = customAttrs.getDrawable(R.styleable.ErrorWidget_errorIcon) ?: ContextCompat.getDrawable(context, R.drawable.placeholder_error)!!
        defaultTitle = customAttrs.getString(R.styleable.ErrorWidget_errorTitle) ?: context.getString(R.string.ups)
        defaultMessage = customAttrs.getString(R.styleable.ErrorWidget_errorMessage) ?: context.getString(R.string.error_unknown)
        displayContactSupport = customAttrs.getBoolean(R.styleable.ErrorWidget_displayContactSupport, false)
        displayRetry = customAttrs.getBoolean(R.styleable.ErrorWidget_displayRetry, true)
        retryButtonText = customAttrs.getString(R.styleable.ErrorWidget_retryButtonText) ?: resources.getString(R.string.retry)

        viewBind.apply {
            title.text = defaultTitle
            message.text = defaultMessage
            icon.setImageDrawable(defaultIcon)
            retryButton.apply {
                isVisible = displayRetry
                text = retryButtonText
            }
        }
    }

    fun setOnRetryClickListener(retry: () -> Unit) {
        viewBind.retryButton.setOnClickListener { retry() }
    }

    fun <E : BaseError> handleError(
        error: E?,
        buildError: (ErrorWidgetParamsBuilder.() -> Unit) = {}
    ) {
        isVisible = error != null
        if (error == null) return

        ErrorWidgetParamsBuilder()
            .build {
                buildError()
                if(message.isNullOrBlank())
                    message = error.textForError(resources)
            }
            .inflateViewWithParams()
    }

    fun show(show: Boolean = true, buildError: (ErrorWidgetParamsBuilder.() -> Unit) = {}) {
        isVisible = show
        if (!show) return

        ErrorWidgetParamsBuilder()
            .build(buildError)
            .inflateViewWithParams()
    }

    internal data class ErrorParams(
        val image: Drawable,
        val title: String,
        val message: String,
        val retryButton: Boolean,
        val contactSupport: Boolean
    )

    inner class ErrorWidgetParamsBuilder {
        var image: Drawable? = null
        var title: String? = null
        var message: String? = null
        var retryButton: Boolean = displayRetry
        var contactSupport: Boolean = displayContactSupport

        internal fun build(buildError: (ErrorWidgetParamsBuilder.() -> Unit) = {}): ErrorParams =
            apply { buildError() }
                .run {
                    ErrorParams(
                        image = image ?: defaultIcon,
                        title = if (title.isNullOrBlank()) defaultTitle else title!!,
                        message = message.orEmpty(),
                        retryButton = retryButton,
                        contactSupport = contactSupport
                    )
                }
    }

    private fun ErrorParams.inflateViewWithParams() {
        this.let {
            viewBind.apply {
                icon.setImageDrawable(it.image)
                title.text = it.title
                message.apply {
                    isVisible = it.message.isNotBlank()
                    text = it.message
                }
                retryButton.isVisible = it.retryButton
            }
        }
    }
}