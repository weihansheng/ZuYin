package com.project.zuji;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.project.zuji.activity.AddActivity;
import com.project.zuji.entity.Const;
import com.special.ResideMenu.ResideMenu;

/**
 * 
 * @author Johan
 * 
 */
public class HomeFragment extends Fragment {

	public static View rootView;
	private ResideMenu resideMenu;
	private BMapManager mBMapManager = null;
	private MapView mMapView = null;
	private MapController mMapController;
	private Button bt_location;
	private TextView bt_start;
	private Context mContext;
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	MyLocationOverlay myLocationOverlay = null;
	LocationData locData = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext=getActivity();
		// 注意：请在试用setContentView前初始化BMapManager对象，否则会报错
		mBMapManager = new BMapManager(this.getActivity());
		mBMapManager.init(Const.BDKey, new MKGeneralListener() {

			@Override
			public void onGetNetworkState(int arg0) {
				if (arg0 == MKEvent.ERROR_NETWORK_CONNECT) {
					Toast.makeText(getActivity(), "网络出错啦！", Toast.LENGTH_LONG)
							.show();
				} else if (arg0 == MKEvent.ERROR_NETWORK_DATA) {
					Toast.makeText(getActivity(), "请输入正确的检索条件！",
							Toast.LENGTH_LONG).show();
				}

			}

			@Override
			public void onGetPermissionState(int iError) {
				if (iError != 0) {
					Toast.makeText(getActivity(), "请输入正确Mapkey！",
							Toast.LENGTH_LONG).show();
					// MapApplication.getInstance().m_keyRight=false;
				} else {
					Toast.makeText(getActivity(), "MapKey认证成功！",
							Toast.LENGTH_LONG).show();
					// MapApplication.getInstance().m_keyRight=true;
				}
			}
		});
		mLocClient = new LocationClient(getActivity());

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型 option.setScanSpan(5000);

		mLocClient.setLocOption(option);
		
		

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.home2, container, false);
		findById(rootView);
		bt_start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getActivity(), AddActivity.class);
				startActivity(intent);

			}
		});
		mMapController = mMapView.getController();
		mMapController.setZoom(15);
		// 设置指南针显示位置 只有旋转地图的时候才显示
		mMapController.setCompassMargin(100, 100);
		/**
		 * 隐藏内置缩放控件
		 */
		mMapView.setBuiltInZoomControls(false);
		//2 隐藏缩放控件 1 隐藏百度logo 隐藏不能用
		//mMapView.removeViewAt(0);
		mMapController.enableClick(true);

		myLocationOverlay = new MyLocationOverlay(mMapView);
		locData = new LocationData();
		myLocationOverlay.setData(locData);
		mMapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		mMapView.refresh();

		startRequestLocation();
		setUpViews();
		// 为Spinner设置内容适配器
		return rootView;
	}

	private void findById(View view) {
		mMapView = (MapView) view.findViewById(R.id.bmapView);
		bt_start = (TextView) view.findViewById(R.id.bt_start);
		bt_location=(Button) view.findViewById(R.id.bt_location);
		bt_location.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (locData!=null) {
					myLocationOverlay.setData(locData);
					mMapView.refresh();
					mMapController.animateTo(new GeoPoint(
							(int) (locData.latitude * 1e6),
							(int) (locData.longitude * 1e6)));
					System.out.println("locData----->"+locData.toString());
					Toast.makeText(getActivity(), "定位成功", 0).show();
				}else {
					Toast.makeText(getActivity(), "定位失败", 0).show();
				}
				
				
			}
		});

	}

	private void setUpViews() {
		MenuActivity parentActivity = (MenuActivity) getActivity();
		resideMenu = parentActivity.getResideMenu();

		// add gesture operation's ignored views
		View ignored_view = rootView.findViewById(R.id.ignored_view);
		resideMenu.addIgnoredView(ignored_view);
	}

	/**
	 * 监听函数，有新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			costTime = System.currentTimeMillis() - startTime;
			Log.d("MapFragment", "" + costTime);
			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			locData.accuracy = location.getRadius();
			locData.direction = location.getDerect();
			myLocationOverlay.setData(locData);
			mMapView.refresh();
			mMapController.animateTo(new GeoPoint(
					(int) (locData.latitude * 1e6),
					(int) (locData.longitude * 1e6)));
			// if request location success , stop it
			stopRequestLocation();
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	private void stopRequestLocation() {
		if (mLocClient != null) {
			mLocClient.unRegisterLocationListener(myListener);
			mLocClient.stop();
		}
	}

	long startTime;
	long costTime;

	private void startRequestLocation() {
		// this nullpoint check is necessary
		if (mLocClient != null) {
			mLocClient.registerLocationListener(myListener);
			mLocClient.start();
			mLocClient.requestLocation();
			startTime = System.currentTimeMillis();
		}
	}

	@Override
	public void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	public void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		if (mLocClient != null)
			mLocClient.stop();
		mMapView.destroy();
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);

	}

}
