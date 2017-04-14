package org.szvsszke.vitezlo.map.model;


/**
 * Class for storing various info about a specific track.
 * */
public class TrackDescription {

	/**
	 * possible hike types* 
	 * */
	public enum HikeType {
		WALK, BIKE, COMBINED, SPECIAL
	}

	private String	name;
	private String	id;
	private HikeType type;
	private String 	badge;
	private String 	date;
	private String	routeFileName;
	private String	starting;
	private String	entryFee;
	private String	other;
	private String 	length;
	private String 	levelTime;
	private String[] checkPointIDs;
	
	public TrackDescription(String name, String iD, int type, 
			String routeFileName, String starting, String entryFee, String other,
			String[] checkPointIDs, String badge, String date, String length, String levelTime) {
		this.name = name;
		this.id = iD;
		
		// set type
		switch (type) {
		case 1:
			this.type = HikeType.BIKE;
			break;
		case 2:
			this.type = HikeType.COMBINED;
			break;
		case 3:
			this.type = HikeType.SPECIAL;
			break;
		default:
			this.type = HikeType.WALK;
		}
			
		this.routeFileName = routeFileName;
		this.starting = starting;
		this.entryFee = entryFee;
		this.other = other;
		this.checkPointIDs = checkPointIDs;
		this.badge = badge;
		this.date = date;
		this.length = length;
		this.levelTime = levelTime;
	}
	
	/** @return an array of the public data of a TrackDescription object:
	 * name, length, date, starting, entryFee, other
	 * */
	public String[] getPublicData () {
		return new String[]{length + " km", date, starting, entryFee, levelTime + " óra", other};
	}
	
	/**
	 * @return the type of hike
	 * */
	public HikeType getType () {
		return type;
	}

	/**
	 * @return the length
	 */
	public String getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(String length) {
		this.length = length;
	}

	/**
	 * @return the badge
	 */
	public String getBadge() {
		return badge;
	}

	/**
	 * @param badge the badge to set
	 */
	public void setBadge(String badge) {
		this.badge = badge;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the iD
	 */
	public String getID() {
		return id;
	}

	/**
	 * @param iD the iD to set
	 */
	public void setID(String iD) {
		id = iD;
	}

	/**
	 * @return the routeFileName
	 */
	public String getRouteFileName() {
		return routeFileName;
	}

	/**
	 * @param routeFileName the routeFileName to set
	 */
	public void setRouteFileName(String routeFileName) {
		this.routeFileName = routeFileName;
	}

	/**
	 * @return the starting
	 */
	public String getStarting() {
		return starting;
	}

	/**
	 * @param starting the starting to set
	 */
	public void setStarting(String starting) {
		this.starting = starting;
	}

	/**
	 * @return the entryFee
	 */
	public String getEntryFee() {
		return entryFee;
	}

	/**
	 * @param entryFee the entryFee to set
	 */
	public void setEntryFee(String entryFee) {
		this.entryFee = entryFee;
	}

	/**
	 * @return the other
	 */
	public String getOther() {
		return other;
	}

	/**
	 * @param other the other to set
	 */
	public void setOther(String other) {
		this.other = other;
	}

	/**
	 * @return the checkPointIDs
	 */
	public String[] getCheckPointIDs() {
		return checkPointIDs;
	}

	public String getLevelTime() {
		return levelTime;
	}

	public void setLevelTime(String levelTime) {
		this.levelTime = levelTime;
	}
	
	
	
}