package com.leaf.plugin;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.leaf.plugin.bean.ApkBean;
import com.leaf.plugin.utils.WeakHandler;
import com.morgoo.droidplugin.pm.PluginManager;
import com.morgoo.helper.compat.PackageManagerCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import static com.morgoo.helper.compat.PackageManagerCompat.INSTALL_FAILED_NOT_SUPPORT_ABI;
import static com.morgoo.helper.compat.PackageManagerCompat.INSTALL_SUCCEEDED;

public class TestActivity extends AppCompatActivity {

    private TextView tvTest;
    private File[] plugins;

    private Button btn_bendi;
    private Button btn_list;
    private Button btn_update;


    private TextView tv_progress;
    private ProgressBar progressBar;

    private final int DOWN_UPDATE = 0x2201;
    private final int DOWN_OVER = 0x2202;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        tvTest = (TextView) findViewById(R.id.tv_test);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        progressBar.setMax(100);



        //本地插件安装
        btn_bendi = (Button) findViewById(R.id.btn_bendi);
        btn_bendi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNativePlugin();
            }
        });

        btn_list = (Button) findViewById(R.id.btn_list);
        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestActivity.this,PluginListActivity.class));
            }
        });

        Button btnTest = (Button) findViewById(R.id.btn_test);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ApkBean apkBean = new ApkBean();
                apkBean.setVersionName("1.0");
                apkBean.setVersionCode(1);
                apkBean.setUrl("http://7xqjy9.com1.z0.glb.clouddn.com/apktestapp-v1.apk");

                progressBar.setProgress(0);
                tv_progress.setText("0%");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        downloadApk(TestActivity.this,apkBean);
                    }
                }).start();
            }
        });

        btn_update = (Button) findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ApkBean apkBean = new ApkBean();
                apkBean.setVersionName("2.0");
                apkBean.setVersionCode(2);
                apkBean.setUrl("http://7xqjy9.com1.z0.glb.clouddn.com/apktestapp-v2.apk");

                progressBar.setProgress(0);
                tv_progress.setText("0%");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        downloadApk(TestActivity.this,apkBean);
                    }
                }).start();

            }
        });

    }

    private void loadNativePlugin(){
        //获取插件
        File file = new File(Environment.getExternalStorageDirectory(), "/plugin");
        plugins = file.listFiles();
        //没有插件
        if (plugins == null || plugins.length == 0) {
            return;
        } else {
            //安装第一个插件
            try {
                if (PluginManager.getInstance().isConnected()) {
                    int result = PluginManager.getInstance().installPackage(plugins[0].getAbsolutePath(), PackageManagerCompat.INSTALL_REPLACE_EXISTING);
                    switch (result) {
                        case PluginManager.INSTALL_FAILED_NO_REQUESTEDPERMISSION:
                            Toast.makeText(TestActivity.this, "安装失败，文件请求的权限太多", Toast.LENGTH_SHORT).show();
                            break;
                        case INSTALL_FAILED_NOT_SUPPORT_ABI:
                            Toast.makeText(TestActivity.this, "宿主不支持插件的abi环境，可能宿主运行时为64位，但插件只支持32位", Toast.LENGTH_SHORT).show();
                            break;
                        case INSTALL_SUCCEEDED:
                            Toast.makeText(TestActivity.this, "安装完成", Toast.LENGTH_SHORT).show();
                            tvTest.setText(plugins[0].getAbsolutePath());
                            break;
                    }
                } else {
                    Toast.makeText(TestActivity.this, "插件服务未启动", Toast.LENGTH_SHORT).show();
                    PluginManager.getInstance().addServiceConnection(serviceConnection);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(TestActivity.this, "插件服务启动成功", Toast.LENGTH_SHORT).show();
            try {
                int result = PluginManager.getInstance().installPackage(plugins[0].getAbsolutePath(), PackageManagerCompat.INSTALL_REPLACE_EXISTING);
                switch (result) {
                    case PluginManager.INSTALL_FAILED_NO_REQUESTEDPERMISSION:
                        Toast.makeText(TestActivity.this, "安装失败，文件请求的权限太多", Toast.LENGTH_SHORT).show();
                        break;
                    case INSTALL_FAILED_NOT_SUPPORT_ABI:
                        Toast.makeText(TestActivity.this, "宿主不支持插件的abi环境，可能宿主运行时为64位，但插件只支持32位", Toast.LENGTH_SHORT).show();
                        break;
                    case INSTALL_SUCCEEDED:
                        Toast.makeText(TestActivity.this, "安装完成", Toast.LENGTH_SHORT).show();
                        tvTest.setText(plugins[0].getAbsolutePath());
                        break;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            tvTest.setText(plugins[0].getAbsolutePath());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    private WeakHandler weakHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case DOWN_OVER:
                    String pluginPath = (String) msg.obj;
                    installPlugin(pluginPath);

                    progressBar.setProgress(100);
                    tv_progress.setText("下载完成");
                    break;
                case DOWN_UPDATE:
                    int progress = (int) msg.obj;
                    progressBar.setProgress(progress);
                    tv_progress.setText(progress+"%");
                    break;
            }
            return false;
        }
    });



    private void downloadApk(Context context, ApkBean item) {
        Looper.prepare();
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
                Toast.makeText(TestActivity.this, "没有SdCard", Toast.LENGTH_SHORT).show();
                return ;
            }

            File ApkFile = new File(apkFilePath);
            // 是否已下载更新文件
            if (ApkFile.exists()) {
                installPlugin(apkFilePath);
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

            int progress = 0;
            do {
                int numread = is.read(buf);
                count += numread;
                // 进度条下面显示的当前下载文件大小
                //tmpFileSize = df.format((float) count / 1024 / 1024) + "MB";
                // 当前进度值
                progress = (int) (((float) count / length) * 100);
                // 更新进度
                Message progressMsg = Message.obtain();
                progressMsg.what = DOWN_UPDATE;
                progressMsg.obj = progress;
                weakHandler.sendMessage(progressMsg);
                if (numread <= 0) {
                    // 下载完成 - 将临时下载文件转成APK文件
                    if (tmpFile.renameTo(ApkFile)) {
                        // 通知安装
                        Message message = Message.obtain();
                        message.obj=apkFilePath;
                        message.what = DOWN_OVER;
                        weakHandler.sendMessage(message);
                    }
                    break;
                }
                fos.write(buf, 0, numread);
            }
            while (true);// 点击取消就停止下载//interceptFlag
            fos.close();
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean installPlugin(String pluginPath){
        File ApkFile = new File(pluginPath);
        // 是否已下载更新文件
        if (ApkFile.exists()) {
            try {
                int result = PluginManager.getInstance().installPackage(pluginPath,PackageManagerCompat.INSTALL_REPLACE_EXISTING);
                switch (result) {
                    case PluginManager.INSTALL_FAILED_NO_REQUESTEDPERMISSION:
                        Toast.makeText(TestActivity.this, "安装失败，文件请求的权限太多", Toast.LENGTH_SHORT).show();
                        return false;
                    case INSTALL_FAILED_NOT_SUPPORT_ABI:
                        Toast.makeText(TestActivity.this, "宿主不支持插件的abi环境，可能宿主运行时为64位，但插件只支持32位", Toast.LENGTH_SHORT).show();
                        return false;
                    case INSTALL_SUCCEEDED:
                        Toast.makeText(TestActivity.this, "安装完成", Toast.LENGTH_SHORT).show();
                        return true;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

}
