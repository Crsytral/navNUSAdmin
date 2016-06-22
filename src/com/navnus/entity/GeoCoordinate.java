package com.navnus.entity;
import java.io.Serializable;

public class GeoCoordinate implements Serializable {
	public double latitude;
	public double longitude;
	
	public GeoCoordinate(double latitude, double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	@Override
	public String toString() {
		return latitude + "," + longitude;
	}
}
