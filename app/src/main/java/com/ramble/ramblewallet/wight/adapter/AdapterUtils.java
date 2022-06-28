/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ramble.ramblewallet.wight.adapter;

import android.view.View;

import androidx.annotation.NonNull;


public class AdapterUtils {
    public static final int ADAPTER_HOLDER = 1 + 2 << 24;

    private AdapterUtils() {
        throw new UnsupportedOperationException("Not supported");
    }

    /**
     * find view
     *
     * @param view   parent view
     * @param viewId view id
     * @param <V>    view
     * @return
     */
    @NonNull
    public static <V extends View> V findView(View view, int viewId) {
        return view.findViewById(viewId);
    }


    /**
     * get view holder
     *
     * @param view
     * @return view holder
     */
    @NonNull
    public static ViewHolder getHolder(View view) {
        return (ViewHolder) view.getTag(ADAPTER_HOLDER);
    }
}
