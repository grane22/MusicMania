package com.example.musicmania.databaseobjects;

public class User {
	int id;
	String username;
	String password;
	
	//Constructors
	public User(){
	}
	
	public User(String name,String password){
		this.username = name;
		this.password = password;
	}

	//getters and setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return username;
	}

	public void setUserName(String userName) {
		this.username = userName;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
