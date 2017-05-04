package com.sci.wumu.wukong;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class FirstActivity extends AppCompatActivity {


    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private NotificationManager nm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        halfmonth();
        //mayRequestLocation();
        getHomeActivity();

    }

    private static final int REQUEST_FINE_LOCATION=0;
    private void mayRequestLocation() {
        String[] permissionString = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.BLUETOOTH_ADMIN,};
        if (Build.VERSION.SDK_INT >= 23) {
            for(int i=0;i<permissionString.length;i++) {
                int checkCallPhonePermission = ContextCompat.checkSelfPermission(FirstActivity.this, permissionString[i]);
                if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                    //判断是否需要 向用户解释，为什么要申请该权限
                    // if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION))
                    //Toast.makeText(context,R.string.ble_need, 1).show();
                    ActivityCompat.requestPermissions(this ,permissionString,REQUEST_FINE_LOCATION);

                    return;
                }else{

                }
            }
            getHomeActivity();
        } else {
            getHomeActivity();

        }
    }


    private void getHomeActivity() {

        Timer timer = new Timer();

        TimerTask task = new TimerTask() {

            public void run() {

                Intent intent = new Intent(FirstActivity.this, wholeActivity.class);

                startActivity(intent);

                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                finish();

            }

        };

        timer.schedule(task, 2000);
    }

    private void halfmonth() {

        preferences = getSharedPreferences("wukong", Context.MODE_PRIVATE);
        editor = preferences.edit();
        //判断是不是首次登录，
        if (preferences.getBoolean("firststart", true)) {


            //将登录标志位设置为false，下次登录时不在显示首次登录界面
            editor.putBoolean("firststart", false);


            long lasttime = System.currentTimeMillis();
            editor.putLong("lasttime",lasttime);


            editor.commit();
            //Intent intentalarm = new Intent(FirstActivity.this,ALARMRemind.class);
            //PendingIntent pi = PendingIntent.getService(FirstActivity.this,0,intentalarm,0);

            //AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
            //manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
            //       0, 4000, pi);
            //1000*60*60*24*15

        }else
        {

            long currenttime = System.currentTimeMillis();
            long lasttime = preferences.getLong("lasttime",currenttime);
            if(currenttime>=lasttime) {
                long betweentime = currenttime - lasttime;
                long betweendate = (int)(betweentime/1000/3600/24);
                if(betweendate>15)
                {
                    nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    Notification notification = new Notification.Builder(this)
                            .setAutoCancel(true)
                            .setTicker("雾空")
                            .setSmallIcon(R.drawable.logo)
                            .setContentTitle("提醒")
                            .setContentText("请更换过滤网")
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setWhen(System.currentTimeMillis())
                            .build();
                    nm.notify(0x345,notification);
                }
            }

            editor.putLong("lasttime",currenttime);


            editor.commit();

        }
    }

}
