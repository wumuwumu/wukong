package com.sci.wumu.wukong;

import android.app.ActionBar;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.clj.fastble.conn.BleCharacterCallback;
import com.clj.fastble.conn.BleGattCallback;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.ListScanCallback;
import com.clj.fastble.utils.HexUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static android.content.ContentValues.TAG;


public class EnterFragment extends Fragment {


    private ViewPager viewPager;
    private ArrayList<View> pageview;



    View view;
    View fragment_gather;
    View fragment_weather;
    private static final UUID ZZR_UUID_BLE_SEVICE = UUID.fromString("0000FFF0-0000-1000-8000-00805f9b34fb");
    private static final UUID ZZR_UUID_BLE_CHAR = UUID.fromString("0000FFF2-0000-1000-8000-00805f9b34fb");
    private static final UUID ZZR_UUID_BLE_CHAR1 = UUID.fromString("0000FFF3-0000-1000-8000-00805f9b34fb");

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();
    private String city = "no";

    TextView location;
    TextView textviewsqi;
    TextView textviewco;
    TextView textviewso2;
    TextView textviewno2;
    TextView textviewo3;
    TextView textviewpm10;
    TextView textviewpm25;
    TextView textviewqlty;
    SeekBar seekBar;

    String no2;
    String aqi;
    int intaqi = 0;
    String co;
    String o3;
    String pm10;
    String pm25;
    String qlty;
    String so2;

    String condcode;

    TextView unit;
    TextView decade;
    TextView hundred;
    TextView thousand;
    TextView myriabit;
    TextView shiwan;
    TextView textblue;
    ImageView dianliangview;

    ImageView weatherzhuangkuang;
    TextView tixing;
    int fengshu = 0;
    int zonggongug = 0;
    int en_fengshu = 0;
    String hexData;

    TextView dengji1;
    TextView dengji2;
    byte[] m_byte;

    ImageButton fenxiang;

    private Timer mTimer = new Timer();
    private TimerTask mtimerTask;
    private Handler mhandler = new Handler();
    private Handler dianyuanshezhi = new Handler();
    private Handler sendinfo = new Handler();

    private Handler threadHandler = new Handler();


    final Runnable dianyuanupdate = new Runnable() {

        @Override
        public void run() {
            //String message = hexData;
            //int len = message.length() / 2;
            //char[] chars = message.toCharArray();
            //String[] hexStr = new String[len];
           // int[] bytes = new int[len];
           // for (int i = 0, j = 0; j < len; i += 2, j++) {
            //    hexStr[j] = "" + chars[i] + chars[i + 1];
            //    bytes[j] = (int) Integer.parseInt(hexStr[j], 16);
            //}
            //unit.setText(m_byte[3]+"");
            byte[] bytes = m_byte;
            dianliangzhi.setText(bytes[3]+"%");
            if (bytes[3] > 90)
                dianliangview.setImageResource(R.drawable.bar10);
            else if (bytes[3] > 80)
                dianliangview.setImageResource(R.drawable.bar9);
            else if (bytes[3] > 70)
                dianliangview.setImageResource(R.drawable.bar8);
            else if (bytes[3] > 60)
                dianliangview.setImageResource(R.drawable.bar7);
            else if (bytes[3] > 50)
                dianliangview.setImageResource(R.drawable.bar6);
            else if (bytes[3] > 40)
                dianliangview.setImageResource(R.drawable.bar5);
            else if (bytes[3] > 30)
                dianliangview.setImageResource(R.drawable.bar4);
            else if (bytes[3] > 20)
                dianliangview.setImageResource(R.drawable.bar3);
            else if (bytes[3] > 10)
                dianliangview.setImageResource(R.drawable.bar3);
            else if (bytes[3] > 0)
                dianliangview.setImageResource(R.drawable.bar2);
            else if (bytes[3] == 0)
                dianliangview.setImageResource(R.drawable.bar1);

        }
    };

    final  Runnable  runnablesendinfo = new Runnable() {
        @Override
        public void run() {
            String str;
            if(fengshu<16)
            {
                str= "FE"+"08"+"0"+Integer.toHexString(leden) +"0"+Integer.toHexString(fengshu)+
                        "00"+"00"+"00"+"00"+"00"+"00"+"00"+"02"+"EF";
            }else
            {
                str = "FE"+"08"+"0"+ Integer.toHexString(leden)+Integer.toHexString(fengshu)+
                        "00"+"00"+"00"+"00"+"00"+"00"+"00"+"02"+"EF";
            }


            startWrite(ZZR_UUID_BLE_SEVICE.toString(), ZZR_UUID_BLE_CHAR.toString(), str);

            fengsutext.setText(fengshu+"%");

        }
    };


    private final String lockName = "wuko";

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private ImageView imageViewblu;
    private String mac;

    int leden = 0;
    Switch switchled;

    TextView fengsutext;
    TextView dianliangzhi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_enter, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.double_viewpager);

        fragment_gather = inflater.inflate(R.layout.fragment_gather, null);
        fragment_weather = inflater.inflate(R.layout.fragment_weather, null);



        pageview = new ArrayList<View>();
        //添加想要切换的界面
        pageview.add(fragment_gather);
        pageview.add(fragment_weather);
        //数据适配器
        PagerAdapter mPagerAdapter = new PagerAdapter() {

            @Override
            public int getCount() {
                return pageview.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ((ViewPager) container).addView(pageview.get(position));
                return pageview.get(position);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public int getItemPosition(Object object) {
                return super.getItemPosition(object);
            }
        };

        //绑定适配器
        viewPager.setAdapter(mPagerAdapter);
        //设置viewPager的初始界面为第一个界面
        viewPager.setCurrentItem(0);

        init();
        location();

        BluetoothManager bluetoothManager = (BluetoothManager) getActivity()
                .getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();


        return view;

    }

    private void init() {
        textviewqlty = (TextView) fragment_weather.findViewById(R.id.qlty);
        location = (TextView) fragment_weather.findViewById(R.id.location);
        textviewsqi = (TextView) fragment_weather.findViewById(R.id.sqi);
        textviewpm25 = (TextView) fragment_weather.findViewById(R.id.pm25);
        textviewpm10 = (TextView) fragment_weather.findViewById(R.id.pm10);
        textviewso2 = (TextView) fragment_weather.findViewById(R.id.textso2);
        textviewco = (TextView) fragment_weather.findViewById(R.id.co);
        textviewo3 = (TextView) fragment_weather.findViewById(R.id.o3);
        textviewno2 = (TextView) fragment_weather.findViewById(R.id.no2);
        dengji1 = (TextView) fragment_weather.findViewById(R.id.dengji1);
        dengji2 = (TextView) fragment_weather.findViewById(R.id.dengji2);
        dianliangview = (ImageView) fragment_weather.findViewById(R.id.dianliangview);
        imageViewblu = (ImageView) fragment_gather.findViewById(R.id.imageViewblu);
        textblue = (TextView) fragment_gather.findViewById(R.id.bluetext);
        seekBar = (SeekBar) fragment_weather.findViewById(R.id.seekbar_wea);
        fengsutext = (TextView)fragment_weather.findViewById(R.id.fengsuzhi) ;
        switchled = (Switch)fragment_weather.findViewById(R.id.switchled);
        weatherzhuangkuang = (ImageView)fragment_weather.findViewById(R.id.tianqi);
        dianliangzhi = (TextView)fragment_weather.findViewById(R.id.dianliangzhi);
        tixing = (TextView)fragment_gather.findViewById(R.id.tixing);
        unit = (TextView) fragment_gather.findViewById(R.id.unit);
        decade = (TextView) fragment_gather.findViewById(R.id.decade);
        hundred = (TextView) fragment_gather.findViewById(R.id.hundred);
        thousand = (TextView) fragment_gather.findViewById(R.id.thousand);
        myriabit = (TextView) fragment_gather.findViewById(R.id.myriabit);
        shiwan = (TextView) fragment_gather.findViewById(R.id.shiwan);
        fenxiang = (ImageButton)fragment_gather.findViewById(R.id.tuisong);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                fengshu = i;
//                String dexi = Integer.toHexString(i);
//                int total = i + Integer.parseInt("FF", 16) + Integer.parseInt("FF", 16)
//                        + Integer.parseInt("FF", 16) + Integer.parseInt("FF", 16)
//                        + Integer.parseInt("FF", 16) + Integer.parseInt("FF", 16)
//                        + Integer.parseInt("FF", 16);
//                String str;
//                if(i<16)
//                {
//                    str= "FE"+"08"+"0"+Integer.toHexString(leden) +"0"+Integer.toHexString(i)+
//                            "00"+"00"+"00"+"00"+"00"+"00"+"00"+"02"+"EF";
//                }else
//                {
//                    str = "FE"+"08"+"0"+ Integer.toHexString(leden)+Integer.toHexString(i)+
//                            "00"+"00"+"00"+"00"+"00"+"00"+"00"+"02"+"EF";
//                }
//
//
//                startWrite(ZZR_UUID_BLE_SEVICE.toString(), ZZR_UUID_BLE_CHAR.toString(), str);
//
//                fengsutext.setText(fengshu+"%");
                //sendinfo.post(runnablesendinfo);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String dexi = Integer.toHexString(fengshu);
                int total = fengshu + Integer.parseInt("FF", 16) + Integer.parseInt("FF", 16)
                        + Integer.parseInt("FF", 16) + Integer.parseInt("FF", 16)
                        + Integer.parseInt("FF", 16) + Integer.parseInt("FF", 16)
                        + Integer.parseInt("FF", 16);
                String str;
                if(fengshu<16)
                {
                    str= "FE"+"08"+"0"+ Integer.toHexString(leden)+"0"+Integer.toHexString(fengshu)+
                            "00"+"00"+"00"+"00"+"00"+"00"+"00"+"02"+"EF";
                }else
                {
                    str = "FE"+"08"+"0"+ Integer.toHexString(leden) +Integer.toHexString(fengshu)+
                            "00"+"00"+"00"+"00"+"00"+"00"+"00"+"02"+"EF";
                }


                startWrite(ZZR_UUID_BLE_SEVICE.toString(), ZZR_UUID_BLE_CHAR.toString(), str);

                fengsutext.setText(fengshu+"%");
               // sendinfo.post(runnablesendinfo);

            }
        });

        fenxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                String fenxiangwenzi = "使用口罩过滤的微小尘埃为"+zonggongug+"ug";
                intent.putExtra(Intent.EXTRA_TEXT, fenxiangwenzi);
                intent.setType("text/plain");
                //设置分享列表的标题，并且每次都显示分享列表
                startActivity(Intent.createChooser(intent, "分享到"));

            }
        });

        switchled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    leden = 1;
                    String str;
                    if (fengshu < 16) {
                        str = "FE" + "08" + "0" + Integer.toHexString(leden) + "0" + Integer.toHexString(fengshu) +
                                "00" + "00" + "00" + "00" + "00" + "00" + "00" + "02" + "EF";
                    } else {
                        str = "FE" + "08" + "0" + Integer.toHexString(leden) + Integer.toHexString(fengshu) +
                                "00" + "00" + "00" + "00" + "00" + "00" + "00" + "02" + "EF";
                    }


                    startWrite(ZZR_UUID_BLE_SEVICE.toString(), ZZR_UUID_BLE_CHAR.toString(), str);

                    //fengsutext.setText(fengshu + "%");
                }
                else
                {
                    leden =0;
                    String str;
                    if(fengshu<16)
                    {
                        str= "FE"+"08"+"0"+ Integer.toHexString(leden)+"0"+Integer.toHexString(fengshu)+
                                "00"+"00"+"00"+"00"+"00"+"00"+"00"+"02"+"EF";
                    }else
                    {
                        str = "FE"+"08"+"0"+ Integer.toHexString(leden) +Integer.toHexString(fengshu)+
                                "00"+"00"+"00"+"00"+"00"+"00"+"00"+"02"+"EF";
                    }


                    startWrite(ZZR_UUID_BLE_SEVICE.toString(), ZZR_UUID_BLE_CHAR.toString(), str);

                    //fengsutext.setText(fengshu+"%");
                }
            }
        });

        mtimerTask = new TimerTask() {
            @Override
            public void run() {
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        sync();
                    }
                });
            }
        };

        mTimer.schedule(mtimerTask, 0, 1000);

    }

    private void sync() {
        zonggongug += 0.06 * intaqi * fengshu * 0.01 * 0.9 * en_fengshu * 100;
        int sum = zonggongug;
        int key = 1;
        while (sum > 0) {
            switch (key) {
                case 1:
                    unit.setText(sum % 10 + "");
                    sum = (int) sum / 10;
                    key++;
                    break;
                case 2:
                    decade.setText(sum % 10 + "");
                    sum = (int) sum / 10;
                    key++;
                    break;
                case 3:
                    hundred.setText(sum % 10 + "");
                    sum = (int) sum / 10;
                    key++;
                    break;
                case 4:
                    thousand.setText(sum % 10 + "");
                    sum = (int) sum / 10;
                    key++;
                    break;
                case 5:
                    myriabit.setText(sum % 10 + "");
                    sum = (int) sum / 10;
                    key++;
                    break;
                case 6:
                    shiwan.setText(sum % 10 + "");
                    sum = (int) sum / 10;
                    key++;
                    break;
                default:
                    sum = 0;
            }
        }

    }

    private void location() {
        //初始化client
        locationClient = new AMapLocationClient(getActivity().getApplicationContext());
        //设置定位参数
        locationClient.setLocationOption(getDefaultOption());
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
        new Thread() {

            @Override
            public void run() {
                startLocation();

            }
        }.start();
    }

    private void pmsearch() {
        new Thread() {

            public void run() {
                String hpm25 = city.substring(0, city.length() - 1);
                String path25 = "https://free-api.heweather.com/v5/weather?city="
                        + hpm25
                        + "&key=045fc8d913a14f2c9872d4139946135d";

                String result = "java";
                try {
                    result = readJsonFromUrl(path25);
                    if (!result.equals("error")) {

                        JSONAnalysis(result);
                    } else {
                        Toast.makeText(getActivity(), "获取数据失败", Toast.LENGTH_SHORT)
                                .show();
                    }

                } catch (Exception e) {

                    e.printStackTrace();
                    /**
                     * 如果获取失败，或出现异常，那么子线程发送失败的消息（FAILURE）到主线程，主线程显示Toast，来告诉使用者，数据获取是失败。
                     */
                    Toast.makeText(getActivity(), "获取失败", Toast.LENGTH_SHORT)
                            .show();
                }
                //location.setText(result);

            }
        }.start();


    }

    final Runnable updateRunnable = new Runnable() {
        public void run() {
            textviewno2.setText(no2);
            textviewo3.setText(o3);
            textviewco.setText(co);
            textviewpm25.setText(pm25);
            textviewpm10.setText(pm10);
            textviewqlty.setText(qlty);
            textviewsqi.setText(aqi);
            textviewso2.setText(so2);

            int weiht = Integer.parseInt(aqi);
            intaqi = weiht;
            dengji1.setLayoutParams(new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, weiht));
            dengji2.setLayoutParams(new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, 500 - weiht));
            if (weiht >100 )
                tixing.setText("空气污染，请戴口罩");

           int id_name = getActivity().getResources().getIdentifier(
                   "m"+condcode,"drawable",getActivity().getPackageName());
//            int id_name = getActivity().getResources().getIdentifier(
//                    "m100.png","drawable","com.sci.wumu.wukong");
            //location.setText(id_name+"m"+condcode+".png");
            weatherzhuangkuang.setImageResource(id_name);

        }
    };

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static String readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);

            return jsonText;
        } catch (Exception e) {
            return "error";
        } finally {
            is.close();
            // System.out.println("同时 从这里也能看出 即便return了，仍然会执行finally的！");
        }
    }

    /**
     * JSON解析方法
     */
    private void JSONAnalysis(String string) throws JSONException {
        //JSONObject object = null;

        try {
            //object = new JSONObject(string);
            JSONObject jsonobject1 = new JSONObject(string);
            // Log.d("mysis",jsonobject1.toString());

            //将jsonArray字符串转化为JSONArray

            //取出数组第一个array
            JSONArray HeWeather5 = jsonobject1.getJSONArray("HeWeather5");
            JSONObject jsonobject2 = HeWeather5.getJSONObject(0);
            JSONObject cityaqi = jsonobject2.getJSONObject("aqi");
            JSONObject jsoncity = cityaqi.getJSONObject("city");


            aqi = jsoncity.getString("aqi");
            co = jsoncity.getString("co");
            no2 = jsoncity.getString("no2");
            o3 = jsoncity.getString("o3");
            pm10 = jsoncity.getString("pm10");
            pm25 = jsoncity.getString("pm25");
            qlty = jsoncity.getString("qlty");
            so2 = jsoncity.getString("so2");

            //天气情况查询
            JSONObject nowweather = jsonobject2.getJSONObject("now");
            JSONObject cond = nowweather.getJSONObject("cond");

            condcode = cond.getString("code");

            threadHandler.post(updateRunnable);

        } catch (JSONException e) {
            //e.printStackTrace();
            Toast.makeText(getActivity(), "解析错误",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation loc) {
            if (null != loc) {
                //解析定位结果
                city = loc.getCity();
                String citycode = loc.getCityCode();
                location.setText(city);
                if(!city.isEmpty())
                {
                    pmsearch();
                    stopLocation();
                }

            } else {
                Toast.makeText(getActivity(), "定位失败，请检查网络",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };


    /**
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void startLocation() {

        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }


    /**
     * 停止定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void stopLocation() {
        // 停止定位
        locationClient.stopLocation();
    }

    /**
     * 销毁定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void destroyLocation() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyLocation();

    }

    @Override
    public void onResume() {

        super.onResume();
        scanDevice();


    }

    @Override
    public void onStop() {
        super.onStop();


    }

    /**
     * 搜索周围蓝牙设备
     */
    private void scanDevice() {
        if (wholeActivity.bleManager.isInScanning())
            return;

        wholeActivity.bleManager.scanDevice(new ListScanCallback(3000) {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                super.onLeScan(device, rssi, scanRecord);
                Log.i(TAG, "发现设备：" + device.getName());
            }

            @Override
            public void onDeviceFound(BluetoothDevice[] devices) {
               // location.setText(devices.length+"");
                for (int i = 0; devices != null && i < devices.length; i++) {
                    //location.setText(devices[i].getAddress());
                    if ((devices[i].getName()).equals(lockName)) {
                       // location.setText(devices[i].getAddress());
                        connectSpecialDevice(devices[i]);

                    }
                }

            }

        });
    }

    /**
     * 连接设备
     */
    private void connectSpecialDevice(final BluetoothDevice device) {

        wholeActivity.bleManager.connectDevice(device, true, new BleGattCallback() {
            @Override
            public void onNotFoundDevice() {
                //scanDevice();
                Toast.makeText(getActivity(), "onNotFoundDevice", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectSuccess(final BluetoothGatt gatt, int status) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       // location.setText(gatt.getDevice().getAddress());
                        mac = gatt.getDevice().getAddress();
                        en_fengshu = 1;
                        imageViewblu.setImageResource(R.drawable.blueen);
                        textblue.setText("连接中");
                    }
                });
                gatt.discoverServices();
            }

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, final int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (newState)
                        {
                            case BluetoothGatt.STATE_DISCONNECTED:
                                imageViewblu.setImageResource(R.drawable.bluedis);
                                textblue.setText("未连接");
                                en_fengshu = 0;
                                break;
                            case BluetoothGatt.STATE_DISCONNECTING:
                                //imageViewblu.setImageResource(R.drawable.bluedis);
                                //textblue.setText("未连接");
                               // en_fengshu = 0;
                                break;
                            default:
                                break;
                        }
                    }
                });


            }

            @Override
            public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
                Log.i(TAG, "onServicesDiscovered() - " + gatt.getDevice());
                //查找使用的uuid
                initConnect(device.getName(), gatt);

            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
            }

            @Override
            public void onConnectFailure(BleException exception) {
                Toast.makeText(getActivity(), "ConnectFailure", Toast.LENGTH_LONG).show();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        imageViewblu.setImageResource(R.drawable.bluedis);
                        textblue.setText("未连接");
                        en_fengshu = 0;

                    }
                });
                wholeActivity.bleManager.handleException(exception);

            }


        });
    }

    private void initConnect(String deviceName, BluetoothGatt gatt){
        wholeActivity.bleManager.getBluetoothState();
        if (gatt != null) {
            for (final BluetoothGattService service : gatt.getServices()) {
                for (final BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    ////识别uuid
                    if(characteristic.getUuid().equals(ZZR_UUID_BLE_CHAR1)){
                        //开启接收notify
                        startNotify(service.getUuid().toString(), characteristic.getUuid().toString());

                    }
                }
            }
        }
    }
    private void startNotify(String serviceUUID, final String characterUUID) {
        Log.i(TAG, "startNotify");
        boolean suc = wholeActivity.bleManager.notify(
                serviceUUID,
                characterUUID,
                new BleCharacterCallback() {
                    @Override
                    public void onSuccess(final BluetoothGattCharacteristic characteristic) {
                        Log.d(TAG, "notify success： " + '\n' + String.valueOf(HexUtil.encodeHex(characteristic.getValue())));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //接收到应答
                                //byte[] data
                                 m_byte =characteristic.getValue();
                                if(m_byte.length == 12) {
                                    //m_byte = data;
                                    //location.setText("he"+m_byte[3]);
                                    dianyuanshezhi.post(dianyuanupdate);
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(BleException exception) {
                        wholeActivity.bleManager.handleException(exception);
                    }
                });

    }



    private void startWrite(String serviceUUID, final String characterUUID, String writeData) {
        Log.i(TAG, "startWrite");
        boolean suc = wholeActivity.bleManager.writeDevice(
                serviceUUID,
                characterUUID,
                HexUtil.hexStringToBytes(writeData),
                new BleCharacterCallback() {
                    @Override
                    public void onSuccess(final BluetoothGattCharacteristic characteristic) {
                        Log.d(TAG, "write success: " + '\n' + String.valueOf(HexUtil.encodeHex(characteristic.getValue())));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                    }

                    @Override
                    public void onFailure(BleException exception) {
                        wholeActivity.bleManager.handleException(exception);
                    }
                });

        if (suc) {
            wholeActivity.bleManager.stopListenCharacterCallback(ZZR_UUID_BLE_CHAR1.toString());
            startNotify(serviceUUID.toString(), ZZR_UUID_BLE_CHAR1.toString());
        }


    }
}










