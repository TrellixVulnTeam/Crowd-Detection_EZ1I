package com.scw.bluetoothdiscover;

import android.content.Context;
import android.location.Location;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by SCW on 2018/10/22.
 */
public class HttpRequest {
    private String mUrl;
    OkHttpClient client;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Location userLocation;
    static String serverResponse = "";
    String time = "2019-01-25 12:05:00";

    public HttpRequest(Context context, String url) {
        this.mUrl = url;
        client = new OkHttpClient.Builder()
                .build();

    }

    public void doGetRequest() {
        Log.d("OKHTTP3", "DO GET");

        Request request = new Request.Builder()
                .url("http://192.168.0.3:5000/get_device")
                .build();

        //Synchronous
        Log.d("OKHTTP3", "DO SY");
        try {
            Response response = client.newCall(request).execute();
            final String myResponse = response.body().string();
            System.out.println("this is a test for get " + myResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String doPostData(JSONArray deviceJson, float lati, float longti, String username) {
        // Perpare response checking


        // Get location
        //userLocation = location;
        // Get time
        Date date = new Date(System.currentTimeMillis());
        // Generate JSON
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("username", username);
            jsonData.put("date", simpleDateFormat.format(date));
            jsonData.put("lat", lati);//userLocation.getLatitude()
            jsonData.put("long", longti);//userLocation.getLongitude()
            jsonData.put("device", deviceJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Set HTTP
        /*
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                sendData("/post_data", JSON, jsonData);
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
        return sendData("/post_data", JSON, jsonData);
        //return serverResponse;
    }

    // Login
    public String doPostLogin(String username, String password) {
        // Perpare response checking


        // Generate JSON
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("username", username);
            jsonData.put("password", password);

        } catch (JSONException e) {
            Log.d("OKHTTP3", "JSON EXCEPTION");
            e.printStackTrace();
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                sendData("/login", JSON, jsonData);
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //return serverResponse;
        return sendData("/login", JSON, jsonData);


    }


    //  Register
    public String doPostRegister(String username, String password, String email) {
        // Perpare response checking
        //String serverResponse = "nothing";

        // Generate JSON
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("username", username);
            jsonData.put("password", password);
            jsonData.put("email", email);

        } catch (JSONException e) {
            Log.d("OKHTTP3", "JSON EXCEPTION");
            e.printStackTrace();
        }

        // Set HTTP
        String goal_locate = "/register";
        RequestBody body = RequestBody.create(JSON, jsonData.toString());

        Request request = new Request.Builder()
                .url(mUrl + goal_locate)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            serverResponse = response.body().string();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverResponse;
    }

    public String doPostQuery() {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Date date = new Date(System.currentTimeMillis());
        JSONObject jsonData = new JSONObject();
        // Test past time
        /*
        try {


            Date oldTime = simpleDateFormat.parse(time);
            System.out.println(time);
            Date addTime = new Date(oldTime.getTime() + 11000);
            time = simpleDateFormat.format(addTime);
            System.out.println(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        */
        time = simpleDateFormat.format(date);
        System.out.println("Test: " + time);
        try {
            jsonData.put("date", time);
            //jsonData.put("date", simpleDateFormat.format(date));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*
        RequestBody body = RequestBody.create(JSON, jsonData.toString());
        Request request = new Request.Builder()
                .url(mUrl + "/post_query")
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                serverResponse = response.body().string();
                //System.out.println("Test response " + response.body().string());
            } else {
                System.out.println("Test web fail");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        // Test
        /*
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                sendData("/post_query", JSON, jsonData);
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
        //return serverResponse;
        return sendData("/post_query", JSON, jsonData);
        //return sendData("/post_query", JSON, jsonData);
    }

    void sendPhoto(byte[] photoArray, String message) {
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                //.addFormDataPart("photo", path.getName(),RequestBody.create(MediaType.parse("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),path))
                .addFormDataPart("photo", "photo.png", RequestBody.create(MediaType.parse("image/png"), photoArray))
                .addFormDataPart("infomessage", message)
                .build();

        Request request = new Request.Builder()
                .url(mUrl + "/post_photo")
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    serverResponse = response.body().string();
                    System.out.println("Test response " + serverResponse);
                } else {
                    serverResponse = "Test web fail";
                    System.out.println("Test web fail");
                }
            }
        });
    }

    private String sendData(String urlGoal, MediaType JSON, JSONObject jsonData) {
        //RequestBody body = RequestBody.create(JSON, jsonData.toString());
        Request request = new Request.Builder()
                .url(mUrl + urlGoal)
                .post(RequestBody.create(JSON, jsonData.toString()))
                .build();

        try {
            //System.out.println("Test " + jsonData.toString());
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                serverResponse = response.body().string();
                System.out.println("Test response " + serverResponse);
            } else {
                serverResponse = "Test web fail";
                System.out.println("Test web fail");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    serverResponse = response.body().string();
                    System.out.println("Test response " + serverResponse);
                } else {
                    serverResponse = "Test web fail";
                    System.out.println("Test web fail");
                }
            }
        });
        */
        return serverResponse;

    }

    // offline
    public String store(JSONArray deviceJson, Location location) throws JSONException {
        String data = "";
        System.out.println(deviceJson.toString());
        Date date = new Date(System.currentTimeMillis());
        // Time
        data += "#" + simpleDateFormat.format(date) + "\n";
        // Location
        data += Double.toString(location.getLatitude()) + "\n";
        data += Double.toString(location.getLongitude()) + "\n";
        for (int i = 0; i < deviceJson.length(); i++) {
            JSONObject jsonObject = deviceJson.getJSONObject(i);
            int rssi = jsonObject.getInt("rssi");
            String mac_address = jsonObject.getString("address");
            data += mac_address + " " + rssi + "\n";
        }
        return data;

    }

    private void getJson(String response) throws IOException {
        try {
            //35.664065, 139.677224 default
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int user_to_device_id = jsonObject.getInt("user_to_device_id");
                String date = jsonObject.getString("date");
                double lat_locate = jsonObject.getDouble("lat");
                double long_locate = jsonObject.getDouble("long");
                String mac_address = jsonObject.getString("mac_address");
                int rssi = jsonObject.getInt("rssi");
                System.out.println(user_to_device_id + date + lat_locate + long_locate + mac_address + rssi);
                //idList.add(id);
                //nameList.add(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // get experiment data from phone, lg v20
    public void phonedata(String data) {
        // Perpare response checking


        // Generate JSON
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("data", data);

        } catch (JSONException e) {
            Log.d("OKHTTP3", "JSON EXCEPTION");
            e.printStackTrace();
        }

        // Set HTTP
        String goal_locate = "/phone";
        RequestBody body = RequestBody.create(JSON, jsonData.toString());
        Request request = new Request.Builder()
                .url(mUrl + goal_locate)
                .post(body)
                .build();
        try {
            client.newCall(request).execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
