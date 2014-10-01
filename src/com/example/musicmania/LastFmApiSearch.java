package com.example.musicmania;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class LastFmApiSearch extends Activity{
	
	Context context;
	EditText searchTermEditText;
	TextView searchTermTextView;
	
	Button fetchLastFmDataBtn;
	RadioGroup musicCriteriaRadioGroup;
	RadioButton musicManiaRadioButton;
	ProgressBar searchProgressBar;
	
	String searchTerm;
	String updatedQueryTerm;
	String musicCriteriaSelected;
	
	private ProgressDialog progressDialog;
	
	private ArrayList<String> data = new ArrayList<String>();
	private HashMap<Integer,String> indexSongMBIDMap = new HashMap<Integer, String>();
	
	public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    public static final JsonFactory JSON_FACTORY = new JacksonFactory();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.last_fm_demo_activity);	
		
		context = this.getApplicationContext();
		
		searchTermEditText = (EditText) findViewById(R.id.searchTermEditText);
		searchProgressBar = (ProgressBar) findViewById(R.id.search_progress_bar);
		searchTermTextView = (TextView)findViewById(R.id.enter_search_term_textview);
		searchTermTextView.setText(Constants.SONG_TEXT_MSG);
		
		musicCriteriaRadioGroup = (RadioGroup) findViewById(R.id.music_mania_radio_button_grp);
		
		fetchLastFmDataBtn = (Button) findViewById(R.id.search_fm_btn);
		fetchLastFmDataBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// get selected radio button from radioGroup
				int selectedId = musicCriteriaRadioGroup.getCheckedRadioButtonId();
				// find the radiobutton by returned id
			    musicManiaRadioButton = (RadioButton) findViewById(selectedId);
			    musicCriteriaSelected = musicManiaRadioButton.getText().toString();
			    searchTerm = searchTermEditText.getText().toString();	
			    /*Log.v("Search Term", searchTerm);
			    String[] spacedQueryTerm = searchTerm.split(" ");
			    for(String term:spacedQueryTerm){
			    	Log.v("SPACED QUERY TERM", term);
			    }			    
				StringBuilder sb = new StringBuilder();
				for(int i=0;i<spacedQueryTerm.length;i++){
					sb.append(spacedQueryTerm[i]);
					if(i != (spacedQueryTerm.length - 1)){
						sb.append("%20");
					}				
				}				
				updatedQueryTerm = sb.toString();
				Log.v("Updated Search Term", updatedQueryTerm);*/
			    new SearchLastFMServer().execute(searchTerm,musicCriteriaSelected);
			}
		});
		
		musicCriteriaRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int selectedId = musicCriteriaRadioGroup.getCheckedRadioButtonId();
				musicManiaRadioButton = (RadioButton) findViewById(selectedId);
				if(Constants.SONG_RADIO_BTN_TEXT.equals(musicManiaRadioButton.getText())){
					searchTermTextView.setText(Constants.SONG_TEXT_MSG);
				}else if(Constants.ALBUM_RADIO_BTN_TEXT.equals(musicManiaRadioButton.getText())){
					searchTermTextView.setText(Constants.ALBUM_TEXT_MSG);
				}else if(Constants.ARTIST_RADIO_BTN_TEXT.equals(musicManiaRadioButton.getText())){
					searchTermTextView.setText(Constants.ARTIST_TEXT_MSG);
				}
			}
		});
	}
	
	@Override
	protected void onResume(){
		super.onResume();		
	}
	
	private class SearchLastFMServer extends AsyncTask<String,Integer,String>{
		
		@Override
		protected void onPreExecute() {    
			super.onPreExecute();
			progressDialog = new ProgressDialog(LastFmApiSearch.this);
			progressDialog.setMessage("Please wait...");
			progressDialog.setCancelable(false);
			progressDialog.show();
		}
		
		@Override
		protected String doInBackground(String... params){
			String queryTerm = params[0];			
			String musicManiaRadioBtn = params[1];	
			ServiceHandler serviceHandler = new ServiceHandler();
			List<NameValuePair> urlParams = new ArrayList<NameValuePair>();   
			if(Constants.SONG_RADIO_BTN_TEXT.equals(musicManiaRadioBtn)){
				urlParams.add(new BasicNameValuePair(Constants.METHOD,Constants.TRACK_SEARCH));
				urlParams.add(new BasicNameValuePair(Constants.TRACK,queryTerm));
			}else if(Constants.ALBUM_RADIO_BTN_TEXT.equals(musicManiaRadioBtn)){
				urlParams.add(new BasicNameValuePair(Constants.METHOD,Constants.ALBUM_SEARCH));
				urlParams.add(new BasicNameValuePair(Constants.ALBUM,queryTerm));
			}else if(Constants.ARTIST_RADIO_BTN_TEXT.equals(musicManiaRadioBtn)){
				urlParams.add(new BasicNameValuePair(Constants.METHOD,Constants.ARTIST_SEARCH));
				urlParams.add(new BasicNameValuePair(Constants.ARTIST,queryTerm));
			}
			urlParams.add(new BasicNameValuePair(Constants.API_KEY,Constants.LAST_FM_API_KEY));			
			urlParams.add(new BasicNameValuePair(Constants.FORMAT,Constants.JSON));
			
			String jsonStr = serviceHandler.makeServiceCall(Constants.LAST_FM_URL, ServiceHandler.GET, urlParams);
			Log.d("Response: ", "> " + jsonStr);
			
			if (jsonStr != null) {
				if(Constants.SONG_RADIO_BTN_TEXT.equals(musicManiaRadioBtn)){
		            try {		            	
		                JSONObject jsonObj = new JSONObject(jsonStr);
		                JSONObject results = jsonObj.getJSONObject(Constants.RESULTS);
		                JSONObject trackMatches = results.getJSONObject(Constants.TRACK_MATCHES);
		                JSONArray tracks = trackMatches.getJSONArray(Constants.TRACK);
		                for(int i=0;i<Constants.NUM_OF_SEARCHES_PER_SONG;i++){
		                	JSONObject track = tracks.getJSONObject(i);
		                	String songName = track.getString(Constants.NAME);	               
		                	String artistName = track.getString(Constants.ARTIST);
		                	String mbid = track.getString(Constants.MBID);
		                	data.add(songName + "-" + artistName);
		                	indexSongMBIDMap.put(Integer.valueOf(i), mbid);
		                }		      
		            } catch (JSONException e) {
		                e.printStackTrace();
		            }
	            }else if(Constants.ALBUM_RADIO_BTN_TEXT.equals(musicManiaRadioBtn)){
	            	 try {		            	
			                JSONObject jsonObj = new JSONObject(jsonStr);
			                JSONObject results = jsonObj.getJSONObject(Constants.RESULTS);
			                JSONObject albumMatches = results.getJSONObject(Constants.ALBUM_MATCHES);
			                JSONArray albums = albumMatches.getJSONArray(Constants.ALBUM);
			                for(int i=0;i<Constants.NUM_OF_SEARCHES_PER_SONG;i++){
			                	JSONObject album = albums.getJSONObject(i);
			                	String albumName = album.getString(Constants.NAME);	               
			                	String artistName = album.getString(Constants.ARTIST);
			                	String mbid = album.getString(Constants.MBID);
			                	data.add(albumName + "-" + artistName);
			                	indexSongMBIDMap.put(Integer.valueOf(i), mbid);
			                }		      
			            } catch (JSONException e) {
			                e.printStackTrace();
			            }
	            }else if(Constants.ARTIST_RADIO_BTN_TEXT.equals(musicManiaRadioBtn)){
	            	try {		            	
		                JSONObject jsonObj = new JSONObject(jsonStr);
		                JSONObject results = jsonObj.getJSONObject(Constants.RESULTS);
		                JSONObject artistMatches = results.getJSONObject(Constants.ARTIST_MATCHES);
		                JSONArray artists = artistMatches.getJSONArray(Constants.ARTIST);
		                for(int i=0;i<Constants.NUM_OF_SEARCHES_PER_SONG;i++){
		                	JSONObject artist = artists.getJSONObject(i);
		                	String artistName = artist.getString(Constants.NAME);	               		                
		                	String mbid = artist.getString(Constants.MBID);
		                	data.add(artistName);
		                	indexSongMBIDMap.put(Integer.valueOf(i), mbid);
		                }		      
		            } catch (JSONException e) {
		                e.printStackTrace();
		            }
	            }
	        } else {
	            Log.e("ServiceHandler", "Couldn't get any data from the url");
	        }			
			return null;
		}	
		
		
		@Override
		protected void onPostExecute(String result){
			super.onPostExecute(result);			
   		
    		if (progressDialog.isShowing()){
                progressDialog.dismiss();
    		}
    		if(data.isEmpty()){
    			Toast.makeText(getApplicationContext(),
	    				"No content found for the given search term!", 
	    				Toast.LENGTH_LONG).show();
    		}else{
				Intent showListOfSongsIntent = new Intent(context,ShowListOfSearchItems.class);
				showListOfSongsIntent.putStringArrayListExtra(Constants.ARRAY_LIST_DATA, data);
				showListOfSongsIntent.putExtra(Constants.MUSIC_CATEGORY_SEARCHED, musicCriteriaSelected);
				Log.d("MUSIC_CRITERIA_SELECTED", musicCriteriaSelected);
				showListOfSongsIntent.putExtra(Constants.HASH_MAP_DATA, indexSongMBIDMap);
				Log.d("HASHMAP-BEFORE", "map: " + indexSongMBIDMap);
				showListOfSongsIntent.putExtra(Constants.SEARCH_TERM,updatedQueryTerm);
				startActivity(showListOfSongsIntent);
    		}
		}
		
	}

}
