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
import androidx.annotation.Nullable;

import java.util.List;

/**
 * PagerAdapter for ViewPager
 */
public final class PagerAdapter2 extends BasePagerAdapter2<Item, ViewHolder> {

    public PagerAdapter2() {
        super();
    }

    public PagerAdapter2(@Nullable List<Item> data) {
        super(data);
    }

    /**
     * create ViewHolder
     *
     * @param convertView item view
     * @return ViewHolder
     */
    @Override
    protected ViewHolder createViewHolder(@NonNull View convertView) {
        return new ViewHolder(convertView, this);
    }
}