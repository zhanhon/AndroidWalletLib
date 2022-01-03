package com.ramble.ramblewallet.bean

/**
 * 时间　: 2021/12/30 16:46
 * 作者　: ricky
 * 描述　:
 */
class WalletManageBean @SuppressWarnings("unused") constructor() {

    var userName: String = ""


    var address: String = ""


    var type: Int = 0//1:eth 2：比特币 3: trx  选择图标

    var isClickDelete: Boolean = false

}