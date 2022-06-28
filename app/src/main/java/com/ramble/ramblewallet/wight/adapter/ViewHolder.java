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
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Method;

public class ViewHolder extends RecyclerView.ViewHolder implements ListenerAttach {

    /**
     * ListenerAttach
     */
    private final ListenerAttachImpl listenerAttach;
    /**
     * binding
     */
    @NonNull
    private Object binding;
    /**
     * adapter size
     */
    private int size = 0;
    /**
     * item
     */
    private Item item;

    public ViewHolder(@Nullable View itemView, @Nullable ListenerProvider provider) {
        super(itemView);
        this.listenerAttach = new ListenerAttachImpl(provider, this);
        try {
            Class<?> clazz = Class.forName("androidx.databinding.DataBindingUtil");
            Method method = clazz.getMethod("bind", View.class);
            binding = method.invoke(null, itemView);
        } catch (Exception ignored) {
            binding = new DefaultBinding(itemView);
        }
    }

    /**
     * get adapter size
     *
     * @return
     */
    public int getSize() {
        return size;
    }

    void setSize(int size) {
        this.size = size;
    }

    /**
     * get item
     *
     * @return
     */
    @NonNull
    public <V extends Item> V getItem() {
        return (V) item;
    }

    void setCurrentPosition(int position) {
        if (position < 0) {
            throw new UnsupportedOperationException("position = -1");
        }
    }

    void setCurrentItem(Item item) {
        this.item = item;
    }

    /**
     * get binding,if you are using DataBinding , it will return ViewDataBinding,
     * or it will return {@link DefaultBinding}
     *
     * @param <T>
     * @return
     */
    @NonNull
    public <T extends Object> T binding() {
        return (T) this.binding;
    }

    /**
     * attach OnClickListener for view
     *
     * @param viewId view id
     */
    @Override
    public void attachOnClickListener(@IdRes int viewId) {
        listenerAttach.attachOnClickListener(viewId);
    }

    /**
     * attach OnTouchListener for view
     *
     * @param viewId view id
     */
    @Override
    public void attachOnTouchListener(@IdRes int viewId) {
        listenerAttach.attachOnTouchListener(viewId);
    }

    /**
     * attach OnLongClickListener for view
     *
     * @param viewId view id
     */
    @Override
    public void attachOnLongClickListener(@IdRes int viewId) {
        listenerAttach.attachOnLongClickListener(viewId);
    }

    /**
     * attach CompoundButton.OnCheckedChangeListener for CompoundButton
     *
     * @param viewId CompoundButton view id
     */
    @Override
    public void attachOnCheckedChangeListener(@IdRes int viewId) {
        listenerAttach.attachOnCheckedChangeListener(viewId);
    }

    @Override
    public void attachImageLoader(@IdRes int viewId) {
        listenerAttach.attachImageLoader(viewId);
    }

    @Override
    public void attachTextChangedListener(@IdRes int viewId) {
        listenerAttach.attachTextChangedListener(viewId);
    }

    @Override
    public void detachTextChangedListener(@IdRes int viewId) {
        listenerAttach.detachTextChangedListener(viewId);
    }

    /**
     * get the current attached Adapter.
     *
     * @param <T>
     * @return
     */
    @NonNull
    public <T extends Object> T adapter() {
        return (T) this.listenerAttach.provider;
    }

    /**
     * get the current attached Parent: ListView，GridView，RecyclerView，ViewPager ect
     *
     * @param <T>
     * @return
     */
    @NonNull
    public <T extends ViewGroup> T parent() {
        return (T) itemView.getParent();
    }
}
