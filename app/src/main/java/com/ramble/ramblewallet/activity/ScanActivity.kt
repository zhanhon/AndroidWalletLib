package com.ramble.ramblewallet.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader

import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.databinding.ActivityScanBinding

import com.ramble.ramblewallet.helper.startMatisseActivity
import com.ramble.ramblewallet.network.ObjUtils.isCameraPermission
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.FileNotFoundException
import java.util.*

class ScanActivity : BaseActivity(), View.OnClickListener, QRCodeView.Delegate {
    private lateinit var binding: ActivityScanBinding
    private var isLight=false

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scan)
        initView()
        initListener()

    }

    private fun initView() {

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val uri: Uri? = data?.data
        try {
            val result: Result? = scanningImage(uri)
            if (result != null) {
                Toast.makeText(this, "识别内容：" + result.text, Toast.LENGTH_LONG)
            } else {
                Toast.makeText(this, "识别失败，请试试其它二维码", Toast.LENGTH_LONG)
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
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
        binding.zxingview.setDelegate(this)
    }

    override fun onStart() {
        super.onStart()
        requestCodeQRCodePermissions()
        binding.zxingview.startCamera()
        binding.zxingview.startSpotAndShowRect()
    }

    override fun onStop() {
        super.onStop()
        binding.zxingview.stopCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.zxingview.onDestroy()
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

    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(200)
    }

    override fun onScanQRCodeSuccess(result: String?) {
        println("====-=->11111111111111111111")
        println("====-=->$result")
//        LogsUtil.e("11111111111111","识别内容")
//        Toast.makeText(this, "识别内容：" , Toast.LENGTH_LONG)
        vibrate()
        binding.zxingview.stopSpot()
    }

    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {

    }

    override fun onScanQRCodeOpenCameraError() {

    }
}