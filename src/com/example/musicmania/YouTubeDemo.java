package com.example.musicmania;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

public class YouTubeDemo extends YouTubeBaseActivity implements OnClickListener,YouTubePlayer.OnFullscreenListener{
	
	Button fetchYouTubeVideoBtn;
	EditText searchTermEditText;
	String videoId;
	String searchTerm;
	
	YouTubePlayerView youTubeView;
	private static YouTube youtube;
	
	public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    public static final JsonFactory JSON_FACTORY = new JacksonFactory();
	
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.youtube_demo_activity);	
		
		
		context = this.getApplicationContext();
		youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_demo_view);
		
		searchTermEditText = (EditText) findViewById(R.id.searchTermEditText);
		
		fetchYouTubeVideoBtn = (Button) findViewById(R.id.search_youtube_video_btn);
		fetchYouTubeVideoBtn.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.search_youtube_video_btn:
			searchTerm = searchTermEditText.getText().toString();
			fetchVideoAndPlay();
		}
	}	

	private void fetchVideoAndPlay() {
		Log.d("Inside fetchVideoAndPlay", searchTerm);
		new CallYouTubeServer().execute(searchTerm);
	}

	
	private class CallYouTubeServer extends AsyncTask<String,Integer,String> implements OnInitializedListener{

		@Override
		protected String doInBackground(String... params) {
			String queryTerm = params[0];
			String result = null;
			try {
			youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
	            public void initialize(HttpRequest request) throws IOException {
	            }
	        }).setApplicationName("youtube-search").build();
			
			YouTube.Search.List search = youtube.search().list("id,snippet");
			search.setKey(Constants.ANDROID_API_KEY);
            search.setQ(queryTerm);
            Log.d("Inside doInBackGround: query term", queryTerm);          
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
                    SearchResult singleVideo = iteratorSearchResults.next();
                    ResourceId rId = singleVideo.getId();
                    // Confirm that the result represents a video. Otherwise, the
                    // item will not contain a video ID.
                    if (rId.getKind().equals(Constants.YOUTUBE_VIDEO_TAG)) {
                        result = rId.getVideoId();
                        //System.out.println(" Title: " + singleVideo.getSnippet().getTitle());                    
                    }
                }
            }
            
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(String result){
			super.onPostExecute(result);			
			if(Constants.NO_RESULTS.equals(result)){
				Toast.makeText(context,"Sorry " + result + " found!",Toast.LENGTH_LONG).show();
			}else{
				videoId = result;
				youTubeView.initialize(Constants.ANDROID_API_KEY, this);				
			}
			
		}
		
		@Override
		public void onInitializationFailure(Provider provider,
				YouTubeInitializationResult error) {
			Toast.makeText(context,"Oh no! " + error.toString(),Toast.LENGTH_LONG).show();
			
		}

		@Override
		public void onInitializationSuccess(Provider provider,
				YouTubePlayer player, boolean wasRestored) {	
			player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
		    //player.setOnFullscreenListener(context);
			player.loadVideo(videoId);		
		}
		
	}


	@Override
	public void onFullscreen(boolean isFullscreen) {
		
		
	}

}
