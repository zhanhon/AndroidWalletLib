package com.ramble.ramblewallet.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.base.BaseActivity
import com.ramble.ramblewallet.constant.ARG_TITLE
import com.ramble.ramblewallet.databinding.ActivityWebviewWalletBinding
import com.ramble.ramblewallet.wight.WebViewListener
import com.ramble.ramblewallet.wight.UrlWebView
import razerdp.util.SimpleAnimationUtils.AnimationListenerAdapter
import java.net.MalformedURLException
import java.net.URL

class MMWebViewActivity : BaseActivity(), WebViewListener {
    private lateinit var binding: ActivityWebviewWalletBinding

    private val progressHandler: Handler = object : Handler() {
        override fun handleMessage(message: Message) {
            val animation = AnimationUtils.loadAnimation(this@MMWebViewActivity, R.anim.disappear)
            animation.setAnimationListener(object : AnimationListenerAdapter() {
                override fun onAnimationEnd(animation: Animation) {
                    binding.loadingProgressBar.visibility = View.GONE
                }
            })
            binding.loadingProgressBar.startAnimation(animation)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_webview_wallet)
        initComponents()
    }

    private fun initComponents() {
        binding.webview.setWebViewListener(this)
        binding.webview.setScrollable(true)
        binding.provider.text = getString(R.string.label_web_provider, getHost(binding.webview.url))
        binding.titleAll.ivBack.setOnClickListener { finish() }
        val intent = intent
        binding.webview.loadUrl(intent.data.toString())
        binding.titleAll.tvAllTitle.text = intent.getStringExtra(ARG_TITLE)
    }

    private fun getHost(url: String?): String? {
        return try {
            URL(url).host
        } catch (e: MalformedURLException) {
            null
        }
    }

    override fun onBackPressed() {
        if (binding.webview.canGoBack()) {
            binding.webview.goBack()
            return
        }
        super.onBackPressed()
    }

    override fun onProgressChanged(newProgress: Int) {
        binding.loadingProgressBar.progress = newProgress
        binding.loadingProgressBar.visibility = View.VISIBLE
        if (newProgress == 100) {
            progressHandler.sendEmptyMessage(0)
        }
        if (newProgress > 50) {
           binding.background.visibility = View.GONE
        }
    }

    override fun onReceivedTitle(title: String) {
//        setTitle(title);
    }

    public override fun onDestroy() {
        super.onDestroy()
        binding.webview.destroy()
        progressHandler.removeCallbacksAndMessages(null)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UrlWebView.FILE_CHOOSE_CODE) {
            binding.webview.onReceiveFile(data!!.data)
        }
        if (resultCode != RESULT_OK && requestCode == UrlWebView.FILE_CHOOSE_CODE) {
            binding.webview.onCancelChooseFile()
        }
    }
}