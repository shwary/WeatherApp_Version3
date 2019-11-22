package com.androdocs.weatherapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class HttpRequest {
    public static String excuteGet(String targetURL) {
        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream is;
            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK)
                is = connection.getErrorStream();
            else
                is = connection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}

class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFrag(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}

public class MainActivity extends AppCompatActivity {

    String CITY = "fairfax";
    String API = "8118ed6ee68db2debfaaa5a44c832918";

    TextView addressTxt, updated_atTxt, statusTxt, tempTxt, temp_minTxt, temp_maxTxt, sunriseTxt,
            sunsetTxt, windTxt, pressureTxt, statusText,suggestion_Text;

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addressTxt = findViewById(R.id.address);
        updated_atTxt = findViewById(R.id.updated_at);
//        statusTxt = findViewById(R.id.status);
        tempTxt = findViewById(R.id.temp);
//        temp_minTxt = findViewById(R.id.temp_min);
//        temp_maxTxt = findViewById(R.id.temp_max);
        sunriseTxt = findViewById(R.id.sunrise);
        sunsetTxt = findViewById(R.id.sunset);
        windTxt = findViewById(R.id.wind);
        pressureTxt = findViewById(R.id.pressure);
        statusText = findViewById(R.id.status);
        //suggestion_Text = findViewById(R.id.clothesSuggestion);

        viewPager = findViewById(R.id.viewpager);
        //SuggestionCalculation sc = new SuggestionCalculation();
        //addTabs(viewPager,sc.suggestion(Integer.parseInt("55"),"15","misty"));

        new weatherTask().execute();
    }

    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /* Showing the ProgressBar, Making the main design GONE */
            findViewById(R.id.loader).setVisibility(View.VISIBLE);
            findViewById(R.id.mainContainer).setVisibility(View.GONE);
            findViewById(R.id.errorText).setVisibility(View.GONE);
        }

        protected String doInBackground(String... args) {
            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=" + CITY + "&units=metric&appid=" + API);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {


            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                Long updatedAt = jsonObj.getLong("dt");
                String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));
                float temp_float = Float.valueOf(main.getString("temp")).floatValue();
                temp_float = (float) (temp_float*1.8+32);//convert from C to F i believe
                temp_float = Math.round(temp_float*10)/10;
                int temp_suggest = (int) temp_float;
                String temperature  = String.valueOf(temp_float);
                String temp = temperature + "°F";
//
//                String tempMin = "Min Temp: " + main.getString("temp_min") + "°C";
//                String tempMax = "Max Temp: " + main.getString("temp_max") + "°C";
                String pressure = main.getString("pressure");
                String humidity = main.getString("humidity");

                Long sunrise = sys.getLong("sunrise");
                Long sunset = sys.getLong("sunset");
                String windSpeed = wind.getString("speed");
                String weatherDescription = weather.getString("description");

                String address = jsonObj.getString("name") + ", " + sys.getString("country");


                /* Populating extracted data into our views */
                addressTxt.setText(address);
                updated_atTxt.setText(updatedAtText);
                statusText.setText(weatherDescription.toUpperCase());
                tempTxt.setText(temp);
//                temp_minTxt.setText(tempMin);
//                temp_maxTxt.setText(tempMax);
                sunriseTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunrise * 1000)));
                sunsetTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunset * 1000)));
                windTxt.setText(windSpeed);
                pressureTxt.setText(pressure);
                //humidityTxt.setText(humidity);

                SuggestionCalculation sc = new SuggestionCalculation();
                addTabs(viewPager,sc.suggestion(temp_suggest,windSpeed,weatherDescription));
                //addTabs(viewPager,sc.suggestion(Integer.parseInt("55"),"12","clear sky"));

                /* Views populated, Hiding the loader, Showing the main design */
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.errorText).setVisibility(View.VISIBLE);
            }

        }
    }

    private void addTabs(ViewPager viewPager, String[] constraints) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),0);
        /*   there's probably a better way of doing this. Probably also a better way instead of Fragment classes*/
        for(int i = 0; i < constraints.length; i++){
            switch (constraints[i]){
                case "T-Shirt": adapter.addFrag(new TShirt(),"TSHIRT");
                    break;
                case "Long Sleeve Shirt": adapter.addFrag(new LongShirt(),"LONGSLEEVESHIRT");
                    break;
                case "Shorts": adapter.addFrag(new Shorts(),"SHORTS");
                    break;
                case "Pants": adapter.addFrag(new LongPants(),"LONGPANTS");
                    break;
                case "Umbrella": adapter.addFrag(new Umbrella(),"UMBRELLA");
                    break;
                case "Sunglasses": adapter.addFrag(new Sunglasses(),"SUNGLASSES");
                    break;
                case "Jacket": adapter.addFrag(new Jacket(),"JACKET");
                    break;
                case "Sweatshirt": adapter.addFrag(new SweatShirt(),"SWEATSHIRT");
                    break;
                case "Snow Cap": adapter.addFrag(new SnowCap(),"SNOWCAP");
                    break;
                case "Hat": adapter.addFrag(new Hat(),"HAT");
                    break;
                case "Gloves": adapter.addFrag(new Gloves(),"GLOVES");
                    break;
                case "Sweater": adapter.addFrag(new Sweater(),"SWEATER");
                    break;
            }

        }
        viewPager.setAdapter(adapter);
    }
}
