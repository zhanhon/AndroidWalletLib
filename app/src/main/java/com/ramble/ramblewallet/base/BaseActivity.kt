package com.ramble.ramblewallet.base

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ramble.ramblewallet.R
import java.util.*

abstract class BaseActivity : AppCompatActivity() {

    var mPermissionListener: PermissionListener? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        //沉浸式状态栏处理
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_FFFFFF)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //状态栏黑色字体

    }

    interface PermissionListener {
        fun onGranted()
        fun onDenied(deniedPermissions: List<String?>?)
    }

    /**
     * 申请运行时权限
     */
    open fun requestRuntimePermission(
        permissions: Array<String>,
        permissionListener: PermissionListener
    ) {
        mPermissionListener = permissionListener
        val permissionList: MutableList<String> = ArrayList()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionList.add(permission)
            }
        }
        if (permissionList.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toTypedArray(), 1)
        } else {
            permissionListener.onGranted()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty()) {
                val deniedPermissions: MutableList<String?> = ArrayList()
                var i = 0
                while (i < grantResults.size) {
                    val grantResult = grantResults[i]
                    val permission = permissions[i]
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        deniedPermissions.add(permission)
                    }
                    i++
                }
                if (deniedPermissions.isEmpty()) {
                    mPermissionListener!!.onGranted()
                } else {
                    mPermissionListener!!.onDenied(deniedPermissions)
                }
            }
        }
    }

}