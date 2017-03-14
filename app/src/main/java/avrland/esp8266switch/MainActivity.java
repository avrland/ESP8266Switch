package avrland.esp8266switch;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    String adres = "192.168.43.20";
    String adres_z_pamieci;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Todo: porządek z uruchamianymi elemetentami

        //Pole tekstowe adresu IP z automatycznym powrotem klawiatury
        final EditText editText = (EditText) findViewById(R.id.editText12);
        editText.setText(odczyt());

        editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    wiadomosc("Saved!");
                    adres = editText.getText().toString();
                    hideSoftKeyboard(MainActivity.this);
                    editText.clearFocus();
                    zapisz(adres);
                }
                return false;
            }
        });

        //Przełącznik GPIO 1
        Switch toggle = (Switch) findViewById(R.id.switch1);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                        gpio_on(adres, "/gpio/1/");
                } else {
                        gpio_on(adres, "/gpio/0/");
                }
            }
        });
        //Przełącznik GPIO 2
        Switch toggle2 = (Switch) findViewById(R.id.switch2);
        toggle2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    wiadomosc("Not implemented.");
                } else {
                    wiadomosc("Not implemented.");
                }
            }
        });

    }

    //Zapytanie GET dla przełączania pinów GPIO
    public void gpio_on(String adres_ip, String gpio) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://"+ adres_ip + gpio;
        final String gpio_numer = gpio;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        wiadomosc(gpio_numer);
                        //TextView odpowiedz = (TextView) findViewById(R.id.textView17);
                       //   odpowiedz.setText(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                wiadomosc("Connection error, check IP address.");
            }
        });
        queue.add(stringRequest);
    }

    //Zapytanie GET dla przełączania pinów GPIO
    //Todo: odczyt stanów wyprowadzeń GPIO
    public void gpio_state(String adres_ip, String gpio) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://"+ adres_ip + gpio;
        final String gpio_numer = gpio;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        wiadomosc(response);
                        //TextView odpowiedz = (TextView) findViewById(R.id.textView17);
                        //   odpowiedz.setText(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                wiadomosc("Connection error, check IP address.");
            }
        });
        queue.add(stringRequest);
    }

    //Funkcja wywołująca wiadomości toast
    public void wiadomosc(String wiadomosc){
        Context context = getApplicationContext();
        CharSequence text = wiadomosc;
        int duration = Toast.LENGTH_SHORT;

        final Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 300);
    }
    //Funkcja ukrywająca klawiaturę
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    //Funkcja pobierająca temperature i cisnienie do textview
    public void pobierz_dane(View view) {
        String url_t ="http://"+ adres + "/temp/";
        String url_p ="http://"+ adres + "/press/";

        RequestQueue queue2 = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_t,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        TextView temperatura = (TextView) findViewById(R.id.textView14);
                        temperatura.setText(response + " °C");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                wiadomosc("Connection error, check IP address.");
            }
        });
        queue2.add(stringRequest);

        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url_p,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        TextView cisnienie = (TextView) findViewById(R.id.textView16);
                        cisnienie.setText(response + " hPa");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                wiadomosc("Connection error, check IP address.");
            }
        });
        queue2.add(stringRequest2);

        Calendar c = Calendar.getInstance();
        int seconds = c.get(Calendar.SECOND);
        TextView czas = (TextView) findViewById(R.id.textView18);
        czas.setText(c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND));
    }


    //zapis nowego adresu IP
    public void zapisz(String nowy_adres){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(adres_z_pamieci,nowy_adres);
        editor.commit();
    }
    //odczyt nowego adresu, domyślna wartość jeśli nie ma
    public String odczyt(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String wynik = sharedPref.getString(adres_z_pamieci, adres);
        if(wynik=="") wynik = adres;
        return wynik;
    }
}
