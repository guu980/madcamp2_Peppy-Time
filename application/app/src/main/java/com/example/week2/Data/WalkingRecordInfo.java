package com.example.week2.Data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class WalkingRecordInfo {
    private JsonObject start;
    private JsonObject end;
    private  JsonObject date;
    private JsonObject totalTime;
    private Double distance;
    private String db_id;

    public WalkingRecordInfo(JsonObject start, JsonObject end, JsonObject date, JsonObject totalTime, Double distance, String _id){
        this.start = start;
        this.end = end;
        this.date = date;
        this.totalTime = totalTime;
        this.distance = distance;
        this.db_id = _id;
    }

    public String getStartLat()
    {
       JsonElement startLat  = start.get("lat");
       String result = startLat.toString();
       String finalResult = result.substring(1, result.length()-1);
       return finalResult;
    }

    public String getStratLon()
    {
        JsonElement startLon = start.get("lon");
        String result = startLon.toString();
        String finalResult = result.substring(1, result.length()-1);
        return finalResult;
    }

    public String getEndLat()
    {
        JsonElement endLat = end.get("lat");
        String result = endLat.toString();
        String finalResult = result.substring(1, result.length()-1);
        return finalResult;
    }

    public String getEndLon()
    {
        JsonElement endLon = end.get("lon");
        String result = endLon.toString();
        String finalResult = result.substring(1, result.length()-1);
        return finalResult;
    }

    public String getDateYear()
    {
        JsonElement dateYear = date.get("year");
        String result = dateYear.toString();
        String finalResult = result.substring(1, result.length()-1);
        return finalResult;
    }

    public String getDateMonth()
    {
        JsonElement dateMonth = date.get("month");
        String result = dateMonth.toString();
        String finalResult = result.substring(1, result.length()-1);
        return finalResult;
    }

    public String getDateDay()
    {
        JsonElement dateDay = date.get("day");
        String result = dateDay.toString();
        String finalResult = result.substring(1, result.length()-1);
        return finalResult;
    }

    public String getDateHour()
    {
        JsonElement dateHour = date.get("hour");
        String result = dateHour.toString();
        String finalResult = result.substring(1, result.length()-1);
        return finalResult;
    }

    public String getDateMin()
    {
        JsonElement dateMin = date.get("min");
        String result = dateMin.toString();
        String finalResult = result.substring(1, result.length()-1);
        return finalResult;
    }

    public String getTotalTimeHours()
    {
        JsonElement totalTimeHours = totalTime.get("hours");
        String result = totalTimeHours.toString();
        String finalResult = result.substring(1, result.length()-1);
        return finalResult;
    }

    public String getTotalTimeMins()
    {
        JsonElement totalTimeMins = totalTime.get("mins");
        String result = totalTimeMins.toString();
        String finalResult = result.substring(1, result.length()-1);
        return finalResult;
    }

    public String getTotalDistance()
    {
        String result = Double.toString(distance);
        //String finalResult = result.substring(1, result.length()-1);
        return result;
    }

    public String getDbId()
    {
        String result = db_id.substring(1, db_id.length()-1);
        return result;
    }
}
