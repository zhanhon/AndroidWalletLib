package com.ramble.ramblewallet.update;

import org.lzh.framework.updatepluginlib.base.UpdateStrategy;
import org.lzh.framework.updatepluginlib.model.Update;

/**
 * 当为强制更新时，将会强制使用此更新策略。
 *
 * <p>此更新策略的表现为：<br>
 * 当下载完成后。强制显示下载完成后的界面通知，其他的通知策略默认不变。
 *
 * @author haoge on 2017/9/25.
 */
public class LotForcedUpdateStrategy extends UpdateStrategy {

    private final UpdateStrategy delegate;

    public LotForcedUpdateStrategy(UpdateStrategy delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean isShowUpdateDialog(Update update) {
        return delegate.isShowUpdateDialog(update);
    }

    @Override
    public boolean isAutoInstall() {
        return true;
    }

    @Override
    public boolean isShowDownloadDialog() {
        return delegate.isShowDownloadDialog();
    }

}
