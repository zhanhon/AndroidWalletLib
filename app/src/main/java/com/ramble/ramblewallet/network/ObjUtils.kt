package com.ramble.ramblewallet.network


import com.ramble.ramblewallet.constant.DEF_HEAD

/**
 * 时间　: 2021/12/21 14:14
 * 作者　: potato
 * 描述　:
 */
object ObjUtils {
    val rsaEncryptor: RsaEncryptor by lazy {
        RsaEncryptor(
            RsaEncryptor.loadPublicKey(publicKey())
        )
    }

    val aesCbcEncryptor: AesCbcEncryptor by lazy { AesCbcEncryptor.getInstance() }

    @JvmStatic
    fun <T : ApiRequest.Body> apiRequest(body: T): ApiRequest<T> {
        return ApiRequest(
            apiRequestHeader(/*com.square.arch.BuildConfig.DEFAULTAPINAME*/DEF_HEAD),
            body
        )
    }

    @JvmStatic
    private fun apiRequestHeader(apiName: String): ApiRequest.Header {
        return ApiRequest.Header(
            apiName,
            System.currentTimeMillis(),
            1,
            sign(),
            "1"
        )
    }

    @JvmStatic
    private fun token(): String = "RxViewModel.globe.token"

    @JvmStatic
    private fun sign(): String = "unencrypted"



//    @JvmStatic
//    fun secretRequest(): ApiRequest<ApiSecret.Req> {
//        val header = ApiRequest.Header(
//            "/common-api/system/getSecret",
//            System.currentTimeMillis(),
//            1,
//            sign(),
//            "",
//            BuildConfig.PLATFORM_ID
//        )
//        // 拼接加密原串为 rsaSrc，拼接格式为：  clientType + platformId + apiName + token + callTime
//        // 先用RSA公钥加密 rsaSrc 后再做 base64编码 ，然后赋值给 conventionValue 属性
//        val rsaSrc = header.clientType.toString() +
//                header.platformId.toString() +
//                header.apiName +
//                header.token +
//                header.callTime
//
//        return ApiRequest(
//            header,
//            ApiSecret.Req(rsaEncryptor.encryptedByPublicKey(rsaSrc))
//        )
//    }

    private fun publicKey(): String = AesCbcEncryptor.getInstance().decrypt(
        buildString {
            append(keyPart1)


        }, "AK1234567890OK00"
    )

    private const val keyPart1 =
        "MuuHo3Y13o2tYYiBWAjSEYdM2sFUHDeZDhAqkaUPPWLK1VNK6aqJGy5tIHwPylTZsdNBOAN3yuDy+i0eAb2KhrFhWUMg/VenEm8zo02m5lWNhRVlGrGy90eVoq/fFjHaV/Grf0ric3Pch9YNd9grpSuZ0Bk1UzMnQ/Fb/94R+QphLw+Pdt+SnxikT1pQ3FapnbUBVpP6xpkdbo3w4k"
}