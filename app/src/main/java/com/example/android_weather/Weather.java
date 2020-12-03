package com.example.android_weather;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Weather extends AppCompatActivity {

    static final int COUNT = 4;
    TextView tvCityName, tvWeatherType, tvCurrentTemperature, tvTemperatureRange, tvDetail;
    List<TextView> tvDayNames = new ArrayList<>(COUNT);
    List<TextView> tvWeaTypes = new ArrayList<>(COUNT);
    List<TextView> tvTemRanges = new ArrayList<>(COUNT);
    String searchCityCode;
    Button concern, refresh;

    private String province;
    private String cityName;
    private String updateTime;
    private String temperature;
    private String fx0;
    private String fl0;
    private String humidity;
    private String type0;
    private final List<String> dayNames = new ArrayList<>();
    private final List<String> weaTypes = new ArrayList<>();
    private final List<String> temRanges = new ArrayList<>();
    MyDBHelper dbHelper;
    int dbCityID;
    String dbLivesData;
    String dbForecastData;
    int sign = 1;
    boolean isConcerned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        tvCityName = findViewById(R.id.city_name);
        tvWeatherType = findViewById(R.id.weather_type);
        tvCurrentTemperature = findViewById(R.id.current_temperature);
        tvTemperatureRange = findViewById(R.id.temperature_range);
        tvDetail = findViewById(R.id.detail);
        concern = findViewById(R.id.concern1);
        refresh = findViewById(R.id.refresh);

        for (int i = 0; i < COUNT; i++) {
            int dayNameId = getResources().getIdentifier("day" + i + "_name", "id", getPackageName());
            tvDayNames.add(findViewById(dayNameId));
            int weaTypeId = getResources().getIdentifier("day" + i + "_wea_type", "id", getPackageName());
            tvWeaTypes.add(findViewById(weaTypeId));
            int temRanId = getResources().getIdentifier("day" + i + "_tem_ran", "id", getPackageName());
            tvTemRanges.add(findViewById(temRanId));
        }


        int dayNameId = getResources().getIdentifier("day_name", "id", getPackageName());
        tvDayNames.add(findViewById(dayNameId));
        int weaTypeId = getResources().getIdentifier("day_wea_type", "id", getPackageName());
        tvWeaTypes.add(findViewById(weaTypeId));
        int temRanId = getResources().getIdentifier("day_tem_ran", "id", getPackageName());
        tvTemRanges.add(findViewById(temRanId));


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        searchCityCode = extras.getString("searchCityCode");
        if (searchCityCode.substring(4).equals("01"))
            searchCityCode = searchCityCode.substring(0, 4) + "00";


        dbHelper = new com.example.android_weather.MyDBHelper(this, "Weather.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();     //同上，获得可写文件
        Cursor cursor0 = db.query("Concern", new String[]{"city_code"}, "city_code=?", new String[]{searchCityCode}, null, null, null);
        if (cursor0.getCount() != 0) {
            isConcerned = true;
            concern.setText("取消关注");
        }
        cursor0.close();
        refresh.setOnClickListener(v -> {
            sign = 3;
            sendRequestWithOkHttp();
            Log.d("MainActivity", "数据库刷新成功");
        });
        concern.setOnClickListener(v -> {
            if (!isConcerned) {
                ContentValues values = new ContentValues();
                values.put("city_code", searchCityCode);
                values.put("city_name", cityName);
                db.insert("Concern", null, values);
                Toast.makeText(Weather.this, "关注成功！", Toast.LENGTH_SHORT).show();
                concern.setText("取消关注");
            } else {
                db.delete("Concern", "city_code=?", new String[]{searchCityCode});
                Toast.makeText(Weather.this, "取消关注成功！", Toast.LENGTH_SHORT).show();
                concern.setText("关注");
            }
            isConcerned = !isConcerned;
        });

        Cursor cursor1 = db.query("Weather", null, "id=?", new String[]{searchCityCode + ""}, null, null, null);
        if (cursor1.moveToNext()) {       //逐行查找，得到匹配信息
            do {
                dbCityID = cursor1.getInt(cursor1.getColumnIndex("id"));
                dbLivesData = cursor1.getString(cursor1.getColumnIndex("Lives_data"));
                dbForecastData = cursor1.getString(cursor1.getColumnIndex("Forecast_data"));
                Log.d("MainActivity", "查询");
            } while (cursor1.moveToNext());
        }
        int cityCode = Integer.parseInt(searchCityCode);

        if (dbCityID == cityCode) {
            sign = 1;
            showResponse(dbLivesData, dbForecastData);
        } else {
            sign = 0;
            sendRequestWithOkHttp();
        }
        cursor1.close();
    }


    private void sendRequestWithOkHttp() {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();  //传入请求地址，并注册一个回调来处理服务器响应
                Request request = new Request.Builder()
                        .url("https://restapi.amap.com/v3/weather/weatherInfo?" + "key=c1956968526e247f1185191f119739c7"
                                + "&city=" + searchCityCode
                                + "&extensions=base"
                                + "&output=JSON")
                        .get()
                        .build();
                Response response = client.newCall(request).execute();
                String responseData = Objects.requireNonNull(response.body()).string();
                Log.d("sss", /*responseData*/responseData);

                Request request2 = new Request.Builder()
                        .url("https://restapi.amap.com/v3/weather/weatherInfo?" + "key=320eeb12b10639dd9175f986fb9874fb"
                                + "&city=" + searchCityCode
                                + "&extensions=all"
                                + "&output=JSON")
                        .get()
                        .build();
                Response response2 = client.newCall(request2).execute();
                String responseData2 = Objects.requireNonNull(response2.body()).string();

                Log.d("sss", responseData);
                Log.d("sss", responseData2);

                showResponse(responseData, responseData2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void parseJSONWithFastJSON(String livesData, String forecastData) {
        Log.d("ppp", livesData);
        Log.d("ppp", forecastData);

        dayNames.clear();
        weaTypes.clear();
        temRanges.clear();
        Log.d("livesData", livesData);
        if (livesData.length() < 100) {
            Log.d("M", "城市ID不存在");
            Toast.makeText(this, "城市ID不存在，请重新输入", Toast.LENGTH_LONG).show();
            // Weather.this.setResult(RESULT_OK, getIntent());
//            Weather.this.finish();
            System.exit(0);
//            Weather.this.onDestroy();
        } else {
            AppLives appLives = JSON.parseObject(livesData, AppLives.class);
            /*App.CityInfo cityInfo = appLives.getCityInfo();
            cityName = cityInfo.getCity();
            updateTime = cityInfo.getUpdateTime();*/

            AppLives.Lives lives = (AppLives.Lives) appLives.getLives().get(0);
            cityName = lives.getCity();
            updateTime = lives.getReporttime();
            temperature = lives.getTemperature();
            type0 = lives.getWeather();
            humidity = lives.getHumidity();
            fx0 = lives.getWinddirection();
            fl0 = lives.getWindpower();
            /*App.data data = appLives.getData();
            App.forecast today = data.getForecast().get(0);
            type0 = today.getType();
            temperature = data.getWendu();*/

            AppForecast appForecast = JSON.parseObject(forecastData, AppForecast.class);
            Log.d("ddd", appForecast.toString());
            List<AppForecast.Forecasts> forecastsList = appForecast.getForecasts();
            AppForecast.Forecasts forecasts = (AppForecast.Forecasts) forecastsList.get(0);
            List<AppForecast.Forecasts.Casts> castsList = forecasts.getCasts();
            for (int i = 0; i < COUNT; i++) {
                dayNames.add("星期" + castsList.get(i).getWeek());
                weaTypes.add(castsList.get(i).getDayweather());
                temRanges.add(castsList.get(i).getNighttemp() + "℃ ~ " + castsList.get(i).getDaytemp() + "℃");
            }
            /*humidity = data.getShidu();
            aqi0 = today.getAqi();
            quality = data.getQuality();*/

            /*sunrise0 = today.getSunrise();
            sunset0 = today.getSunset();

            fl0 = today.getFl();
            fx0 = today.getFx();*/

            /*suggestion = data.getGanmao();
            notice0 = today.getNotice();*/


            /*App.data.yesterday yesterday = data.getYesterday();
            List<App.forecast> forecasts = data.getForecast();

            dayNames.add("昨天");
            weaTypes.add(yesterday.getType());
            temRanges.add(yesterday.getHigh().substring(2) + " ~" + yesterday.getLow().substring(2));
            //今天和未来四天的天气情况
            for (int i = 1; i < COUNT; i++) {
                dayNames.add(forecasts.get(i - 1).getWeek());
                weaTypes.add(forecasts.get(i - 1).getType());
                temRanges.add(forecasts.get(i - 1).getHigh().substring(2) + " ~" + forecasts.get(i - 1).getLow().substring(2));
            }
            dayNames.set(1, "今天");*/

            if (sign == 0) {
                dbHelper = new com.example.android_weather.MyDBHelper(this, "Weather.db", null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("id", searchCityCode);
                values.put("Lives_data", livesData);
                values.put("Forecast_data", forecastData);
                db.insert("Weather", null, values);
                Log.d("MainActivity", "数据库写入成功");
            } else if (sign == 1) {
                Log.d("数据库写入失败：", "数据已存在");

            } else {
                com.example.android_weather.MyDBHelper dbHelper = new com.example.android_weather.MyDBHelper(this, "Weather.db", null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("id", searchCityCode);
                values.put("Lives_data", livesData);
                values.put("Forecast_data", forecastData);
                db.update("Weather", values, "id=?", new String[]{searchCityCode + ""});
                Log.d("MainActivity", "数据库更新成功");

            }

        }
    }


    private void showResponse(String livesData, String forecastData) {
        Log.d("sss", "show");
        runOnUiThread(() -> {
            Log.d("sss", "show");
            parseJSONWithFastJSON(livesData, forecastData);
            String todayDetail;
//            todayDetail = "数据更新时间：" + time + "\n" + "当前状态：" + message + "\n" + "状态号：" + status + "\n" + "当前日期：" + date + "\n" + "当前城市：" + city + "\n" + "城市ID：" + cityId + "\n" + "所在省：" + parent + "\n" + "更新时间" + updateTime;
//            todayDetail = todayDetail + "\n" + "空气湿度：" + shidu + "\n" + "pm10：" + pm10 + "\n" + "pm2.5：" + pm25 + "\n" + "空气质量：" + quality + "\n" + "活动适宜群体：" + ganmao + "\n" + "当前温度" + wendu + "\n";
//            todayDetail = todayDetail + "当前日期：" + ymd0 + "\n" + week0 + "\n" + "日出时间：" + sunrise0 + "\n" + "最高温度：" + high0 + "\n" + "最低温度：" + low0 + "\n" + "日落时间：" + sunset0 + "\n" + "空气指数：" + aqi0 + "\n" + "风力：" + fl0 + "\n" + "风向：" + fx0 + "\n" + "提示：" + notice0 + "\n" + "天气：" + type0;
           /* todayDetail = "湿度：" + humidity + "      空气指数：" + aqi0 + "      空气质量：" + quality + "\n" +
                    "日出时间：" + sunrise0 + "          日落时间：" + sunset0 + "\n" +
                    "风力：" + fl0 + "                       风向：" + fx0 + "\n" +
                    "活动建议：" + suggestion + "\n" + "提示：" + notice0 + "\n" + "更新时间：" + updateTime;*/
            todayDetail = "湿度：" + humidity + "%"+ "   风力：" + fl0 + "    风向：" + fx0 + "\n" + "更新时间：" + updateTime;
            Log.d("sss", todayDetail);

            tvCityName.setText(cityName);
            tvWeatherType.setText(type0);
            tvCurrentTemperature.setText(temperature);
            tvTemperatureRange.setText(temRanges.get(0));
            tvDetail.setText(todayDetail);
            for (int i = 0; i < COUNT; i++) {
                tvDayNames.get(i).setText(dayNames.get(i));
                tvDayNames.get(i).setTextSize(20);
                tvWeaTypes.get(i).setText(weaTypes.get(i));
                tvWeaTypes.get(i).setTextSize(20);
                tvTemRanges.get(i).setText(temRanges.get(i));
                tvTemRanges.get(i).setTextSize(20);
            }
        });
    }


}
