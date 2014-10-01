package com.example.musicmania.databaseobjects;

public class Song {
	int id;
	String name;
	String artist;
	String mbid;
	String videoId;
	
	
	// constructors
    public Song() {
    }
 
    public Song(String name, String artist,String videoId) {
        this.artist = artist;
        this.name = name;
        this.videoId = videoId;
    }
 
    public Song(String name, String artist, String mbid,String videoId) {
    	this.artist = artist;
        this.name = name;
        this.mbid = mbid;
        this.videoId = videoId;
    }
 
	//getters and setters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getVideoId() {
		return videoId;
	}
	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}
	public String getMbid() {
		return mbid;
	}
	public void setMbid(String mbid) {
		this.mbid = mbid;
	}

}
