package com.szr.co.smart.qr.activity

import android.content.Intent
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.adapter.AppLangAdapter
import com.szr.co.smart.qr.data.DataSetting
import com.szr.co.smart.qr.databinding.ActivityLanguageBinding
import com.szr.co.smart.qr.event.EventLanguageSwitch
import com.szr.co.smart.qr.manager.UserManager
import com.szr.co.smart.qr.utils.AppLangUtils
import org.greenrobot.eventbus.EventBus

class LanguageActivity : BaseActivity<ActivityLanguageBinding>(), AppLangAdapter.Callback {

    override fun inflateBinding(): ActivityLanguageBinding {
        return ActivityLanguageBinding.inflate(layoutInflater)
    }

    override fun initOnCreate() {
        super.initOnCreate()
        mBinding.ivNavBack.setOnClickListener { onAppBackPage() }

        mBinding.recycleData.adapter = mAdapter
        mBinding.recycleData.layoutManager = LinearLayoutManager(this)
        mBinding.tvActionOk.setOnClickListener {
            if (selectIndex == -1) return@setOnClickListener
            val lang = langList[selectIndex]
            AppLangUtils.switchLanguage(this, lang)
            applyLang()
        }
        mAdapter.callback = this
        getLangData()
        mBinding.tvActionOk.isVisible = selectIndex != -1
        mAdapter.refreshData(langList)

    }


    private val mAdapter by lazy { AppLangAdapter() }
    private var selectIndex = -1
    private lateinit var langList: List<AppLangUtils.Language>


    private fun getLangData() {
        langList = AppLangUtils.getSupportedLanguages(UserManager.instance.buyUser())
        val lang = if (UserManager.instance.buyUser()) {
            AppLangUtils.getSavedLanguageOrNull(this)
        } else {
            AppLangUtils.getSavedLanguage(this)
        }

        for (i in langList.indices) {
            if (lang == langList[i].displayName) {
                selectIndex = i
                mAdapter.checkedIndex = selectIndex
                break
            }
        }
    }

    private fun applyLang() {
        if (DataSetting.instance.langGuide) {
            EventBus.getDefault().postSticky(EventLanguageSwitch())
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        } else {
            DataSetting.instance.langGuide = true
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }

    override fun onItemClick(position: Int) {
        selectIndex = position
        mBinding.tvActionOk.isVisible = selectIndex != -1
    }

}