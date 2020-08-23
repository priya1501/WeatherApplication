package priya.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView weatherReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.editText);
        weatherReport = findViewById(R.id.resultTextView);

    }

    public void getWeather (View view){

        DownloadTask task = new DownloadTask();
        String encodedCityName  = null;
        try {
            encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");
            task.execute("https://openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=439d4b804bc8187953eb36d2a8c26a02");
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);
        }catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not find weather !", Toast.LENGTH_SHORT).show();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... urls) {

            String result="";
            URL url;
            HttpURLConnection urlConnection=null;

            try
            {
                url= new URL(urls[0]);
                urlConnection= (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader= new InputStreamReader(in);
                int data= reader.read();


                while(data != -1){
                    char current = (char) data;
                    result+= current;
                    data = reader.read();
                }
                return result;
            }

            catch(Exception e){
                e.printStackTrace();
                Log.i("Err","Site Not Found!");
                //Toast.makeText(MainActivity.this, "Could not find weather !", Toast.LENGTH_SHORT).show();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject obj = new JSONObject(s);
                String weatherInfo= obj.getString("weather");

                JSONArray weather =  new JSONArray(weatherInfo);
                String tempInfo= obj.getString("main");
                Log.i("Temp", tempInfo);
                JSONObject jsonPart1 = new JSONObject(tempInfo);
                String temper = "Temperature : "+jsonPart1.getString("temp");
                String temp_min = "Min. Temperature : "+jsonPart1.getString("temp_min");
                String temp_max = "Max. Temperature : "+jsonPart1.getString("temp_max");
                String humid = "Humidity : "+jsonPart1.getString("humidity");
                String message = temper+"\r\n" + temp_max+"\r\n"+ temp_min+"\r\n"+ humid+"\r\n";
                for(int i=0; i< weather.length(); i++){
                    JSONObject jsonPart = weather.getJSONObject(i);
                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                    if(!main.equals("")&&!description.equals("")){
                        message += (main+" : "+description+"\r\n");
                    }
                }

                if(!message.equals("")){
                    weatherReport.setText(message);
                }
                else{
                    Toast.makeText(MainActivity.this, "Could not find weather !", Toast.LENGTH_SHORT).show();
                    weatherReport.setText("");
                }
            }catch(Exception e){
                e.printStackTrace();
                weatherReport.setText("");
                Toast.makeText(MainActivity.this, "Could not find weather !", Toast.LENGTH_SHORT).show();
            }
        }
    }
}