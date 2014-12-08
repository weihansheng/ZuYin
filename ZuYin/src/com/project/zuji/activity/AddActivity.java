package com.project.zuji.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.project.zuji.R;
import com.project.zuji.entity.Const;
import com.project.zuji.entity.Line;
import com.project.zuji.entity.List_LineInfo;
import com.project.zuji.entity.PointOverlay;
import com.project.zuji.service.DatebaseService;
import com.project.zuji.tools.SymbolTools;
import com.project.zuji.tools.TextTool;
import com.project.zuji.tools.TimeUtil;
import com.project.zuji.tools.ToastTool;

public class AddActivity extends Activity implements BDLocationListener,
		OnTouchListener {
	private View backLayout;
	private BMapManager mapman = null;
	private MapView mapview = null;

	private MapController mapcontroller;
	// 我的位置相关
	private MyLocationOverlay mylocoverlay;
	private MKMapViewListener mylistener;
	// 定位相关
	private LocationClient locClient;
	private LocationClientOption option;
	private LocationData locData;

	/**
	 * 记录行走轨迹跟播放行走轨迹相关
	 */
	// 画线相关
	private GraphicsOverlay graphicsoverlay;
	Double startLat, startLng, endLat, endLng;
	String latlng_start, latlng_end;
	GeoPoint point_end, point_start;
	List<GeoPoint> myListGeo = new ArrayList<GeoPoint>();
	List<GeoPoint> myListGeo2 = new ArrayList<GeoPoint>();

	String[] startlatlng = new String[2];
	String[] endlatlng = new String[2];
	// 轨迹相关
	private Line line;
	private List<List_LineInfo> list = null;
	private String tripMethod;

	// 记录相关
	private long mstartTime = 0;
	private long mLongRunTime = 0;
	private String startTime;
	private String runTime;
	private double distansce = 0;
	private String distanceStr="0";
	private TextView tvDistance;
	private TextView tvRuntime;
	private TextView tvMethod;
	private ImageView imMethod;

	/**
	 * 添加自定义View
	 */
	private final int ID_record = 0, ID_track = 1;
	private View v_record;
	private MapView.LayoutParams layoutparams_record, layoutparams_track;
	// private MapView.LayoutParams layoutparams_record, layoutparams_track;

	// 提示
	ToastTool toast = new ToastTool(this);
	private int count = 0;

	/**
	 * 画Markert
	 */
	private PointOverlay pointOVerlay = null;
	private OverlayItem overlay_item = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// tripMethod= getIntent().getStringExtra("tripMethod");
		tripMethod = Const.METHOD;
		System.out.println("tripMethod---第二页" + tripMethod);
		mapman = new BMapManager(getApplication());
		mapman.init(Const.BDKey, null);
		setContentView(R.layout.activity_add_route);
		init();
		mapview.setBuiltInZoomControls(false);
		mapcontroller = mapview.getController();
		mapcontroller.setZoom(15);
		mapcontroller.enableClick(true);

		mylocoverlay = new MyLocationOverlay(mapview);
		mylocoverlay.enableCompass();

		locData = new LocationData();
		locClient = new LocationClient(getApplicationContext());
		locClient.registerLocationListener(AddActivity.this);
		// 初始化定位
		init_location();
		locClient.start();
		locClient.requestLocation();

		// 地图添加:画线图层、画Markert图层和我的位置图层
		graphicsoverlay = new GraphicsOverlay(mapview);
		mapview.getOverlays().add(graphicsoverlay);
		pointOVerlay = new PointOverlay(null, this, mapview);
		mapview.getOverlays().add(pointOVerlay);
		mapview.getOverlays().add(mylocoverlay);
		mapview.regMapViewListener(mapman, mylistener);

		// 地图上添加自定义控件
		Display display = getWindowManager().getDefaultDisplay();
		@SuppressWarnings("deprecation")
		int width = display.getWidth();
		int height = display.getHeight();

		// 记录轨迹按钮
		v_record = this.getLayoutInflater().inflate(R.layout.view_button, null);
		v_record.setBackgroundResource(R.drawable.icon_start);
		v_record.setId(ID_record);
		layoutparams_record = new MapView.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, (int) (width * 0.5),
				(int) (height * 0.78), MapView.LayoutParams.CENTER);
		mapview.addView(v_record, layoutparams_record);
		v_record.setOnTouchListener(this);

	}

	@Override
	protected void onResume() {

		if (mapman != null) {
			mapman.start();
		}
		mapview.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (mapman != null) {
			mapman.stop();
		}
		mapview.onPause();

		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (Const.isTrack) {
			Const.isTrack = false;
		}
		if (locClient != null) {
			locClient.stop();
			locClient = null;
		}
		if (mylocoverlay != null) {
			mylocoverlay = null;
		}

		if (mapman != null) {
			mapman.stop();
			mapman = null;
		}
		mapview.destroy();
		super.onDestroy();
	}

	private void init_location() {
		option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setScanSpan(5 * 1000);
		option.disableCache(true);
		locClient.setLocOption(option);
	}

	private void init() {
		mapview = (MapView) findViewById(R.id.bmapView2);
		backLayout = findViewById(R.id.addroute_back_layout);
		tvDistance=(TextView) findViewById(R.id.tv_distance);
		tvRuntime=(TextView) findViewById(R.id.tv_runtime);
		tvMethod=(TextView) findViewById(R.id.tv_mothod);
		tvMethod.setText(Const.METHOD);
		imMethod=(ImageView) findViewById(R.id.im_method);
		if ("汽车".equals(Const.METHOD)) {
			imMethod.setImageResource(R.drawable.small_walk);
		}else if ("骑车".equals(Const.METHOD)) {
			imMethod.setImageResource(R.drawable.small_walk);
		}else {
			imMethod.setImageResource(R.drawable.small_walk);
		}
		backLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		locData.latitude = location.getLatitude();
		locData.longitude = location.getLongitude();

		mylocoverlay.setData(locData);
		mapview.refresh();
		GeoPoint point = new GeoPoint((int) (locData.latitude * 1e6),
				(int) (locData.longitude * 1e6));

		mapcontroller.animateTo(point);
		if (Const.isRecord) {
			if (Const.isFirst) {
				latlng_start = locData.latitude + "," + locData.longitude;
				Const.isFirst = false;
				DrawMarker(locData.latitude, locData.longitude,
						Const.MARKERT_START);
			} else {
				latlng_end = locData.latitude + "," + locData.longitude;
				System.out.println("list.size()------------->" + list.size());
				List_LineInfo item = new List_LineInfo(latlng_end, latlng_start);
				list.add(item);
				DrawLine(item);
				latlng_start = latlng_end;
				myListGeo2.add(point); // 如果正在记录 就把记录期间的point点记录到myListGeo2

			}
		}
	}

	@Override
	public void onReceivePoi(BDLocation arg0) {

	}

	// 画Markert
	private boolean DrawMarker(double lat, double lng, int index) {
		GeoPoint point = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
		if (index == Const.MARKERT_START) {
			overlay_item = new OverlayItem(point, "起点", "起点");
			overlay_item.setMarker(getResources().getDrawable(
					R.drawable.v3_icon_qidian));
		} else {
			overlay_item = new OverlayItem(point, "终点", "终点");
			overlay_item.setMarker(getResources().getDrawable(
					R.drawable.v3_icon_zhongdian));
		}
		pointOVerlay.onCenter(overlay_item);
		pointOVerlay.addItem(overlay_item);
		mapview.refresh();
		return true;
	}

	// 用于任务中画轨迹
	private boolean DrawLine(List_LineInfo item) {
		Symbol lineSymbol = SymbolTools.getSymbol_line(0, 0, 255, 255, 4);
		Graphic lineGraphic = new Graphic(getGeometrytLine(item), lineSymbol);
		graphicsoverlay.setData(lineGraphic);
		mapview.refresh();
		return true;
	}

	// 根据两点经纬度坐标得到一条线
	private Geometry getGeometrytLine(List_LineInfo item) {
		endlatlng = item.getEnd_latlng().split(",");
		endLng = Double.parseDouble(endlatlng[1]);
		endLat = Double.parseDouble(endlatlng[0]);

		startlatlng = item.getStart_latlng().split(",");

		startLng = Double.parseDouble(startlatlng[1]);
		startLat = Double.parseDouble(startlatlng[0]);

		// 构建点并显示
		Geometry linegeometry = new Geometry();
		GeoPoint[] linegeopoint = new GeoPoint[2];
		linegeopoint[0] = new GeoPoint((int) (endLat * 1E6),
				(int) (endLng * 1E6));
		linegeopoint[1] = new GeoPoint((int) (startLat * 1E6),
				(int) (startLng * 1E6));
		linegeometry.setPolyLine(linegeopoint);

		return linegeometry;
	}

	// 地图上的自定义控件触摸监听
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			switch (v.getId()) {
			case ID_record:

				break;
			case ID_track:

				break;
			}
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			switch (v.getId()) {
			case ID_record:
				if (Const.isTrack) {
					toast.show("正在播放轨迹！");
				} else {
					if (Const.isRecord) {
						Const.isRecord = false;
						if (!Const.isFirst) {
							Const.isFirst = true;
						}

						DrawMarker(locData.latitude, locData.longitude,
								Const.MARKERT_END);
						toast.show("恭喜您，记录完成！");

						// 存到本地------------------------------------------------------

						/**
						 * -用来分隔起点和终点， ；用来分隔每一条线  把list转换成字符串存到本地
						 */
						StringBuffer sb = new StringBuffer();
						if (list.size() > 1) {
							for (int i = 0; i < list.size(); i++) {
								if (i == list.size() - 1) {
									sb.append(list.get(i).getStart_latlng());
									sb.append("-");
									sb.append(list.get(i).getEnd_latlng());

								} else {
									sb.append(list.get(i).getStart_latlng());
									sb.append("-");
									sb.append(list.get(i).getEnd_latlng());
									sb.append(";");
								}

							}
						}
						/**
						 * 只有当记录距离大于30米时才记录
						 */
						if (distansce > 30) {
							// 存储数据到本地
							DatebaseService service = new DatebaseService(
									getApplicationContext());
							// line.setLineStr(sb.toString());
							if (sb.toString() != null) {
								//处理数据
								mLongRunTime=mLongRunTime/1000;//把毫秒换成秒
								String avg_speed = String.valueOf((distansce *1000) / (mLongRunTime/3600));
								String str_avg_speed = avg_speed.substring(0, avg_speed.lastIndexOf(".") + 2);
								line = new Line(sb.toString(), startTime, str_avg_speed, null, distanceStr,
										runTime, tripMethod);
								
								service.insertData(line);
							} else {
								System.out.println("路线为空");
							}

						} else {
							toast.show("距离太短！");
						}

						v_record.setBackgroundResource(R.drawable.icon_start);
					} else {
						System.out.println("开始记录");
						new TimeThread().start(); //开始计时  开始计算里程
						mstartTime = System.currentTimeMillis();
						startTime = TimeUtil
								.getCurrentTime(TimeUtil.FORMAT_DATE);
						System.out
								.println("开始时间===="
										+ TimeUtil
												.getCurrentTime(TimeUtil.FORMAT_DATE_TIME_SECOND));
						ClearLine();
						ClearMarkert();
						list = new ArrayList<List_LineInfo>();
						Const.isRecord = true;
						v_record.setBackgroundResource(R.drawable.icon_stop);

						toast.show("正在记录轨迹！");
					}
				}
				break;

			}
		}
		return false;
	}

	public class TimeThread extends Thread {
        @Override
        public void run () {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 000;
                    
                    //如果正在记录  则实时更新  记录时间和里程
                    if (Const.isRecord) {
                    	// 计算实时距离
						//初始化距离  不然会重复计算
                    	distansce=0;
						for (int j = 0; j < myListGeo2.size() - 1; j++) {
							// 计算两个位置点之间的距离
							double dTempDis = DistanceUtil.getDistance(
									myListGeo2.get(j), myListGeo2.get(j + 1));
							//distansce_now = distansce_now + dTempDis+j*1000;
							distansce= distansce + dTempDis;
						}
						String disStr=String.valueOf(distansce/1000);
						// 里程保留一位小数
						distanceStr = disStr.substring(0, disStr.lastIndexOf(".") + 2);
						//下面得到的是毫秒
						mLongRunTime = (System.currentTimeMillis() - mstartTime);
						//System.out.println("mLongRunTime-------"+(mLongRunTime/1000)+"s");
						// 中国为东八区 需要减去8个小时
						runTime = TimeUtil.longToString(mLongRunTime
								- TimeZone.getDefault().getRawOffset(), TimeUtil.FORMAT_TIME2);
                    }
                    
                    Bundle bundle=new Bundle();  
                    bundle.putString("distance", distanceStr);  
                    bundle.putString("runtime", runTime);  
                    msg.setData(bundle);//bundle传值，耗时，效率低  
                    mHandler.sendMessage(msg);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while(true);
        }
    }
	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 000:
                    Bundle b = msg.getData();
                    tvDistance.setText(b.getString("distance"));
                    tvRuntime.setText(b.getString("runtime"));
                    break;
                
                default:
                    break;
            }
        }
    };


	private void ClearLine() {
		if (graphicsoverlay.getAllGraphics().size() != 0) {
			graphicsoverlay.removeAll();
			mapview.refresh();
		}
	}

	private void ClearMarkert() {
		if (pointOVerlay.getAllItem().size() != 0) {
			pointOVerlay.removeAll();
			mapview.refresh();
		}
	}

}
