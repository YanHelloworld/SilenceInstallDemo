package com.mudo.silenceinstalldemo;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {


    TextView apkPathText;

    String apkPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apkPathText = (TextView) findViewById(R.id.apkPathText);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0 && resultCode == RESULT_OK){
            apkPath = data.getStringExtra("apk_path");
            apkPathText.setText(apkPath);
        }
    }

    public void onChooseApkFile(View v){
        // TODO 选择安装包
        Intent intent = new Intent(this,FileExplorerActivity.class);
        startActivityForResult(intent, 0);
    }

    public void onSilentInstall(View v){
        // TODO 秒装
        if(!isRoot()){
            Toast.makeText(MainActivity.this,"没有root权限，无法使用秒装",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(apkPath)){
            Toast.makeText(MainActivity.this,"请选择安装包",Toast.LENGTH_SHORT).show();
            return;
        }

        final Button button = (Button)v;
        button.setText("安装中。。。");
        new Thread(new Runnable() {
            @Override
            public void run() {
                SilentInstall installHelper = new SilentInstall();
                final boolean result = installHelper.install(apkPath);

                // 表示下面的运行在UI线程即主线程中。
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(result){
                            Toast.makeText(MainActivity.this,"安装成功",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this,"安装失败",Toast.LENGTH_SHORT).show();
                        }
                        button.setText("秒装");
                    }
                });
            }
        }).start();
    }

    public void onForwardToAccessibility(View v){
        // TODO 开启智能安装服务
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    public void onSmartInstall(View v){
        // TODO 智能安装
        if (TextUtils.isEmpty(apkPath)) {
            Toast.makeText(this, "请选择安装包！", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri = Uri.fromFile(new File(apkPath));
        Intent localIntent = new Intent(Intent.ACTION_VIEW);
        localIntent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(localIntent);
    }

    /**
     * 判断手机是否拥有root权限，
     * @return 有root权限返回true，没有返回false
     */

    public boolean isRoot(){

        boolean bool = false;

        // 该文件底下是否有su命令，如果存在则拥有root权限

        try{
            bool = new File("/system/bin/su").exists() || new File("/system/xbin/su").exists();
        }catch (Exception e){
            System.out.println("获取root权限出错");
        }

        return bool;
    }
}
