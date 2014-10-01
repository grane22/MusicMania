package com.example.musicmania.databaseobjects;

public class Playlist {
	int id;
	String name;
	Song song;
	
	//constructors
	public Playlist(String name, Song song) {
		this.name = name;
		this.song = song;
	}

	public Playlist() {
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

	public Song getSong() {
		return song;
	}

	public void setSong(Song song) {
		this.song = song;
	}
}
