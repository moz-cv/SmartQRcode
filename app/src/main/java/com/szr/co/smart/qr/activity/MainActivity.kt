package com.szr.co.smart.qr.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.Gravity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.szr.co.smart.qr.BuildConfig
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.activity.base.BaseAdActivity
import com.szr.co.smart.qr.adapter.MainQrSrcAdapter
import com.szr.co.smart.qr.bill.ViBillHelper
import com.szr.co.smart.qr.bill.position.ViBillPosition
import com.szr.co.smart.qr.data.DataSetting
import com.szr.co.smart.qr.databinding.ActivityMainBinding
import com.szr.co.smart.qr.dialog.HistoryMenuDialog
import com.szr.co.smart.qr.dialog.PostBottomDialog
import com.szr.co.smart.qr.dialog.PushCodeDialog
import com.szr.co.smart.qr.dialog.WaitingDialog
import com.szr.co.smart.qr.event.EventLanguageSwitch
import com.szr.co.smart.qr.logic.QrResLogic
import com.szr.co.smart.qr.manager.UserManager
import com.szr.co.smart.qr.utils.Utils
import com.szr.co.smart.qr.utils.dpToPx
import com.szr.co.smart.qr.utils.permission.PermissionCallback
import com.szr.co.smart.qr.utils.permission.requestCameraPermission
import com.szr.co.smart.qr.view.ItemGridDecoration
import com.szr.co.smart.qr.vm.MainVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : BaseAdActivity<ActivityMainBinding>() {


    private lateinit var mAdapter: MainQrSrcAdapter
    private val mMainVM by viewModels<MainVM>()
    private var mQRCodeVideDialog: PushCodeDialog? = null

    private var mWaitingDialog: WaitingDialog? = null
    private var startPostPerTime = 0L

    override val billHelper: ViBillHelper by lazy {
        ViBillHelper(
            this,
            ViBillPosition.POS_MAIN_CLICK_INTERS,
            mutableListOf(
                ViBillPosition.POS_MAIN_NATIVE,
                ViBillPosition.POS_MAIN_CLICK_INTERS,
                ViBillPosition.POS_QR_RESULT_NATIVE,
                ViBillPosition.POS_QR_SCAN_INTERS,
                ViBillPosition.POS_QR_CREATE_CLICK_INTERS
            ),
            ViBillPosition.POS_MAIN_NATIVE,
            mBinding.layoutNativeAd,
            true
        )
    }

    override fun inflateBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun lightStatusBar(): Boolean {
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun initOnCreate() {
        super.initOnCreate()
        EventBus.getDefault().register(this)
        mBinding.recycleData.layoutManager = GridLayoutManager(this, 3)
        mAdapter = MainQrSrcAdapter(QrResLogic.listBgImages) {
            billHelper.showAd {
                startActivity(Intent(this, TemplatesActivity::class.java))
            }
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
            billHelper.showAd {
                startActivity(Intent(this, TemplatesActivity::class.java))
            }

        }
        mBinding.scan.setOnClickListener {
            startScan()
        }

        mBinding.layoutCreateQrcode.setOnClickListener {
            billHelper.showAd {
                GenQRCodeActivity.toGenType(this)
            }
        }

        mBinding.layoutCreateBarcode.setOnClickListener {
            billHelper.showAd {
                GenBarCodeActivity.toGenType(this)
            }
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

        mBinding.layoutMenu.setOnClickListener { }
        observerData()

        //check 权限
        val requirePer = checkPermission()
        if (requirePer) {
            skipOnceNativeResume = true
            DataSetting.instance.notifyHitDialogLastTime = System.currentTimeMillis()
            PostBottomDialog(this, {
                startPostPerTime = System.currentTimeMillis()
                potNotifyPerLaunch.launch(Manifest.permission.POST_NOTIFICATIONS)
            }) {
                if (it) checkPostPerNexTask()
            }.show()
        } else {
            checkPostPerNexTask()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        mMainVM.checkPushVideoParse()
    }

    /**
     * 申请相机权限
     */
    private fun startScan() {
        requestCameraPermission(object : PermissionCallback {
            override fun onPermissionGranted(permissions: Array<String>) {
                startActivity(Intent(this@MainActivity, ScanActivity::class.java))
            }

            override fun onPermissionDenied(deniedPermissions: Array<String>) {
            }

            override fun onPermissionPermanentlyDenied(permanentlyDeniedPermissions: Array<String>) {
                showPermissionPermanentlyDeniedDialog(getString(R.string.set_open_camera_hint))
            }
        })
    }

    /**
     * 显示权限被永久拒绝的对话框
     */
    private fun showPermissionPermanentlyDeniedDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_denied))
            .setMessage(message)
            .setPositiveButton(getString(R.string.go_set)) { _, _ ->
                // 跳转到应用设置页面
                Utils.openAppSettings(this)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun observerData() {
        mMainVM.status.observe(this) {
            when (it) {
                1 -> {
                    val data = mMainVM.mVideo?.url
                    if (data.isNullOrEmpty()) return@observe
                    if (mQRCodeVideDialog?.isShowing == true) return@observe
                    mQRCodeVideDialog = PushCodeDialog(this, data) {
                        mWaitingDialog = WaitingDialog(this)
                        mWaitingDialog?.show()
                        toScanResultVideo()
                    }
                    mQRCodeVideDialog?.show()
                }
            }
        }
    }

    private fun toScanResultVideo() {
        mQRCodeVideDialog = null
        lifecycleScope.launch {
            withContext(Dispatchers.Default) { delay(2000) }
            mWaitingDialog?.dismiss()
            mWaitingDialog = null
            ParseResultActivity.toScanResult(this@MainActivity, mMainVM.mVideo)
        }
    }


    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT < 33) {
            return false
        }
        val time = DataSetting.instance.notifyHitDialogLastTime
        if (Utils.sameDay(time)) return false

        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return true
    }

    private val potNotifyPerLaunch =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                checkPostPerNexTask()
            } else {
                val diffTime = System.currentTimeMillis() - startPostPerTime
                if (diffTime <= 500) {
                    Utils.openAppSettings(this)
                } else {
                    checkPostPerNexTask()
                }
            }
        }

    private fun checkPostPerNexTask() {
        val firstGuide = userFirstParsing()
        if (firstGuide) {
            mMainVM.checkPushVideoParseGuide()
        } else {
            mMainVM.checkPushVideoParse()
        }
    }

    private fun userFirstParsing(): Boolean {
        if (DataSetting.instance.firstGuideQr) return false
        DataSetting.instance.firstGuideQr = true
        return UserManager.instance.buyUser()
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onAppLanguageSwitch(event: EventLanguageSwitch) {
        EventBus.getDefault().removeStickyEvent(event)
        recreate()
    }
}