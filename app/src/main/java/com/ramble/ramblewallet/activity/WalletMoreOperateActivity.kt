package com.ramble.ramblewallet.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.AddressReport
import com.ramble.ramblewallet.bean.AllTokenBean
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityWalletMoreOperateBinding
import com.ramble.ramblewallet.network.reportAddressUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.utils.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class WalletMoreOperateActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityWalletMoreOperateBinding
    private lateinit var walletCurrent: Wallet
    private var saveWalletList: ArrayList<Wallet> = arrayListOf()
    private val sdCardDir =
        Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_DCIM
    private lateinit var walletSelleted: Wallet
    var mPermissionListener: PermissionListener? = null
    private var times = 0
    private var myAllToken: ArrayList<AllTokenBean> = arrayListOf()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet_more_operate)
        SharedPreferencesUtils.saveBoolean(this, IS_CONFIRM_MNEMONIC, false)
        walletCurrent = Gson().fromJson(
            intent.getStringExtra(ARG_PARAM1),
            object : TypeToken<Wallet>() {}.type
        )

        if (walletCurrent.walletType == 1) {
            myAllToken = Gson().fromJson(
                SharedPreferencesUtils.getString(this, TOKEN_INFO_NO, ""),
                object : TypeToken<ArrayList<AllTokenBean>>() {}.type
            )
        }
        when (walletCurrent.walletType) {
            1 -> {
                binding.tvRecoverWalletTitle.text =
                    "ETH" + " " + walletCurrent.walletName + getString(R.string.di_wallet)
                binding.rlKeystoreBackups.visibility = View.VISIBLE
            }
            2 -> {
                binding.tvRecoverWalletTitle.text =
                    "TRX" + " " + walletCurrent.walletName + getString(R.string.di_wallet)
                binding.rlKeystoreBackups.visibility = View.VISIBLE
            }
            3 -> {
                binding.tvRecoverWalletTitle.text =
                    "BTC" + " " + walletCurrent.walletName + getString(R.string.di_wallet)
                binding.rlKeystoreBackups.visibility = View.INVISIBLE
            }
        }

        saveWalletList = Gson().fromJson(
            SharedPreferencesUtils.getString(this, WALLETINFO, ""),
            object : TypeToken<ArrayList<Wallet>>() {}.type
        )
        walletSelleted = Gson().fromJson(
            SharedPreferencesUtils.getString(this, WALLETSELECTED, ""),
            object : TypeToken<Wallet>() {}.type
        )

        if (saveWalletList.size == 1) {
            binding.tvDeleteWallet.visibility = View.GONE
        } else {
            binding.tvDeleteWallet.visibility = View.VISIBLE
        }

        if (walletCurrent.mnemonic != null) {
            binding.rlContributingWordsBackups.visibility = View.VISIBLE
        } else {
            binding.rlContributingWordsBackups.visibility = View.GONE
        }

        initClick()
    }

    private fun initClick() {
        binding.ivBack.setOnClickListener(this)
        binding.rlEditWallet.setOnClickListener(this)
        binding.rlContributingWordsBackups.setOnClickListener(this)
        binding.rlSecretKeyBackups.setOnClickListener(this)
        binding.rlKeystoreBackups.setOnClickListener(this)
        binding.tvDeleteWallet.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.rl_edit_wallet -> {
                inputPasswordDialog(getString(R.string.edit_wallet))
            }
            R.id.rl_contributing_words_backups -> {
                inputPasswordDialog(getString(R.string.backup_memorization_words))
            }
            R.id.rl_secret_key_backups -> {
                inputPasswordDialog(getString(R.string.secret_key_backup))
            }
            R.id.rl_keystore_backups -> {
                inputPasswordDialog(getString(R.string.keystore_backup))
            }
            R.id.tv_delete_wallet -> {
                deleteConfirmTipsDialog()
            }
        }
    }

    private fun deleteConfirmTipsDialog() {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_delete_confirm_tips)
            dialogCenterTheme(window)

            window.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
            window.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
                dialog.dismiss()
            }
            window.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                btnConfirmSub()
                dialog.dismiss()
                finish()
            }
        }
    }

    private fun btnConfirmSub() {
        var detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
        saveWalletList.forEach {
            if (it.isClickDelete) {
                detailsList.add(AddressReport.DetailsList(it.address, 2, it.walletType))
            } else {
                detailsList.add(AddressReport.DetailsList(it.address, 0, it.walletType))
            }
        }
        putAddress(detailsList)
        val list = saveWalletList.iterator()
        list.forEach {
            if (it.address == walletCurrent.address) {
                list.remove()
            }
        }

        SharedPreferencesUtils.saveString(
            this,
            WALLETSELECTED,
            Gson().toJson(saveWalletList[0])
        )
        SharedPreferencesUtils.saveString(this, WALLETINFO, Gson().toJson(saveWalletList))
        if (walletCurrent.walletType == 1) {
            val lists = myAllToken.iterator()
            lists.forEach {
                if (it.myCurrency == walletCurrent.address) {
                    lists.remove()
                }
            }
            SharedPreferencesUtils.saveString(
                this,
                TOKEN_INFO_NO,
                Gson().toJson(myAllToken)
            )
        }
    }

    private fun dialogCenterTheme(window: Window) {
        //设置属性
        val params = window.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        //弹出一个窗口，让背后的窗口变暗一点
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        //dialog背景层
        params.dimAmount = 0.5f
        window.attributes = params
        window.setGravity(Gravity.CENTER)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun inputPasswordDialog(title: String) {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_input_password)
            dialogTheme(window)

            window.findViewById<TextView>(R.id.tv_title).text = title
            val edtWalletPassword = window.findViewById<TextView>(R.id.edt_wallet_password)
            edtWalletPassword.addTextChangedListener(object : TextWatcher {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun afterTextChanged(s: Editable?) {
                    if ((edtWalletPassword.text.isNotEmpty())
                        && (edtWalletPassword.text.length >= 6)
                    ) {
                        window.findViewById<Button>(R.id.btn_confirm).isEnabled = true
                        window.findViewById<Button>(R.id.btn_confirm).background =
                            getDrawable(R.drawable.shape_green_bottom_btn)
                    } else {
                        window.findViewById<Button>(R.id.btn_confirm).isEnabled = false
                        window.findViewById<Button>(R.id.btn_confirm).background =
                            getDrawable(R.drawable.shape_gray_bottom_btn)
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    //暂时不需要实现此方法
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    //暂时不需要实现此方法
                }
            })
            window.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                if (edtWalletPassword.text.trim().toString() == walletCurrent.walletPassword) {
                    when (title) {
                        getString(R.string.edit_wallet) -> {
                            editWalletDialog(title)
                        }
                        getString(R.string.backup_memorization_words) -> {
                            startActivity(Intent(this, MnemonicActivity::class.java).apply {
                                putExtra(ARG_PARAM1, walletCurrent.walletName)
                                putExtra(ARG_PARAM2, walletCurrent.walletPassword)
                                putExtra(ARG_PARAM3, walletCurrent.walletType)
                                putExtra(ARG_PARAM4, true)
                                putExtra(ARG_PARAM5, walletCurrent.mnemonic)
                                putStringArrayListExtra(
                                    ARG_PARAM6,
                                    walletCurrent.mnemonicList as java.util.ArrayList<String>?
                                )
                            })
                        }
                        getString(R.string.secret_key_backup) -> {
                            secretKeyDialog(title)
                        }
                        getString(R.string.keystore_backup) -> {
                            keystoreDialog(title)
                        }
                    }
                    dialog.dismiss()
                } else {
                    ToastUtils.showToastFree(
                        this,
                        getString(R.string.input_correct_wallet_password)
                    )
                }
            }
        }
    }

    private fun editWalletDialog(title: String) {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_edit_wallet)
            dialogTheme(window)

            window.findViewById<TextView>(R.id.tv_title).text = title
            val edtWalletName = window.findViewById<TextView>(R.id.edt_wallet_name)
            edtWalletName.addTextChangedListener(object : TextWatcher {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun afterTextChanged(s: Editable?) {
                    if (edtWalletName.text.isNotEmpty()) {
                        window.findViewById<Button>(R.id.btn_confirm).isEnabled = true
                        window.findViewById<Button>(R.id.btn_confirm).background =
                            getDrawable(R.drawable.shape_green_bottom_btn)
                    } else {
                        window.findViewById<Button>(R.id.btn_confirm).isEnabled = false
                        window.findViewById<Button>(R.id.btn_confirm).background =
                            getDrawable(R.drawable.shape_gray_bottom_btn)
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    //暂时不需要实现此方法
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    //暂时不需要实现此方法
                }
            })
            window.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                saveWalletList.forEach {
                    if (it.address == walletCurrent.address) {
                        it.walletName = edtWalletName.text.trim().toString()
                    }
                }
                SharedPreferencesUtils.saveString(
                    this,
                    WALLETINFO,
                    Gson().toJson(saveWalletList)
                )
                if (walletCurrent.address == walletSelleted.address) {
                    walletSelleted.walletName = edtWalletName.text.trim().toString()
                    SharedPreferencesUtils.saveString(
                        this,
                        WALLETSELECTED,
                        Gson().toJson(walletSelleted)
                    )
                }
                dialog.dismiss()
            }

        }
    }

    private fun secretKeyDialog(title: String) {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_secret_key)
            dialogTheme(window)

            window.findViewById<TextView>(R.id.tv_title).text = title
            window.findViewById<EditText>(R.id.edt_wallet_name)
                .setText(walletCurrent.walletName.toString())
            window.findViewById<EditText>(R.id.edt_secret_key)
                .setText(walletCurrent.privateKey.toString())
            window.findViewById<Button>(R.id.btn_copy).setOnClickListener {
                ClipboardUtils.copy(walletCurrent.privateKey.toString(), this)
            }
            window.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun keystoreDialog(title: String) {
        var dialog = AlertDialog.Builder(this).create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            window.setContentView(R.layout.dialog_keystore)
            dialogTheme(window)

            window.findViewById<TextView>(R.id.tv_title).text = title
            window.findViewById<EditText>(R.id.edt_wallet_name)
                .setText(walletCurrent.walletName.toString())
            window.findViewById<EditText>(R.id.edt_keystore)
                .setText(walletCurrent.keystore.toString())
            window.findViewById<Button>(R.id.btn_copy).setOnClickListener {
                ClipboardUtils.copy(walletCurrent.keystore.toString(), this)
            }
            window.findViewById<Button>(R.id.btn_generate_qr_code).setOnClickListener {
                savePicture()
                dialog.dismiss()
            }
            window.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun dialogTheme(window: Window) {
        //设置属性
        val params = window.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        //弹出一个窗口，让背后的窗口变暗一点
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        //dialog背景层
        params.dimAmount = 0.5f
        window.attributes = params
        window.setGravity(Gravity.BOTTOM)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun savePicture() {
        val lang = SharedPreferencesUtils.getString(this, LANGUAGE, CN)
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
                            walletCurrent.keystore.toString(),
                            450,
                            450,
                            "UTF-8",
                            "L",//设置密度
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
                        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
                        ivQrPicture.setImageBitmap(bitmap)
                        when (lang) {
                            CN -> llQrPicture.setBackgroundResource(R.mipmap.qr_picture_bg_cn)
                            TW -> llQrPicture.setBackgroundResource(R.mipmap.qr_picture_bg_tw)
                            EN -> llQrPicture.setBackgroundResource(R.mipmap.qr_picture_bg_en)
                        }
                        tvTitle.text =
                            walletCurrent.walletName + getString(R.string.save_qr_picture_title)
                        saveBitmap(createBitmap(view, 800, 1400))
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
    }

    fun createBitmap(v: View, width: Int, height: Int): Bitmap {
        //测量使得view指定大小
        val measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        v.measure(measuredWidth, measuredHeight)
        //调用layout方法布局后，可以得到view的尺寸大小
        v.layout(0, 0, v.measuredWidth, v.measuredHeight)
        val bmp = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        c.drawColor(Color.WHITE)
        v.draw(c)
        return bmp
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
        ToastUtils.showToastFree(this, getString(R.string.save_success))
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

    @SuppressLint("CheckResult")
    private fun putAddress(detailsList: ArrayList<AddressReport.DetailsList>) {
        val languageCode = SharedPreferencesUtils.getString(appContext, LANGUAGE, CN)
        val deviceToken = SharedPreferencesUtils.getString(appContext, DEVICE_TOKEN, "")
        if (detailsList.size == 0) return
        mApiService.putAddress(
            AddressReport.Req(detailsList, deviceToken, languageCode).toApiRequest(reportAddressUrl)
        ).applyIo().subscribe(
            {
                if (it.code() == 1) {
                    it.data()?.let { data -> println("-=-=-=->putAddress:${data}") }
                } else {
                    if (times < 3) {
                        putAddress(detailsList)
                        times++
                    }
                    println("-=-=-=->putAddress:${it.message()}")
                }
            }, {
                println("-=-=-=->putAddress:${it.printStackTrace()}")
            }
        )
    }

}