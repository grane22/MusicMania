package com.example.musicmania;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class HomeActivity extends ActionBarActivity implements OnClickListener{
	
	private Button youtubeDemoBtn, searchLastFmBtn,userLoginBtn,finalProjBtn;
	private SharedPreferences sharedPreferences;
	private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        context = getApplicationContext();
        sharedPreferences = getMusicManiaPreferences(context);
        
        youtubeDemoBtn = (Button) findViewById(R.id.youtube_demo_btn);
        youtubeDemoBtn.setOnClickListener(this);
        
        searchLastFmBtn = (Button) findViewById(R.id.search_last_fm_demo_btn);
        searchLastFmBtn.setOnClickListener(this);
        
        userLoginBtn = (Button) findViewById(R.id.user_login_entry_btn);
        userLoginBtn.setOnClickListener(this);
        
        finalProjBtn = (Button) findViewById(R.id.final_project_btn);
        finalProjBtn.setOnClickListener(this);
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.youtube_demo_btn:
			startYoutubeDemo();
			break;
		case R.id.search_last_fm_demo_btn:
			startSearchLastFMDemo();
			break;
		case R.id.user_login_entry_btn:
			startUserLoginDemo();
			break;
		case R.id.final_project_btn:
			startFinalProject();
			break;
		}
		
	}


	private void startUserLoginDemo() {
		Intent startUserLoginDemoActivity = new Intent(this,LoginPage.class);
		startActivity(startUserLoginDemoActivity);  	
	}


	private void startSearchLastFMDemo() {
		Intent startLastFmApiDemoActivity = new Intent(this,LastFmApiSearch.class);
		startActivity(startLastFmApiDemoActivity);
		
	}


	private void startFinalProject() {
		String loggedInUser = sharedPreferences.getString(Constants.CURRENT_LOGGED_USER, Constants.NO_LOGGED_USER);
		Intent startFinalProjectActivity;
		if(Constants.NO_LOGGED_USER.equals(loggedInUser)){
			startFinalProjectActivity = new Intent(this,LoginPage.class);
		}else{
			startFinalProjectActivity = new Intent(this,ProjectHomeActivity.class);
		}
		startActivity(startFinalProjectActivity);
	}


	private void startYoutubeDemo() {
		Intent startYouTubeDemoActivity = new Intent(this,YouTubeDemo.class);
		startActivity(startYouTubeDemoActivity);
		
	}
	
	
	private SharedPreferences getMusicManiaPreferences(Context context) {
        return getSharedPreferences(Constants.MUSIC_MANIA,
                Context.MODE_PRIVATE);
    }
}
