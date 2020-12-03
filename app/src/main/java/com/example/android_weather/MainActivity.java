package com.example.android_weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final List<Integer> idList = new ArrayList<>();
    //private final List<Integer> pidList = new ArrayList<>();
    private final List<String> city_nameList = new ArrayList<>();
    private final List<String> city_codeList = new ArrayList<>();
    private final List<String> code_List = new ArrayList<>();
    ArrayAdapter<String> simpleAdapter;
    Button OK, MyConcern;
    EditText etSearch;
    ListView ProvinceList;

    private void parseJSONWithJSONObject(String jsonData, String code) { //将字符串转化为JSONObject对象 //4

        try {
            Log.d("aaa", city_codeList.toString());
            Log.d("aaa", code_List.toString());
            Log.d("aaa", city_nameList.toString());

            idList.clear();
            //pidList.clear();
            code_List.clear();
            city_nameList.clear();
            city_codeList.clear();
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 1; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                /*int id = jsonObject.getInt("id");
                int pid = jsonObject.getInt("pid");
                String city_code = jsonObject.getString("city_code");
                String city_name = jsonObject.getString("city_name");*/ //2
                String city_code = jsonObject.getString("citycode");
                String city_adcode = jsonObject.getString("adcode");
                String city_name = jsonObject.getString("中文名");
                /*if (city_code.equals(targetCityCode)) { */
                if (code.equals(city_adcode.substring(2, 6))) {
                    //pidList.add(pid);
                    code_List.add(city_code);
                    city_codeList.add(city_adcode);
                    city_nameList.add(city_name);
                    Log.d("aaa1", city_codeList.toString());
                    Log.d("aaa1", code_List.toString());
                    Log.d("aaa1", city_nameList.toString());
                }

                /*if("000".equals(city_adcode.substring(3,6))) {
                    city_codeList.add(city_adcode);
                    city_nameList.add(city_name);
                    code_List.add(city_code);
                }*/
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseJSONWithJSONObject_Province(String jsonData, String targetAdcode) { //将字符串转化为JSONObject对象 //4

        try {
            //pidList.clear();
            Log.d("aaa2", city_codeList.toString());
            Log.d("aaa2", code_List.toString());
            Log.d("aaa2", city_nameList.toString());
            code_List.clear();
            city_nameList.clear();
            city_codeList.clear();
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String city_code = jsonObject.getString("citycode");
                String city_adcode = jsonObject.getString("adcode");
                String city_name = jsonObject.getString("中文名");
                /*if(((city_adcode.substring(0,3)).equals("110")||(city_adcode.substring(0,3)).equals("120")||
                        (city_adcode.substring(0,3)).equals("310")||(city_adcode.substring(0,3)).equals("500"))&&!city_adcode.substring(5,6).equals("0")){
                    code_List.add(city_code);
                    city_codeList.add(city_adcode);
                    city_nameList.add(city_name);
                }*/

//                if (((city_adcode.substring(4, 6).equals(targetAdcode.substring(4, 6))) && (city_adcode.substring(0, 2).equals(targetAdcode.substring(0, 2))) && (!city_code.equals("")))
//                        || (("11".equals(city_adcode.substring(0, 2)) || "12".equals(city_adcode.substring(0, 2)) || "31".equals(city_adcode.substring(0, 2)) || "50".equals(city_adcode.substring(0, 2))) &&
//                        (city_adcode.substring(0, 2).equals(targetAdcode.substring(0, 2))))) {
                String head=city_adcode.substring(0, 2);
                if ((((city_adcode.substring(4, 6).equals(targetAdcode.substring(4, 6))) || ("11".equals(head) || "12".equals(head) || "31".equals(head) || "50".equals(head)||"81".equals(head)||"82".equals(head)))
                        && (head.equals(targetAdcode.substring(0, 2))) && (!city_code.equals("")))
                ) {
                    //pidList.add(pid);
                    code_List.add(city_code);
                    city_codeList.add(city_adcode);
                    city_nameList.add(city_name);
                    Log.d("aaa3", city_codeList.toString());
                    Log.d("aaa3", code_List.toString());
                    Log.d("aaa3", city_nameList.toString());
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseJSONWithJSONObject_City(String jsonData, String targetCode, String targetAdcode) { //将字符串转化为JSONObject对象 //4

        try {
            Log.d("aaa4", city_codeList.toString());
            Log.d("aaa4", code_List.toString());
            Log.d("aaa4", city_nameList.toString());
            code_List.clear();
            city_nameList.clear();
            city_codeList.clear();
            Log.d("aaa4", city_nameList.toString());
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String city_code = jsonObject.getString("citycode");
                String city_adcode = jsonObject.getString("adcode");
                String city_name = jsonObject.getString("中文名");
                if ((city_code.equals(targetCode)) && (!city_adcode.substring(4, 6).equals(targetAdcode.substring(4, 6))) /*&& (!"市辖区".equals(city_name.substring(city_name.length() - 3)))*//*(!code.substring(0,2).equals(targetAdcode.substring(4,6)))*/) {
                    //pidList.add(pid);
                    code_List.add(city_code);
                    city_codeList.add(city_adcode);
                    city_nameList.add(city_name.replace("市辖区",""));
                    Log.d("aaa5", city_codeList.toString());
                    Log.d("aaa5", code_List.toString());
                    Log.d("aaa5", city_nameList.toString());
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getJson(Context context) {
        StringBuilder stringBuilder = new StringBuilder(); //创建StringBuilder对象，存储信息
        try {
            InputStream is = context.getResources().openRawResource(R.raw.cities); //获取资源的数据流，读取资源数据1
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is)); //从字符输入流中读取文本，缓冲字符，以便有效地读取字符，数组和行。
            String line;
            while ((line = bufferedReader.readLine()) != null) { //读取一行数据
                stringBuilder.append(line); //为stringBuilder添加数据
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OK = findViewById(R.id.ok);
        etSearch = findViewById(R.id.search);
        ProvinceList = findViewById(R.id.provincelist);
        OK.setOnClickListener(this);
        MyConcern = findViewById(R.id.concern_list);
        MyConcern.setOnClickListener(this);

        String responseData = getJson(this);
        /*parseJSONWithJSONObject(responseData, 0);*/ //5
        parseJSONWithJSONObject(responseData, "0000");
        simpleAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, city_nameList);

        ProvinceList.setAdapter(simpleAdapter); //添加适配器
        ProvinceList = findViewById(R.id.provincelist);
        //配置ArrayList点击按钮
        ProvinceList.setOnItemClickListener((parent, view, position, id) -> {
            //int tran_id = idList.get(position);
            String tran_city_adcode = city_codeList.get(position);
            String tran_city_name = city_nameList.get(position);
            String tran_code = code_List.get(position);
            if ("0000".equals(tran_city_adcode.substring(2, 6))) {
                /*parseJSONWithJSONObject(responseData, tran_id*//*hierarchy++*//*);*/
                parseJSONWithJSONObject_Province(responseData, tran_city_adcode);
                Toast.makeText(this, tran_city_name /*+ "-" + tran_city_adcode + "-" + tran_code*/, Toast.LENGTH_SHORT).show();
                ProvinceList.setAdapter(simpleAdapter);
                Button buttonBack = findViewById(R.id.back);
                buttonBack.setVisibility(View.VISIBLE);
                buttonBack.setOnClickListener(v -> {
                    /*parseJSONWithJSONObject(responseData, 0*//*hierarchy++*//*);*/
                    parseJSONWithJSONObject(responseData, "0000");
                    ProvinceList.setAdapter(simpleAdapter);
                    buttonBack.setVisibility(View.INVISIBLE);
                });
            } else if (tran_code.length() == 4 && "00".equals(tran_city_adcode.substring(4, 6))) {
                /*parseJSONWithJSONObject(responseData, tran_id*//*hierarchy++*//*);*/
                parseJSONWithJSONObject_City(responseData, tran_code, tran_city_adcode);
                Toast.makeText(this, tran_city_name, Toast.LENGTH_SHORT).show();
                ProvinceList.setAdapter(simpleAdapter);
                Button buttonBack = findViewById(R.id.back);
                buttonBack.setVisibility(View.VISIBLE);
                buttonBack.setOnClickListener(v -> {
                    /*parseJSONWithJSONObject(responseData, 0*//*hierarchy++*//*);*/
                    parseJSONWithJSONObject(responseData, "0000");
                    ProvinceList.setAdapter(simpleAdapter);
                    buttonBack.setVisibility(View.INVISIBLE);
                });
            } else {
                Intent intent = new Intent(MainActivity.this, com.example.android_weather.Weather.class);
                intent.putExtra("searchCityCode", tran_city_adcode);
                startActivity(intent);
            }
        });


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok:
                String searchCityCode = String.valueOf(etSearch.getText());
                if (searchCityCode.length() > 6) {
                    Toast.makeText(this, "请输入六位城市ID号！", Toast.LENGTH_LONG).show();
                }
                else if(searchCityCode.length() < 6){
                    Toast.makeText(this, "请输入六位城市ID号！", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(MainActivity.this, com.example.android_weather.Weather.class);
                    intent.putExtra("searchCityCode", searchCityCode);
                    startActivity(intent);
                }
                break;
            case R.id.concern_list:
                Intent intent = new Intent(MainActivity.this, com.example.android_weather.MyConcernList.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }
}