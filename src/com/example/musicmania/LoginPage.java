package com.example.musicmania;

import com.example.musicmania.databasehelpers.UserDataSource;
import com.example.musicmania.databaseobjects.User;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginPage extends Activity{
	
	private Context context;
	private Button submitLoginInfo;
	private EditText username,password;
	private UserDataSource userDataSource;
	private SharedPreferences sharedPreferences;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login_activity);
        context = this.getApplicationContext();
        userDataSource = new UserDataSource(context);
        userDataSource.open();
        sharedPreferences = getMusicManiaPreferences(context);
        
        username = (EditText)findViewById(R.id.login_user_name_edittext);
        password = (EditText)findViewById(R.id.login_user_password_edittext);
        
        submitLoginInfo = (Button) findViewById(R.id.user_authenticate_button);
        submitLoginInfo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean isUserListEmpty = userDataSource.checkIfUserTableIsEmpty();
				if(isUserListEmpty){
					userDataSource.addNewUserToUserTable(new User("adam","adam123"));
					userDataSource.addNewUserToUserTable(new User("lisa","lisa123"));
					userDataSource.addNewUserToUserTable(new User("ryan","ryan123"));
					userDataSource.addNewUserToUserTable(new User("mike","mike123"));
					userDataSource.addNewUserToUserTable(new User("kate","kate123"));				
				}
				
				boolean isUserValid = userDataSource.validateUser(username.getText().toString(), password.getText().toString());
				if(isUserValid){
					Intent projectHomeActivityIntent = new Intent(context,ProjectHomeActivity.class);
					Editor editor = sharedPreferences.edit();
					editor.putString(Constants.CURRENT_LOGGED_USER, username.getText().toString());
					editor.commit();
					startActivity(projectHomeActivityIntent);									
				}else{
					Toast.makeText(LoginPage.this,"Wrong user name and password! Please re-enter.",Toast.LENGTH_LONG).show();
				}
				
			}
		});        
        
    }
	
	private SharedPreferences getMusicManiaPreferences(Context context) {
        return getSharedPreferences(Constants.MUSIC_MANIA,
                Context.MODE_PRIVATE);
    }

}
