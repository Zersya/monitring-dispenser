package com.inersya.monitoringdispenser;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;

interface ThingSpeakService{
    @GET("channels/"+MainActivity.CHANNELID+"/fields/1.json?api_key="+MainActivity.API_READ)
    Call<Field> listFeeds();
}

interface ThingSpeakServiceRefresh{
    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "channels/"+MainActivity.CHANNELID+"/feeds.json", hasBody = true)
    Call<List<String>> reset(@retrofit2.http.Field("api_key") String api_key);
}

class Field {
    Channel channel;
    List<Feeds> feeds;
}
class Channel {
    int id;
    String name;
    String latitude;
    String longtitude;
    String field1;
    String created_at;
    String updated_at;
    int last_entry_at;

    public Channel(int id, String name, String latitude, String longtitude, String field1,
                   String created_at, String updated_at, int last_entry_at) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.field1 = field1;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.last_entry_at = last_entry_at;
    }
}

class Feeds {
    int entry_id;
    String created_at;
    String field1;

    public Feeds(int entry_id, String created_at, String field1) {
        this.entry_id = entry_id;
        this.created_at = created_at;
        this.field1 = field1;
    }
}