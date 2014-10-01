package com.example.musicmania;

import java.util.ArrayList;

import com.example.musicmania.databasehelpers.UserDataSource;

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

public class FollowOtherUsers extends Activity{
	ListView listView;
	UserDataSource userDataSource;
	Context context;
	private SharedPreferences sharedPreferences; 
	private ArrayList<String> otherUserNames;
	private String loggedInUser;
	private Button backToProjectMenuBtn;
	private ArrayAdapter<String> sd;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_user_related_activity);
		
		context = this.getApplicationContext();
		sharedPreferences = getMusicManiaPreferences(context);
		loggedInUser = sharedPreferences.getString(Constants.CURRENT_LOGGED_USER, Constants.COMMON_USER);
		userDataSource = new UserDataSource(context);
		userDataSource.open();
		
		listView = (ListView) findViewById(R.id.user_oriented_listView);
		
		backToProjectMenuBtn = (Button) findViewById(R.id.user_oriented_btn);
		backToProjectMenuBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent homePageIntent = new Intent(context,ProjectHomeActivity.class);
				startActivity(homePageIntent);
			}
		});
		
		otherUserNames = userDataSource.getOtherUserList(loggedInUser);
		
		sd = new ArrayAdapter<String>(context, R.layout.playlist_row_layout, otherUserNames);
		listView.setAdapter(sd); 
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {			
				String otherUserName = otherUserNames.get(position);
				Toast.makeText(context,
						otherUserName, Toast.LENGTH_LONG).show();
				Intent showOtherUserPlayListSongs = new Intent(context,ShowOtherUserPlaylist.class);
				showOtherUserPlayListSongs.putExtra(Constants.OTHER_USER_SELECTED, otherUserName);
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
