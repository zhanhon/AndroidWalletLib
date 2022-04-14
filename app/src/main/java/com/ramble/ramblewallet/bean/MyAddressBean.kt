package com.ramble.ramblewallet.bean

/**
 * 时间　: 2021/12/28 16:46
 * 作者　: potato
 * 描述　:
 */
class MyAddressBean @SuppressWarnings("unused") constructor() {

    var userName: String = ""


    var address: String = ""


    var type: Int = 0//1:eth 2：比特币 3: trx  选择图标//4 :sola//5:doge
    var isNeedDelete: Boolean = false

}