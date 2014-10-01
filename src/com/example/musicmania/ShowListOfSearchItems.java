package com.example.musicmania;

import android.app.Activity;
import android.app.ProgressDialog;

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

import com.google.android.youtube.player.YouTubeBaseActivity;
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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowListOfSearchItems extends YouTubeBaseActivity{
	
	private ListView listview;
	private TextView title;
	private Button btn_prev;
	private Button btn_next;

	String searchTerm;
	String musicCriteriaSearched;
	ArrayList<String> data;
	//HashMap<String,String> songTitleVideoIdMap;
	HashMap<Integer,String> indexSongMBIDMap;
	ArrayAdapter<String> sd;
	HashMap<String, String> musicPropertiesMap;
	String selectionTitle;
	private ProgressDialog progressDialog;
	private int pageCount;
	private YouTube youtube;
	
	public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    public static final JsonFactory JSON_FACTORY = new JacksonFactory();

	/**
	 * Using this increment value we can move the listview items
	 */
	private int increment = 0;

	/**
	 * Here set the values, how the ListView to be display
	 * 
	 * Be sure that you must set like this
	 * 
	 * TOTAL_LIST_ITEMS > NUM_ITEMS_PAGE
	 */

	public int TOTAL_LIST_ITEMS = 30;
	public int NUM_ITEMS_PAGE = 12;

	@SuppressWarnings("unchecked")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_list_of_songs_activity);
		Intent currIntent = getIntent();
		searchTerm = currIntent.getStringExtra(Constants.SEARCH_TERM);	
		musicCriteriaSearched = currIntent.getStringExtra(Constants.MUSIC_CATEGORY_SEARCHED);
		data = currIntent.getStringArrayListExtra(Constants.ARRAY_LIST_DATA);
		//songTitleVideoIdMap = (HashMap<String, String>) currIntent.getSerializableExtra(Constants.HASH_MAP_DATA);
		indexSongMBIDMap = (HashMap<Integer, String>) currIntent.getSerializableExtra(Constants.HASH_MAP_DATA);
		Log.d("Data String after passing", "data -> " + data);
		Log.d("HashMap after message pass", "map ->" + indexSongMBIDMap);

		listview = (ListView) findViewById(R.id.list);
		btn_prev = (Button) findViewById(R.id.prev);
		btn_next = (Button) findViewById(R.id.next);
		title = (TextView) findViewById(R.id.title);

		btn_prev.setEnabled(false);

		/**
		 * this block is for checking the number of pages
		 * ====================================================
		 */

		int val = TOTAL_LIST_ITEMS % NUM_ITEMS_PAGE;
		val = val == 0 ? 0 : 1;
		pageCount = TOTAL_LIST_ITEMS / NUM_ITEMS_PAGE + val;
		/**
		 * =====================================================
		 */

		loadList(0);

		btn_next.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				increment++;
				loadList(increment);
				CheckEnable();
			}
		});

		btn_prev.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				increment--;
				loadList(increment);
				CheckEnable();
			}
		});
		
		listview.setOnItemClickListener(new OnItemClickListener(){
			@Override
			  public void onItemClick(AdapterView<?> parent, View view,
			    int position, long id) {
				selectionTitle = getSelectedTitleFromArrayList(position);
			    Toast.makeText(getApplicationContext(),
			    				getSelectedTitleFromArrayList(position), 
			    				Toast.LENGTH_LONG).show();
			    if(Constants.ARTIST_RADIO_BTN_TEXT.equals(musicCriteriaSearched)){
			    	new GetArtistInfo().execute(position+"",selectionTitle);
			    }else if(Constants.ALBUM_RADIO_BTN_TEXT.equals(musicCriteriaSearched)){
			    	new GetAlbumInfo().execute(position+"",selectionTitle);
			    }else if(Constants.SONG_RADIO_BTN_TEXT.equals(musicCriteriaSearched)){
			    	new GetSongInfo().execute(position+"",selectionTitle);
			    }			    
			  }
		});

	}

	/**
	 * Method for enabling and disabling Buttons
	 */
	private void CheckEnable() {
		if (increment + 1 == pageCount) {
			btn_next.setEnabled(false);
		} else if (increment == 0) {
			btn_prev.setEnabled(false);
		} else {
			btn_prev.setEnabled(true);
			btn_next.setEnabled(true);
		}
	}

	/**
	 * Method for loading data in listview
	 * 
	 * @param number
	 */
	private void loadList(int number) {
		ArrayList<String> sort = new ArrayList<String>();
		title.setText("Page " + (number + 1) + " of " + pageCount);

		int start = number * NUM_ITEMS_PAGE;
		for (int i = start; i < (start) + NUM_ITEMS_PAGE; i++) {
			if (i < data.size()) {
				sort.add(data.get(i));
			} else {
				break;
			}
		}
		sd = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, sort);
		listview.setAdapter(sd);
	}
	
    private String getSelectedTitleFromArrayList(int index){
    	return data.get(index);
    }
    
    private class GetSongInfo extends AsyncTask<String,Void,String> {
    	
    	@Override
    	protected void onPreExecute(){
    		super.onPreExecute();
    		progressDialog = new ProgressDialog(ShowListOfSearchItems.this);
    		progressDialog.setMessage("Please wait...");
    		progressDialog.setCancelable(false);
    		progressDialog.show();
    		
    	}
    	
    	@Override
    	protected String doInBackground(String... args){    		
    		String result = null;
    		int songIndex = Integer.parseInt(args[0]);
    		String songTitleSelection = args[1];
    		musicPropertiesMap = new HashMap<String, String>();
    		ServiceHandler serviceHandler = new ServiceHandler();
    		List<NameValuePair> urlParams = new ArrayList<NameValuePair>();  
    		
    		String mbid = indexSongMBIDMap.get(songIndex);
    		if(mbid != null && !mbid.isEmpty()){
    			urlParams.add(new BasicNameValuePair(Constants.MBID,mbid));
    		}else{
    			String[] splitSelectionTerm = songTitleSelection.split("-");
        		String song = splitSelectionTerm[0];
        		String artist = splitSelectionTerm[1];
    			urlParams.add(new BasicNameValuePair(Constants.ARTIST,artist));
        		urlParams.add(new BasicNameValuePair(Constants.TRACK,song));
    		}    		  	
    		urlParams.add(new BasicNameValuePair(Constants.METHOD,Constants.TRACK_GET_INFO));
    		urlParams.add(new BasicNameValuePair(Constants.API_KEY,Constants.LAST_FM_API_KEY));    		
    		urlParams.add(new BasicNameValuePair(Constants.FORMAT,Constants.JSON));
    		
    		String jsonStr = serviceHandler.makeServiceCall(Constants.LAST_FM_URL, ServiceHandler.GET, urlParams);
    		Log.d("Response: ", "> " + jsonStr);
    		
    		if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject track = jsonObj.getJSONObject(Constants.TRACK);
                    musicPropertiesMap.put(Constants.MBID, track.getString(Constants.MBID));
                    musicPropertiesMap.put(Constants.DURATION, track.getString(Constants.DURATION));
                    musicPropertiesMap.put(Constants.NAME,track.getString(Constants.NAME));
                    JSONObject artist = track.getJSONObject(Constants.ARTIST);     
                    musicPropertiesMap.put(Constants.ARTIST,artist.getString(Constants.NAME));
                                     
                    StringBuilder sb = new StringBuilder();
                    JSONObject topTags = track.getJSONObject(Constants.TOPTAGS);
                    JSONArray tags = topTags.getJSONArray(Constants.TAG);
                    for (int i = 0; i < tags.length(); i++) {
                    	JSONObject tag = tags.getJSONObject(i);
                    	if(i == (tags.length() - 1)){
                    		sb.append(tag.getString(Constants.NAME));
                    	}else{
                    		sb.append(tag.getString(Constants.NAME) + " ");
                    	}
                    }                
                    musicPropertiesMap.put(Constants.SONG_TAG, sb.toString());
                    
                    youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
        	            public void initialize(HttpRequest request) throws IOException {
        	            }
        	        }).setApplicationName("youtube-song-search").build();
        			
        			YouTube.Search.List search = youtube.search().list("id,snippet");
        			search.setKey(Constants.ANDROID_API_KEY);
                    search.setQ(songTitleSelection + " [Official Music Video]");
                    Log.d("Inside doInBackGround: query term", songTitleSelection);             
                    search.setVideoCaption(Constants.CLOSED_CAPTION);
                    search.setType(Constants.YOUTUBE_VIDEO_SEARCH_TYPE);                    
                    search.setMaxResults(Constants.MAX_NUM_OF_RESULTS);
                    search.setFields("items(id/kind,id/videoId,snippet/title)");
                    SearchListResponse searchResponse = search.execute();
                    
                    Log.d("Inside doInBackGround: Search Response", searchResponse.toString());
                    
                    List<SearchResult> searchResultList = searchResponse.getItems();
                    if(searchResultList != null){
                    	Iterator<SearchResult> iteratorSearchResults = searchResultList.iterator();
                    	if (!iteratorSearchResults.hasNext()) {
                            result = Constants.NO_RESULTS;
                        }
                    	
                    	while (iteratorSearchResults.hasNext()) {
                            SearchResult searchRes = iteratorSearchResults.next();
                            ResourceId rId = searchRes.getId();
                            // Confirm that the result represents a video. Otherwise, the
                            // item will not contain a video ID.
                            if (rId.getKind().equals(Constants.YOUTUBE_VIDEO_TAG)) {
                                String videoId = rId.getVideoId();
                                String songTitle = searchRes.getSnippet().getTitle();                             
                                musicPropertiesMap.put(Constants.VIDEOID,videoId);
                            }
                    	}
                    }
	            } catch (JSONException e) {
	                e.printStackTrace();
	            } catch (IOException e) {				
					e.printStackTrace();
				}
	        } else {
	            Log.e("ServiceHandler", "Couldn't get any data from the url");
	            result = Constants.NO_RESULTS;
	        }
    		return result;
    	}
    	
    	@Override
    	protected void onPostExecute(String result){
    		super.onPostExecute(result);
    		
    		if (progressDialog.isShowing()){
                progressDialog.dismiss();
    		}    		
    		if(Constants.NO_RESULTS.equals(result)){
    			Toast.makeText(ShowListOfSearchItems.this,"Sorry " + result + " found!",Toast.LENGTH_LONG).show();
    		}else{	    			
	    		Intent songPageIntent = new Intent(ShowListOfSearchItems.this,SongPage.class);
	    		songPageIntent.putExtra(Constants.SONG_MAP, musicPropertiesMap);
	    		Log.d("ON_POST_EXECUTE", "SONG MAP -> " + musicPropertiesMap);
	    		startActivity(songPageIntent);
    		}
    	}
    }
    
    private class GetArtistInfo extends AsyncTask<String,Void,String> {
    	
    	@Override
    	protected void onPreExecute(){
    		super.onPreExecute();
    		progressDialog = new ProgressDialog(ShowListOfSearchItems.this);
    		progressDialog.setMessage("Please wait...");
    		progressDialog.setCancelable(false);
    		progressDialog.show();
    		
    	}
    	
    	@Override
    	protected String doInBackground(String... args){    		
    		String result = null;
    		int artistIndex = Integer.parseInt(args[0]);
    		String selectedArtist = args[1];
    		musicPropertiesMap = new HashMap<String, String>();
    		ServiceHandler serviceHandler = new ServiceHandler();
    		List<NameValuePair> urlParams = new ArrayList<NameValuePair>();  
    		
    		String mbid = indexSongMBIDMap.get(artistIndex);
    		if(mbid != null && !mbid.isEmpty()){
    			urlParams.add(new BasicNameValuePair(Constants.MBID,mbid));
    			Log.v("INSIDE MBID Loop", mbid);
    		}else{    			
    			urlParams.add(new BasicNameValuePair(Constants.ARTIST,selectedArtist));    
    			Log.v("INSIDE ARTIST",(selectedArtist == null)? "No Artist found ": selectedArtist);	
    		}    		  	
    		urlParams.add(new BasicNameValuePair(Constants.METHOD,Constants.ARTIST_GET_INFO));
    		urlParams.add(new BasicNameValuePair(Constants.API_KEY,Constants.LAST_FM_API_KEY));    		
    		urlParams.add(new BasicNameValuePair(Constants.FORMAT,Constants.JSON));
    		
    		String jsonStr = serviceHandler.makeServiceCall(Constants.LAST_FM_URL, ServiceHandler.GET, urlParams);
    		Log.d("Response: ", "> " + jsonStr);
    		
    		if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject artist = jsonObj.getJSONObject(Constants.ARTIST);
                    musicPropertiesMap.put(Constants.MBID, artist.getString(Constants.MBID));                  
                    musicPropertiesMap.put(Constants.NAME,artist.getString(Constants.NAME));    
                    
                    StringBuilder sb = new StringBuilder();
                    JSONObject topTags = artist.getJSONObject(Constants.TAGS);
                    JSONArray tags = topTags.getJSONArray(Constants.TAG);
                    for (int i = 0; i < tags.length(); i++) {
                    	JSONObject tag = tags.getJSONObject(i);
                    	if(i == (tags.length() - 1)){
                    		sb.append(tag.getString(Constants.NAME));
                    	}else{
                    		sb.append(tag.getString(Constants.NAME) + " ");
                    	}
                    }                
                    musicPropertiesMap.put(Constants.SONG_TAG, sb.toString());
                    JSONObject bio = artist.getJSONObject(Constants.BIO);                    
                    Log.v("PUBLISHED", bio.getString(Constants.PUBLISHED));
                    musicPropertiesMap.put(Constants.PUBLISHED,bio.getString(Constants.PUBLISHED));
                      
                    youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
        	            public void initialize(HttpRequest request) throws IOException {
        	            }
        	        }).setApplicationName("youtube-artist-search").build();
        			
        			YouTube.Search.List search = youtube.search().list("id,snippet");
        			search.setKey(Constants.ANDROID_API_KEY);
                    search.setQ(selectedArtist);
                    Log.d("Inside doInBackGround: query term", selectedArtist);             
                    search.setVideoCaption(Constants.CLOSED_CAPTION);
                    search.setType(Constants.YOUTUBE_VIDEO_SEARCH_TYPE);                    
                    search.setMaxResults(Constants.MAX_NUM_OF_RESULTS);
                    search.setFields("items(id/kind,id/videoId,snippet/title)");
                    SearchListResponse searchResponse = search.execute();
                    
                    Log.d("Inside doInBackGround: Search Response", searchResponse.toString());
                    
                    List<SearchResult> searchResultList = searchResponse.getItems();
                    if(searchResultList != null){
                    	Iterator<SearchResult> iteratorSearchResults = searchResultList.iterator();
                    	if (!iteratorSearchResults.hasNext()) {
                            result = Constants.NO_RESULTS;
                        }
                    	
                    	while (iteratorSearchResults.hasNext()) {
                            SearchResult searchRes = iteratorSearchResults.next();
                            ResourceId rId = searchRes.getId();
                            // Confirm that the result represents a video. Otherwise, the
                            // item will not contain a video ID.
                            if (rId.getKind().equals(Constants.YOUTUBE_VIDEO_TAG)) {
                                String videoId = rId.getVideoId();
                                String songTitle = searchRes.getSnippet().getTitle();                             
                                musicPropertiesMap.put(Constants.VIDEOID,videoId);
                            }
                    	}
                    }
	            } catch (JSONException e) {
	                e.printStackTrace();
	            } catch (IOException e) {				
					e.printStackTrace();
				}
	        } else {
	            Log.e("ServiceHandler", "Couldn't get any data from the url");
	            result = Constants.NO_RESULTS;
	        }
    		return result;
    	}
    	
    	@Override
    	protected void onPostExecute(String result){
    		super.onPostExecute(result);
    		
    		if (progressDialog.isShowing()){
                progressDialog.dismiss();
    		}
    		
    		if(Constants.NO_RESULTS.equals(result)){
    			Toast.makeText(ShowListOfSearchItems.this,"Sorry " + result + " found!",Toast.LENGTH_LONG).show();
    		}else{	    		
	    		Intent artistPageIntent = new Intent(ShowListOfSearchItems.this,ArtistPage.class);
	    		artistPageIntent.putExtra(Constants.ARTIST_MAP, musicPropertiesMap);
	    		Log.d("ON_POST_EXECUTE", "ARTIST MAP -> " + musicPropertiesMap);
	    		startActivity(artistPageIntent);
    		}
    	}
    }
    	
	private class GetAlbumInfo extends AsyncTask<String,Void,String> {
		
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			progressDialog = new ProgressDialog(ShowListOfSearchItems.this);
			progressDialog.setMessage("Please wait...");
			progressDialog.setCancelable(false);
			progressDialog.show();
			
		}
		
		@Override
		protected String doInBackground(String... args){			
			String result = null;
			int albumIndex = Integer.parseInt(args[0]);
			String albumTitleSelection = args[1];
			musicPropertiesMap = new HashMap<String, String>();
			ServiceHandler serviceHandler = new ServiceHandler();
			List<NameValuePair> urlParams = new ArrayList<NameValuePair>();  
			
			String mbid = indexSongMBIDMap.get(albumIndex);
			if(mbid != null && !mbid.isEmpty()){
				urlParams.add(new BasicNameValuePair(Constants.MBID,mbid));
			}else{
				String[] splitSelectionTerm = albumTitleSelection.split("-");
	    		String album = splitSelectionTerm[0];
	    		String artist = splitSelectionTerm[1];
				urlParams.add(new BasicNameValuePair(Constants.ALBUM,album));
	    		urlParams.add(new BasicNameValuePair(Constants.ARTIST,artist));
			}    		  	
			urlParams.add(new BasicNameValuePair(Constants.METHOD,Constants.ALBUM_GET_INFO));
			urlParams.add(new BasicNameValuePair(Constants.API_KEY,Constants.LAST_FM_API_KEY));    		
			urlParams.add(new BasicNameValuePair(Constants.FORMAT,Constants.JSON));
			
			String jsonStr = serviceHandler.makeServiceCall(Constants.LAST_FM_URL, ServiceHandler.GET, urlParams);
			Log.d("Response: ", "> " + jsonStr);
			
			if (jsonStr != null) {
	            try {
	                JSONObject jsonObj = new JSONObject(jsonStr);
	                JSONObject album = jsonObj.getJSONObject(Constants.ALBUM);
	                musicPropertiesMap.put(Constants.MBID, album.getString(Constants.MBID));
	                musicPropertiesMap.put(Constants.ALBUM_RELEASE_DATE, album.getString(Constants.ALBUM_RELEASE_DATE));
	                musicPropertiesMap.put(Constants.NAME,album.getString(Constants.NAME));	                 
	                musicPropertiesMap.put(Constants.ARTIST,album.getString(Constants.ARTIST));	               
	                
	                StringBuilder sb = new StringBuilder();
	                JSONObject topTags = album.getJSONObject(Constants.TOPTAGS);
	                JSONArray tags = topTags.getJSONArray(Constants.TAG);
	                for (int i = 0; i < tags.length(); i++) {
	                	JSONObject tag = tags.getJSONObject(i);
	                	if(i == (tags.length() - 1)){
	                		sb.append(tag.getString(Constants.NAME));
	                	}else{
	                		sb.append(tag.getString(Constants.NAME) + " ");
	                	}
	                }                
	                musicPropertiesMap.put(Constants.SONG_TAG, sb.toString());
	                
	                youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
	    	            public void initialize(HttpRequest request) throws IOException {
	    	            }
	    	        }).setApplicationName("youtube-album-search").build();
	    			
	    			YouTube.Search.List search = youtube.search().list("id,snippet");
	    			search.setKey(Constants.ANDROID_API_KEY);
	                search.setQ(albumTitleSelection);
	                Log.d("Inside doInBackGround: query term", albumTitleSelection);             
	                search.setVideoCaption(Constants.CLOSED_CAPTION);
	                search.setType(Constants.YOUTUBE_VIDEO_SEARCH_TYPE);                    
	                search.setMaxResults(Constants.MAX_NUM_OF_RESULTS);
	                search.setFields("items(id/kind,id/videoId,snippet/title)");
	                SearchListResponse searchResponse = search.execute();
	                
	                Log.d("Inside doInBackGround: Search Response", searchResponse.toString());
	                
	                List<SearchResult> searchResultList = searchResponse.getItems();
	                if(searchResultList != null){
	                	Iterator<SearchResult> iteratorSearchResults = searchResultList.iterator();
	                	if (!iteratorSearchResults.hasNext()) {
	                        result = Constants.NO_RESULTS;
	                    }
	                	
	                	while (iteratorSearchResults.hasNext()) {
	                        SearchResult searchRes = iteratorSearchResults.next();
	                        ResourceId rId = searchRes.getId();
	                        // Confirm that the result represents a video. Otherwise, the
	                        // item will not contain a video ID.
	                        if (rId.getKind().equals(Constants.YOUTUBE_VIDEO_TAG)) {
	                            String videoId = rId.getVideoId();
	                            String songTitle = searchRes.getSnippet().getTitle();                             
	                            musicPropertiesMap.put(Constants.VIDEOID,videoId);
	                        }
	                	}
	                }
	            } catch (JSONException e) {
	                e.printStackTrace();
	            } catch (IOException e) {				
					e.printStackTrace();
				}
	        } else {
	            Log.e("ServiceHandler", "Couldn't get any data from the url");
	            result = Constants.NO_RESULTS;
	        }
			return result;
		}
		
		@Override
		protected void onPostExecute(String result){
			super.onPostExecute(result);
			
			if (progressDialog.isShowing()){
	            progressDialog.dismiss();
			}
			
			if(Constants.NO_RESULTS.equals(result)){
				Toast.makeText(ShowListOfSearchItems.this,"Sorry " + result + " found!",Toast.LENGTH_LONG).show();
			}else{				
				Intent albumPageIntent = new Intent(ShowListOfSearchItems.this,AlbumPage.class);
				albumPageIntent.putExtra(Constants.ALBUM_MAP, musicPropertiesMap);
				Log.d("ON_POST_EXECUTE", "ALBUM MAP -> " + musicPropertiesMap);
				startActivity(albumPageIntent);
			}
		}
	}
}
