package com.example.musicmania;

import com.example.musicmania.databasehelpers.UserPlaylistDataSource;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateNewPlaylist extends Activity{
	private SharedPreferences sharedPreferences;
	private Context context;
	private UserPlaylistDataSource userPlaylistDataSource;
	private EditText enterNewPlaylistEditText;
	private Button submitNewPlaylist;
	private String currentLoggedInUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.create_new_playlist_activity);
	    context = getApplicationContext();
	    sharedPreferences = getMusicManiaPreferences(context);
	    currentLoggedInUser = sharedPreferences.getString(Constants.CURRENT_LOGGED_USER, Constants.COMMON_USER);
	    userPlaylistDataSource = new UserPlaylistDataSource(context);
	    userPlaylistDataSource.open();
	    
	    enterNewPlaylistEditText = (EditText)findViewById(R.id.create_new_playlist_enter_name_editText);
	    
	    submitNewPlaylist = (Button) findViewById(R.id.create_new_playlist_submit_name_btn);
	    submitNewPlaylist.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {		
				String playListName = enterNewPlaylistEditText.getText().toString();
				userPlaylistDataSource.addNewUserPlayList(currentLoggedInUser, playListName);
				Toast.makeText(CreateNewPlaylist.this,
						"Added new playlist for the user " + currentLoggedInUser,
						Toast.LENGTH_LONG).show();
			}
		});
	}
	
	// TODO : Write logic for deleting the playlist.
	
	private SharedPreferences getMusicManiaPreferences(Context context) {
        return getSharedPreferences(Constants.MUSIC_MANIA,
                Context.MODE_PRIVATE);
    }
}
