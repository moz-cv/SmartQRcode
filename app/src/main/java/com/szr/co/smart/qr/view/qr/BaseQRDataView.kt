package com.szr.co.smart.qr.view.qr

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

abstract class BaseQRDataView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    abstract fun qrData(): String
}