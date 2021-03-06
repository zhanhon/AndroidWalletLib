package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.zxing.WriterException
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityGatheringBinding
import com.ramble.ramblewallet.utils.*
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
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gathering)
        gatherTitle = intent.getStringExtra(ARG_PARAM1).toString()
        gatherAddress = intent.getStringExtra(ARG_PARAM2).toString()
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
        if (gatherTitle.contains("SOL")) {
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
                savePicture()
            }
            R.id.ll_copy -> {
                ClipboardUtils.copy(binding.tvAddress.text.toString(), this)
            }
        }
    }

    private fun savePicture() {
        val lang = SharedPreferencesUtils.getSecurityString(this, LANGUAGE, CN)
        requestRuntimePermission(
            arrayOf(
                "android.permission.CAMERA",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.READ_EXTERNAL_STORAGE"
            ), object : PermissionListener {
                override
                fun onGranted() {
                    try {
                        val bmp: Bitmap =
                            BitmapFactory.decodeResource(resources, R.mipmap.ic_logo_qrcode)
                        val bitmap: Bitmap = QRCodeUtil.createQRCodeBitmap(
                            gatherAddress,
                            450,
                            450,
                            "UTF-8",
                            "L",//????????????
                            "1",
                            Color.BLACK,
                            Color.WHITE,
                            bmp,
                            0.2f,
                            null
                        )
                        val view: View =
                            layoutInflater.inflate(R.layout.qr_picture_generate, null)
                        val llQrPicture = view.findViewById<LinearLayout>(R.id.ll_qr_picture)
                        val ivQrPicture = view.findViewById<ImageView>(R.id.iv_qr_picture)
                        when (lang) {
                            CN -> llQrPicture.setBackgroundResource(R.mipmap.qr_picture_bg_cn)
                            TW -> llQrPicture.setBackgroundResource(R.mipmap.qr_picture_bg_tw)
                            EN -> llQrPicture.setBackgroundResource(R.mipmap.qr_picture_bg_en)
                        }
                        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
                        ivQrPicture.setImageBitmap(bitmap)
                        tvTitle.text = gatherAddress
                        saveBitmap(createBitmap(view, 800, 1400))
                        bitmap.recycle()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override
                fun onDenied(deniedPermissions: List<String?>?) {
                }
            })
    }

    fun createBitmap(v: View, width: Int, height: Int): Bitmap {
        //????????????view????????????
        val measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        v.measure(measuredWidth, measuredHeight)
        //??????layout??????????????????????????????view???????????????
        v.layout(0, 0, v.measuredWidth, v.measuredHeight)
        val bmp = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        c.drawColor(Color.WHITE)
        v.draw(c)
        return bmp
    }

    //????????????
    fun viewConversionBitmap(v: View): Bitmap {
        val w = v.width
        val h = v.height
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        c.drawColor(Color.WHITE)
        /** ???????????????canvas?????????????????????????????????  */
        v.layout(0, 0, w, h)
        v.draw(c)
        return bmp
    }

    /**
     * ????????????
     *
     * @param bitmap
     */
    @SuppressLint("ShowToast")
    private fun saveBitmap(bitmap: Bitmap) {
        var file: File? = null
        try {
            val dirFile: File = File(sdCardDir)
            if (!dirFile.exists()) {              //?????????????????????????????????????????????
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

        // ??????????????????????????????
        try {
            MediaStore.Images.Media.insertImage(
                this.contentResolver,
                file!!.absolutePath, file.name, null
            )
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        // ??????????????????
        this.sendBroadcast(
            Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + "/sdcard/namecard/")
            )
        )
        showScan()
        ToastUtils.showToastFree(this, getString(R.string.save_success))
    }

    interface PermissionListener {
        fun onGranted()
        fun onDenied(deniedPermissions: List<String?>?)
    }

    /**
     * ?????????????????????
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