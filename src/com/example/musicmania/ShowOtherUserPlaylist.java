package com.example.musicmania;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.example.musicmania.databasehelpers.UserPlaylistDataSource;

public class ShowOtherUserPlaylist extends Activity{
	ListView listView;
	UserPlaylistDataSource userPlaylistDataSource;
	Context context;
	private SharedPreferences sharedPreferences; 
	private ArrayList<String> otherUserPlaylistNames;
	private String loggedInUser;
	private Button backToProjectMenuBtn;
	private ArrayAdapter<String> sd;
	private String otherUserSelected;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_user_related_activity);
		
		context = this.getApplicationContext();
		sharedPreferences = getMusicManiaPreferences(context);
		Intent currIntent = getIntent();
		otherUserSelected = currIntent.getStringExtra(Constants.OTHER_USER_SELECTED);
		loggedInUser = sharedPreferences.getString(Constants.CURRENT_LOGGED_USER, Constants.COMMON_USER);
		userPlaylistDataSource = new UserPlaylistDataSource(context);
		userPlaylistDataSource.open();
		
		listView = (ListView) findViewById(R.id.user_oriented_listView);
		
		backToProjectMenuBtn = (Button) findViewById(R.id.user_oriented_btn);
		backToProjectMenuBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent homePageIntent = new Intent(context,ProjectHomeActivity.class);
				homePageIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(homePageIntent);
			}
		});
		
		otherUserPlaylistNames = userPlaylistDataSource.getPlayListNamesForUser(otherUserSelected);
		
		sd = new ArrayAdapter<String>(context, R.layout.playlist_row_layout, otherUserPlaylistNames);
		listView.setAdapter(sd); 
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {			
				String otherUserPlayListName = otherUserPlaylistNames.get(position);
				Toast.makeText(context,
						otherUserPlayListName, Toast.LENGTH_LONG).show();
				Intent showOtherUserPlayListSongs = new Intent(context,ShowOtherUserPlayListSong.class);
				showOtherUserPlayListSongs.putExtra(Constants.OTHER_USER_PLAYLIST_SELECTED, otherUserPlayListName);
				showOtherUserPlayListSongs.putExtra(Constants.OTHER_USER_SELECTED, otherUserSelected);
				showOtherUserPlayListSongs.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(showOtherUserPlayListSongs);
			}
		});
	}
	
	private SharedPreferences getMusicManiaPreferences(Context context) {
        return getSharedPreferences(Constants.MUSIC_MANIA,
                Context.MODE_PRIVATE);
    }
}
