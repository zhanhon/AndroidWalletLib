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
import com.ramble.ramblewallet.constant.ARG_PARAM1
import com.ramble.ramblewallet.constant.WALLETINFO
import com.ramble.ramblewallet.constant.WALLETSELECTED
import com.ramble.ramblewallet.databinding.ActivityWalletMoreOperateBinding
import com.ramble.ramblewallet.ethereum.WalletETH
import com.ramble.ramblewallet.utils.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class WalletMoreOperateActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityWalletMoreOperateBinding
    private lateinit var walletCurrent: WalletETH
    private var saveWalletList: ArrayList<WalletETH> = arrayListOf()
    private val sdCardDir =
        Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_DCIM
    private lateinit var walletSelleted: WalletETH
    var mPermissionListener: PermissionListener? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet_more_operate)
        walletCurrent = Gson().fromJson(
            intent.getStringExtra(ARG_PARAM1),
            object : TypeToken<WalletETH>() {}.type
        )
        when (walletCurrent.walletType) {
            1 -> {
                binding.tvRecoverWalletTitle.text = "ETH" + " " + walletCurrent.walletName
            }
            2 -> {
                binding.tvRecoverWalletTitle.text = "TRX" + " " + walletCurrent.walletName
            }
            0 -> {
                binding.tvRecoverWalletTitle.text = "BTC" + " " + walletCurrent.walletName
            }
        }

        saveWalletList = Gson().fromJson(
            SharedPreferencesUtils.getString(this, WALLETINFO, ""),
            object : TypeToken<ArrayList<WalletETH>>() {}.type
        )
        walletSelleted = Gson().fromJson(
            SharedPreferencesUtils.getString(this, WALLETSELECTED, ""),
            object : TypeToken<WalletETH>() {}.type
        )

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
                val list = saveWalletList.iterator()
                list.forEach {
                    if (it.address == walletCurrent.address) {
                        list.remove()
                    }
                }
                SharedPreferencesUtils.saveString(this, WALLETINFO, Gson().toJson(saveWalletList))
                finish()
            }
        }
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
                    if (edtWalletPassword.text.isNotEmpty()) {
                        window.findViewById<Button>(R.id.btn_confirm).background =
                            getDrawable(R.drawable.shape_green_bottom_btn)
                    } else {
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
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
            window.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                if (edtWalletPassword.text.trim().toString() == walletCurrent.walletPassword) {
                    when (title) {
                        getString(R.string.edit_wallet) -> {
                            editWalletDialog(title)
                        }
                        getString(R.string.secret_key_backup) -> {
                            secretKeyDialog(title)
                        }
                        getString(R.string.keystore_backup) -> {
                            keystoreDialog(title)
                        }
                        else -> {
                            if (walletCurrent.mnemonic.toString() != null) {
                                MnemonicSaveFile.saveFile.content(walletCurrent.walletName, walletCurrent.mnemonic)
                            }
                        }
                    }
                    dialog.dismiss()
                } else {
                    toastDefault(getString(R.string.input_correct_wallet_password))
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
                        window.findViewById<Button>(R.id.btn_confirm).background =
                            getDrawable(R.drawable.shape_green_bottom_btn)
                    } else {
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
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
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
            window.findViewById<EditText>(R.id.edt_secret_key)
                .setText(walletCurrent.privateKey.toString())
            window.findViewById<Button>(R.id.btn_copy).setOnClickListener {
                ClipboardUtils.copy(walletCurrent.privateKey.toString())
            }
            val edtWalletName = window.findViewById<TextView>(R.id.edt_wallet_name)
            edtWalletName.addTextChangedListener(object : TextWatcher {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun afterTextChanged(s: Editable?) {
                    if (edtWalletName.text.isNotEmpty()) {
                        window.findViewById<Button>(R.id.btn_confirm).background =
                            getDrawable(R.drawable.shape_green_bottom_btn)
                    } else {
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
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

            window.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                if (walletCurrent.privateKey.toString() != null) {
                    PrivateKeySaveFile.saveFile.content(walletCurrent.walletName, walletCurrent.privateKey)
                }
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
            window.findViewById<EditText>(R.id.edt_keystore)
                .setText(walletCurrent.keystore.toString())
            window.findViewById<Button>(R.id.btn_copy).setOnClickListener {
                ClipboardUtils.copy(walletCurrent.keystore.toString())
            }
            val edtWalletName = window.findViewById<TextView>(R.id.edt_wallet_name)
            edtWalletName.addTextChangedListener(object : TextWatcher {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun afterTextChanged(s: Editable?) {
                    if (edtWalletName.text.isNotEmpty()) {
                        window.findViewById<Button>(R.id.btn_generate_qr_code).background =
                            getDrawable(R.drawable.shape_green_bottom_btn)
                        window.findViewById<Button>(R.id.btn_confirm).background =
                            getDrawable(R.drawable.shape_green_bottom_btn)
                    } else {
                        window.findViewById<Button>(R.id.btn_generate_qr_code).background =
                            getDrawable(R.drawable.shape_gray_bottom_btn)
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
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
            window.findViewById<Button>(R.id.btn_generate_qr_code).setOnClickListener { v1: View? ->
                showSaveDialog()
                dialog.dismiss()
            }
            window.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                if (walletCurrent.keystore.toString() != null) {
                    KeyStoreSaveFile.saveFile.content(walletCurrent.walletName, walletCurrent.keystore.toString())
                }
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
                        walletCurrent.keystore.toString(),
                        DisplayHelper.dpToPx(200)
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val btnNext = window.findViewById<Button>(R.id.btn_next)
            btnNext.text = getText(R.string.gathering_save)
            val btnCancel = window.findViewById<Button>(R.id.btn_cancel)
            btnCancel.setOnClickListener { v1: View? -> dialog.dismiss() }
            btnNext.setOnClickListener { v1: View? ->
                requestRuntimePermission(
                    arrayOf(
                        "android.permission.CAMERA",
                        "android.permission.WRITE_EXTERNAL_STORAGE",
                        "android.permission.READ_EXTERNAL_STORAGE"
                    ), object :  PermissionListener {
                        override
                        fun onGranted() {
                            try {
                                val bitmap: Bitmap = viewConversionBitmap(ivImg)
                                if (bitmap != null) {
                                    saveBitmap(bitmap)
                                    bitmap.recycle()
                                }
                            } catch (e: Exception) {
                            }
                        }

                        override
                        fun onDenied(deniedPermissions: List<String?>?) {
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
        println("-=-=-=->save:${getString(R.string.save_success)}")
        Toast.makeText(this, getString(R.string.save_success), Toast.LENGTH_LONG)
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