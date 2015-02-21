package com.clarkgarrett.instagramclient;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PhotosActivity extends ActionBarActivity {

    public static final String CLIENT_ID = "a85c428f83ee435799be0d427ca3610e";
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotosAdapter aPhotos;
    private SwipeRefreshLayout srlPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        photos = new ArrayList<InstagramPhoto>();
        aPhotos = new InstagramPhotosAdapter(this,photos);
        ListView lvPhotos =(ListView)findViewById(R.id.lvPhotos);
        lvPhotos.setAdapter(aPhotos);
        fetchPopularPhotos();
        srlPhotos = (SwipeRefreshLayout)findViewById(R.id.srlPhotos);
        /*
        srlPhotos.setColorSchemeResources(android.R.color.holo_blue_dark,
                                          android.R.color.holo_green_light,
                                          android.R.color.holo_orange_light,
                                          android.R.color.holo_red_light);*/
        srlPhotos.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.CYAN,Color.RED);
        srlPhotos.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchPopularPhotos();
            }
        });

        lvPhotos.setOnItemClickListener(new ListView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id){
                Log.i("DEBUG", "Id= " + id + "  Id == caption =" +(id == R.id.tvCaption));

            }
        });


    }

    public void fetchPopularPhotos(){
        String url ="https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;

        photos.clear();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, null, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray photosJSON = null;
                JSONObject temp;
                Log.d("DEBUG", "response=" + response.toString() +"##################################");
                try {
                    photosJSON = response.getJSONArray("data");
                    for (int i = 0; i < photosJSON.length(); i++) {
                        JSONObject photoJSON = photosJSON.getJSONObject(i);
                        //Log.d("DEBUG","###################################################################");
                        //longInfo(photoJSON.toString());
                        InstagramPhoto photo = new InstagramPhoto();
                        photo.username=  photoJSON.getJSONObject("user").getString("username");
                        photo.profilePicUrl = photoJSON.getJSONObject("user").getString("profile_picture");
                        Log.i("DEBUG","" + photoJSON.isNull("caption"));
                        if(photoJSON.isNull("caption")){
                            photo.caption=" ";
                            photo.created_Time=0;
                        }else {
                            photo.caption = photoJSON.getJSONObject("caption").getString("text");
                            photo.created_Time =Long.parseLong(photoJSON.getJSONObject("caption").getString("created_time"));
                            Log.i("DEBUG", "created time= " + photo.created_Time);
                        }
                        photo.imageUrl = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                        photo.imageHeight = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                        photo.likesCount = photoJSON.getJSONObject("likes").getInt("count");
                        //Log.d("DEBUG","here " + i);
                        photos.add(photo);
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
                aPhotos.notifyDataSetChanged();
                srlPhotos.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                srlPhotos.setRefreshing(false);
            }
        });

    }

    public static void longInfo(String str) {
        if(str.length() > 4000) {
            Log.d("DEBUG", str.substring(0, 4000));
            longInfo(str.substring(4000));
        } else
            Log.d("DEBUG", str);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
