package com.szr.co.smart.qr.activity

import android.content.Intent
import android.view.Gravity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.szr.co.smart.qr.BuildConfig
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.adapter.MainQrSrcAdapter
import com.szr.co.smart.qr.databinding.ActivityMainBinding
import com.szr.co.smart.qr.dialog.HistoryMenuDialog
import com.szr.co.smart.qr.utils.dpToPx
import com.szr.co.smart.qr.view.ItemGridDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : BaseActivity<ActivityMainBinding>() {


    private val listImages = arrayListOf<Int>(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)

    private lateinit var mAdapter: MainQrSrcAdapter

    override fun inflateBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }


    override fun initOnCreate() {
        super.initOnCreate()
        mBinding.recycleData.layoutManager = GridLayoutManager(this, 3)
        mAdapter = MainQrSrcAdapter(listImages) {
            startActivity(Intent(this, TemplatesActivity::class.java))
        }
        mBinding.recycleData.adapter = mAdapter
        mBinding.recycleData.addItemDecoration(
            ItemGridDecoration(
                3,
                8f.dpToPx().toInt(),
                8f.dpToPx().toInt(),
                false
            )
        )


        mBinding.layoutPopularTemplates.setOnClickListener {
            startActivity(Intent(this, TemplatesActivity::class.java))
        }
        mBinding.scan.setOnClickListener { startActivity(Intent(this, ScanActivity::class.java)) }

        mBinding.layoutCreateQrcode.setOnClickListener {
            startActivity(Intent(this, GenQRCodeActivity::class.java))
        }

        mBinding.layoutCreateBarcode.setOnClickListener {
            startActivity(Intent(this, GenBarCodeActivity::class.java))
        }

        mBinding.icHistory.setOnClickListener {
            HistoryMenuDialog(this).show()
        }

        mBinding.tvVersionName.text = BuildConfig.VERSION_NAME

        mBinding.icSettings.setOnClickListener {
            mBinding.drawerLayout.openDrawer(GravityCompat.END)
        }

        mBinding.layoutLanguage.setOnClickListener {

            startActivity(Intent(this, LanguageActivity::class.java))
            lifecycleScope.launch {
                withContext(Dispatchers.Default) { delay(500) }
                mBinding.drawerLayout.closeDrawer(GravityCompat.END)
            }
        }
        mBinding.layoutPp.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.Default) { delay(500) }
                mBinding.drawerLayout.closeDrawer(GravityCompat.END)
            }
        }

        mBinding.layoutMenu.setOnClickListener {  }
    }
}