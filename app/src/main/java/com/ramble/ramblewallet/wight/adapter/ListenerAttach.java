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

import androidx.annotation.IdRes;

interface ListenerAttach {
    /**
     * attach OnClickListener for view
     *
     * @param viewId view id
     */
    void attachOnClickListener(@IdRes int viewId);

    /**
     * attach OnTouchListener for view
     *
     * @param viewId view id
     */
    void attachOnTouchListener(@IdRes int viewId);

    /**
     * attach OnLongClickListener for view
     *
     * @param viewId view id
     */
    void attachOnLongClickListener(@IdRes int viewId);

    /**
     * attach CompoundButton.OnCheckedChangeListener for CompoundButton
     *
     * @param viewId CompoundButton view id
     */
    void attachOnCheckedChangeListener(@IdRes int viewId);

    /**
     * load image
     *
     * @param viewId
     */
    void attachImageLoader(@IdRes int viewId);

    /**
     * attach TextChangedListener for TextView or EditText
     *
     * @param viewId
     */
    void attachTextChangedListener(@IdRes int viewId);


    /**
     * detach TextChangedListener for TextView or EditText
     *
     * @param viewId
     */
    void detachTextChangedListener(@IdRes int viewId);
}
