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

import com.example.musicmania.databasehelpers.AlbumDataSource;
import com.example.musicmania.databasehelpers.ArtistDataSource;
import com.example.musicmania.databasehelpers.PlayListDataSource;
import com.example.musicmania.databaseobjects.Album;
import com.example.musicmania.databaseobjects.Artist;
import com.example.musicmania.databaseobjects.Song;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubePlayer.Provider;

public class AlbumPage extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{
	HashMap<String,String> albumMap;
	TextView mbid,name,releaseDate,artist,tags;
	Button goHomeButton,recommendAlbumBtn;
	YouTubePlayerView youTubeView;
	
	AlbumDataSource albumDataSource;
	SharedPreferences sharedPreferences;
	
	String videoId;
	String currentLoggedInUser;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album_page_activity);
		albumDataSource = new AlbumDataSource(AlbumPage.this);
		albumDataSource.open();
		Intent currIntent = getIntent();
		albumMap = (HashMap<String, String>) currIntent.getSerializableExtra(Constants.ALBUM_MAP);
		videoId = albumMap.get(Constants.VIDEOID);
		Log.d("SongPageVideoId", "videoId -> " + videoId);

		sharedPreferences = getMusicManiaPreferences(AlbumPage.this);
		currentLoggedInUser = sharedPreferences.getString(Constants.CURRENT_LOGGED_USER, Constants.COMMON_USER);
		
		mbid = (TextView) findViewById(R.id.album_page_mbid_text);
		mbid.setText(albumMap.get(Constants.MBID));
		name = (TextView) findViewById(R.id.album_page_name_text);
		name.setText(albumMap.get(Constants.NAME));
		artist = (TextView) findViewById(R.id.album_page_artist_text);
		artist.setText(albumMap.get(Constants.ARTIST));
		releaseDate = (TextView) findViewById(R.id.album_release_date_text);
		releaseDate.setText(albumMap.get(Constants.ALBUM_RELEASE_DATE));	
		tags = (TextView) findViewById(R.id.album_page_tags_text);
		tags.setText(albumMap.get(Constants.SONG_TAG));
		
		youTubeView = (YouTubePlayerView) findViewById(R.id.album_page_youtube_demo_view);
		youTubeView.initialize(Constants.ANDROID_API_KEY, this);
		

		goHomeButton = (Button) findViewById(R.id.go_to_main_page);
		goHomeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent homeIntent = new Intent(AlbumPage.this,HomeActivity.class);
				homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(homeIntent);
			}
		});
		
		recommendAlbumBtn = (Button) findViewById(R.id.add_to_album_recommend_list_button);
		recommendAlbumBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Album album = new Album(name.getText().toString(),
								tags.getText().toString());
				long id = albumDataSource.addAlbumToRecommendList(currentLoggedInUser, album);
				Toast.makeText(AlbumPage.this, "Album added to recommendation list!" + id,
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
		 albumDataSource.open();	    
	  }

	  @Override
	  protected void onPause() {
		  super.onPause();
		  albumDataSource.close();
	  }
	  
	  private SharedPreferences getMusicManiaPreferences(Context context) {
	        return getSharedPreferences(Constants.MUSIC_MANIA,
	                Context.MODE_PRIVATE);
	  }
}
