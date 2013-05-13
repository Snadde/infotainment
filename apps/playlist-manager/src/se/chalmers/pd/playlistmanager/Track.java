package se.chalmers.pd.playlistmanager;

public class Track {
	private String name;
	private String artist;
	private String spotifyUri;

	public Track(String name, String artist, String spotifyUri) {
		this.name = name;
		this.artist = artist;
		this.spotifyUri = spotifyUri;
	}

	/**
	 * @return the name of the track
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the artist
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * @return the spotifyUri
	 */
	public String getUri() {
		return spotifyUri;
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof Track) {
			Track track = (Track) object;
			if(track.name.equals(this.name)&&track.artist.equals(this.artist)&&track.spotifyUri.equals(this.spotifyUri))
				return true;
		}
		return false;
	}

}
