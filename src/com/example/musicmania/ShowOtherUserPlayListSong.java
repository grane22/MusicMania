package com.example.musicmania;

import java.util.ArrayList;

import com.example.musicmania.databasehelpers.FollowerDataSource;
import com.example.musicmania.databasehelpers.PlayListDataSource;

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

public class ShowOtherUserPlayListSong extends Activity{
	ListView listView;
	PlayListDataSource playListDataSource;
	FollowerDataSource followerDataSource;
	Context context;
	private SharedPreferences sharedPreferences; 
	private ArrayList<String> otherUserPlaylistNames;
	private String loggedInUser;
	private Button followUserBtn;
	private ArrayAdapter<String> sd;
	private String otherUserPlayListSelected;
	private String otherUserSelected;
	private boolean isFollowModeOn; 
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_user_related_activity);
		
		context = this.getApplicationContext();
		sharedPreferences = getMusicManiaPreferences(context);
		Intent currIntent = getIntent();
		otherUserPlayListSelected = currIntent.getStringExtra(Constants.OTHER_USER_PLAYLIST_SELECTED);
		otherUserSelected = currIntent.getStringExtra(Constants.OTHER_USER_SELECTED);
		loggedInUser = sharedPreferences.getString(Constants.CURRENT_LOGGED_USER, Constants.COMMON_USER);
		isFollowModeOn = sharedPreferences.getBoolean(Constants.IS_FOLLOW_MODE_ON, false);
		playListDataSource = new PlayListDataSource(context);
		followerDataSource = new FollowerDataSource(context);
		playListDataSource.open();
		followerDataSource.open();
		
		listView = (ListView) findViewById(R.id.user_oriented_listView);
		
		followUserBtn = (Button) findViewById(R.id.user_oriented_btn);
		if(isFollowModeOn){
			followUserBtn.setText(Constants.FOLLOW);
		}else{
			followUserBtn.setText(Constants.GO_HOME);
		}
		followUserBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(isFollowModeOn){
					if(followerDataSource.checkIfFollowing(loggedInUser,otherUserSelected)){
						Toast.makeText(context,
								loggedInUser + " is already following " + otherUserSelected, Toast.LENGTH_LONG).show();
					}else{
						followerDataSource.addToFollowersList(loggedInUser, otherUserSelected);
						Toast.makeText(context,
							loggedInUser + " is now following " + otherUserSelected, Toast.LENGTH_LONG).show();
					}
				}else{
					Intent goHomeIntent = new Intent(context,ProjectHomeActivity.class);
					goHomeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(goHomeIntent);
				}
			}
		});
		
		otherUserPlaylistNames = playListDataSource.getStringPlayListFromName(otherUserPlayListSelected,otherUserSelected);
		
		sd = new ArrayAdapter<String>(context, R.layout.playlist_row_layout, otherUserPlaylistNames);
		listView.setAdapter(sd); 
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {			
				String otherUserPlayListName = otherUserPlaylistNames.get(position);
				Toast.makeText(context,
						otherUserPlayListName, Toast.LENGTH_LONG).show();				
			}
		});
	}
	
	private SharedPreferences getMusicManiaPreferences(Context context) {
        return getSharedPreferences(Constants.MUSIC_MANIA,
                Context.MODE_PRIVATE);
    }
}
