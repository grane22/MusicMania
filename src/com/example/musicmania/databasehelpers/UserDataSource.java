package com.example.musicmania.databasehelpers;

import java.util.ArrayList;

import com.example.musicmania.Constants;
import com.example.musicmania.databaseobjects.Song;
import com.example.musicmania.databaseobjects.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class UserDataSource{
	private SQLiteDatabase db;
	private final Context context; 
	private final MySQLiteDbHelper dbhelper; 
	
	public UserDataSource(Context c){
		context = c;
		dbhelper = MySQLiteDbHelper.getInstance(c);
	}
	
	public void close() 
	{
		db.close(); 
	} 
	
	public void open() throws SQLiteException 
	{
		try { 
			db = dbhelper.getWritableDatabase();
			Log.v("Inside DB open", db.getPath());
		} catch(SQLiteException ex) {
			Log.v("Open database exception caught", ex.getMessage()); 
			db = dbhelper.getReadableDatabase();
		} 
	}
	
	public long addNewUserToUserTable(User user){
		 Log.v("addNewUserToUserTable", 
				 user.getUserName()); 		 
		 long insertId = 0;
		try{
			ContentValues values = new ContentValues();
			values.put(Constants.USER_NAME, user.getUserName());
			values.put(Constants.USER_PASSWORD, user.getPassword());			
			insertId = db.insert(Constants.USER_TABLE, null,values);
		}catch (SQLiteException ex) {
			 Log.v("Insert into database exception caught", 
			 ex.getMessage()); 
		}		
		return insertId;		
	}
	
	public boolean checkIfUserTableIsEmpty(){
		boolean isEmpty = false;
		Cursor cursor = db.rawQuery("SELECT count(*) FROM " + Constants.USER_TABLE,null);
		if(cursor!=null && cursor.moveToFirst()){
			if (cursor.getInt(0) <= 0){
				isEmpty = true;
			}		
		}
		return isEmpty;
	}
	
	public boolean validateUser(String username,String password){
		boolean isValidUser = false;
		String where = Constants.USER_NAME + "=?";
		String[] whereArgs = new String[]{username+""};
		Cursor cursor = db.query(Constants.USER_TABLE, null, where, whereArgs, null, null, null, null);
		while(cursor!=null && cursor.moveToNext()){
			Log.v("Cursor:password", cursor.getString(cursor.getColumnIndex(Constants.USER_PASSWORD)));
			if(password.equals(cursor.getString(cursor.getColumnIndex(Constants.USER_PASSWORD)))){
				isValidUser = true;
			}
		}
		return isValidUser;
	}	
	
	public ArrayList<String> getOtherUserList(String userName){
		ArrayList<String> otherUserList = new ArrayList<String>();
		String where = Constants.USER_NAME + "!=? ";
	    String[] whereArgs = new String[]{userName};
	    Cursor cursor = db.query(Constants.USER_TABLE, null, where, whereArgs, null, null, null, null);
	    while(cursor!=null && cursor.moveToNext()){
	    	Log.v("PlayList Name", cursor.getString(cursor.getColumnIndex(Constants.USER_NAME)));
	    	String otherUserName = cursor.getString(cursor.getColumnIndex(Constants.USER_NAME));
	    	otherUserList.add(otherUserName);
	    }
	    return otherUserList;
	}
}
