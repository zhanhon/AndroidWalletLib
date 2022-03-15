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

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public final class RecyclerAdapter extends BaseRecyclerAdapter<RecyclerItem, ViewHolder> {

    public RecyclerView mRecyclerView;
    public Handler mHandler;
    public int mFirstVisiblePosition = -1;
    public int mLastVisiblePosition = -1;

    public RecyclerAdapter() {
        super();
    }

    public RecyclerAdapter(List<RecyclerItem> data) {
        super(data);
    }


    public void setRecycleView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        return new ViewHolder(inflater.inflate(viewType, parent, false), this);
    }

    public void setCurrentPostion(int firstVisiblePosition, int lastVisiblePosition) {
        mFirstVisiblePosition = firstVisiblePosition;
        mLastVisiblePosition = lastVisiblePosition;
    }
}