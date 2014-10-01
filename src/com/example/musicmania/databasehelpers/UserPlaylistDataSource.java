package com.example.musicmania.databasehelpers;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.musicmania.Constants;

public class UserPlaylistDataSource {
	
	private SQLiteDatabase db; 
	private final Context context; 
	private final MySQLiteDbHelper dbhelper; 
	
	public UserPlaylistDataSource(Context c){
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
	
	public long addNewUserPlayList(String userName,String playlistName){
		 Log.v("playlist Name", 
				 playlistName); 
		 Log.v("userName", userName); 
		 long insertId = 0;
		try{
			ContentValues values = new ContentValues();
			values.put(Constants.USER_NAME, userName);
			values.put(Constants.PLAYLIST_NAME, playlistName);		
			insertId = db.insert(Constants.USER_PLAYLIST_TABLE, null,values);
		}catch (SQLiteException ex) {
			 Log.v("Insert into database exception caught", 
			 ex.getMessage()); 
		}		
		return insertId;		
	}
	
	public ArrayList<String> getPlayListNamesForUser(String name){
		ArrayList<String> playlistNames = new ArrayList<String>();
		String from[] = {Constants.KEY_ID,Constants.USER_NAME,Constants.PLAYLIST_NAME};
	    String where = Constants.USER_NAME + " =? ";
	    String[] whereArgs = new String[]{name};
	    Cursor cursor = db.query(Constants.USER_PLAYLIST_TABLE, from, where, whereArgs, null, null, null, null);
	    while(cursor !=null && cursor.moveToNext()){
	    	Log.v("PlayList Name", cursor.getString(cursor.getColumnIndex(Constants.PLAYLIST_NAME)));
	    	String playlistName = cursor.getString(cursor.getColumnIndex(Constants.PLAYLIST_NAME));
	    	playlistNames.add(playlistName);
	    }
	    return playlistNames;
	}
	
	public int getUserPlaylistsCount(String userName){
		String where = Constants.USER_NAME + "=? ";
	    String[] whereArgs = new String[]{userName};
	    Cursor cursor = db.query(Constants.USER_PLAYLIST_TABLE, null, where, whereArgs, null, null, null, null);	    
	    int count = cursor.getCount();
	    Log.v("Playlist Count", count+"");
	    return count;
	}
	
	public void deletePlayList(String userName,String playListName){		
		db.delete(Constants.USER_PLAYLIST_TABLE, Constants.PLAYLIST_NAME
		        + "=? AND " + Constants.USER_NAME + " =? ", new String[]{playListName,userName});
		db.delete(Constants.PLAYLIST_TABLE, Constants.NAME
		        + "=? AND " + Constants.USER_NAME + " =? ", new String[]{playListName,userName});
	}
}
