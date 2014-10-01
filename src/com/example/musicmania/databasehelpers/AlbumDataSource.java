package com.example.musicmania.databasehelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.musicmania.Constants;
import com.example.musicmania.databaseobjects.Album;

public class AlbumDataSource {
	private SQLiteDatabase db; 
	private final Context context; 
	private final MySQLiteDbHelper dbhelper; 
	
	public AlbumDataSource(Context c){
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
	
	public long addAlbumToRecommendList(String userName,Album album){
		 Log.v("UserName", userName); 
		 Log.v("addSongToPlayList", album.getName()); 
		 long insertId = 0;
		try{
			ContentValues values = new ContentValues();		
			values.put(Constants.NAME, album.getName());
			values.put(Constants.TAG, album.getTag());
			values.put(Constants.USER_NAME, userName);			
			insertId = db.insert(Constants.ALBUM_TABLE, null,values);
		}catch (SQLiteException ex) {
			 Log.v("Insert into database exception caught", 
			 ex.getMessage()); 
		}		
		return insertId;		
	}
	
	public Cursor getAlbumsFromUserName(String userName){
		String from[] = {Constants.KEY_ID,Constants.NAME};
	    String where = Constants.USER_NAME + "=? ";
	    String[] whereArgs = new String[]{userName};
	    Cursor cursor = db.query(Constants.ALBUM_TABLE, from, where, whereArgs, null, null, null, null);
	    while(cursor !=null && cursor.moveToNext()){
	    	Log.v("Cursor:read", "SongName:" +cursor.getString(0));
	    }
	    return cursor;
	}
	
	public Cursor getAlbumTagsFromRecommendList(String userName){
		String from[] = {Constants.KEY_ID,Constants.TAG};
	    String where = Constants.USER_NAME + "=? ";
	    String[] whereArgs = new String[]{userName};
	    Cursor cursor = db.query(Constants.ALBUM_TABLE, from, where, whereArgs, null, null, null, null);
	    if(cursor !=null && cursor.moveToNext()){
	    	Log.v("Cursor:read", "MBID:" +cursor.getString(0));
	    }
	    return cursor;
	}
	
	public int getRecommendAlbumListCount(String userName){
		String where = Constants.USER_NAME + "=? ";
	    String[] whereArgs = new String[]{userName};
	    Cursor cursor = db.query(Constants.ALBUM_TABLE, null, where, whereArgs, null, null, null, null);
	    int count = cursor.getCount();
	    return count;
	}
	
	public void deleteAlbumFromRecommendPlayList(String userName){
		db.delete(Constants.ALBUM_TABLE, Constants.USER_NAME
		        + " =? ", new String[]{userName});
	}
}
