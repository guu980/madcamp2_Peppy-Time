package com.example.week2.Retrofit;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitAPI {

    /* lat={lat}&Ion={lon}&APPID={APPID} */
    @GET("weather?")
    Call<JsonObject> getCurrentWeather(@Query("lat") String lat,
                                       @Query("lon") String lon,
                                       @Query("APPID") String APPID);

    @GET("get/walking/ymd")
    Call<JsonArray> getWalkingRecordYmd(@Query("id") String id,
                                     @Query("year") String year,
                                     @Query("month") String month,
                                     @Query("day") String day);

    @GET("get/walking/ym")
    Call<JsonArray> getWalkingRecordYm(@Query("id") String id,
                                        @Query("year") String year,
                                        @Query("month") String month);

    @GET("get/walking/y")
    Call<JsonArray> getWalkingRecordY(@Query("id") String id,
                                       @Query("year") String year);

    @POST("push/walking")
    Call<JsonObject> storeWalkingRecord(@Body JsonObject data, @Query("id") String id);

    @GET("delete/record")
    Call <JsonObject> removeWalkingREcord(@Query("id") String id,
                                          @Query("dbid") String dbid);

    @GET("get/walking/forPoint")
    Call<JsonArray> getWalkingRecordforPoint(@Query("id") String id,
                                             @Query("year") String year,
                                             @Query("month") String month);


//    String appkey = "c41182fa3633071e849d3e55fd1f5231";
//    String header = "Authorization: KakaoAK " +  appkey;
//    @Headers(header)
//    @GET("transcoord.json")
//    Call<JsonObject> getTmData(@Query("x") String x,
//                               @Query("y") String y,
//                               @Query("input_coord") String inputCoord,
//                               @Query("output_coord") String outputCoord);
}