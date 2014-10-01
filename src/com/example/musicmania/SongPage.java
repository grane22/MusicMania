package com.example.musicmania;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.musicmania.databasehelpers.PlayListDataSource;
import com.example.musicmania.databasehelpers.SongDataSource;
import com.example.musicmania.databasehelpers.UserPlaylistDataSource;
import com.example.musicmania.databaseobjects.Song;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SongPage extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{
	
	HashMap<String,String> songMap;
	TextView mbid,name,duration,artist,tags;
	Button saveToPlayListButton,recommendSongBtn;
	YouTubePlayerView youTubeView;
	
	UserPlaylistDataSource userPlaylistDataSource;
	PlayListDataSource playListDataSource;	
	SongDataSource songDataSource;
	Spinner showPlayListNames;
	
	SharedPreferences sharedPreferences;
	String videoId,currentLoggedInUser;
	
	List<String> userPlaylistNames;
	boolean isUserPlayListNamesEmpty;
	
	String playListNameSelected;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.song_page_activity);
		
		sharedPreferences = getMusicManiaPreferences(SongPage.this);
	    currentLoggedInUser = sharedPreferences.getString(Constants.CURRENT_LOGGED_USER, Constants.COMMON_USER);
	    
		userPlaylistDataSource = new UserPlaylistDataSource(SongPage.this);
		playListDataSource = new PlayListDataSource(SongPage.this);
		songDataSource = new SongDataSource(SongPage.this);
		userPlaylistDataSource.open();
		songDataSource.open();
		playListDataSource.open();

		Intent currIntent = getIntent();
		songMap = (HashMap<String, String>) currIntent.getSerializableExtra(Constants.SONG_MAP);
		videoId = songMap.get(Constants.VIDEOID);
		Log.d("SongPageVideoId", "videoId -> " + videoId);

		showPlayListNames = (Spinner)findViewById(R.id.playlist_option_spinner);
		
		userPlaylistNames = userPlaylistDataSource.getPlayListNamesForUser(currentLoggedInUser);
		if(userPlaylistNames.size()<1){
			isUserPlayListNamesEmpty = true;
		}else{
			showPlayListNames.setVisibility(View.VISIBLE);
		}
		
		ArrayAdapter<String> spinnerDataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, userPlaylistNames);
		spinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		showPlayListNames.setAdapter(spinnerDataAdapter);
		
		showPlayListNames.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				playListNameSelected = parent.getItemAtPosition(position).toString();
				Toast.makeText(parent.getContext(), 
						"OnItemSelectedListener : " + playListNameSelected,
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub		
			}
		
		});
		
		
		mbid = (TextView) findViewById(R.id.song_page_mbid_text);
		mbid.setText(songMap.get(Constants.MBID));
		name = (TextView) findViewById(R.id.song_page_name_text);
		name.setText(songMap.get(Constants.NAME));
		duration = (TextView) findViewById(R.id.song_page_duration_text);
		duration.setText(songMap.get(Constants.DURATION));
		artist = (TextView) findViewById(R.id.song_page_artist_text);
		artist.setText(songMap.get(Constants.ARTIST));
		tags = (TextView) findViewById(R.id.song_page_tags_text);
		tags.setText(songMap.get(Constants.SONG_TAG));
		
		youTubeView = (YouTubePlayerView) findViewById(R.id.song_page_youtube_demo_view);
		youTubeView.initialize(Constants.ANDROID_API_KEY, this);
		
		
		
		
		saveToPlayListButton = (Button) findViewById(R.id.add_to_playlist_button);
		saveToPlayListButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isUserPlayListNamesEmpty){
					Toast.makeText(SongPage.this, "Cannot add the song. Create a new playlist first!",
							 Toast.LENGTH_LONG).show();
				}else{
					Song song = new Song(name.getText().toString(),
							artist.getText().toString(),
							mbid.getText().toString(),
							videoId);
					long id = playListDataSource.addSongToPlayList(currentLoggedInUser, playListNameSelected, song);
					Toast.makeText(SongPage.this, "Song added to playlist!" + id,
								Toast.LENGTH_LONG).show();
				}				
			}
		});
		
		recommendSongBtn = (Button) findViewById(R.id.recommendUserButton);
		recommendSongBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Song song = new Song(name.getText().toString(),
						artist.getText().toString(),
						mbid.getText().toString(),
						videoId);
				long id = songDataSource.addSongToRecommendList(currentLoggedInUser, song);
				Toast.makeText(SongPage.this, "Song added to recommendation list!" + id,
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
		 playListDataSource.open();	    
	  }

	  @Override
	  protected void onPause() {
		  super.onPause();
		  playListDataSource.close();
	  }
	  
	  private SharedPreferences getMusicManiaPreferences(Context context) {
	        return getSharedPreferences(Constants.MUSIC_MANIA,
	                Context.MODE_PRIVATE);
	  }
}
