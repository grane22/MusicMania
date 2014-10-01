package com.example.musicmania;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ProjectHomeActivity extends Activity implements OnClickListener{
	
	private SharedPreferences sharedPreferences;
	private Context context;
	private TextView welcomeUserTextView;
	private Button seeUserProfilePageBtn,searchMusicBtn, seePlaylistBtn, 
					getRecommendationBtn,createPlaylistBtn,logoutBtn;
	private String currentLoggedInUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.final_project_layout);
	    context = getApplicationContext();
	    sharedPreferences = getMusicManiaPreferences(context);
	    currentLoggedInUser = sharedPreferences.getString(Constants.CURRENT_LOGGED_USER, Constants.COMMON_USER);
	    
	    welcomeUserTextView = (TextView) findViewById(R.id.welcome_user_text_view);
	    welcomeUserTextView.setText("Welcome " + currentLoggedInUser);
	    
	    seeUserProfilePageBtn = (Button) findViewById(R.id.see_user_profile_page_btn);
	    seeUserProfilePageBtn.setOnClickListener(this);
	    
	    searchMusicBtn = (Button) findViewById(R.id.search_music_btn);
	    searchMusicBtn.setOnClickListener(this);
	    
	    createPlaylistBtn = (Button) findViewById(R.id.create_new_playlist_button);
	    createPlaylistBtn.setOnClickListener(this);
	    
	    seePlaylistBtn = (Button) findViewById(R.id.see_playlist_button);
	    seePlaylistBtn.setOnClickListener(this);
	    
	    getRecommendationBtn = (Button) findViewById(R.id.get_recommendation_btn);
	    getRecommendationBtn.setOnClickListener(this);	
	    
	    logoutBtn = (Button) findViewById(R.id.logout_button);
	    logoutBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.see_user_profile_page_btn:
			seeUserProfilePage();
			break;
		case R.id.search_music_btn:
			startSearchMusic();
			break;
		case R.id.see_playlist_button:
			seePlayList();
			break;
		case R.id.create_new_playlist_button:
			createNewPlayList();
			break;
		case R.id.get_recommendation_btn:
			getRecommendation();
			break;
		case R.id.logout_button:
			userLogout();
			break;
		}			
	}

	private void seeUserProfilePage() {
		Intent seeUserProfilePage = new Intent(context,UserProfilePage.class);
		startActivity(seeUserProfilePage);		
	}

	private void createNewPlayList() {
		Intent createPlaylistIntent = new Intent(context,CreateNewPlaylist.class);
		startActivity(createPlaylistIntent);
	}
	
	private void userLogout(){
		Editor editor = sharedPreferences.edit();
		editor.putString(Constants.CURRENT_LOGGED_USER, Constants.NO_LOGGED_USER);
		editor.commit();
		
		Intent homeActivity = new Intent(context,HomeActivity.class);
		startActivity(homeActivity);
	}

	private void seePlayList() {
		Intent showPlayListIntent = new Intent(ProjectHomeActivity.this,ShowPlayListNamesFromUser.class);
		showPlayListIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(showPlayListIntent);
	}

	private void getRecommendation() {
		Intent showRecommendationActivity = new Intent(ProjectHomeActivity.this,ShowRecommendationHomePage.class);
		startActivity(showRecommendationActivity);	
		
	}

	private void startSearchMusic() {
		Intent startLastFmApiDemoActivity = new Intent(ProjectHomeActivity.this,LastFmApiSearch.class);
		startActivity(startLastFmApiDemoActivity);		
	}
	
	private SharedPreferences getMusicManiaPreferences(Context context) {
        return getSharedPreferences(Constants.MUSIC_MANIA,
                Context.MODE_PRIVATE);
    }
}