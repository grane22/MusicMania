package com.example.musicmania;

import java.util.ArrayList;

import com.example.musicmania.databasehelpers.AlbumDataSource;
import com.example.musicmania.databasehelpers.ArtistDataSource;
import com.example.musicmania.databasehelpers.FollowerDataSource;
import com.example.musicmania.databasehelpers.PlayListDataSource;
import com.example.musicmania.databasehelpers.SongDataSource;
import com.example.musicmania.databasehelpers.UserPlaylistDataSource;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class UserProfilePage extends Activity{
	
	private TextView name,playlists,playlistSongs,recomSongs,recomArtists,recomAlbums,followerListMsg;
	private Button followSomeoneBtn,seeFollowersListBtn,unfollowSomeoneBtn,goBackButton;
	private Spinner showFollowedUserListNames;
	
	private UserPlaylistDataSource userPlaylistDataSource;
	private PlayListDataSource playListDataSource;	
	private SongDataSource songDataSource;
	private ArtistDataSource artistDataSource;
	private AlbumDataSource albumDataSource;
	private FollowerDataSource followerDataSource;

	private SharedPreferences sharedPreferences;
	private Context context;
	
	boolean isFollowerListNamesEmpty;
	private ArrayList<String> followingListNames;
	private String followingUserSelected;
 	
	private String currentLoggedInUser;
	private int numOfPlayList,numOfPlayListSongs,numOfRecomSongs,numOfRecomArtists,numOfRecomAlbums;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_profile_page_activity);
		
		context = getApplicationContext();
		sharedPreferences = getMusicManiaPreferences(context);
	    currentLoggedInUser = sharedPreferences.getString(Constants.CURRENT_LOGGED_USER, Constants.COMMON_USER);
	    
	    userPlaylistDataSource = new UserPlaylistDataSource(context);
	    playListDataSource = new PlayListDataSource(context);
	    songDataSource = new SongDataSource(context);
	    artistDataSource = new ArtistDataSource(context);
	    albumDataSource = new AlbumDataSource(context);
	    followerDataSource = new FollowerDataSource(context);
	    
	    followerDataSource.open();
	    userPlaylistDataSource.open();
	    playListDataSource.open();
	    songDataSource.open();
	    artistDataSource.open();
	    albumDataSource.open();
	    
	    new InitUserProfile().execute(currentLoggedInUser);
	    name = (TextView) findViewById(R.id.user_profile_page_name_textview);
	    name.setText(name.getText().toString() + " - " + currentLoggedInUser);
	    
	    playlists = (TextView) findViewById(R.id.user_profile_page_num_of_playlist_textview);	    	    
	    playlistSongs = (TextView) findViewById(R.id.user_profile_page_num_of_playlist_songs_textview);	  	   
	    recomSongs = (TextView) findViewById(R.id.user_profile_page_num_of_recommend_songs_textview);	    	    
	    recomArtists = (TextView) findViewById(R.id.user_profile_page_num_of_recommend_artist_textview);	   	    
	    recomAlbums = (TextView) findViewById(R.id.user_profile_page_num_of_recommend_album_textview);	
	    followerListMsg = (TextView) findViewById(R.id.user_profile_page_users_followed_textview);
	    
	    showFollowedUserListNames = (Spinner)findViewById(R.id.user_profile_page_follows_spinner);
	    seeFollowersListBtn = (Button) findViewById(R.id.user_profile_see_other_user_playlist_btn);
	    followSomeoneBtn = (Button) findViewById(R.id.user_profile_follow_someone_btn);
	    unfollowSomeoneBtn = (Button) findViewById(R.id.user_profile_unfollow_someone_btn);
	    goBackButton = (Button) findViewById(R.id.go_back_from_user_profile_btn);
	    
	    followingListNames = followerDataSource.getFollowingListUserName(currentLoggedInUser);
		if(followingListNames.size()<1){
			isFollowerListNamesEmpty = true;
		}else{
			showFollowedUserListNames.setVisibility(View.VISIBLE);
			seeFollowersListBtn.setVisibility(View.VISIBLE);
			unfollowSomeoneBtn.setVisibility(View.VISIBLE);
			followerListMsg.setVisibility(View.VISIBLE);
		}
		
		ArrayAdapter<String> spinnerDataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, followingListNames);
		spinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		showFollowedUserListNames.setAdapter(spinnerDataAdapter);
		
		showFollowedUserListNames.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				followingUserSelected = parent.getItemAtPosition(position).toString();
				Toast.makeText(parent.getContext(), 
						"OnItemSelectedListener : " + followingUserSelected,
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub		
			}
		
		});

		followSomeoneBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = sharedPreferences.edit();
				editor.putBoolean(Constants.IS_FOLLOW_MODE_ON, true);
				editor.commit();
				
				Intent followOthersIntent = new Intent(context, FollowOtherUsers.class);
				followOthersIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(followOthersIntent);
			}
		});

		seeFollowersListBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = sharedPreferences.edit();
				editor.putBoolean(Constants.IS_FOLLOW_MODE_ON, false);
				editor.commit();
				
				Intent seeFollowerPlaylist = new Intent(context,ShowOtherUserPlaylist.class);
				seeFollowerPlaylist.putExtra(Constants.OTHER_USER_SELECTED, followingUserSelected);
				seeFollowerPlaylist.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(seeFollowerPlaylist);				
			}
		});

		unfollowSomeoneBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean isFollowingAnyone = followerDataSource.removeFromFollowersList(currentLoggedInUser,followingUserSelected);
				if(!isFollowingAnyone){
					showFollowedUserListNames.setVisibility(View.INVISIBLE);
					seeFollowersListBtn.setVisibility(View.INVISIBLE);
					unfollowSomeoneBtn.setVisibility(View.INVISIBLE);
					followerListMsg.setVisibility(View.INVISIBLE);
				}
				Toast.makeText(context, 
						currentLoggedInUser + " is unfollowing " + followingUserSelected,
						Toast.LENGTH_SHORT).show();
			}
		});

		goBackButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent goBackIntent = new Intent(context,ProjectHomeActivity.class);
				goBackIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(goBackIntent);				
			}
		});
		
	}
	
	private class InitUserProfile extends AsyncTask<String,Void,String> {
    	
    	@Override
    	protected void onPreExecute(){
    		super.onPreExecute();    	    		
    	}
    	
    	@Override
    	protected String doInBackground(String... args){    		
    		String result = null;    		
    		String userLoggedIn = args[0];
    		
    		numOfPlayList = userPlaylistDataSource.getUserPlaylistsCount(userLoggedIn);
    		Log.v("NUM_OF_PLAYLIST", numOfPlayList+"");
    		numOfPlayListSongs = playListDataSource.getUserAllPlayListSongsListCount(userLoggedIn);
    		numOfRecomSongs = songDataSource.getRecommendSongListCount(userLoggedIn);
    		numOfRecomArtists = artistDataSource.getRecommendArtistListCount(userLoggedIn);
    		numOfRecomAlbums = albumDataSource.getRecommendAlbumListCount(userLoggedIn);
    		
    		return result;
    	}
    	
    	@Override
    	protected void onPostExecute(String result){
    		super.onPostExecute(result);
    		playlists.setText(playlists.getText().toString() + " - " + Integer.toString(numOfPlayList));
    		playlistSongs.setText(playlistSongs.getText().toString() + " - " + Integer.toString(numOfPlayListSongs));
    		recomSongs.setText(recomSongs.getText().toString() + " - " + Integer.toString(numOfRecomSongs));
    		recomArtists.setText(recomArtists.getText().toString() + " - " + Integer.toString(numOfRecomArtists));
    		recomAlbums.setText(recomAlbums.getText().toString() + " - " + Integer.toString(numOfRecomAlbums));
    	}
    }
	
	 private SharedPreferences getMusicManiaPreferences(Context context) {
	        return getSharedPreferences(Constants.MUSIC_MANIA,
	                Context.MODE_PRIVATE);	        
	  }
}
