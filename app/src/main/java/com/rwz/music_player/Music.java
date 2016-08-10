package com.rwz.music_player;

public class Music {
	private String name;
	private String singer;
	private String musicPath;
	private String lrcPath;
	private String duration;
	private int times;
	private int id;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
	public Music() {
		super();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSinger() {
		return singer;
	}
	public void setSinger(String singer) {
		this.singer = singer;
	}
	public String getMusicPath() {
		return musicPath;
	}
	public void setMusicPath(String musicPath) {
		this.musicPath = musicPath;
	}
	public String getLrcPath() {
		return lrcPath;
	}
	public void setLrcPath(String lrcPath) {
		this.lrcPath = lrcPath;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	@Override
	public String toString() {
		return "Music [name=" + name + ", singer=" + singer + ", musicPath=" + musicPath + ", lrcPath=" + lrcPath
				+ ", duration=" + duration + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((duration == null) ? 0 : duration.hashCode());
		result = prime * result + ((lrcPath == null) ? 0 : lrcPath.hashCode());
		result = prime * result + ((musicPath == null) ? 0 : musicPath.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((singer == null) ? 0 : singer.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Music other = (Music) obj;
		if (duration == null) {
			if (other.duration != null)
				return false;
		} else if (!duration.equals(other.duration))
			return false;
		if (lrcPath == null) {
			if (other.lrcPath != null)
				return false;
		} else if (!lrcPath.equals(other.lrcPath))
			return false;
		if (musicPath == null) {
			if (other.musicPath != null)
				return false;
		} else if (!musicPath.equals(other.musicPath))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (singer == null) {
			if (other.singer != null)
				return false;
		} else if (!singer.equals(other.singer))
			return false;
		return true;
	}
	
}
