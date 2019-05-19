package com.inersya.monitoringdispenser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class AirKeluar extends Fragment {
    Retrofit retrofit;
    TextView dataText, nameText, lastUpdate, dataTotalText;

    public AirKeluar() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_air_keluar, container, false);
        nameText = view.findViewById(R.id.nameText);
        dataText = view.findViewById(R.id.dataText);
        lastUpdate = view.findViewById(R.id.lastUpdateText);
        dataTotalText = view.findViewById(R.id.dataTotalText);

        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.thingspeak.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        ThingSpeakService service = retrofit.create(ThingSpeakService.class);
        Call<Field> call = service.listFeeds();

        call.enqueue(new Callback<Field>() {
            @Override
            public void onResponse(Call<Field> call, Response<Field> response) {
                Log.i("ThingSpeak", response.body().channel.name+"");
                int totalAirKeluar = findSumWithoutUsingStream(response.body().feeds);
                int panjangFeeds = response.body().feeds.size();
                SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date date = null;

                try {
                    date = formater.parse(response.body().channel.updated_at.trim());
                    Log.i("ThingSpeak", date.toString());

                    if(response.body().feeds.size() > 0) {
                        lastUpdate.setText("Last Updated : \n" + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date));
                        nameText.setText(response.body().channel.name);
                        dataText.setText("Berat air keluar : " + response.body().feeds.get(panjangFeeds - 1).field1.trim() + " ml");
                        dataTotalText.setText("Total air keluar : " + totalAirKeluar + " ml");
                    }else{
                        lastUpdate.setText("");
                        nameText.setText(response.body().channel.name);
                        dataText.setText("No Data");
                        dataTotalText.setText("");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<Field> call, Throwable t) {
                Log.e("ThingSpeak", t.getMessage());
                Toast.makeText(container.getContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
    public static int findSumWithoutUsingStream(List<Feeds> array) {
        int sum = 0;
        for (Feeds value : array) {
            sum += Integer.parseInt(value.field1.trim());
        }
        return sum;
    }
}
