package com.syh.baidumap;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;

@SuppressLint("HandlerLeak") public class MainActivity extends Activity implements OnClickListener {

	private MapView mapView;

	private BaiduMap baiduMap;

	private TextView locationInfo;

	public LocationClient mLocationClient = null;
	public MyLocationListener myListener;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			locationInfo.setText(msg.getData().getString("textInfo"));

			LocationDTO dto = new LocationDTO();
			dto = (LocationDTO) msg.getData().get("locationDtoInfo");
			LatLng cenpt = new LatLng(dto.getLatitude(), dto.getLontitude());
			// 定义地图状态
			MapStatus mMapStatus = new MapStatus.Builder().target(cenpt)
					.zoom(15).build();
			// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化

			MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
					.newMapStatus(mMapStatus);
			// 改变地图状态
			baiduMap.setMapStatus(mMapStatusUpdate);
			addCenterPoint(dto.getLatitude(), dto.getLontitude());
		};
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);
		initViews();
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		myListener = new MyLocationListener(mHandler, mLocationClient);
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		initOption();
	}

	private void initViews() {
		mapView = (MapView) findViewById(R.id.bmapView);
		baiduMap = mapView.getMap();
		locationInfo = (TextView) findViewById(R.id.tv_location_info);
		Button location = (Button) findViewById(R.id.btn_location);
		location.setOnClickListener(this);
		Button taxi = (Button) findViewById(R.id.btn_taxi);
		taxi.setOnClickListener(this);
		Button bus = (Button) findViewById(R.id.btn_bus);
		bus.setOnClickListener(this);
		Button walk = (Button) findViewById(R.id.btn_walk);
		walk.setOnClickListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onClick(View v) {
		PlanNode stNode;
		PlanNode enNode;
		// 第一步，创建线路规划检索实例
		RoutePlanSearch mSearch = RoutePlanSearch.newInstance();
		// 清除地图上的覆盖物
		baiduMap.clear();
		switch (v.getId()) {
		case R.id.btn_location:
			mLocationClient.start();
			break;

		case R.id.btn_taxi:

			// 第三步，设置驾车线路规划检索监听者
			mSearch.setOnGetRoutePlanResultListener(listener);
			// 第四步，准备检索起、终点信息
			stNode = PlanNode.withCityNameAndPlaceName("北京", "龙泽");
			enNode = PlanNode.withCityNameAndPlaceName("北京", "西单");
			// 第五步，发起驾车线路规划检索；
			mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode)
					.to(enNode));
			// 第六步，释放检索实例；
			// mSearch.destroy();
			break;

		case R.id.btn_bus:

			// 第三步，设置驾车线路规划检索监听者
			mSearch.setOnGetRoutePlanResultListener(listener);
			// 第四步，准备检索起、终点信息
			stNode = PlanNode.withCityNameAndPlaceName("北京", "龙泽");
			enNode = PlanNode.withCityNameAndPlaceName("北京", "西单");
			// 第五步，发起公交线路规划检索；
			mSearch.transitSearch((new TransitRoutePlanOption()).from(stNode)
					.city("北京").to(enNode));
			// 第六步，释放检索实例；
			// mSearch.destroy();
			break;

		case R.id.btn_walk:

			// 第三步，设置驾车线路规划检索监听者
			mSearch.setOnGetRoutePlanResultListener(listener);
			// 第四步，准备检索起、终点信息
			stNode = PlanNode.withCityNameAndPlaceName("北京", "龙泽");
			enNode = PlanNode.withCityNameAndPlaceName("北京", "西单");
			// 第五步，发起公交线路规划检索；
			mSearch.walkingSearch((new WalkingRoutePlanOption()).from(stNode)
					.to(enNode));
			// 第六步，释放检索实例；
			// mSearch.destroy();
			break;
		}

	}

	/**
	 * 初始化定位参数 Tile:initOption void
	 */
	private void initOption() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
		mLocationClient.setLocOption(option);
	}

	/**
	 * 在指定的位置添加标记 Tile:addCenterPoint
	 * 
	 * @param lat 经度
	 * @param lon 纬度 void
	 */
	private void addCenterPoint(double lat, double lon) {
		// 定义Maker坐标点
		LatLng point = new LatLng(lat, lon);
		// 构建Marker图标
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.biaoji);
		// 构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions().position(point)
				.icon(bitmap);
		// 在地图上添加Marker，并显示
		baiduMap.addOverlay(option);
	}

	/**
	 * 第二步，创建线路规划检索监听者
	 */
	OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
		public void onGetWalkingRouteResult(WalkingRouteResult result) {
			// 获取步行线路规划结果
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(MainActivity.this, "抱歉，未找到结果",
						Toast.LENGTH_SHORT).show();
			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
				// result.getSuggestAddrInfo()
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				// route = result.getRouteLines().get(0);
				WalkingRouteOverlay overlay = new WalkingRouteOverlay(baiduMap);
				baiduMap.setOnMarkerClickListener(overlay);
				// routeOverlay = overlay;
				overlay.setData(result.getRouteLines().get(0));
				overlay.addToMap();
				overlay.zoomToSpan();
			}
		}

		public void onGetTransitRouteResult(TransitRouteResult result) {
			// 获取公交换乘路径规划结果
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(MainActivity.this, "抱歉，未找到结果",
						Toast.LENGTH_SHORT).show();
			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
				// result.getSuggestAddrInfo()
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				TransitRouteOverlay overlay = new TransitRouteOverlay(baiduMap);
				baiduMap.setOnMarkerClickListener(overlay);
				overlay.setData(result.getRouteLines().get(0));
				overlay.addToMap();
				overlay.zoomToSpan();
			}

		}

		public void onGetDrivingRouteResult(DrivingRouteResult result) {
			// 获取驾车线路规划结果
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(MainActivity.this, "抱歉，未找到结果",
						Toast.LENGTH_SHORT).show();
			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
				// result.getSuggestAddrInfo()
				System.out.println("<-----起终点或途经点地址有岐义----->");
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				System.out.println("<-----路线查找成功----->");
				// DrivingRouteLine route = result.getRouteLines().get(0);
				DrivingRouteOverlay overlay = new DrivingRouteOverlay(baiduMap);
				// routeOverlay = overlay;
				baiduMap.setOnMarkerClickListener(overlay);
				overlay.setData(result.getRouteLines().get(0));
				overlay.addToMap();
				overlay.zoomToSpan();
			}
		}
	};
}
