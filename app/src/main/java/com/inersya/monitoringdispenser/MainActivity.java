package com.inersya.monitoringdispenser;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_air_keluar:
                    loadFragment(new AirKeluar());
                    return true;
                case R.id.navigation_sisa_air:
                    loadFragment(new SisaAirGalon());
                    return true;
                case R.id.navigation_grafik_penggunaan:
                    loadFragment(new GrafikPenggunaan());
                    return true;
            }
            return false;
        }
    };

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    Retrofit retrofit;
    Context context;

    final static String API_ACCOUNT = "74QCJLYQLYO380H0";
    final static String API_READ = "302WHZI3GP8NHJG5";
    final static String CHANNELID = "783655";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFragment(new AirKeluar());
        context = this.getApplicationContext();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.thingspeak.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.app_bar_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.refresh_galon:

                    ThingSpeakServiceRefresh service = retrofit.create(ThingSpeakServiceRefresh.class);
                    Call<List<String>> call = service.reset(API_ACCOUNT);

                    call.enqueue(new Callback<List<String>>() {
                        @Override
                        public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                            Log.i("ThingSpeak", response.code()+"");
                            if(response.code() == 200){
                                Toast.makeText(context, "Sukses Reset", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, "Error " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<String>> call, Throwable t) {
                            Log.e("ThingSpeak", t.getMessage());
                            Toast.makeText(context, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return true;
            }

        return super.onOptionsItemSelected(item);
    }
}
