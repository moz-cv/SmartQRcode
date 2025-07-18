package com.szr.co.smart.qr.activity.base

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewbinding.ViewBinding
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.utils.AppLangUtils

abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {

    // 使用懒加载方式初始化 binding，避免在 onCreate 之前访问
    val mBinding: T by lazy { inflateBinding() }

    var skipOnceNativeResume = false
    var isShowing = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = if (lightStatusBar()) SystemBarStyle.dark(Color.TRANSPARENT) else SystemBarStyle.auto(
                Color.TRANSPARENT, Color.TRANSPARENT
            ),
            if (lightStatusNav()) SystemBarStyle.dark(Color.TRANSPARENT) else SystemBarStyle.auto(
                Color.argb(0xe6, 0xFF, 0xFF, 0xFF), Color.argb(0x80, 0x1b, 0x1b, 0x1b)
            )
        )
        setContentView(mBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                if (systemBarPaddingTop() == -1) systemBars.top else systemBarPaddingTop(),
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onAppBackPage()
            }
        })
        initOnCreate()
    }

    abstract fun inflateBinding(): T

    open fun lightStatusBar(): Boolean {
        return false
    }

    open fun lightStatusNav(): Boolean {
        return false
    }

    open fun systemBarPaddingTop(): Int {
        return -1
    }


    open fun initOnCreate() {}

    override fun onResume() {
        isShowing = true
        super.onResume()

    }

    override fun onPause() {
        isShowing = false
        super.onPause()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val l = intArrayOf(0, 0)
                v.getLocationInWindow(l)
                onTouchEditText(
                    ev.x > l[0]
                            && ev.x < l[0] + v.width
                            && ev.y > l[1]
                            && ev.y < l[1] + v.height, v
                )
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun onTouchEditText(isTouchInEditText: Boolean, view: View) {
        if (!isTouchInEditText) {
            hideSoftInput(view)
            view.clearFocus()
        }
    }

    private fun hideSoftInput(view: View?) {
        if (view == null) return
        val inputMethodManager: InputMethodManager =
            view.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    open fun onAppBackPage() {
        finish()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(AppLangUtils.attachBaseContext(newBase))
    }
}