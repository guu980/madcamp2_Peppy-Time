package com.example.week2.XML;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;

import com.example.week2.CustomProgressDialog;
import com.example.week2.Data.UrlInfo;
import com.example.week2.Data.LandMark;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class GetXML  extends AsyncTask<String, Void, ArrayList<LandMark>> {
    private ArrayList<LandMark> landMarksList;
    private LandMark landMark;
    private String getPage;
    private String getKeyword;
    private Context context;
    private Activity activity;
    private CustomProgressDialog customProgressDialog;
    private String totalPage = null;
    private String numOfRows = null;
    private String[] pageInfo = new String[2];


    public GetXML(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public String[] getPageInfo() {
        return pageInfo;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        customProgressDialog = new CustomProgressDialog(activity);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customProgressDialog.show();
    }

    @Override
    protected ArrayList<LandMark> doInBackground(String... strings) {

        URL url;
        try {
            getPage = strings[0];
            getKeyword = strings[1];
        } catch (Exception e) {
            getPage = "1";
        }
        try {
            if (UrlInfo.getMode() == UrlInfo.SEARCH_KEYWORD) {
                url = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/searchKeyword?serviceKey=jG9PX8i%2BoC9KR%2BHgXx9KnkCfNed54pdkGVoLIEYO%2Fqpq3Hn17zjx%2BB%2B%2BXiZFeWxl13XMhiRu7aeW7%2BvvJI%2B%2Bpw%3D%3D&MobileApp=AppTest&MobileOS=ETC&pageNo=" + getPage + "&numOfRows=10&listYN=Y&arrange=A&keyword=" + getKeyword );
            } else if (UrlInfo.getMode() == UrlInfo.SEARCH_AREA_CONTENT) {
                if(UrlInfo.getSelectedCat3().equals("NULL")){
                    url = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?serviceKey=jG9PX8i%2BoC9KR%2BHgXx9KnkCfNed54pdkGVoLIEYO%2Fqpq3Hn17zjx%2BB%2B%2BXiZFeWxl13XMhiRu7aeW7%2BvvJI%2B%2Bpw%3D%3D&pageNo=" + getPage + "&numOfRows=10&MobileApp=AppTest&MobileOS=ETC&contentTypeId=" + UrlInfo.getContentType() + "&areaCode=" + UrlInfo.getAreaCode() + "&cat1=" + UrlInfo.getSelectedCat1() + "&cat2=" + UrlInfo.getSelectedCat2() + "&listYN=Y");
                }
                else{
                    url = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?serviceKey=jG9PX8i%2BoC9KR%2BHgXx9KnkCfNed54pdkGVoLIEYO%2Fqpq3Hn17zjx%2BB%2B%2BXiZFeWxl13XMhiRu7aeW7%2BvvJI%2B%2Bpw%3D%3D&pageNo=" + getPage + "&numOfRows=10&MobileApp=AppTest&MobileOS=ETC&contentTypeId=" + UrlInfo.getContentType() + "&areaCode=" + UrlInfo.getAreaCode() + "&cat1=" + UrlInfo.getSelectedCat1() + "&cat2=" + UrlInfo.getSelectedCat2() + "&cat3=" + UrlInfo.getSelectedCat3()+ "&listYN=Y");}
            } else {
                url = null;
            }
            boolean bAddress = false, bTitle = false, bImage = false, bNumOfRows = false, bTotalCount = false, bContentId = false, bTel = false
                    , bMapx = false, bMapy = false, bContentTypeId = false;;
            InputStream inputStream = url.openStream();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new InputStreamReader(inputStream, "UTF-8"));

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        landMarksList = new ArrayList<>();
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("item") && landMark !=null) {
                            landMarksList.add(landMark);
                        }
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("item")) {
                            landMark = new LandMark();
                        }
                        if (parser.getName().equals("addr1")) {
                            bAddress = true;
                        }
                        if (parser.getName().equals("title")) {
                            bTitle = true;
                        }
                        if (parser.getName().equals("firstimage")) {
                            bImage = true;
                        }
                        if (parser.getName().equals("numOfRows")) {
                            bNumOfRows = true;
                        }
                        if (parser.getName().equals("totalCount")) {
                            bTotalCount = true;
                        }
                        if (parser.getName().equals("contentid")) {
                            bContentId = true;
                        }
                        if (parser.getName().equals("contenttypeid")) {
                            bContentTypeId = true;
                        }
                        if (parser.getName().equals("tel")) {
                            bTel = true;
                        }
                        if (parser.getName().equals("mapx")) {
                            bMapx = true;
                        }
                        if (parser.getName().equals("mapy")) {
                            bMapy = true;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (bAddress) {
                            landMark.getTagList().put("addr1", parser.getText());
                            bAddress = false;
                        } else if (bTitle) {
                            landMark.getTagList().put("title", parser.getText());
                            bTitle = false;
                        } else if (bImage) {
                            landMark.getTagList().put("firstimage", parser.getText());
                            bImage = false;
                        } else if (bNumOfRows) {
                            pageInfo[0] = parser.getText();
                            bNumOfRows = false;
                        } else if (bTotalCount) {
                            pageInfo[1] = parser.getText();

                            bTotalCount = false;
                        } else if (bContentId) {
                            landMark.getTagList().put("contentid", parser.getText());
                            bContentId = false;
                        } else if (bTel) {
                            landMark.getTagList().put("tel", parser.getText());
                            bTel = false;
                        } else if (bMapx) {
                            landMark.getTagList().put("mapx", parser.getText());
                            bMapx = false;
                        } else if (bMapy) {
                            landMark.getTagList().put("mapy", parser.getText());
                            bMapy = false;
                        } else if (bContentTypeId) {
                            landMark.getTagList().put("contenttypeid", parser.getText());
                            bContentTypeId = false;
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return landMarksList;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(ArrayList<LandMark> document) {
        super.onPostExecute(document);
        customProgressDialog.dismiss();
    }
}
