package com.example.musicmania;

import java.util.ArrayList;

import com.example.musicmania.databasehelpers.UserPlaylistDataSource;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ShowPlayListNamesFromUser extends Activity{
	ListView listView;
	UserPlaylistDataSource userPlayListDataSource;
	Context context;
	private SharedPreferences sharedPreferences; 
	private ArrayList<String> playListNames;
	private String loggedInUser;
	private Button backToProjectMenuBtn;
	private ArrayAdapter<String> sd;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_playlist_names_activity);
		
		context = this.getApplicationContext();
		sharedPreferences = getMusicManiaPreferences(context);
		loggedInUser = sharedPreferences.getString(Constants.CURRENT_LOGGED_USER, Constants.COMMON_USER);
		userPlayListDataSource = new UserPlaylistDataSource(context);
		userPlayListDataSource.open();
		
		listView = (ListView) findViewById(R.id.playlist_names_listView);
		
		backToProjectMenuBtn = (Button) findViewById(R.id.back_to_project_main_page_from_playlist_btn);
		backToProjectMenuBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent homePageIntent = new Intent(context,ProjectHomeActivity.class);
				startActivity(homePageIntent);
			}
		});
		
		playListNames = userPlayListDataSource.getPlayListNamesForUser(loggedInUser);
		
		sd = new ArrayAdapter<String>(context, R.layout.playlist_row_layout, playListNames);
		listView.setAdapter(sd); 
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {			
				String playlistName = playListNames.get(position);
				Toast.makeText(context,
						playlistName, Toast.LENGTH_LONG).show();
				Intent showPlayListSongs = new Intent(context,ShowPlayListSongs.class);
				showPlayListSongs.putExtra(Constants.PLAYLIST_SELECTED, playlistName);
				startActivity(showPlayListSongs);
			}
		});
	}
	
	private SharedPreferences getMusicManiaPreferences(Context context) {
        return getSharedPreferences(Constants.MUSIC_MANIA,
                Context.MODE_PRIVATE);
    }
}
