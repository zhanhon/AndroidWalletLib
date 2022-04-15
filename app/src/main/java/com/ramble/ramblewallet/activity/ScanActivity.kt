package com.ramble.ramblewallet.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import cn.bingoogolapple.qrcode.core.QRCodeView
import cn.bingoogolapple.qrcode.zxing.ZXingView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.MainETHTokenBean
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.constant.ARG_PARAM2
import com.ramble.ramblewallet.constant.REQUEST_CODE_1029
import com.ramble.ramblewallet.constant.WALLETSELECTED
import com.ramble.ramblewallet.databinding.ActivityScanBinding
import com.ramble.ramblewallet.helper.getExtras
import com.ramble.ramblewallet.helper.startMatisseActivity
import com.ramble.ramblewallet.network.ObjUtils.isCameraPermission
import com.ramble.ramblewallet.utils.*
import com.zhihu.matisse.Matisse
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.FileNotFoundException
import java.util.*

/**
 * 时间　: 2022/1/13 9:45
 * 作者　: potato
 * 描述　: 二维码扫码
 */

class ScanActivity : BaseActivity(), View.OnClickListener, QRCodeView.Delegate,
    EasyPermissions.PermissionCallbacks {
    private lateinit var binding: ActivityScanBinding
    private var isLight = false
    private var zxingview: ZXingView? = null
    private var type = 0
    private lateinit var tokenBean: MainETHTokenBean
    private lateinit var walletSelleted: Wallet


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scan)
        type = getExtras().getInt(ARG_PARAM1, 0)
        tokenBean = intent.getSerializableExtra(ARG_PARAM2) as MainETHTokenBean
        zxingview = findViewById(R.id.zxingview)
        zxingview?.setDelegate(this)
        initListener()
        walletSelleted = Gson().fromJson(
            SharedPreferencesUtils.getString(this, WALLETSELECTED, ""),
            object : TypeToken<Wallet>() {}.type
        )
    }


    @AfterPermissionGranted(1)
    private fun requestCodeQRCodePermissions() {
        val perms = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (!EasyPermissions.hasPermissions(this, *perms)) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.alert_request_permission),
                1,
                *perms
            )
        } else {
            // 请求权限已经被授权
            zxingview?.startCamera()
            zxingview?.startSpotAndShowRect()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == REQUEST_CODE_1029) && (resultCode == Activity.RESULT_OK) && Matisse.obtainPathResult(
                data
            ).isNotEmpty()
        ) {
            val uris = Matisse.obtainResult(data)
            val uri: Uri? = uris[0]
            try {
                val result: Result? = scanningImage(uri)
                if (result != null) {
                    onScanQRCodeSuccess(result.text)
                } else {
                    ToastUtils.showToastFree(this, getString(R.string.scan_qr_code_fail))
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }

    }


    /** 扫描图片 **/
    private fun scanningImage(uri: Uri?): Result? {
        if (uri == null) {
            return null
        }
        val hints = Hashtable<DecodeHintType, String>()
        hints[DecodeHintType.CHARACTER_SET] = "UTF8" //设置二维码内容的编码
        val scanBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
        val data = IntArray(scanBitmap.width * scanBitmap.height)
        scanBitmap.getPixels(data, 0, scanBitmap.width, 0, 0, scanBitmap.width, scanBitmap.height)
        val source = RGBLuminanceSource(scanBitmap.width, scanBitmap.height, data)
        val bitmap1 = BinaryBitmap(HybridBinarizer(source))
        val reader = QRCodeReader()
        return try {
            reader.decode(bitmap1, hints)
        } catch (e: NotFoundException) {
            e.printStackTrace()
            null
        } catch (e: ChecksumException) {
            e.printStackTrace()
            null
        } catch (e: FormatException) {
            e.printStackTrace()
            null
        }
    }


    private fun initListener() {
        binding.ivBack.setOnClickListener(this)
        binding.imvGallery.setOnClickListener(this)
        binding.light.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        requestCodeQRCodePermissions()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        // 请求权限被拒绝
        finish()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        // 请求权限已经被授权
        zxingview?.startCamera()
        zxingview?.startSpotAndShowRect()
    }


    override fun onStop() {
        super.onStop()
        zxingview?.stopCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        zxingview?.onDestroy()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> finish()
            R.id.imv_gallery -> {
                if (isCameraPermission(this)) {
                    startMatisseActivity()
                }
            }
            R.id.light -> {
                isLight = !isLight
                if (isLight) {
                    binding.zxingview.openFlashlight()// 打开闪光灯
                } else {
                    binding.zxingview.closeFlashlight() // 关闭闪光灯
                }
            }

        }
    }

    override fun onScanQRCodeSuccess(result: String?) {
        zxingview?.stopSpot()
        when (type) {
            1 -> {
                RxBus.emitEvent(Pie.EVENT_ADDRESS_BOOK_SCAN, result)
                finish()
            }
            else -> {
                if (result != null) {
                    showBottomSan(this, result, walletSelleted.walletType, tokenBean, type)
                }

            }
        }
    }


    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {
        //暂时不需要实现此方法
    }

    override fun onScanQRCodeOpenCameraError() {
        //暂时不需要实现此方法
    }

}