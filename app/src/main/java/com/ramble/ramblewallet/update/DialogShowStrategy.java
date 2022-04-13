package com.ramble.ramblewallet.update;

import org.lzh.framework.updatepluginlib.base.UpdateStrategy;
import org.lzh.framework.updatepluginlib.impl.WifiFirstStrategy;
import org.lzh.framework.updatepluginlib.model.Update;

/**
 * 自定义强制显示所有Dialog策略，
 * 默认使用参考 {@link WifiFirstStrategy}
 */
public class DialogShowStrategy extends UpdateStrategy {
    /**
     * 指定是否在判断出有需要更新的版本时。弹出更新提醒弹窗
     *
     * @param update 需要更新的版本信息
     * @return true 显示弹窗
     */
    @Override
    public boolean isShowUpdateDialog(Update update) {
        return !update.isForced();
    }

    /**
     * 指定是否下载完成后自动进行安装页不显示弹窗
     *
     * @return true 直接安装，不显示弹窗
     */
    @Override
    public boolean isAutoInstall() {
        return true;
    }

    /**
     * 指定是否在下载的时候显示下载进度弹窗
     *
     * @return true 显示弹窗
     */
    @Override
    public boolean isShowDownloadDialog() {
        return true;
    }
}
