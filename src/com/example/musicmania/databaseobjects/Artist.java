package com.example.musicmania.databaseobjects;

public class Artist {
	int id;
	String name;
	String mbid;
	
	// constructors
	public Artist(){
	}
	
	public Artist(String name,String mbid) {	      
		this.name = name;  
		this.mbid = mbid;
	}
	
	public Artist(String name) {
		this.name = name;
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
	public String getMbid() {
		return mbid;
	}
	public void setMbid(String mbid) {
		this.mbid = mbid;
	}
	
	
}
