package com.ramble.ramblewallet.activity

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.databinding.DataBindingUtil
import cn.bingoogolapple.qrcode.core.QRCodeView
import cn.bingoogolapple.qrcode.zxing.ZXingView
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
    private var isLight = false
    private var zxingview: ZXingView? = null
    var isChecked: Boolean = false

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scan)
        zxingview = findViewById(R.id.zxingview)
        zxingview?.setDelegate(this)
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
                println("====-=->11111111111111111111识别内容 "+result.text)
            } else {
                println("====-=->11111111111111111111识别失败，请试试其它二维码")
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

    }

    override fun onStart() {
        super.onStart()
        requestCodeQRCodePermissions()
        zxingview?.startCamera()
        zxingview?.startSpotAndShowRect()
//        binding.zxingview.startCamera()
//        binding.zxingview.startSpotAndShowRect()
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

    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(200)
    }

    override fun onScanQRCodeSuccess(result: String?) {
        println("====-=->11111111111111111111")
        println("====-=->$result")
        transDialog(result)
        vibrate()
        zxingview?.stopSpot()
    }

    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {

    }

    override fun onScanQRCodeOpenCameraError() {

    }

    private fun transDialog(result: String?) {
        var dialogLanguage = AlertDialog.Builder(this).create()
        dialogLanguage.show()
        val window: Window? = dialogLanguage.window
        if (window != null) {
            window.setContentView(R.layout.dialog_trans_item)
            window.setGravity(Gravity.BOTTOM)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.findViewById<View>(R.id.tv_address)
                .findViewById<TextView>(R.id.tv_address).text = result
            window.findViewById<View>(R.id.tv_check).findViewById<AppCompatCheckBox>(R.id.tv_check).isChecked = isChecked
            window.findViewById<View>(R.id.ok).setOnClickListener { v1: View? ->
                dialogLanguage.dismiss()
                finish()
            }
            window.findViewById<View>(R.id.tv_check).setOnClickListener { v1: View? ->
                isChecked = (v1!! as CheckBox).isChecked
            }
            //设置属性
            val params = window.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            //弹出一个窗口，让背后的窗口变暗一点
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            //dialog背景层
            params.dimAmount = 0.5f
            window.attributes = params
            //点击空白处不关闭dialog
            dialogLanguage.show()
        }
    }

}