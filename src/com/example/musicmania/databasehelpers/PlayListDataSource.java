package com.example.musicmania.databasehelpers;

import java.util.ArrayList;

import com.example.musicmania.Constants;
import com.example.musicmania.databaseobjects.Song;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class PlayListDataSource {
	private SQLiteDatabase db; 
	private final Context context; 
	private final MySQLiteDbHelper dbhelper; 
	
	public PlayListDataSource(Context c){
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
	
	public long addSongToPlayList(String userName,String playlistName,Song song){
		 Log.v("addSongToPlayList", 
				 playlistName); 
		 Log.v("addSongToPlayList", song.getArtist()); 
		 long insertId = 0;
		try{
			ContentValues values = new ContentValues();
			values.put(Constants.NAME, playlistName);
			values.put(Constants.USER_NAME, userName);
			values.put(Constants.SONG, song.getName());
			values.put(Constants.ARTIST, song.getArtist());
			values.put(Constants.MBID, song.getMbid());
			insertId = db.insert(Constants.PLAYLIST_TABLE, null,values);
		}catch (SQLiteException ex) {
			 Log.v("Insert into database exception caught", 
			 ex.getMessage()); 
		}		
		return insertId;		
	}
	
	public Cursor getPlayListFromName(String playListName,String userName){
		String from[] = {Constants.KEY_ID,Constants.SONG, Constants.ARTIST};
	    String where = Constants.NAME + "=? AND " + Constants.USER_NAME + " =? ";
	    String[] whereArgs = new String[]{playListName,userName};
	    Cursor cursor = db.query(Constants.PLAYLIST_TABLE, from, where, whereArgs, null, null, null, null);
	    if(cursor !=null && cursor.moveToNext()){
	    	Log.v("Cursor:read", "SongName:" +cursor.getString(0));
	    }
	    return cursor;
	}
	
	public ArrayList<String> getStringPlayListFromName(String playListName,String userName){
		ArrayList<String> playlistSongNames = new ArrayList<String>();
		String from[] = {Constants.KEY_ID,Constants.SONG, Constants.ARTIST};
	    String where = Constants.NAME + "=? AND " + Constants.USER_NAME + " =? ";
	    String[] whereArgs = new String[]{playListName,userName};
	    Cursor cursor = db.query(Constants.PLAYLIST_TABLE, from, where, whereArgs, null, null, null, null);
	    if(cursor !=null && cursor.moveToNext()){
	    	Log.v("PlayListSong Name", cursor.getString(cursor.getColumnIndex(Constants.SONG)));
	    	String songName = cursor.getString(cursor.getColumnIndex(Constants.SONG));
	    	String artistName = cursor.getString(cursor.getColumnIndex(Constants.ARTIST));
	    	playlistSongNames.add(songName + " - " + artistName);
	    }
	    return playlistSongNames;
	}
	
	public Cursor getMbidFromPlayListName(String userName, String playlistName){
		String from[] = {Constants.KEY_ID,Constants.MBID};
	    String where = Constants.NAME + "=? AND " + Constants.USER_NAME + " =? ";
	    String[] whereArgs = new String[]{playlistName,userName};
	    Cursor cursor = db.query(Constants.PLAYLIST_TABLE, from, where, whereArgs, null, null, null, null);
	    while(cursor !=null && cursor.moveToNext()){
	    	Log.v("Cursor:read", "MBID:" +cursor.getString(0));
	    }
	    return cursor;
	}
	
	public int getUserAllPlayListSongsListCount(String userName){
		String where = Constants.USER_NAME + "=? ";
	    String[] whereArgs = new String[]{userName};
	    Cursor cursor = db.query(Constants.PLAYLIST_TABLE, null, where, whereArgs, null, null, null, null);
	    int count = cursor.getCount();
	    return count;
	}
	
	public void deleteSongFromPlayList(String userName,String playListName,String songName){
		db.delete(Constants.PLAYLIST_TABLE, 
				  Constants.NAME + " =? AND " 
				+ Constants.USER_NAME + " =? AND " 
				+ Constants.SONG + " =? ", new String[]{playListName,userName,songName});
	}
	
}
