package com.example.week2.Data;

public class UrlInfo {
    public final static int SEARCH_AREA_CONTENT = 1;
    public final static int SEARCH_KEYWORD = 2;

    private static String keyword;
    private static int areaCode;
    private static int contentType;
    private static int currentPage;
    private static boolean isTotalCountZero;
    private static String selectedCat1;
    private static String selectedCat2;
    private static String selectedCat3;

    private static int mode = -1;

    public static int getMode() {
        return mode;
    }

    public static void setMode(int mode) {
        UrlInfo.mode = mode;
    }

    public static int getCurrentPage() {
        return currentPage;
    }

    public static void setCurrentPage(int currentPage) {
        UrlInfo.currentPage = currentPage;
    }

    public static String getKeyword() {
        return keyword;
    }

    public static void setKeyword(String keyword) {
        UrlInfo.keyword = keyword;
    }

    public static int getAreaCode() {
        return areaCode;
    }

    public static void setAreaCode(int areaCode) {
        UrlInfo.areaCode = areaCode;
    }

    public static int getContentType() {
        return contentType;
    }

    public static void setContentType(int contentType) {
        UrlInfo.contentType = contentType;
    }

    public static String getSelectedCat1() {return  selectedCat1;}

    public static String getSelectedCat2() {return  selectedCat2;}

    public static String getSelectedCat3() {return  selectedCat3;}

    public static void setSelectedCat1(String cat1) {UrlInfo.selectedCat1 = cat1;}

    public static void setSelectedCat2(String cat2) {UrlInfo.selectedCat2 = cat2;}

    public static void setSelectedCat3(String cat3) {UrlInfo.selectedCat3 = cat3;}

    public static boolean isIsTotalCountZero() {
        return isTotalCountZero;
    }

    public static void setIsTotalCountZero(boolean isTotalCountZero) {
        UrlInfo.isTotalCountZero = isTotalCountZero;
    }
}
