package org.szvsszke.vitezlo.map.model;

import java.io.Serializable;

import com.google.android.gms.maps.model.LatLng;

// a representation of a waypoint in a gpx file
public class Waypoint implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private LatLng latlng;
	private double longitude;
	private String comment;
	private String description;
	private String link;
	
	public Waypoint(String name, double latitude, double longitude,
			String comment, String description, String link) {
		this.name = name;
		latlng = new LatLng(latitude, longitude);
		this.comment = comment;
		this.description = description;
		this.link = link;
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
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @param link correspondig to the waypoint
	 * */
	public void setLink(String link) {
		this.link = link;
	}
	
	/**
	 * @return the link correspondint to the waypoint
	 * */
	public String getLink() {
		return link;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	/**
	 * @return the latlng representation of coordinates
	 * */
	public LatLng getLatLng () {
		return latlng;
	}
	
	/**
	 * @param set coordinates
	 * */
	public void setLatLng (LatLng latlng) {
		this.latlng = latlng; 
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Waypoint [name=" + name + ", latitude=" + latlng.latitude
				+ ", longitude=" + latlng.longitude + ", comment=" + comment
				+ ", description=" + description 
				+ ", link=" + link + "]";
	}
	
	
}
