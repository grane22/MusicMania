package com.example.musicmania.databasehelpers;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.musicmania.Constants;

public class FollowerDataSource {
	private SQLiteDatabase db; 
	private final Context context; 
	private final MySQLiteDbHelper dbhelper; 
	
	public FollowerDataSource(Context c){
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
	
	public long addToFollowersList(String follower,String following){
		 Log.v("Follower", follower); 
		 Log.v("Following", following); 
		 long insertId = 0;
		try{
			ContentValues values = new ContentValues();		
			values.put(Constants.FOLLOWER, follower);
			values.put(Constants.FOLLOWING, following);		
			insertId = db.insert(Constants.FOLLOWER_TABLE, null,values);
		}catch (SQLiteException ex) {
			 Log.v("Insert into database exception caught", 
			 ex.getMessage()); 
		}		
		return insertId;		
	}
	
	public boolean checkIfFollowing(String follower,String following){
		boolean isFollowing = false;				
	    String where = Constants.FOLLOWER + "=? AND " + Constants.FOLLOWING + " =? ";
	    String[] whereArgs = new String[]{follower,following};
	    Cursor cursor = db.query(Constants.FOLLOWER_TABLE, null, where, whereArgs, null, null, null, null);
		if(cursor.getCount()>0){
			isFollowing = true;
		}
		return isFollowing;
	}
	
	public ArrayList<String> getFollowingListUserName(String follower){
		ArrayList<String> followingListNames = new ArrayList<String>();
		String from[] = {Constants.KEY_ID,Constants.FOLLOWING};
	    String where = Constants.FOLLOWER + "=? ";
	    String[] whereArgs = new String[]{follower};
	    Cursor cursor = db.query(Constants.FOLLOWER_TABLE, from, where, whereArgs, null, null, null, null);
	    while(cursor !=null && cursor.moveToNext()){
	    	Log.v("Following User Name", cursor.getString(cursor.getColumnIndex(Constants.FOLLOWING)));
	    	String followingName = cursor.getString(cursor.getColumnIndex(Constants.FOLLOWING));
	    	followingListNames.add(followingName);
	    }
	    return followingListNames;
	}
	
	public boolean removeFromFollowersList(String follower,String following){
		db.delete(Constants.FOLLOWER_TABLE, Constants.FOLLOWER
		        + " =? AND " + Constants.FOLLOWING + " =? ", new String[]{follower,following});
		boolean isFollowingAnyone = false;				
	    String where = Constants.FOLLOWER + "=? ";
	    String[] whereArgs = new String[]{follower};
	    Cursor cursor = db.query(Constants.FOLLOWER_TABLE, null, where, whereArgs, null, null, null, null);
		if(cursor.getCount()>0){
			isFollowingAnyone = true;
		}
		return isFollowingAnyone;
	}
}
