package com.example.musicmania;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class ShowRecommendationHomePage extends Activity{
	Context context;
	RadioGroup musicCriteriaRadioGroup;
	RadioButton musicManiaRadioButton;
	String musicCriteriaSelected;
	TextView searchTermTextView;
	Button getRecommendationBtn;
	
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recommendation_home_page_activity);
		
		context = this.getApplicationContext();
		
		searchTermTextView = (TextView)findViewById(R.id.recommend_music_selection_text_view);
		searchTermTextView.setText(Constants.RECOMMEND_SONG_TEXT_MSG);
		musicCriteriaRadioGroup = (RadioGroup) findViewById(R.id.recommend_radio_button_grp);
		
		getRecommendationBtn = (Button) findViewById(R.id.recommend_music_btn);
		getRecommendationBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// get selected radio button from radioGroup
				int selectedId = musicCriteriaRadioGroup.getCheckedRadioButtonId();
				// find the radiobutton by returned id
			    musicManiaRadioButton = (RadioButton) findViewById(selectedId);
			    musicCriteriaSelected = musicManiaRadioButton.getText().toString();			 			   
			    Intent showMusicRecommendation = new Intent(context,ShowMusicRecommendation.class);
			    showMusicRecommendation.putExtra(Constants.MUSIC_CATEGORY_SEARCHED, musicCriteriaSelected);
			    startActivity(showMusicRecommendation);
			}
		});
		
		musicCriteriaRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int selectedId = musicCriteriaRadioGroup.getCheckedRadioButtonId();
				musicManiaRadioButton = (RadioButton) findViewById(selectedId);
				if(Constants.SONG_RADIO_BTN_TEXT.equals(musicManiaRadioButton.getText())){
					searchTermTextView.setText(Constants.RECOMMEND_SONG_TEXT_MSG);
				}else if(Constants.ALBUM_RADIO_BTN_TEXT.equals(musicManiaRadioButton.getText())){
					searchTermTextView.setText(Constants.RECOMMEND_ALBUM_TEXT_MSG);
				}else if(Constants.ARTIST_RADIO_BTN_TEXT.equals(musicManiaRadioButton.getText())){
					searchTermTextView.setText(Constants.RECOMMEND_ARTIST_TEXT_MSG);
				}
			}
		});
		
	}
}
