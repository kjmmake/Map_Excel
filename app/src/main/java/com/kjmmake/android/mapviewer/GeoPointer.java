package com.kjmmake.android.mapviewer;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by LinePlus on 2017-07-28.
 */

public class GeoPointer extends AsyncTask<Person, Void, ArrayList> {

    private final static String NAVER_CLIENT_ID = "2dgdejc1NaqSYcXKV12F";
    private final static String NAVER_CLIENT_SECRET = "O2qmo0YE9n";

    private OnGeoPointListener onGeoPointListener;

    private Context context;

    public GeoPointer(Context context, OnGeoPointListener listener) {
        this.context = context;
        onGeoPointListener = listener;
    }

    @Override
    protected ArrayList doInBackground(Person... persons) {
        // 리턴할 포인터 객체를 파람의 수만큼 배열로 만든다.
        int i=0;
        ArrayList<Person> results = new ArrayList<>();
        for (Person person : persons) {
            // 프로그래스를 돌린다.
            onGeoPointListener.onProgress(i + 1, persons.length);

            final String addr = person.getAddress();

            Point point = getPointFromNaver(addr);

            if(!point.havePoint){
                Log.d("망함","너도");
            }


            Location newloc = new Location("dummy");
            newloc.setLongitude(point.x);
            newloc.setLatitude(point.y);
            person.setLoc(newloc);
            results.add(person);
            i++;
        }

        return results;
    }

    public interface OnGeoPointListener {
        void onPoint(Point[] p);

        void onProgress(int progress, int max);
    }

    class Point {
        // 위도
        public double x;
        // 경도
        public double y;
        public String addr;
        // 포인트를 받았는지 여부
        public boolean havePoint;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("x : ");
            builder.append(x);
            builder.append(" y : ");
            builder.append(y);
            builder.append(" addr : ");
            builder.append(addr);

            return builder.toString();
        }
    }

    /**
     * 네이버 맵 api를 통해서 주소를 가져온다.
     * https://developers.naver.com/docs/map/overview/
     */
    private Point getPointFromNaver(String addr) {
        Point point = new Point();
        point.addr = addr;

        String json = null;
        String clientId = NAVER_CLIENT_ID;// 애플리케이션 클라이언트 아이디값";
        String clientSecret = NAVER_CLIENT_SECRET;// 애플리케이션 클라이언트 시크릿값";
        try {
            addr = URLEncoder.encode(addr, "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/map/geocode?query=" + addr; // json
            // String apiURL =
            // "https://openapi.naver.com/v1/map/geocode.xml?query=" + addr; //
            // xml
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else { // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            json = response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (json == null) {
            return point;
        }

        Log.d("TEST2", "json => " + json);

        Gson gson = new Gson();
        NaverData data = new NaverData();
        try {
            data = gson.fromJson(json, NaverData.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (data.result != null) {
            point.x = data.result.items.get(0).point.x;
            point.y = data.result.items.get(0).point.y;
            point.havePoint = true;
        }

        return point;
    }
}
