package com.szr.co.smart.qr.activity

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.recyclerview.widget.LinearLayoutManager
import com.szr.co.smart.qr.R
import com.szr.co.smart.qr.activity.base.BaseActivity
import com.szr.co.smart.qr.adapter.QRHistoryAdapter
import com.szr.co.smart.qr.databinding.ActivityHistoryCodeBinding
import com.szr.co.smart.qr.model.QRCodeType
import com.szr.co.smart.qr.room.AppDB
import com.szr.co.smart.qr.room.model.QRDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

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

    override fun inflateBinding(): ActivityHistoryCodeBinding {
        return ActivityHistoryCodeBinding.inflate(layoutInflater)
    }

    override fun initOnCreate() {
        super.initOnCreate()
        dataType = intent.getIntExtra("type", Type.GEN)

        val resId = if (dataType == Type.GEN) {
            R.string.generated
        } else {
            R.string.scanned
        }
        mBinding.layoutNavTop.tvTitle.setText(resId)
        mBinding.recycleData.layoutManager = LinearLayoutManager(this)
        mBinding.recycleData.adapter = mAdapter
        dataList = genData(dataType).cachedIn(lifecycleScope).flowOn(Dispatchers.IO)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                dataList.collectLatest {
                    mAdapter.submitData(it)
                }
            }
        }


    }
}