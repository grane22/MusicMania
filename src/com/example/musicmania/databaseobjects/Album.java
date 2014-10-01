package com.example.musicmania.databaseobjects;

public class Album {
	int id;
	String name;
	String tag;
	
	//constructors
	public Album() {
	}
	
	public Album(String name,String tag){
		this.name = name;
		this.tag = tag;
	}

	// Getters and Setters
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

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	
	
	
}
