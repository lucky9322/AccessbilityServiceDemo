package com.zdd.accessbilityservicedemo.service;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.zdd.accessbilityservicedemo.util.LogUtil;

import java.util.List;

/**
 * @CreateDate: 2017/1/21 上午9:44
 * @Author: lucky
 * @Description:
 * @Version: [v1.0]
 */

public class MyAccessbilityService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        LogUtil.i("onAccessibilityEvent");
        if (event.getSource() != null)
        {
            findAndPerformAction("安装");
            findAndPerformAction("下一步");
            findAndPerformAction("完成");
        }
    }

    private void findAndPerformAction(String action) {
// AccessibilityEvent.getSource(),
// findFocus(int),
// getWindow()或者
// getRootInActiveWindow()获取窗口内容
        if (getRootInActiveWindow() == null) {
            return;
        }
        List<AccessibilityNodeInfo> text = getRootInActiveWindow().
                findAccessibilityNodeInfosByText(action);
        for (int i = 0; i < text.size(); i++) {
            AccessibilityNodeInfo info = text.get(i);
            if (info.getClassName().equals("android.widget.Button")&&info.isEnabled()){
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        LogUtil.i("onServiceConnected");
    }
}
