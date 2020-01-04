package com.example.week2.Data;

import java.util.HashMap;
import java.util.Map;

public class LandMark {
    private Map <String, String> tagList;

    public LandMark() {
        this.tagList = new HashMap<>();
    }

    public Map<String, String> getTagList() {
        return tagList;
    }
}
