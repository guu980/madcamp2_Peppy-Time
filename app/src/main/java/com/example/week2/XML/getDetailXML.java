package com.example.week2.XML;

import android.os.AsyncTask;

import com.example.week2.Data.LandMark;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class getDetailXML  extends AsyncTask<String, Void, ArrayList<LandMark>> {
    private ArrayList<LandMark> landMarksList;
    private LandMark landMark;
    String contentid;
    String contenttypeid;

    public getDetailXML(String contentid, String contenttypeid){
        this.contentid = contentid;
        this.contenttypeid = contenttypeid;
    }

    @Override
    protected ArrayList<LandMark> doInBackground(String... strings) {
        try {
            URL url = new URL("http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon?ServiceKey=FmD3e%2FhDm47HLlFHkAp7gBaIK8WlW5BDMs7OhhU9EJ95VyiOWW%2BGtQ39sCPOpn0OF%2FfE0rYzt%2Fxwdta9FJN16w%3D%3D&contentId="+contentid+"&contenttypeid="+contenttypeid+"&defaultYN=Y&addrinfoYN=Y&overviewYN=Y&MobileOS=ETC&MobileApp=AppTest");
            boolean bOverview = false, bHomepage = false;
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
                        if (parser.getName().equals("overview")) {
                            bOverview = true;
                        }
                        if (parser.getName().equals("homepage")) {
                            bHomepage = true;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (bOverview) {
                            landMark.getTagList().put("overview", parser.getText());
                            bOverview = false;
                        } else if (bHomepage){
                            landMark.getTagList().put("homepage", parser.getText());
                            bHomepage = false;
                        }break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return landMarksList;
    }

    @Override
    protected void onPostExecute(ArrayList<LandMark> document) {
        super.onPostExecute(document);
    }
}

