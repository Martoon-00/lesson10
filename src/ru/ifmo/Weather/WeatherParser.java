package ru.ifmo.Weather;

/**
 * Created with IntelliJ IDEA.
 * User: asus
 * Date: 20.11.13
 * Time: 18:27
 * To change this template use File | Settings | File Templates.
 */

import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: asus
 * Date: 23.10.13
 * Time: 20:13
 * To change this template use File | Settings | File Templates.
 */

class WeatherParser {
    private static RootElement prepare(ArrayList<WeatherCond> a) throws Exception{
        final WeatherCond currentWeatherCond = new WeatherCond();
        final WeatherCond currentWeatherCondNow = new WeatherCond();
        final ArrayList<WeatherCond> days = a;
        a.add(currentWeatherCondNow);
        RootElement root = new RootElement("data");
        android.sax.Element cur_item = root.requireChild("current_condition");

        currentWeatherCondNow.param[WeatherCond.NOW] = "1";
        cur_item.setEndElementListener(new EndElementListener() {
            public void end() {
            }
        });
        for (int i = 0; i < currentWeatherCondNow.SqlTags.length; i++){
            final int k = i;
            if (WeatherCond.nowTags[i] == null) continue;
            cur_item.getChild(WeatherCond.nowTags[i]).setEndTextElementListener(new EndTextElementListener() {
                public void end(String body) {
                    currentWeatherCondNow.param[k] = body;
                }
            });
        }


        android.sax.Element forecast_item = root.getChild("weather");

        forecast_item.setEndElementListener(new EndElementListener() {
            public void end() {
                currentWeatherCond.param[WeatherCond.NOW] = "0";
                days.add(currentWeatherCond.makeCopy());
                currentWeatherCond.clear();
            }
        });
        for (int i = 0; i < currentWeatherCond.SqlTags.length; i++){
            final int k = i;
            if (WeatherCond.forecastTags[i] == null) continue;
            forecast_item.getChild(WeatherCond.forecastTags[i]).setEndTextElementListener(new EndTextElementListener() {
                public void end(String body) {
                    currentWeatherCond.param[k] = body;
                }
            });
        }
        return root;
    }
    public static ArrayList<WeatherCond> parse(String URLAdress) throws Exception {
        final ArrayList<WeatherCond> data = new ArrayList<WeatherCond>();
        InputStream in = null;
        InputStreamReader inr = null;
        try {
            URL feedUrl = new URL(URLAdress);
            URLConnection conn = feedUrl.openConnection();
            in = conn.getInputStream();
            RootElement root = prepare(data);

            inr = new InputStreamReader(in);
            Xml.parse(inr, root.getContentHandler());
        } finally
        {
            try{
                if (in != null) in.close();
            } catch (Throwable ex){
            }
            try{
                if (inr != null) inr.close();
            } catch (Throwable ex){
            }
        }

        return data;
    }
}