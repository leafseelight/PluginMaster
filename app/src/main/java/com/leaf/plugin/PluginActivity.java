package com.leaf.plugin;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leaf.plugin.base.AbsCommonAdapter;
import com.leaf.plugin.base.AbsViewHolder;
import com.leaf.plugin.bean.ApkBean;
import com.leaf.plugin.bean.ResponseRoot;
import com.leaf.plugin.utils.FastJsonTools;
import com.leaf.plugin.utils.WeakHandler;
import com.leaf.plugin.widget.AbsListView;
import com.morgoo.droidplugin.pm.PluginManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PluginActivity extends AppCompatActivity {

    private WeakHandler mHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            return false;
        }
    });

    private AbsCommonAdapter<ApkBean> mPluginStoreAdapter;
    private AbsCommonAdapter<ApkBean> mPluginInstalledAdapter;

    private AbsListView lv_plugin_store;
    private AbsListView lv_plugin_installed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin);

        lv_plugin_store = (AbsListView) findViewById(R.id.lv_plugin_store);
        lv_plugin_installed = (AbsListView) findViewById(R.id.lv_plugin_installed);

        mPluginStoreAdapter = new AbsCommonAdapter<ApkBean>(this, R.layout.listitem_plugin) {
            @Override
            public void convert(AbsViewHolder helper, final ApkBean item, int pos) {
                ImageView iv_icon = helper.getView(R.id.iv_icon);
                TextView tv_name = helper.getView(R.id.tv_name);
                TextView tv_desc = helper.getView(R.id.tv_desc);
                Button btn_manager = helper.getView(R.id.btn_manager);
                btn_manager.setText("添加");
                btn_manager.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addApk(item);
                    }
                });
            }
        };
        lv_plugin_store.setAdapter(mPluginStoreAdapter);



        mPluginInstalledAdapter = new AbsCommonAdapter<ApkBean>(this, R.layout.listitem_plugin) {
            @Override
            public void convert(AbsViewHolder helper, ApkBean item, int pos) {
                ImageView iv_icon = helper.getView(R.id.iv_icon);
                TextView tv_name = helper.getView(R.id.tv_name);
                TextView tv_desc = helper.getView(R.id.tv_desc);
                Button btn_manager = helper.getView(R.id.btn_manager);
            }
        };
        lv_plugin_installed.setAdapter(mPluginInstalledAdapter);

        String json = getAssetsFileContent(this, "plugins.json");
        ResponseRoot responseRoot = FastJsonTools.getObject(json, ResponseRoot.class);
        List<ApkBean> apkBeanList = FastJsonTools.getObjectArray(responseRoot.getData(), ApkBean.class);
        mPluginStoreAdapter.addData(apkBeanList,false);

    }

    private void addApk(ApkBean item) {
        // 异步加载, 防止Apk过多, 影响速度
        Observable.just(downloadApk(this,item))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    private File downloadApk(Context context,ApkBean item) {
        try {
            String apkFilePath = "";
            String tmpFilePath = "";
            String apkName = item.getName()+ "_v"+ item.getVersionName() +".apk";
            String tmpApk = item.getName()+ "_v"+ item.getVersionName() +".tmp";
            // 判断是否挂载了SD卡
            String storageState = Environment.getExternalStorageState();
            if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                String savePath = context.getExternalCacheDir() + "/plugins/";
                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                apkFilePath = savePath + apkName;
                tmpFilePath = savePath + tmpApk;
            }

            // 没有挂载SD卡，无法下载文件
            if (TextUtils.isEmpty(apkFilePath)) {
                Toast.makeText(PluginActivity.this, "没有SdCard", Toast.LENGTH_SHORT).show();
                return null;
            }

            File ApkFile = new File(apkFilePath);

            // 是否已下载更新文件
            if (ApkFile.exists()) {
                try {
                    int result = PluginManager.getInstance().installPackage(apkFilePath,0);
                    if(result == PluginManager.INSTALL_FAILED_NO_REQUESTEDPERMISSION){
                        Toast.makeText(PluginActivity.this,"fail install",Toast.LENGTH_SHORT).show();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return null;
            }

            // 输出临时下载文件
            File tmpFile = new File(tmpFilePath);
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
            FileOutputStream fos = new FileOutputStream(tmpFile);

            URL url = new URL(item.getUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            int length = conn.getContentLength();
            InputStream is = conn.getInputStream();

            // 显示文件大小格式：2个小数点显示
            DecimalFormat df = new DecimalFormat("0.00");
            // 进度条下面显示的总文件大小
//            apkFileSize = df.format((float) length / 1024 / 1024) + "MB";
            int count = 0;
            byte buf[] = new byte[1024];

//            do {
//                int numread = is.read(buf);
//                count += numread;
//                // 进度条下面显示的当前下载文件大小
//                //tmpFileSize = df.format((float) count / 1024 / 1024) + "MB";
//                // 当前进度值
//                //progress = (int) (((float) count / length) * 100);
//                // 更新进度
//                //mHandler.sendEmptyMessage(DOWN_UPDATE);
//                if (numread <= 0) {
//                    // 下载完成 - 将临时下载文件转成APK文件
//                    if (tmpFile.renameTo(ApkFile)) {
//                        // 通知安装
//                        //mHandler.sendEmptyMessage(DOWN_OVER);
//                    }
//                    break;
//                }
//                fos.write(buf, 0, numread);
//            } while (!interceptFlag);// 点击取消就停止下载//interceptFlag
            fos.close();
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void installApk(String path){

    }

    private String getAssetsFileContent(Context context, String fileName) {
        String result = "";
        try {
            InputStream in = context.getAssets().open(fileName);
            // 获取文件的字节数
            int lenght = in.available();
            // 创建byte数组
            byte[] buffer = new byte[lenght];
            // 将文件中的数据读到byte数组中
            in.read(buffer);
            result = new String(buffer, "UTF-8");
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
