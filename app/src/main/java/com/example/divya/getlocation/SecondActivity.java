package com.example.divya.getlocation;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SecondActivity extends AppCompatActivity {
    //OpenWeatherMapHelper helper = new OpenWeatherMapHelper();
    TextView textView_Date, textView_Temp, textView_Desc,textView_Icon,textView_Emp,textView_Loc;
    Button button_Info;
    Typeface weatherFont;
    //http://api.openweathermap.org/data/2.5/weather?lat=22.5783189&lon=88.4198707&appid=ff0753913ed1f0334da55e619bc83bb4&units=metric
    private static final String OPEN_WEATHER_MAP_URL = "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric";
    private static final String OPEN_WEATHER_MAP_API = "ff0753913ed1f0334da55e619bc83bb4";
    String lat,lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_second);
        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weather.ttf");
        textView_Date = (TextView) findViewById(R.id.textView_Date);
        textView_Temp =(TextView) findViewById(R.id.textView_Temp) ;
        textView_Desc =(TextView) findViewById(R.id.textView_Desc) ;
        textView_Icon =(TextView) findViewById(R.id.textView_Icon) ;
        //textView_minTemp =(TextView) findViewById(R.id.textView_minTemp) ;
        textView_Emp =(TextView) findViewById(R.id.textView_Emp) ;
        textView_Loc =(TextView) findViewById(R.id.textView_Loc) ;
        button_Info = (Button)findViewById(R.id.button_Info);
        Intent myIntent = getIntent();
        Bundle extras = myIntent.getExtras();
        lat = extras.getString("lati");
        lon = extras.getString("longi");
        //Double lat = myIntent.getDoubleExtra("lati",latitude);
        //Double lon = myIntent.getDoubleExtra("longi",longitude);
        Toast.makeText(getApplicationContext(),"lat = "+lat,Toast.LENGTH_SHORT).show();
        textView_Icon.setTypeface(weatherFont);
        //String currentDateTimeString = String.valueOf(DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.MEDIUM));
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy\nhh:mm a\nEEEE");
        String currentDateTimeString = sdf.format(new Date());
        textView_Date.setText(currentDateTimeString);

        button_Info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_Info.setVisibility(View.GONE);
                MyAsyncTasks myAsyncTasks = new MyAsyncTasks();
                myAsyncTasks.execute(lat,lon);
            }
        });
    }

    public class MyAsyncTasks extends AsyncTask<String,Void,JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject data = null;
            URL url = null;
            try {
                // parameters of the Asynchronous task are passed to this step for execution
                url = new URL(String.format(OPEN_WEATHER_MAP_URL,params[0],params[1]));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.addRequestProperty("x-api-key", OPEN_WEATHER_MAP_API);
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuffer json = new StringBuffer(1024);
                String tmp="";
                while((tmp=reader.readLine())!=null)
                    json.append(tmp).append("\n");
                reader.close();

                try {
                    data = new JSONObject(json.toString());
                    // This value will be 404 if the request was not successful
                    if(data.getInt("cod") != 200){
                        return null;
                    }
                    return data;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try{
                if(json!=null)
                {
                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");
                    //DateFormat df = DateFormat.getDateTimeInstance();
                    String city = json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country");
                    String description = details.getString("description").toUpperCase(Locale.US);
                    String temp = main.getString("temp")+ "°C";
                    //Double tempMax = main.getDouble("temp_max");
                    //String temp_Max = String.format(Locale.US,"%.2f",main.getDouble("temp_max"))+"°";
                    String temp_Min = main.getString("temp_min");
                    String hum = main.getString("humidity")+"%";
                    textView_Desc.setText(description);
                    textView_Temp.setText(temp);
                    textView_Emp.setText(hum);
                    textView_Loc.setText("Feels like\n"+temp_Min+ "°C in "+city);
                    setWeatherIcon(details.getInt("id"),json.getJSONObject("sys").getLong("sunrise"),json.getJSONObject("sys").getLong("sunset"));
                }
                //super.onPostExecute(jsonObject);
            } catch(JSONException e) {
                //Log.e(LOG_TAG, "Cannot process JSON results", e);
            }
        }

    }
    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = getString(R.string.weather_sunny);
            } else {
                icon = getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = getString(R.string.weather_thunder);
                    break;
                case 3 : icon = getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = getString(R.string.weather_foggy);
                    break;
                case 8 : icon = getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = getString(R.string.weather_snowy);
                    break;
                case 5 : icon = getString(R.string.weather_rainy);
                    break;
            }
        }
        textView_Icon.setText(icon);
    }

}
