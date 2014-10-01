package com.example.musicmania;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.musicmania.databasehelpers.AlbumDataSource;
import com.example.musicmania.databasehelpers.ArtistDataSource;
import com.example.musicmania.databasehelpers.PlayListDataSource;
import com.example.musicmania.databasehelpers.SongDataSource;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ShowMusicRecommendation extends Activity{
	Context context;
	Cursor cursor;
	ArrayList<String> listOfMbids;
	HashSet<String> setOfTags;
	ArrayList<String> recommendedSongData;
	ProgressDialog progressDialog;
	ListView listView;
	SharedPreferences sharedPreferences;
	Button deleteAll, deleteSong;
	
	ArtistDataSource artistDataSource;
	AlbumDataSource albumDataSource;
	SongDataSource songDataSource;
	String musicCategorySelection;
	String currentUserLoggedIn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_playlist_songs);
		Intent currIntent = getIntent();
		musicCategorySelection = currIntent.getStringExtra(Constants.MUSIC_CATEGORY_SEARCHED);
		context = this.getApplicationContext();
		listOfMbids = new ArrayList<String>();
		recommendedSongData = new ArrayList<String>();
		setOfTags = new HashSet<String>();
		
		sharedPreferences = getMusicManiaPreferences(context);
		currentUserLoggedIn = sharedPreferences.getString(Constants.CURRENT_LOGGED_USER, Constants.COMMON_USER);
		
		if(Constants.ARTIST_RADIO_BTN_TEXT.equals(musicCategorySelection)){
			artistDataSource = new ArtistDataSource(context);
			artistDataSource.open();
			cursor = artistDataSource.getArtistMbidFromRecommendList(currentUserLoggedIn);
			for(String colName : cursor.getColumnNames()){
				Log.v("ShowPlaylist:OnCreate", colName);
			}
			populateListOfMBIDS(cursor);
			new CallArtistLastFMAPI().execute();			
	    }else if(Constants.ALBUM_RADIO_BTN_TEXT.equals(musicCategorySelection)){
	    	albumDataSource = new AlbumDataSource(context);
	    	albumDataSource.open();
			cursor = albumDataSource.getAlbumTagsFromRecommendList(currentUserLoggedIn);
			for(String colName : cursor.getColumnNames()){
				Log.v("ShowPlaylist:OnCreate", colName);
			}
			populateListOfTags(cursor);
			new CallAlbumLastFMAPI().execute();
	    }else if(Constants.SONG_RADIO_BTN_TEXT.equals(musicCategorySelection)){
	    	songDataSource = new SongDataSource(context);
	    	songDataSource.open();
			cursor = songDataSource.getSongMbidFromRecommendList(currentUserLoggedIn);
			for(String colName : cursor.getColumnNames()){
				Log.v("ShowPlaylist:OnCreate", colName);
			}
			populateListOfMBIDS(cursor);
			 new CallSongLastFMAPI().execute();
	    }
					  	 
	    listView = (ListView) findViewById(R.id.song_playlist_listview);	
	    listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String songDetails = (String) listView.getItemAtPosition(position);				
				Toast.makeText(context,
						songDetails, Toast.LENGTH_LONG).show();				
			}
	    	
		});
	    
	    deleteAll = (Button) findViewById(R.id.delete_entire_playlist_btn);
	    deleteSong = (Button) findViewById(R.id.delete_song_from_playlist_list_btn);
	    
	    deleteAll.setVisibility(View.GONE);
	    deleteSong.setVisibility(View.GONE);
	}
	
	private void populateListOfMBIDS(Cursor cursor){
		cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	String mbid = cursor.getString(1);
	    	if(mbid != null){
	    		listOfMbids.add(mbid);
	    	}
	      cursor.moveToNext();
	    }	    
	    Log.v("SONG/ARTIST_RECOMMEND", "mbid_list:" + listOfMbids.toString());
	}
	
	private void populateListOfTags(Cursor cursor){
		cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	String tags = cursor.getString(1);
	    	if(tags != null){
	    		String[] tagList = tags.split(" ");
	    		for(String tag: tagList){
	    			setOfTags.add(tag);
	    		}	    		
	    	}
	      cursor.moveToNext();
	    }	    
	    Log.v("ALBUM_RECOMMEND", "tag_set:" + setOfTags.toString());

	}
	
	
	@Override
	  protected void onResume() {
		 super.onResume();
		 //playListDataSource.open();	    
	  }

	  @Override
	  protected void onPause() {
		  super.onPause();
		  //playListDataSource.close();
	    
	  }


	private class CallSongLastFMAPI extends AsyncTask<String,Void,String> {
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			progressDialog = new ProgressDialog(ShowMusicRecommendation.this);
			progressDialog.setMessage("Please wait...");
			progressDialog.setCancelable(false);
			progressDialog.show();
			
		}
		
		@Override
		protected String doInBackground(String... args){
			
			for(String mbid:listOfMbids){
				ServiceHandler serviceHandler = new ServiceHandler();
				List<NameValuePair> urlParams = new ArrayList<NameValuePair>();    		
				urlParams.add(new BasicNameValuePair(Constants.METHOD,Constants.TRACK_GET_SIMILAR));
				urlParams.add(new BasicNameValuePair(Constants.API_KEY,Constants.LAST_FM_API_KEY));
				urlParams.add(new BasicNameValuePair(Constants.MBID,mbid));
				urlParams.add(new BasicNameValuePair(Constants.FORMAT,Constants.JSON));
				
				String jsonStr = serviceHandler.makeServiceCall(Constants.LAST_FM_URL, ServiceHandler.GET, urlParams);
				Log.d("Response: ", "> " + jsonStr);
				
				if (jsonStr != null) {
		            try {
		                JSONObject jsonObj = new JSONObject(jsonStr);
		                JSONObject similarTracks = jsonObj.getJSONObject(Constants.SIMILAR_TRACKS);
		                JSONArray tracks = similarTracks.getJSONArray(Constants.TRACK);
		                for(int i=0;i<Constants.NUM_OF_RECOMMENDATION_PER_SONG;i++){
		                	JSONObject track = tracks.getJSONObject(i);
		                	String songName = track.getString(Constants.NAME);
		                	JSONObject artist = track.getJSONObject(Constants.ARTIST);
		                	String artistName = artist.getString(Constants.NAME);
		                	recommendedSongData.add(songName + "-" + artistName);
		                }		      
		            } catch (JSONException e) {
		                e.printStackTrace();
		            }
		        } else {
		            Log.e("ServiceHandler", "Couldn't get any data from the url");
		        }			
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result){
			super.onPostExecute(result);    		
			if (progressDialog.isShowing()){
	            progressDialog.dismiss();
			}
			ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(ShowMusicRecommendation.this, 
					android.R.layout.simple_list_item_1,recommendedSongData);
			listView.setAdapter(itemsAdapter);			    		    
		}
	}
	
	
	private class CallArtistLastFMAPI extends AsyncTask<String,Void,String> {
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			progressDialog = new ProgressDialog(ShowMusicRecommendation.this);
			progressDialog.setMessage("Please wait...");
			progressDialog.setCancelable(false);
			progressDialog.show();
			
		}
		
		@Override
		protected String doInBackground(String... args){
			
			for(String mbid:listOfMbids){
				ServiceHandler serviceHandler = new ServiceHandler();
				List<NameValuePair> urlParams = new ArrayList<NameValuePair>();    		
				urlParams.add(new BasicNameValuePair(Constants.METHOD,Constants.ARTIST_GET_SIMILAR));
				urlParams.add(new BasicNameValuePair(Constants.API_KEY,Constants.LAST_FM_API_KEY));
				urlParams.add(new BasicNameValuePair(Constants.MBID,mbid));
				urlParams.add(new BasicNameValuePair(Constants.FORMAT,Constants.JSON));
				
				String jsonStr = serviceHandler.makeServiceCall(Constants.LAST_FM_URL, ServiceHandler.GET, urlParams);
				Log.d("Response: ", "> " + jsonStr);
				
				if (jsonStr != null) {
		            try {
		                JSONObject jsonObj = new JSONObject(jsonStr);
		                JSONObject similarArtists = jsonObj.getJSONObject(Constants.SIMILAR_ARTISTS);
		                JSONArray artists = similarArtists.getJSONArray(Constants.ARTIST);
		                for(int i=0;i<Constants.NUM_OF_RECOMMENDATION_PER_SONG;i++){
		                	JSONObject artist = artists.getJSONObject(i);
		                	String artistName = artist.getString(Constants.NAME);		                	
		                	recommendedSongData.add(artistName);
		                }		      
		            } catch (JSONException e) {
		                e.printStackTrace();
		            }
		        } else {
		            Log.e("ServiceHandler", "Couldn't get any data from the url");
		        }			
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result){
			super.onPostExecute(result);    		
			if (progressDialog.isShowing()){
	            progressDialog.dismiss();
			}
			ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(ShowMusicRecommendation.this, 
					android.R.layout.simple_list_item_1,recommendedSongData);
			listView.setAdapter(itemsAdapter);			    		    
		}
	}
	
	
	private class CallAlbumLastFMAPI extends AsyncTask<String,Void,String> {
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			progressDialog = new ProgressDialog(ShowMusicRecommendation.this);
			progressDialog.setMessage("Please wait...");
			progressDialog.setCancelable(false);
			progressDialog.show();
			
		}
		
		@Override
		protected String doInBackground(String... args){
			
			for(String tag:setOfTags){
				ServiceHandler serviceHandler = new ServiceHandler();
				List<NameValuePair> urlParams = new ArrayList<NameValuePair>();    		
				urlParams.add(new BasicNameValuePair(Constants.METHOD,Constants.TAG_TOP_ALBUMS));
				urlParams.add(new BasicNameValuePair(Constants.API_KEY,Constants.LAST_FM_API_KEY));
				urlParams.add(new BasicNameValuePair(Constants.TAG,tag));
				urlParams.add(new BasicNameValuePair(Constants.FORMAT,Constants.JSON));
				
				String jsonStr = serviceHandler.makeServiceCall(Constants.LAST_FM_URL, ServiceHandler.GET, urlParams);
				Log.d("Response: ", "> " + jsonStr);
				
				if (jsonStr != null) {
		            try {
		                JSONObject jsonObj = new JSONObject(jsonStr);
		                JSONObject topAlbums = jsonObj.getJSONObject(Constants.TOP_ALBUMS);
		                JSONArray albums = topAlbums.getJSONArray(Constants.ALBUM);
		                for(int i=0;i<Constants.NUM_OF_RECOMMENDATION_PER_SONG;i++){
		                	JSONObject album = albums.getJSONObject(i);
		                	String songName = album.getString(Constants.NAME);
		                	JSONObject artist = album.getJSONObject(Constants.ARTIST);
		                	String artistName = artist.getString(Constants.NAME);
		                	recommendedSongData.add(songName + "-" + artistName);
		                }		      
		            } catch (JSONException e) {
		                e.printStackTrace();
		            }
		        } else {
		            Log.e("ServiceHandler", "Couldn't get any data from the url");
		        }			
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result){
			super.onPostExecute(result);    		
			if (progressDialog.isShowing()){
	            progressDialog.dismiss();
			}
			ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(ShowMusicRecommendation.this, 
					android.R.layout.simple_list_item_1,recommendedSongData);
			listView.setAdapter(itemsAdapter);			    		    
		}
	}
	
	private SharedPreferences getMusicManiaPreferences(Context context) {
        return getSharedPreferences(Constants.MUSIC_MANIA,
                Context.MODE_PRIVATE);
  }
}
