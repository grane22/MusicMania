package com.example.musicmania;

public class Constants {
	
	public static final String MUSIC_MANIA = "MUSIC_MANIA";
	public static final String ANDROID_API_KEY = "AIzaSyDLBJ5plFbkhyTw2ZnhbLuf7OqOkbjGofo";
	public static final String YOUTUBE_VIDEO_SEARCH_TYPE = "video";
	public static final String YOUTUBE_SEARCH_ORDER = "viewCount";
	public static final String YOUTUBE_VIDEO_CAPTION = "videoCaption";
	public static final String CLOSED_CAPTION = "closedCaption";
	public static final long MAX_NUM_OF_RESULTS = 1;
	public static final long MAX_ITEMS_FETCHED = 30;
	public static final String TOPIC_TO_SEARCH = "music";
	public static final String NO_RESULTS = "NO_RESULTS";
	public static final String YES_RESULTS = "YES_RESULTS";
	public static final String YOUTUBE_VIDEO_TAG = "youtube#video";
	public static final String ARRAY_LIST_DATA = "ARRAY_LIST_DATA";
	public static final String MUSIC_CATEGORY_SEARCHED = "MUSIC_CATEGORY_SEARCHED";
	public static final String HASH_MAP_DATA = "HASH_MAP_DATA";
	public static final String SEARCH_TERM = "SEARCH_TERM";
	
	public static final String SONG_RADIO_BTN_TEXT = "song";
	public static final String ARTIST_RADIO_BTN_TEXT = "artist";
	public static final String ALBUM_RADIO_BTN_TEXT = "album";
	
	public static final String SONG_TEXT_MSG = "Enter the song name to search";
	public static final String ARTIST_TEXT_MSG = "Enter the artist name to search";
	public static final String ALBUM_TEXT_MSG = "Enter the album name to search";
	
	public static final String RECOMMEND_SONG_TEXT_MSG = "You have selected song recommendation";
	public static final String RECOMMEND_ARTIST_TEXT_MSG = "You have selected artist recommendation";
	public static final String RECOMMEND_ALBUM_TEXT_MSG = "You have selected album recommendation";

	
	// Constants for Parsing Song Search query
	public static final String SONGID = "SONGID";
	public static final String LAST_FM_API_KEY = "75ef2e0ade33b40cfcd0f765b27d17f5";
	public static final String LAST_FM_URL = "http://ws.audioscrobbler.com/2.0/";
	
	public static final String TRACK_GET_INFO = "track.getInfo";
	public static final String TRACK_GET_SIMILAR = "track.getSimilar";
	public static final String TRACK_SEARCH = "track.search";
	
	public static final String ALBUM_SEARCH = "album.search";
	public static final String ALBUM_GET_INFO = "album.getInfo";
	public static final String ALBUM_GET_SIMILAR = "album.getSimilar";
	
	public static final String ARTIST_SEARCH = "artist.search";
	public static final String ARTIST_GET_INFO = "artist.getInfo";
	public static final String ARTIST_GET_SIMILAR = "artist.getSimilar";
	
	public static final String METHOD = "method";
	public static final String API_KEY = "api_key";
	public static final String ARTIST = "artist";
	public static final String TRACK = "track";	
	public static final String ALBUM = "album";
	public static final String SIMILAR_TRACKS = "similartracks";
	public static final String SIMILAR_ARTISTS = "similarartists";
	public static final String TAG_TOP_ALBUMS = "tag.gettopalbums";
	public static final String TOP_ALBUMS = "topalbums";
	public static final String RESULTS = "results";
	public static final String TRACK_MATCHES = "trackmatches";
	public static final String ALBUM_MATCHES = "albummatches";
	public static final String ARTIST_MATCHES = "artistmatches";
	public static final String VIDEOID = "videoId";	
	public static final String FORMAT = "format";
	
	public static final String JSON = "json";
	public static final String MBID = "mbid";
	public static final String DURATION = "duration";
	public static final String BIO = "bio";
	public static final String LINKS = "links";
	public static final String YEAR_FORMED = "yearformed";
	public static final String PUBLISHED = "published";
	public static final String ALBUM_RELEASE_DATE = "releasedate";
	public static final String WIKI = "wiki";
	public static final String SUMMARY = "summary";
	public static final String TAG = "tag";
	public static final String TAGS = "tags";
	public static final String SONG_TAG = "songTag";
	public static final String TOPTAGS = "toptags";
	public static final String SONG_MAP = "SONG_MAP";
	public static final String ARTIST_MAP = "ARTIST_MAP";
	public static final String ALBUM_MAP = "ALBUM_MAP";
	
	// Database constants
	public static final String DB_NAME = "Music";
	public static final int DB_VERSION = 1;
	public static final String NAME = "name";
	public static final String SONG = "song";
	public static final String KEY_ID = "_id";
	public static final String FAIL = "Fail";
		
	//Playlist Table
	public static final String PLAYLIST_TABLE = "Playlist";
	public static final String EMPTY = "empty";
	public static final String DEFAULT_PLAYLIST_NAME = "user1";
	
	//Artist,Album,Song Table
	public static final String ARTIST_TABLE = "Artist";
	public static final String ALBUM_TABLE = "Album";
	public static final String SONG_TABLE = "Song";
	
	//Recommendation
	public static final int NUM_OF_RECOMMENDATION_PER_SONG = 5;
	public static final int NUM_OF_SEARCHES_PER_SONG = 30;
	
	//User Table
	public static final String USER_TABLE = "User";
	public static final String USER_NAME = "user";
	public static final String USER_PASSWORD = "password";
	
	// User Playlist Table
	public static final String USER_PLAYLIST_TABLE = "UserPlaylist";
	public static final String PLAYLIST_NAME = "playlist";
	public static final String PLAYLIST_SELECTED = "playlistSelected";
	public static final String DEFAULT_PLAYLIST = "Default";
		
	//Follower Table
	public static final String FOLLOWER_TABLE = "follower";
	public static final String FOLLOWER = "follower";
	public static final String FOLLOW = "follow";
	public static final String GO_HOME = "Go Home";
	public static final String FOLLOWING = "following";
	public static final String OTHER_USER_SELECTED = "otherUserSelected";
	public static final String OTHER_USER_PLAYLIST_SELECTED = "otherUserPlayListSelected";
	public static final String IS_FOLLOW_MODE_ON = "isFollowModeOn";
	
	//Shared Prefs
	public static final String CURRENT_LOGGED_USER = "currentUser";
	public static final String NO_LOGGED_USER = "noLoggedUser";
	public static final String COMMON_USER = "User";
	
	
	
	
}
