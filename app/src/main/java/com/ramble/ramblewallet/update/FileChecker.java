package com.ramble.ramblewallet.update;

public class FileChecker extends org.lzh.framework.updatepluginlib.base.FileChecker {
    @Override
    protected boolean onCheckBeforeDownload() throws Exception {
        return false;
    }

    @Override
    protected void onCheckBeforeInstall() throws Exception {

    }
}
