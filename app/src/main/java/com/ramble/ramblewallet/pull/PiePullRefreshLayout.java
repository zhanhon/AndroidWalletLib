/*
 * Tencent is pleased to support the open source community by making QMUI_Android available.
 *
 * Copyright (C) 2017-2018 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the MIT License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ramble.ramblewallet.pull;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class PiePullRefreshLayout extends QMUIPullRefreshLayout {
    private PieRefreshHeaderView refreshView;

    public PiePullRefreshLayout(Context context) {
        super(context);
    }

    public PiePullRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PiePullRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View createRefreshView() {
        refreshView = new PieRefreshHeaderView(getContext());
        return refreshView;
    }

    public void setTextColor(int color) {
        if (refreshView != null) {
            refreshView.setTextColor(color);
        }
    }

    public boolean isRefreshing() {
        return mIsRefreshing;
    }
}