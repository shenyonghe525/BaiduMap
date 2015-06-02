package com.syh.baidumap;

import java.io.Serializable;

public class LocationDTO implements Serializable {

	private static final long serialVersionUID = -493089781800055013L;
	private double latitude;
	private double lontitude;
	private String city;
	private String address;

	public LocationDTO() {

	}

	public LocationDTO(float latitude, float lontitude, String city,
			String address) {
		super();
		this.latitude = latitude;
		this.lontitude = lontitude;
		this.city = city;
		this.address = address;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLontitude() {
		return lontitude;
	}

	public void setLontitude(double lontitude) {
		this.lontitude = lontitude;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "LocationDTO [latitude=" + latitude + ", lontitude=" + lontitude
				+ ", city=" + city + ", address=" + address + "]";
	}

}
