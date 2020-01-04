package com.example.week2.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.week2.Adapter.AutoScrollAdapter;
import com.example.week2.Data.APIConstant;
import com.example.week2.Data.UrlInfo;
import com.example.week2.MainActivity;
import com.example.week2.R;

import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

public class TripMainFragment extends Fragment{
    private AutoScrollViewPager autoViewPager;
    private View v;
    private int selectedArea = -1;
    private int selectedContent = -1;
    private String selectedCat1 = "";
    private String selectedCat2 = "";
    private String selectedCat3 = "";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.trip_main, container, false);

        ArrayList<String> data = new ArrayList<>(); //이미지 url를 저장하는 arraylist
        data.add("https://postfiles.pstatic.net/MjAxOTEyMzFfMjM5/MDAxNTc3NzY3NjQ0OTM0.GrhlCfxi-dILyccEgKmBLGVI-KN0pHg8Qv7BlBu9pdAg.ZzXGv4eI9ZrGbmKFE1734n5Dqtl_4eivWrAI25IBj_wg.PNG.otl0711/%EC%84%9C%EC%9A%B8.png?type=w580");
        data.add("https://postfiles.pstatic.net/MjAxOTEyMzFfMjEw/MDAxNTc3NzY3NjQ3NTEx.NyXbKU_xr3a0pT1GTuQ4wzHtr68nXw2Emzto-p1klBsg.v1VJipbb2YWJT280fdcVKL01Qpsrs-HNTR05Wq5ZYZkg.PNG.otl0711/%EB%B6%80%EC%82%B0.png?type=w580");
        data.add("https://postfiles.pstatic.net/MjAxOTEyMzFfMTI2/MDAxNTc3NzcyNTI2ODM2.Tlht_Y-I9iGsZIpRiCC771fgIVZq9_ovT_2HYFxCpiIg.qwoydP9o86RNOS1Aoq7H1kFmcRy_XArer0nB_EFW-OMg.PNG.otl0711/%EB%8C%80%EC%A0%84.png?type=w580");
        data.add("https://postfiles.pstatic.net/MjAxOTEyMzFfMjc3/MDAxNTc3NzY3NjQ4OTM4.AaAzBjo1ENt6tG4P2oafCCByoDjUB2RZWcntw2W5IG4g.gsP_IlkylQFK7Y4GKYWvevigZF5ez5m9y-UUCZs5000g.PNG.otl0711/%EA%B2%BD%EC%A3%BC.png?type=w580");
        data.add("https://postfiles.pstatic.net/MjAxOTEyMzFfODUg/MDAxNTc3NzY4NTI0ODMw.r1kdaUw08GHNo49zOW7Ucd-wvlRHjJkH2tPrc0xTIzkg.Pc59j7Ex2gRwO1z3-njRBF2T7Y3TrjOXHlA1Q06fpHQg.PNG.otl0711/%EC%A0%9C%EC%A3%BC.png?type=w580");

        autoViewPager = (AutoScrollViewPager) v.findViewById(R.id.autoViewPager);
        AutoScrollAdapter scrollAdapter = new AutoScrollAdapter(getContext(), data);
        autoViewPager.setAdapter(scrollAdapter); //Auto Viewpager에 Adapter 장착
        autoViewPager.setInterval(1900); // 페이지 넘어갈 시간 간격 설정
        autoViewPager.startAutoScroll(); //Auto Scroll 시작

        Button buttongo = (Button) v.findViewById(R.id.go);
        buttongo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedArea != -1 && selectedContent != -1) {
                    UrlInfo.setAreaCode(selectedArea);
                    UrlInfo.setContentType(selectedContent);
                    UrlInfo.setSelectedCat1(selectedCat1);
                    UrlInfo.setSelectedCat2(selectedCat2);
                    UrlInfo.setSelectedCat3(selectedCat3);
                    UrlInfo.setCurrentPage(1);
                    UrlInfo.setMode(UrlInfo.SEARCH_AREA_CONTENT);
                    ((MainActivity)getActivity()).replaceFragment(new TripFragment());
                } else {
                    Toast.makeText(getContext(), "항목을 모두 선택해 주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Button button = (Button) v.findViewById(R.id.choosearea);
        final TextView area = v.findViewById(R.id.area);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show(area);
            }
        });

        Button button1 = (Button) v.findViewById(R.id.content);
        final TextView cnt = v.findViewById(R.id.cnt);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showcnt(cnt);
            }
        });

        return v;
    }

    void show(final TextView area)
    {
        final List<String> ListItems = new ArrayList<>();
        ListItems.add("서울");ListItems.add("인천");ListItems.add("대전");ListItems.add("대구");ListItems.add("광주");ListItems.add("부산");
        ListItems.add("울산");ListItems.add("세종");ListItems.add("경기");ListItems.add("강원");ListItems.add("충북");
        ListItems.add("충남");ListItems.add("경북");ListItems.add("경남");ListItems.add("전북");ListItems.add("전남");
        ListItems.add("제주");
        final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("지역을 선택해주세요");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int pos) {
                String selectedText = items[pos].toString();
                selectedArea = APIConstant.AREA_LIST[pos];
                Toast.makeText(getContext(), selectedText+"(으)로 이동합니다!", Toast.LENGTH_SHORT).show();
                area.setText(selectedText);
            }
        });
        builder.show();
    }

    void showcnt(final TextView cnt)
    {
        final List<String> ListItems = new ArrayList<>();
        ListItems.add("관광지");ListItems.add("문화시설");ListItems.add("축제/공연/행사");ListItems.add("여행코스");
        ListItems.add("레포츠");ListItems.add("숙박");ListItems.add("쇼핑");ListItems.add("음식");
        final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("컨텐츠를 선택하세요");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int pos) {
                String selectedText = items[pos].toString();
                selectedContent = APIConstant.CONTENT_LIST[pos];
                Toast.makeText(getContext(), selectedText+"(으)로 이동합니다!", Toast.LENGTH_SHORT).show();
                cnt.setText(selectedText);
                if(pos == 0) {
                    String[] cat1 = {"자연", "인문(문화/예술/역사)"};
                    String[] cat21 = {"자연관광지", "관광자원"};
                    String[] cat22 = {"역사관광지", "휴양관광지", "체험관광지", "산업관광지", "건축/조형물"};
                    addDialog(2, cat1, cat21, cat22);
                } else if(pos == 1) {
                    selectedCat1 = "A02"; selectedCat2 = "A0206";
                    String[] cat = {"박물관","기념관","전시관","컨벤션센터", "미술관/화랑", "공연장","문화원","외국문화원", "도서관","대형서점",
                            "문화전수시설", "영화관", "어학당","학교"};
                    addDialog(pos, 14, cat);
                } else if(pos == 2) {
                    selectedCat1 = "A02";
                    String[] cat = {"축제", "공연/행사"};
                    addDialog(pos,2, cat);
                } else if(pos == 3) {
                    selectedCat1 = "C01";
                    String[] cat = {"가족코스", "나홀로코스", "힐링코스", "도보코스", "캠핑코스", "맛코스"};
                    addDialog(pos,6, cat);
                } else if(pos == 4) {
                    selectedCat1 = "A03";
                    String[] cat = {"레포츠소개", "육상 레포츠", "수상 레포츠", "항공 레포츠", "복합 레포츠"};
                    addDialog(pos, 5, cat);
                } else if(pos == 5) {
                    selectedCat1 = "B02"; selectedCat2 = "B0201";
                    String[] cat = {"숙박시설"};
                    addDialog(pos, 1, cat);
                } else if(pos == 6) {
                    selectedCat1 = "A04"; selectedCat2 = "A0401";
                    String[] cat = {"5일장", "상설시장", "백화점","면세점","할인매장","전문상가","공예","공방","관광기념품점", "특산물판매점"};
                    addDialog(pos, 10, cat);
                } else if(pos == 7) {
                    selectedCat1 = "A05"; selectedCat2 = "A0502";
                    String[] cat = {"한식","서양식","일식","중식","아시아식","패밀리레스토랑","이색음식점","채식전문점","바/까페","클럽"};
                    addDialog(pos, 10, cat);
                }
            }
        });
        builder.show();
    }



    public void addDialog(final int pos, int catNum, String[] catItems) {
        final List<String> Cat1 = new ArrayList<>();
        for (int i = 0; i < catNum; i++) {
            Cat1.add(catItems[i]);
        }
        final CharSequence[] cat1items = Cat1.toArray(new String[Cat1.size()]);
        final AlertDialog.Builder buildercat1 = new AlertDialog.Builder(getContext());
        buildercat1.setItems(cat1items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedText = cat1items[which].toString();
                Toast.makeText(getContext(), selectedText + " (으)로 이동합니다!", Toast.LENGTH_SHORT).show();
                if(pos == 1){
                    String[] cat3 = {"A02060100","A02060200","A02060300","A02060400","A02060500","A02060600","A02060700","A02060800","A02060900","A02061000","A02061100","A02061200","A02061300","A02061400"};
                    selectedCat3 = cat3[which];
                } else if (pos == 2){
                    String[] cat2 = {"A0207", "A0208"};
                    selectedCat2 = cat2[which];
                } else if (pos == 3){
                    String[] cat2 = {"C0112","C0113","C0114","C0115","C0116","C0117"};
                    String[] cat3 = {"C01120001","C01130001","C01140001","C01150001", "C01160001","C01170001"};
                    selectedCat2 = cat2[which];
                    selectedCat3 = cat3[which];
                } else if (pos == 4){
                    String[] cat2 = {"A0301", "A0302", "A0303", "A0304", "A0305"};
                    selectedCat2 = cat2[which];
                } else if (pos == 6){
                    selectedCat2 = "A0401";
                    String[] cat3 = {"A04010100","A04010200","A04010300","A04010400","A04010500","A04010600","A04010700","A04010800","A04010900"};
                    selectedCat3 = cat3[which];
                } else if (pos == 7){
                    selectedCat2 = "A0502";
                    String[] cat3 = {"A05020100","A05020200","A05020300","A05020400","A05020500","A05020600","A05020700","A05020800","A05020900","A05021000"};
                    selectedCat3 = cat3[which];
                } else if (pos == 10){
                    String[] cat2 = {"A0101", "A0102"};
                    selectedCat2 = cat2[which];
                } else if (pos == 11){
                    String[] cat2 = {"A0201", "A0202", "A0203", "A0204", "A0205"};
                    selectedCat2 = cat2[which];
                }
            }
        });
        buildercat1.show();
    }

    public void addDialog(int catNum, String[] cat1, final String[] cat21, final String[] cat22) {
        final List<String> Cat1 = new ArrayList<>();
        for (int i = 0; i < catNum; i++) {
            Cat1.add(cat1[i]);
        }
        final CharSequence[] cat1items = Cat1.toArray(new String[Cat1.size()]);
        final AlertDialog.Builder buildercat1 = new AlertDialog.Builder(getContext());
        buildercat1.setItems(cat1items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedText = cat1items[which].toString();
                Toast.makeText(getContext(), selectedText + " (으)로 이동합니다!", Toast.LENGTH_SHORT).show();
                if (which == 0){
                    selectedCat1 = "A01";
                    addDialog(10,2, cat21);
                } else if(which == 1){
                    selectedCat1 = "A02";
                    addDialog(11, 5, cat22);
                }
            }
        });
        buildercat1.show();
    }
}
