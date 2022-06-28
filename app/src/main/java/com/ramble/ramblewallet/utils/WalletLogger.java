/*
 * Copyright 2013-2019 Xia Jun(3979434@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * **************************************************************************************
 *
 *                         Website :                            *
 *
 * **************************************************************************************
 */
package com.ramble.ramblewallet.utils;

import android.util.Log;

import com.ramble.ramblewallet.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

public class WalletLogger {

    public static void i(String tag, String log) {
        if (BuildConfig.API_LOG) {
            Log.i(tag, log);
        }
    }

    public static void d(String tag, String log) {
        if (BuildConfig.API_LOG) {
            Log.d(tag, log);
        }
    }

    public static void w(String tag, String log) {
        if (BuildConfig.API_LOG) {
            Log.w(tag, log);
        }
    }

    public static void e(String tag, String log) {
        if (BuildConfig.API_LOG) {
            Log.e(tag, log);
        }
    }

    public static void e(String tag, String log, Throwable throwable) {
        if (BuildConfig.API_LOG) {
            Log.e(tag, log, throwable);
        }
    }

    /**
     * json 数据格式化输出
     * @param response
     * @return
     */
    public static String formatDataFromJson(String response) {
        try {
            if (response.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject.toString(4);
            } else if (response.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(response);
                return jsonArray.toString(4);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

}
