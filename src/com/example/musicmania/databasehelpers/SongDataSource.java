package com.example.musicmania.databasehelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.musicmania.Constants;
import com.example.musicmania.databaseobjects.Song;

public class SongDataSource {
	private SQLiteDatabase db; 
	private final Context context; 
	private final MySQLiteDbHelper dbhelper; 
	
	public SongDataSource(Context c){
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
	
	public long addSongToRecommendList(String userName,Song song){
		 Log.v("UserName", userName); 
		 Log.v("addSongToPlayList", song.getArtist()); 
		 long insertId = 0;
		try{
			ContentValues values = new ContentValues();		
			values.put(Constants.USER_NAME, userName);
			values.put(Constants.SONG, song.getName());
			values.put(Constants.ARTIST, song.getArtist());
			values.put(Constants.MBID, song.getMbid());
			insertId = db.insert(Constants.SONG_TABLE, null,values);
		}catch (SQLiteException ex) {
			 Log.v("Insert into database exception caught", 
			 ex.getMessage()); 
		}		
		return insertId;		
	}
	
	public Cursor getSongsFromUserName(String userName){
		String from[] = {Constants.KEY_ID,Constants.SONG, Constants.ARTIST};
	    String where = Constants.USER_NAME + "=? ";
	    String[] whereArgs = new String[]{userName};
	    Cursor cursor = db.query(Constants.SONG_TABLE, from, where, whereArgs, null, null, null, null);
	    if(cursor !=null && cursor.moveToNext()){
	    	Log.v("Cursor:read", "SongName:" +cursor.getString(0));
	    }
	    return cursor;
	}
	
	public Cursor getSongMbidFromRecommendList(String userName){
		String from[] = {Constants.KEY_ID,Constants.MBID};
	    String where = Constants.USER_NAME + "=? ";
	    String[] whereArgs = new String[]{userName};
	    Cursor cursor = db.query(Constants.SONG_TABLE, from, where, whereArgs, null, null, null, null);
	    while(cursor !=null && cursor.moveToNext()){
	    	Log.v("Cursor:read", "MBID:" +cursor.getString(0));
	    }
	    return cursor;
	}
	
	public int getRecommendSongListCount(String userName){
		String where = Constants.USER_NAME + "=? ";
	    String[] whereArgs = new String[]{userName};
	    Cursor cursor = db.query(Constants.SONG_TABLE, null, where, whereArgs, null, null, null, null);
	    int count = cursor.getCount();
	    return count;
	}
	
	public void deleteSongFromRecommendPlayList(String userName){
		db.delete(Constants.SONG_TABLE, Constants.USER_NAME
		        + " =? ", new String[]{userName});
	}
	
}
