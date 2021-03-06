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
import android.os.Environment.getExternalStorageDirectory
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
import androidx.core.graphics.ColorUtils
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.ramble.ramblewallet.MyApp
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.bean.AddressReport
import com.ramble.ramblewallet.bean.AllTokenBean
import com.ramble.ramblewallet.bean.Wallet
import com.ramble.ramblewallet.constant.*
import com.ramble.ramblewallet.databinding.ActivityWalletMoreOperateBinding
import com.ramble.ramblewallet.fragment.MainETHFragment
import com.ramble.ramblewallet.network.reportAddressUrl
import com.ramble.ramblewallet.network.toApiRequest
import com.ramble.ramblewallet.popup.EditPopup
import com.ramble.ramblewallet.utils.*
import com.ramble.ramblewallet.wight.FingerprintDialogFragment
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import javax.crypto.Cipher

class WalletMoreOperateActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityWalletMoreOperateBinding
    private lateinit var walletCurrent: Wallet
    private var saveWalletList: ArrayList<Wallet> = arrayListOf()
    private val sdCardDir =
        getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_DCIM
    private lateinit var walletSelleted: Wallet
    var mPermissionListener: PermissionListener? = null
    private var times = 0
    private var myAllToken: ArrayList<AllTokenBean> = arrayListOf()
    private var isFinger = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet_more_operate)
        SharedPreferencesUtils.saveSecurityBoolean(this, IS_CONFIRM_MNEMONIC, false)
        walletCurrent = Gson().fromJson(
            intent.getStringExtra(ARG_PARAM1),
            object : TypeToken<Wallet>() {}.type
        )

        if (walletCurrent.walletType == 1) {
            myAllToken = Gson().fromJson(
                SharedPreferencesUtils.getSecurityString(this, TOKEN_INFO_NO, ""),
                object : TypeToken<ArrayList<AllTokenBean>>() {}.type
            )
        }
        when (walletCurrent.walletType) {
            WALLET_TYPE_ETH -> {
                binding.tvRecoverWalletTitle.text =
                    "ETH" + " " + walletCurrent.walletName + getString(R.string.di_wallet)
            }
            2 -> {
                binding.tvRecoverWalletTitle.text =
                    "TRX" + " " + walletCurrent.walletName + getString(R.string.di_wallet)
            }
            3 -> {
                binding.tvRecoverWalletTitle.text =
                    "BTC" + " " + walletCurrent.walletName + getString(R.string.di_wallet)
                binding.lineKeystore.visibility = View.GONE
                binding.rlKeystoreBackups.visibility = View.GONE
            }
            4 -> {
                binding.tvRecoverWalletTitle.text =
                    "SOL" + " " + walletCurrent.walletName + getString(R.string.di_wallet)
                binding.lineKeystore.visibility = View.GONE
                binding.rlKeystoreBackups.visibility = View.GONE
            }
        }

        saveWalletList = Gson().fromJson(
            SharedPreferencesUtils.getSecurityString(this, WALLETINFO, ""),
            object : TypeToken<ArrayList<Wallet>>() {}.type
        )
        walletSelleted = Gson().fromJson(
            SharedPreferencesUtils.getSecurityString(this, WALLETSELECTED, ""),
            object : TypeToken<Wallet>() {}.type
        )

        if (saveWalletList.size == 1 || walletCurrent.isIdWallet) {
            binding.tvDeleteWallet.visibility = View.GONE
        } else {
            binding.tvDeleteWallet.visibility = View.VISIBLE
        }

        if (!walletCurrent.mnemonic.isNullOrEmpty()) {
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
                if (isFinger) {//??????????????????
                    setFingerprint(getString(R.string.edit_wallet))
                } else {
                    inputPasswordDialog(getString(R.string.edit_wallet))
                }
            }
            R.id.rl_contributing_words_backups -> {
                if (isFinger) {//??????????????????
                    setFingerprint(getString(R.string.backup_memorization_words))
                } else {
                    inputPasswordDialog(getString(R.string.backup_memorization_words))
                }
            }
            R.id.rl_secret_key_backups -> {
                if (isFinger) {//??????????????????
                    setFingerprint(getString(R.string.secret_key_backup))
                } else {
                    inputPasswordDialog(getString(R.string.secret_key_backup))
                }
            }
            R.id.rl_keystore_backups -> {
                if (isFinger) {//??????????????????
                    setFingerprint(getString(R.string.keystore_backup))
                } else {
                    inputPasswordDialog(getString(R.string.keystore_backup))
                }
            }
            R.id.tv_delete_wallet -> {
                passwordConfirmationDialog()

            }
        }
    }

    private var cipher: Cipher? = null
    private fun setFingerprint(title: String) {
        if (ToolUtils.supportFingerprint(this)) {
            ToolUtils.initKey() //???????????????????????????key
            //????????????Cipher??????
            cipher = ToolUtils.initCipher()
        }
        cipher?.let { showFingerPrintDialog(it, title) }
    }

    private fun showFingerPrintDialog(cipher: Cipher, title: String) {
        val dialogFragment = FingerprintDialogFragment()
        dialogFragment.setCipher(cipher)
        dialogFragment.show(supportFragmentManager, "fingerprint")
        dialogFragment.setOnFingerprintSetting(FingerprintDialogFragment.OnFingerprintSetting { isSucceed ->
            if (isSucceed) {
                ToastUtils.showToastFree(this, getString(R.string.fingerprint_success))
                when (title) {
                    getString(R.string.edit_wallet) -> {
                        editWalletDialog(title)
                    }
                    getString(R.string.backup_memorization_words) -> {
                        startActivity(Intent(this, MnemonicActivity::class.java).apply {
                            putExtra(ARG_PARAM1, walletCurrent)
                        })
                    }
                    getString(R.string.secret_key_backup) -> {
                        secretKeyDialog(title)
                    }
                    getString(R.string.keystore_backup) -> {
                        keystoreDialog(title)
                    }
                }
            } else {
                ToastUtils.showToastFree(this, getString(R.string.fingerprint_failed))
            }
        })
    }

    private fun passwordConfirmationDialog() {
        val editPopup = EditPopup(this,
            getString(R.string.wallet_password),
            "",
            getString(R.string.please_input_password),
            getString(R.string.cancel),
            getString(R.string.confirm), object : EditPopup.OnClickConfirmListener {
                override fun onClickConfirm(view: View?, text: String?) {
                    if (text == walletSelleted.walletPassword) {
                        btnConfirmSub()
                        finish()
                    } else {
                        ToastUtils.showToastFree(
                            this@WalletMoreOperateActivity,
                            getString(R.string.password_incorrect)
                        )
                    }

                }
            })
        XPopup.Builder(this)
            .isDarkTheme(true)
            .setPopupCallback(object : SimpleCallback(){
                override fun beforeShow(popupView: BasePopupView?) {
                    editPopup.setIsPasswordView(true)
                }
            })
            .asCustom(editPopup)
            .show()
    }

    private fun btnConfirmSub() {
        val detailsList: ArrayList<AddressReport.DetailsList> = arrayListOf()
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

        SharedPreferencesUtils.saveSecurityString(this, WALLETSELECTED, Gson().toJson(saveWalletList[0]))
        SharedPreferencesUtils.saveSecurityString(this, WALLETINFO, Gson().toJson(saveWalletList))
        if (walletCurrent.walletType == WALLET_TYPE_ETH) {
            val lists = myAllToken.iterator()
            lists.forEach {
                if (it.myCurrency == walletCurrent.address) {
                    lists.remove()
                }
            }
            SharedPreferencesUtils.saveSecurityString(this, TOKEN_INFO_NO, Gson().toJson(myAllToken))
        }
        RxBus.emitEvent(Pie.EVENT_FRAGMENT_TOGGLE, MyApp.getInstance.startActivityJun(this))
    }

    override fun onResume() {
        super.onResume()
        isFinger = SharedPreferencesUtils.getSecurityBoolean(
            this,
            ISFINGERPRINT_KEY_COMMON,
            false
        )
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
                    after: Int,
                ) {
                    //??????????????????????????????
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    //??????????????????????????????
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
                                putExtra(ARG_PARAM1, walletCurrent)
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
                    after: Int,
                ) {
                    //??????????????????????????????
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    //??????????????????????????????
                }
            })
            window.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
                saveWalletList.forEach {
                    if (it.address == walletCurrent.address) {
                        it.walletName = edtWalletName.text.trim().toString()
                    }
                }
                SharedPreferencesUtils.saveSecurityString(
                    this,
                    WALLETINFO,
                    Gson().toJson(saveWalletList)
                )
                if (walletCurrent.address == walletSelleted.address) {
                    walletSelleted.walletName = edtWalletName.text.trim().toString()
                    SharedPreferencesUtils.saveSecurityString(
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
        //????????????
        val params = window.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        //???????????????????????????????????????????????????
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        //dialog?????????
        params.dimAmount = 0.5f
        window.attributes = params
        window.setGravity(Gravity.BOTTOM)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
                            walletCurrent.keystore.toString(),
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
        permissionListener: PermissionListener,
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
        grantResults: IntArray,
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
        val languageCode = SharedPreferencesUtils.getSecurityString(appContext, LANGUAGE, CN)
        val deviceToken = SharedPreferencesUtils.getSecurityString(appContext, DEVICE_TOKEN, "")
        if (detailsList.size == 0) return
        mApiService.putAddress(
            AddressReport.Req(detailsList, deviceToken, languageCode).toApiRequest(reportAddressUrl)
        ).applyIo().subscribe(
            {
                if (it.code() != 1) {
                    if (times < 3) {
                        putAddress(detailsList)
                        times++
                    }
                }
            }, {
            }
        )
    }

}