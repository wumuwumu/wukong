package com.sci.wumu.wukong;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.clj.fastble.BleManager;

public class wholeActivity extends AppCompatActivity implements View.OnClickListener {

    private ShopFragment shopFragment =null;
    private EnterFragment enterFragment =null;


    private View enterlayout;
    private  View weblayout;


    private ImageView mainview;
    private  ImageView webview;


    private FragmentManager fragmentmanager;

    private static final int FRAGMENT_SCAN = 0;
    private static final int FRAGMENT_CONNECTED = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    public static BleManager bleManager;
    private BluetoothAdapter mBluetoothAdapter;
    private static final String TAG = "MainActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mayRequestLocation();
        setContentView(R.layout.activity_whole);

        initview();

        //firstLogin();
        fragmentmanager = getSupportFragmentManager();
        // 第一次启动时选中第0个tab
        setTabSelection(0);
        checkBLEFeature();


    }


    private void checkBLEFeature() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        bleManager = new BleManager(this);
        bleManager.enableBluetooth();
    }

    private void initview() {
        mainview=(ImageView)findViewById(R.id.main_image);
        webview=(ImageView)findViewById(R.id.web_image);


        enterlayout = findViewById(R.id.main_layout);
        weblayout = findViewById(R.id.web_layout);


        enterlayout.setOnClickListener(this);
        weblayout.setOnClickListener(this);

    }

    /****
     * 判断选择的选项
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_layout:
                // 当点击了maintab时，选中第主要tab，设置0
                setTabSelection(0);
                break;
            case R.id.web_layout:
                // 当点击了webtab时，设置1
                setTabSelection(1);
                break;
            default:
                break;
        }

    }


    /***
     * 对选中的tab进行设置
     * @param index
     */
    private void setTabSelection(int index) {
        // 每次选中之前先清楚掉上次的选中状态
        clearSelection();

        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentmanager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index) {
            case 0:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                mainview.setImageResource(R.drawable.ic_home_blue_24dp);

                if (enterFragment == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    enterFragment  = new EnterFragment ();
                    transaction.add(R.id.content, enterFragment );
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(enterFragment );
                    // transaction.add(R.id.content, mainfragment);
                }
                break;
            case 1:
                // 当点击了联系人tab时，改变控件的图片和文字颜色
                webview.setImageResource(R.drawable.ic_dashboard_blue_24dp);
                // contactsText.setTextColor(Color.WHITE);
                if (shopFragment == null) {
                    // 如果ContactsFragment为空，则创建一个并添加到界面上
                    shopFragment = new ShopFragment();
                    transaction.add(R.id.content,shopFragment);
                } else {
                    // 如果ContactsFragment不为空，则直接将它显示出来
                    transaction.show(shopFragment);
                }
                break;

        }
        transaction.commit();
    }

    /***
     * 清除所有的设置
     */
    private void clearSelection() {
        mainview.setImageResource(R.drawable.ic_home_black_24dp);
        webview.setImageResource(R.drawable.ic_dashboard_black_24dp);

        //settingImage.setImageResource(R.drawable.setting_unselected);
        // settingText.setTextColor(Color.parseColor("#82858b"));
    }

    /***
     * 隐藏frame
     * @param transaction
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (enterFragment != null) {
            transaction.hide(enterFragment);
        }
        if (shopFragment != null) {
            transaction.hide(shopFragment);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

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
                int checkCallPhonePermission = ContextCompat.checkSelfPermission(wholeActivity.this, permissionString[i]);
                if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                    //判断是否需要 向用户解释，为什么要申请该权限
                    // if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION))
                    //Toast.makeText(context,R.string.ble_need, 1).show();
                    ActivityCompat.requestPermissions(this ,permissionString,REQUEST_FINE_LOCATION);
                    return;
                }else{
                }
            }
        } else {

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // The requested permission is granted.
                } else{
                    // The user disallowed the requested permission.
                }
                break;
        }
    }
}
