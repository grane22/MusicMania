package com.example.musicmania;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicmania.databasehelpers.ArtistDataSource;
import com.example.musicmania.databasehelpers.PlayListDataSource;
import com.example.musicmania.databaseobjects.Artist;
import com.example.musicmania.databaseobjects.Song;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubePlayer.Provider;

public class ArtistPage extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{
	HashMap<String,String> artistMap;
	TextView mbid,name,yearFormed,tags;
	Button goHomeButton,recommendArtistBtn;
	YouTubePlayerView youTubeView;
	
	ArtistDataSource artistDataSource;
	SharedPreferences sharedPreferences;
	
	String videoId;
	String currentLoggedInUser;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.artist_page_activity);
		artistDataSource = new ArtistDataSource(ArtistPage.this);
		artistDataSource.open();
		Intent currIntent = getIntent();
		artistMap = (HashMap<String, String>) currIntent.getSerializableExtra(Constants.ARTIST_MAP);
		videoId = artistMap.get(Constants.VIDEOID);
		Log.d("SongPageVideoId", "videoId -> " + videoId);

		sharedPreferences = getMusicManiaPreferences(ArtistPage.this);
		currentLoggedInUser = sharedPreferences.getString(Constants.CURRENT_LOGGED_USER, Constants.COMMON_USER);
		
		mbid = (TextView) findViewById(R.id.artist_page_mbid_text);
		mbid.setText(artistMap.get(Constants.MBID));
		name = (TextView) findViewById(R.id.artist_page_name_text);
		name.setText(artistMap.get(Constants.NAME));
		yearFormed = (TextView) findViewById(R.id.artist_year_formed_text);
		yearFormed.setText(artistMap.get(Constants.PUBLISHED));
		tags = (TextView) findViewById(R.id.artist_page_tags_text);
		tags.setText(artistMap.get(Constants.SONG_TAG));
		
		youTubeView = (YouTubePlayerView) findViewById(R.id.artist_page_youtube_demo_view);
		youTubeView.initialize(Constants.ANDROID_API_KEY, this);
		

		goHomeButton = (Button) findViewById(R.id.go_to_main_page);
		goHomeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent homeIntent = new Intent(ArtistPage.this,HomeActivity.class);
				homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(homeIntent);
			}
		});
		
		recommendArtistBtn = (Button) findViewById(R.id.add_to_artist_recommend_list_button);
		recommendArtistBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Artist artist = new Artist(name.getText().toString(),
								mbid.getText().toString());
				long id = artistDataSource.addArtistToRecommendList(currentLoggedInUser, artist);
				Toast.makeText(ArtistPage.this, "Artist added to recommendation list!" + id,
						 Toast.LENGTH_LONG).show();
			}
		});
		
	}


	@Override
	public void onInitializationFailure(Provider provider,
			YouTubeInitializationResult error) {
		 Toast.makeText(this, "Oh no! "+error.toString(),
				 Toast.LENGTH_LONG).show();
		
	}


	@Override
	public void onInitializationSuccess(Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		try{
			player.loadVideo(videoId);
		}catch(IllegalStateException illse){
			return;
		}
	}
	
	@Override
	  protected void onResume() {
		 super.onResume();
		 artistDataSource.open();	    
	  }

	  @Override
	  protected void onPause() {
		  super.onPause();
		  artistDataSource.close();
	  }
	  
	  private SharedPreferences getMusicManiaPreferences(Context context) {
	        return getSharedPreferences(Constants.MUSIC_MANIA,
	                Context.MODE_PRIVATE);
	  }
}
