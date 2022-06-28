package com.ramble.ramblewallet.helper

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * 时间　: 2021/12/15 16:48
 * 作者　: potato
 * 描述　:
 */
@JvmOverloads
fun <T : ViewDataBinding> LayoutInflater.dataBinding(
    @LayoutRes layoutRes: Int,
    container: ViewGroup? = null
): T =
    DataBindingUtil.inflate(this, layoutRes, container, false)


@ColorInt
fun Context.toColor(@ColorRes id: Int): Int = ContextCompat.getColor(this, id)


fun formatHTML(content: String, title: String = ""): String {
    return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name=viewport content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no">
                  <title>$title</title>
                  <style type="text/css">
                    .htmls { width: auto; overflow: auto; text-align: left; display: block !important; box-sizing: border-box}
                    .htmls * { word-break: break-all; }
                    .htmls img { max-width: 100%; }
                  </style>
                </head>
                <body class="htmls">
                  $content
                </body>
                </html>
            """.trimIndent()
}