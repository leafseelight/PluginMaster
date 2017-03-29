package com.leaf.plugin.bean;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

/**
 * Apk项的条目
 *
 * @author wangchenlong
 */
public class ApkItem extends BaseBean{

    public Drawable icon; // 图标
    public CharSequence title; // 标题
    public String versionName; // 版本名称
    public int versionCode; // 版本号
    public String apkFile; // Apk路径
    public PackageInfo packageInfo; // 包信息

    public ApkItem(PackageManager packageManager, PackageInfo packageInfo) {

        // 必须设置, 否则title无法获取
        String path = packageInfo.applicationInfo.publicSourceDir;
        packageInfo.applicationInfo.sourceDir = path;
        packageInfo.applicationInfo.publicSourceDir = path;

        try {
            icon = packageManager.getApplicationIcon(packageInfo.applicationInfo);
        } catch (Exception e) {
            icon = packageManager.getDefaultActivityIcon();
        }
        try {
            title = packageManager.getApplicationLabel(packageInfo.applicationInfo);
        } catch (Exception e) {
            title = packageInfo.packageName;
        }
        versionName = packageInfo.versionName;
        versionCode = packageInfo.versionCode;
        apkFile = path;
        this.packageInfo = packageInfo;
    }
}