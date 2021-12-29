package com.ramble.ramblewallet.activity

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.databinding.ActivityScanBinding
import com.ramble.ramblewallet.helper.startMatisseActivity
import com.ramble.ramblewallet.network.ObjUtils.isCameraPermission
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class ScanActivity : BaseActivity() , View.OnClickListener{
    private lateinit var binding: ActivityScanBinding
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scan)
        initView()
        initListener()
    }

    private fun initView() {
        requestCodeQRCodePermissions()
    }
    @AfterPermissionGranted(1)
    private fun requestCodeQRCodePermissions() {
        val perms = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (!EasyPermissions.hasPermissions(this, *perms)) {
            EasyPermissions.requestPermissions(
                this,
                "请授予权限，否则app功能无法使用",
                1,
                *perms
            )
        }
    }
    private fun initListener() {
        binding.ivBack.setOnClickListener(this)
        binding.imvGallery.setOnClickListener(this)
        binding.light.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> finish()
            R.id.imv_gallery -> {
                if (isCameraPermission(this)) {
                    startMatisseActivity()
                }
            }
            R.id.light -> finish()
        }
    }
}