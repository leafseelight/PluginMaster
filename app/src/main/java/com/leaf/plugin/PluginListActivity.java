package com.leaf.plugin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leaf.plugin.base.AbsCommonAdapter;
import com.leaf.plugin.base.AbsViewHolder;
import com.leaf.plugin.bean.ApkItem;
import com.leaf.plugin.utils.WeakHandler;
import com.leaf.plugin.widget.AbsListView;
import com.morgoo.droidplugin.pm.PluginManager;

import java.util.ArrayList;
import java.util.List;

public class PluginListActivity extends AppCompatActivity {

    private AbsListView listView;
    private AbsCommonAdapter<ApkItem> mAdapter;

    private WeakHandler weakHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            ArrayList<ApkItem> apkList = (ArrayList<ApkItem>) msg.obj;
            mAdapter.addData(apkList,false);
            return false;
        }
    });

    private LinearLayout linLayoutProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin_list);

        linLayoutProgress = (LinearLayout) findViewById(R.id.linLay);

        listView = (AbsListView) findViewById(R.id.listView);
        mAdapter = new AbsCommonAdapter<ApkItem>(this,R.layout.listitem_plugin) {
            @Override
            public void convert(AbsViewHolder helper, final ApkItem item,final int pos) {
                ImageView iv_icon = helper.getView(R.id.iv_icon);
                TextView tv_name = helper.getView(R.id.tv_name);
                TextView tv_desc = helper.getView(R.id.tv_desc);
                Button btn_manager = helper.getView(R.id.btn_manager);
                Button btn_delete = helper.getView(R.id.btn_delete);

                iv_icon.setImageDrawable(item.icon);
                tv_name.setText(item.title);
                tv_desc.setText("versionName::"+item.versionName+"\nversionCode::"+item.versionCode);


                btn_manager.setText("打开");
                btn_manager.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openApk(item);
                    }
                });

                btn_delete.setText("卸载");
                btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uninstallApk(item,pos);
                    }
                });
            }
        };
        listView.setAdapter(mAdapter);


        weakHandler.post(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                message.obj = getApkFromInstall();
                weakHandler.sendMessage(message);
            }
        });

    }

    // 卸载Apk
    public void uninstallApk(final ApkItem item,final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PluginListActivity.this);
        builder.setTitle("警告");
        builder.setMessage("警告，你确定要卸载" + item.title + "么？");
        builder.setPositiveButton("卸载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!PluginManager.getInstance().isConnected()) {
                    Toast.makeText(PluginListActivity.this, "服务未连接", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        PluginManager.getInstance().deletePackage(item.packageInfo.packageName, 0);
                        Toast.makeText(PluginListActivity.this, "卸载完成", Toast.LENGTH_SHORT).show();
                        mAdapter.remove(position);
                        mAdapter.notifyDataSetChanged();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    // 打开Apk
    public void openApk(final ApkItem item) {
        linLayoutProgress.setVisibility(View.VISIBLE);
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(item.packageInfo.packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // 在安装中获取Apk
    private ArrayList<ApkItem> getApkFromInstall() {
        ArrayList<ApkItem> apkItems = new ArrayList<>();
        try {
            final List<PackageInfo> infos = PluginManager.getInstance().getInstalledPackages(0);
            if (infos == null) {
                return apkItems;
            }
            final PackageManager pm = getPackageManager();
            // noinspection all
            for (PackageInfo info : infos) {
                apkItems.add(new ApkItem(pm, info));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return apkItems;
    }

    @Override
    protected void onResume() {
        super.onResume();
        linLayoutProgress.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
