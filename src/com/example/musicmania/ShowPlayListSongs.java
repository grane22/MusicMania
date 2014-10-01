package com.example.musicmania;

import com.example.musicmania.databasehelpers.PlayListDataSource;
import com.example.musicmania.databasehelpers.UserPlaylistDataSource;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ShowPlayListSongs extends Activity{
	ListView listView;
	PlayListDataSource playListDataSource;
	UserPlaylistDataSource userPlayListDataSource;
	Context context;
	Cursor cursor;
	private SharedPreferences sharedPreferences;
	private String userLoggedIn;
	private String playlistNameSelected; 
	private Button deleteSongFromPlaylistBtn, deleteEntirePlaylistButton;
	private SimpleCursorAdapter simpleCursorAdapter;
	private String[] columns;
	private String songSelected;
	private int[] to;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_playlist_songs);
		
		Intent currIntent = getIntent();
		playlistNameSelected = currIntent.getStringExtra(Constants.PLAYLIST_SELECTED);	
		context = this.getApplicationContext();
		sharedPreferences = getMusicManiaPreferences(context);
		
		playListDataSource = new PlayListDataSource(context);
		userPlayListDataSource = new UserPlaylistDataSource(context);
		playListDataSource.open();
		userPlayListDataSource.open();
		
		userLoggedIn = sharedPreferences.getString(Constants.CURRENT_LOGGED_USER, Constants.COMMON_USER);
		listView = (ListView) findViewById(R.id.song_playlist_listview);
		columns = new String[] { Constants.SONG, Constants.ARTIST };
		to = new int[] { R.id.playlist_song_name_entry, R.id.playlist_artist_name_entry};
		cursor = playListDataSource.getPlayListFromName(playlistNameSelected,userLoggedIn);
		for(String colName : cursor.getColumnNames()){
			Log.v("ShowPlaylist:OnCreate", colName);
		}
		simpleCursorAdapter = new SimpleCursorAdapter(context, R.layout.playlist_name_row_layout, cursor, columns, to,2);
		listView.setAdapter(simpleCursorAdapter); 
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Cursor lcursor = (Cursor) listView.getItemAtPosition(position);
				String songName = lcursor.getString(lcursor.getColumnIndexOrThrow(Constants.SONG));
				songSelected = songName;
				Toast.makeText(context,
						songName, Toast.LENGTH_LONG).show();
			}
		});
		
		
		deleteSongFromPlaylistBtn = (Button) findViewById(R.id.delete_song_from_playlist_list_btn);
		deleteSongFromPlaylistBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				playListDataSource.deleteSongFromPlayList(userLoggedIn, playlistNameSelected,songSelected);
				Cursor newCursor = playListDataSource.getPlayListFromName(playlistNameSelected,userLoggedIn);
				for(String colName : newCursor.getColumnNames()){
					Log.v("ShowPlaylist:OnCreate", colName);
				}
				SimpleCursorAdapter newSimpleCursorAdapter = new SimpleCursorAdapter(context, R.layout.playlist_name_row_layout, newCursor, columns, to,2);
				listView.setAdapter(newSimpleCursorAdapter); 			
			}
		});
		
		deleteEntirePlaylistButton = (Button) findViewById(R.id.delete_entire_playlist_btn);
		deleteEntirePlaylistButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				userPlayListDataSource.deletePlayList(userLoggedIn,playlistNameSelected);
				Intent backToPlayListIntent = new Intent(context,ShowPlayListNamesFromUser.class);
				startActivity(backToPlayListIntent);
			}
		});
	}
	
	
	 @Override
	  protected void onResume() {
		 super.onResume();
		 //playListDataSource.open();	    
	  }

	  @Override
	  protected void onPause() {
		  super.onPause();
		  //playListDataSource.close();
	    
	  }
	  
	  private SharedPreferences getMusicManiaPreferences(Context context) {
	        return getSharedPreferences(Constants.MUSIC_MANIA,
	                Context.MODE_PRIVATE);
	  }
}
