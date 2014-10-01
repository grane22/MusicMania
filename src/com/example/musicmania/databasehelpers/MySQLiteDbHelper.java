package com.example.musicmania.databasehelpers;

import com.example.musicmania.Constants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteDbHelper extends SQLiteOpenHelper{
	
	private static MySQLiteDbHelper sInstance;

	// Database creation sql statement
	private static final String CREATE_PLAYLIST_TABLE="create table "+ 
			Constants.PLAYLIST_TABLE+" ("+ 
			Constants.KEY_ID+" integer primary key autoincrement, "+ 
			Constants.USER_NAME+" text not null, "+
			Constants.NAME+" text not null, "+ 
			Constants.SONG+" text not null, "+ 
			Constants.ARTIST+" text not null, "+ 
			Constants.MBID+" text not null);";
	
	private static final String CREATE_ARTIST_TABLE="create table "+ 
			Constants.ARTIST_TABLE+" ("+ 
			Constants.KEY_ID+" integer primary key autoincrement, "+ 
			Constants.USER_NAME+" text not null, "+
			Constants.NAME+" text not null, "+ 			
			Constants.MBID+" text not null);";
	
	private static final String CREATE_ALBUM_TABLE="create table "+ 
			Constants.ALBUM_TABLE+" ("+ 
			Constants.KEY_ID+" integer primary key autoincrement, "+ 
			Constants.USER_NAME+" text not null, "+
			Constants.NAME+" text not null, "+ 
			Constants.TAG+" text not null);";
	
	private static final String CREATE_SONG_TABLE="create table "+ 
			Constants.SONG_TABLE+" ("+ 
			Constants.KEY_ID+" integer primary key autoincrement, "+ 
			Constants.USER_NAME+" text not null, "+
			Constants.SONG+" text not null, "+ 
			Constants.ARTIST+" text not null, "+ 
			Constants.MBID+" text not null);";
	
	private static final String CREATE_USER_TABLE="create table "+ 
			Constants.USER_TABLE+" ("+ 
			Constants.KEY_ID+" integer primary key autoincrement, "+ 
			Constants.USER_NAME+" text not null, "+ 
			Constants.USER_PASSWORD +" text not null);";
	
	private static final String CREATE_USER_PLAYLIST_TABLE="create table "+ 
			Constants.USER_PLAYLIST_TABLE+" ("+ 
			Constants.KEY_ID+" integer primary key autoincrement, "+ 
			Constants.USER_NAME+" text not null, "+ 
			Constants.PLAYLIST_NAME +" text not null);";
	
	private static final String CREATE_FOLLOWER_TABLE="create table "+ 
			Constants.FOLLOWER_TABLE+" ("+ 
			Constants.KEY_ID+" integer primary key autoincrement, "+ 
			Constants.FOLLOWER+" text not null, "+ 
			Constants.FOLLOWING +" text not null);";
	
	
	public static MySQLiteDbHelper getInstance(Context context) {	
	    if (sInstance == null) {
	      sInstance = new MySQLiteDbHelper(context.getApplicationContext());
	      Log.v("MySQLiteDbHelper: getInstance", "new instance "); 
	    }
	    Log.v("MySQLiteDbHelper: getInstance", "reused instance ");
	    return sInstance;
	  }
	
	private MySQLiteDbHelper(Context context){
		super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
	}
	
	@Override 
	public void onCreate(SQLiteDatabase db) {
		Log.v("MyDBhelper onCreate","Creating all the tables"); 
		db.execSQL(CREATE_PLAYLIST_TABLE); 
		db.execSQL(CREATE_USER_TABLE);
		db.execSQL(CREATE_USER_PLAYLIST_TABLE);
		db.execSQL(CREATE_SONG_TABLE); 
		db.execSQL(CREATE_ALBUM_TABLE);
		db.execSQL(CREATE_ARTIST_TABLE);
		db.execSQL(CREATE_FOLLOWER_TABLE);
	}
	
	@Override 
	public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion) {
		Log.w("TaskDBAdapter", "Upgrading from version "+oldVersion +" to "+newVersion +", which will destroy all old data");
		db.execSQL("drop table if exists "+Constants.PLAYLIST_TABLE); 
		onCreate(db);
	}
	
}
