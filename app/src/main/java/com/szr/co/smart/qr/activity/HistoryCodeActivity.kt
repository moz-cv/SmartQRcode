package com.szr.co.smart.qr.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.recyclerview.widget.LinearLayoutManager
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.adapter.QRHistoryAdapter
import com.szr.co.smart.qr.databinding.ActivityHistoryCodeBinding
import com.szr.co.smart.qr.room.AppDB
import com.szr.co.smart.qr.room.model.QRDataModel
import com.szr.co.smart.qr.utils.QrUtils
import com.szr.co.smart.qr.utils.ShareUtils
import com.szr.co.smart.qr.utils.Utils
import com.szr.co.smart.qr.utils.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.compareTo

class HistoryCodeActivity : BaseActivity<ActivityHistoryCodeBinding>() {

    @Retention(AnnotationRetention.SOURCE)
    annotation class Type {
        companion object {
            val SCAN = 0
            val GEN = 1

        }
    }

    private lateinit var dataList: Flow<PagingData<QRDataModel>>

    private fun genData(type: Int): Flow<PagingData<QRDataModel>> {
        return Pager(
            PagingConfig(pageSize = 30)
        ) {
            if (type == Type.SCAN) {
                AppDB.db.qrDataDao().scanQRData()
            } else {
                AppDB.db.qrDataDao().genQRData()
            }
        }.flow
    }

    private val mAdapter by lazy { QRHistoryAdapter(this) }

    companion object {
        fun toHistory(context: Context, @Type type: Int) {
            val intent = Intent(context, HistoryCodeActivity::class.java)
            intent.putExtra("type", type)
            context.startActivity(intent)
        }
    }

    private var dataType = Type.GEN
    private var enableEdit = false

    override fun inflateBinding(): ActivityHistoryCodeBinding {
        return ActivityHistoryCodeBinding.inflate(layoutInflater)
    }

    override fun initOnCreate() {
        super.initOnCreate()
        mBinding.layoutNavTop.setOnClickListener { onAppBackPage() }

        dataType = intent.getIntExtra("type", Type.GEN)

        val resId = if (dataType == Type.GEN) {
            R.string.generated
        } else {
            R.string.scanned
        }
        mBinding.tvTitle.setText(resId)
        mBinding.recycleData.layoutManager = LinearLayoutManager(this)
        mBinding.recycleData.adapter = mAdapter

        mAdapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.NotLoading -> {
                    if (mAdapter.itemCount > 0) {
                        mBinding.layoutEmpty.isVisible = false
                        mBinding.recycleData.isVisible = true
                    } else {
                        mBinding.layoutEmpty.isVisible = true
                        mBinding.recycleData.isVisible = false
                        changeSelect(false)
                    }
                }

                is LoadState.Error -> {
                    mBinding.layoutEmpty.isVisible = true
                    mBinding.recycleData.isVisible = false
                }

                else -> {}
            }
        }


        dataList = genData(dataType).cachedIn(lifecycleScope).flowOn(Dispatchers.IO)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                dataList.collectLatest {
                    mAdapter.submitData(it)
                }
            }
        }

        mBinding.tvActionEdit.setOnClickListener {
            changeSelect(!enableEdit)
        }


        mBinding.layoutDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.tips))
                .setMessage(getString(R.string.delete_data_content))
                .setPositiveButton(getString(R.string.delete)) { _, _ ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        val list = mAdapter.selectIdList.values
                        AppDB.db.qrDataDao().deleteList(list.toList())
                    }
                    changeSelect(false)
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
        }

        mBinding.layoutShare.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val list = mAdapter.selectIdList.values
                if (list.isEmpty()) return@launch
                if (dataType == Type.GEN) {
                    //分享图片，8个
                    if (list.size > 8) {
                        toast(getString(R.string.share_max_limit))
                        return@launch
                    }
                    val listBitmap = mutableListOf<Bitmap>()

                    list.forEach {
                        listBitmap.add(
                            QrUtils.createQrBitmap(
                                this@HistoryCodeActivity,
                                it.content,
                                QrUtils.convertTypeZxingType(it.type),
                                it.bgId
                            )
                        )
                    }
                    withContext(Dispatchers.Main) {
                        ShareUtils.shareBitmaps(
                            this@HistoryCodeActivity,
                            listBitmap.toList(),
                            getString(R.string.share)
                        )
                    }
                } else {
                    //分享文字
                    val sb = StringBuilder()
                    list.forEach {
                        sb.append(it.content)
                        sb.append("\n")
                    }
                    withContext(Dispatchers.Main) {
                        ShareUtils.shareText(
                            this@HistoryCodeActivity,
                            sb.toString(),
                            getString(R.string.share)
                        )
                    }
                }


            }
        }
    }

    private fun changeSelect(select: Boolean) {
        enableEdit = select
        mAdapter.changeSelect(select)

        mBinding.layoutBottom.isVisible = select
        mBinding.tvActionEdit.text = getString(if (select) R.string.cancel else R.string.edit)
    }
}