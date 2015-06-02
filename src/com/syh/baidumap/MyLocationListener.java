package com.syh.baidumap;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;

public class MyLocationListener implements BDLocationListener {

	private LocationDTO mDto;

	private Handler mHandler;

	private LocationClient locationClient;

	public MyLocationListener(Handler handler, LocationClient locationClient) {
		this.mHandler = handler;
		this.locationClient = locationClient;
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		// Receive Location
		mDto = new LocationDTO();
		StringBuffer sb = new StringBuffer(1024);
		sb.append("time : ");
		sb.append(location.getTime());
		sb.append("\nerror code : ");
		sb.append(location.getLocType());
		sb.append("\nlatitude : ");
		sb.append(location.getLatitude());
		mDto.setLatitude(location.getLatitude());
		sb.append("\nlontitude : ");
		sb.append(location.getLongitude());
		mDto.setLontitude(location.getLongitude());
		sb.append("\nradius : ");
		sb.append(location.getRadius());
		sb.append("\ncity : ");
		sb.append(location.getCity());
		mDto.setCity(location.getCity());
		if (location.getLocType() == BDLocation.TypeGpsLocation) {
			sb.append("\nspeed : ");
			sb.append(location.getSpeed());
			sb.append("\nsatellite : ");
			sb.append(location.getSatelliteNumber());
			sb.append("\ndirection : ");
			sb.append("\naddr : ");
			sb.append(location.getAddrStr());
			sb.append(location.getDirection());
			mDto.setAddress(location.getAddrStr());
		} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
			sb.append("\naddr : ");
			sb.append(location.getAddrStr());
			mDto.setAddress(location.getAddrStr());
		}
		locationClient.stop();
		System.out.println(mDto);
		setDtoToCaller(sb.toString());
		Log.i("BaiduLocationApiDem", sb.toString());
	}

	/**
	 * 定位完成后的回调函数
	 * 
	 * @param str
	 */
	private void setDtoToCaller(String str) {
		Message message = mHandler.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putSerializable("locationDtoInfo", mDto);
		bundle.putString("textInfo", str);
		message.setData(bundle);
		mHandler.sendMessage(message);
	}

}
