package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.zxing.WriterException
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.constant.ARG_PARAM2
import com.ramble.ramblewallet.databinding.ActivityGatheringBinding
import com.ramble.ramblewallet.utils.ClipboardUtils
import com.ramble.ramblewallet.utils.DisplayHelper
import com.ramble.ramblewallet.utils.QRCodeUtil
import com.ramble.ramblewallet.utils.toastDefault
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class GatheringActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityGatheringBinding
    private lateinit var gatherTitle: String
    private lateinit var gatherAddress: String
    private var bitmap: Bitmap? = null
    private val sdCardDir =
        Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_DCIM
    var mPermissionListener: PermissionListener? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gathering)
        gatherTitle = intent.getStringExtra(ARG_PARAM1)
        gatherAddress = intent.getStringExtra(ARG_PARAM2)
        setOnClickListener()
        initData()
    }

    fun setOnClickListener() {
        binding.ivBack.setOnClickListener(this)
        binding.llSave.setOnClickListener(this)
        binding.llCopy.setOnClickListener(this)
    }

    private fun initData() {
        binding.tvGatheringTitle.text = gatherTitle + " " + getString(R.string.gathering)
        if (gatherTitle.contains("ETH")) {
            binding.tvGatheringTips.text = String.format(
                getString(R.string.gathering_tips_eth),
                gatherTitle
            )
        }
        if (gatherTitle.contains("TRX")) {
            binding.tvGatheringTips.text = String.format(
                getString(R.string.gathering_tips_trx),
                gatherTitle
            )
        }
        if (gatherTitle.contains("BTC")) {
            binding.tvGatheringTips.text = String.format(
                getString(R.string.gathering_tips_btc),
                gatherTitle
            )
        }
        binding.tvGatheringScanTips.text = String.format(
            getString(R.string.gathering_scan_tips),
            gatherTitle
        )
        showScan()
    }

    private fun showScan() {
        try {
            bitmap = QRCodeUtil.createQRCode(gatherAddress, DisplayHelper.dpToPx(200))
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        binding.ivCode.setImageBitmap(bitmap)
        binding.tvAddress.text = gatherAddress
    }

    @SuppressLint("ShowToast")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.ll_save -> {
                showSaveDialog()
            }
            R.id.ll_copy -> {
                ClipboardUtils.copy(binding.tvAddress.text.toString(),this)
            }
        }
    }

    private fun showSaveDialog() {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_common)
            window.setGravity(Gravity.CENTER)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //设置属性
            val params = window.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            //弹出一个窗口，让背后的窗口变暗一点
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            //dialog背景层
            params.dimAmount = 0.5f
            window.attributes = params
            val tvContent = window.findViewById<TextView>(R.id.tv_content)
            tvContent.text = getText(R.string.save_scan)
            val ivImg = window.findViewById<ImageView>(R.id.iv_img)
            ivImg.visibility = View.VISIBLE
            try {
                ivImg.setImageBitmap(
                    QRCodeUtil.createQRCode(
                        gatherAddress,
                        DisplayHelper.dpToPx(200)
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val btnNext = window.findViewById<Button>(R.id.btn_next)
            btnNext.text = getText(R.string.gathering_save)
            val btnCancel = window.findViewById<Button>(R.id.btn_cancel)
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            window.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
                dialog.dismiss()
            }
            btnNext.setOnClickListener {
                requestRuntimePermission(
                    arrayOf(
                        "android.permission.CAMERA",
                        "android.permission.WRITE_EXTERNAL_STORAGE",
                        "android.permission.READ_EXTERNAL_STORAGE"
                    ), object : PermissionListener {
                        override
                        fun onGranted() {
                            try {
                                val bitmap: Bitmap = viewConversionBitmap(ivImg)
                                saveBitmap(bitmap)
                                bitmap.recycle()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override
                        fun onDenied(deniedPermissions: List<String?>?) {
                            println("-=-=->${deniedPermissions.toString()}")
                        }
                    })
                dialog.dismiss()
            }
        }
    }

    //生成图片
    fun viewConversionBitmap(v: View): Bitmap {
        val w = v.width
        val h = v.height
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        c.drawColor(Color.WHITE)
        /** 如果不设置canvas画布为白色，则生成透明  */
        v.layout(0, 0, w, h)
        v.draw(c)
        return bmp
    }

    /**
     * 保存图片
     *
     * @param bitmap
     */
    @SuppressLint("ShowToast")
    private fun saveBitmap(bitmap: Bitmap) {
        var file: File? = null
        try {
            val dirFile: File = File(sdCardDir)
            if (!dirFile.exists()) {              //如果不存在，那就建立这个文件夹
                dirFile.mkdirs()
            }
            file = File(sdCardDir, System.currentTimeMillis().toString() + ".jpg")
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // 把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(
                this.contentResolver,
                file!!.absolutePath, file.name, null
            )
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        // 通知图库更新
        this.sendBroadcast(
            Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + "/sdcard/namecard/")
            )
        )
        showScan()
        toastDefault(getString(R.string.save_success))
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