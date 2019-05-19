package com.inersya.monitoringdispenser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GrafikPenggunaan extends Fragment {

    Retrofit retrofit;
    private BarChart chart;

    public GrafikPenggunaan() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grafik_penggunaan, container, false);

        chart = view.findViewById(R.id.penggunaan_chart_view);
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);

        final ArrayList<BarEntry> feeds = new ArrayList();

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
           public void onResponse(Call<Field> call, Response<Field> res) {
               final List<String> dates = new ArrayList<>();

               if(res.code() == 200){
                   for (int i = 0; i < res.body().feeds.size() ; i++) {
                       Log.i("ThingSpeak", res.body().feeds.get(i).entry_id + "");
                       SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                       Date date = null;


                       try {
                           date = formater.parse(res.body().channel.updated_at.trim());
                           if((res.body().feeds.get(i).entry_id % 4) == 0)
                               dates.add(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date));
                           else dates.add("");

                           feeds.add(new BarEntry(res.body().feeds.get(i).entry_id,
                                   Integer.parseInt(res.body().feeds.get(i).field1.trim())));
                       } catch (ParseException e) {
                           e.printStackTrace();
                       }
                   }

                   XAxis xAxis = chart.getXAxis();
                   xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                   xAxis.setDrawGridLines(false);
                   xAxis.setGranularity(1f); // only intervals of 1 day
                   xAxis.setLabelCount(7);
                   xAxis.setValueFormatter(new ValueFormatter() {
                       @Override
                       public String getFormattedValue(float value) {
                           return dates.get((int) value-1);
                       }
                   });

                   BarDataSet bardataset = new BarDataSet(feeds, "Data");
                   chart.animateY(2000);

                   BarData data = new BarData(bardataset);
                   bardataset.setColors(ColorTemplate.MATERIAL_COLORS);
                   chart.setData(data);
               }

           }

           @Override
           public void onFailure(Call<Field> call, Throwable t) {

           }
       });

        return view;
    }

}
