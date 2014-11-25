package com.project.zuji;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.project.zuji.entity.Const;
import com.project.zuji.widget.ActionSheetExit;
import com.project.zuji.widget.ActionSheetExit.OnActionSheetSelected;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

public class MenuActivity extends FragmentActivity implements View.OnClickListener, 
OnActionSheetSelected, OnCancelListener{

    private ResideMenu resideMenu;
    private MenuActivity mContext;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemProfile;
    private ResideMenuItem itemCalendar;
    private ResideMenuItem itemSettings;
    private PopupWindow popupWindow;
    private View pop_view;
    private View open_pop_view;
    private View view_walk;
    private View view_bike;
    private View view_car;
    private TextView tv_trip_method;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        open_pop_view=findViewById(R.id.open_pop_view);
        pop_view = getLayoutInflater().inflate(R.layout.select_popup_window, null);
        view_bike=pop_view.findViewById(R.id.view_select_bike);
        view_car=pop_view.findViewById(R.id.view_select_car);
        view_walk=pop_view.findViewById(R.id.view_select_walk);
        tv_trip_method=(TextView) findViewById(R.id.trip_method);
        mContext = this;
        setUpMenu();
        changeFragment(new HomeFragment());
    }

    private void setUpMenu() {

        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip. 
        resideMenu.setScaleValue(0.6f);

        // create menu items;
        itemHome     = new ResideMenuItem(this, R.drawable.icon_home,     "首页");
        itemProfile  = new ResideMenuItem(this, R.drawable.icon_line,  "我的轨迹");
        itemCalendar = new ResideMenuItem(this, R.drawable.icon_toolsbox, "工具箱");
        itemSettings = new ResideMenuItem(this, R.drawable.icon_settings, "设置");

        itemHome.setOnClickListener(this);
        itemProfile.setOnClickListener(this);
        itemCalendar.setOnClickListener(this);
        itemSettings.setOnClickListener(this);
        ClickListener click=new ClickListener();
        open_pop_view.setOnClickListener(click);
        view_bike.setOnClickListener(click);
        view_car.setOnClickListener(click);
        view_walk.setOnClickListener(click);

        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemProfile, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemCalendar, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_LEFT);

        //将左划菜单设置为不可用
        // You can disable a direction by setting ->
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        //resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);

        findViewById(R.id.title_bar_left_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });
       
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View view) {

        if (view == itemHome){
            changeFragment(new HomeFragment());
            open_pop_view.setVisibility(View.VISIBLE);
        }else if (view == itemProfile){
            changeFragment(new MyrouteFragment());
            open_pop_view.setVisibility(View.INVISIBLE);
        }else if (view == itemCalendar){
            changeFragment(new ToolboxFragment());
            open_pop_view.setVisibility(View.INVISIBLE);
        }else if (view == itemSettings){
            changeFragment(new SettingsFragment());
            open_pop_view.setVisibility(View.INVISIBLE);
        }
        resideMenu.closeMenu();
    }
    class ClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.open_pop_view:
				 popupWindow = new PopupWindow(pop_view,
		 					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
		 					true);

		 			popupWindow.setTouchable(true);
		 			popupWindow.setOutsideTouchable(true);
		 			// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
		 			// 我觉得这里是API的一个bug
		 			popupWindow.setBackgroundDrawable(new BitmapDrawable(
		 					getResources(), (Bitmap) null));

		 			// 设置好参数之后再show
		 			popupWindow.showAsDropDown(open_pop_view, -10, 0);
				break;
			case R.id.view_select_bike:
				tv_trip_method.setText("骑车");
				Const.METHOD="骑车";
				//System.out.println("tv_trip_method---main"+tv_trip_method.getText());
				if(popupWindow.isShowing()){
					popupWindow.dismiss();
				}
				break;
			case R.id.view_select_car:
				tv_trip_method.setText("汽车");
				Const.METHOD="汽车";
				if(popupWindow.isShowing()){
					popupWindow.dismiss();
				}
				break;
			case R.id.view_select_walk:
				tv_trip_method.setText("步行");
				Const.METHOD="步行";
				if(popupWindow.isShowing()){
					popupWindow.dismiss();
				}
				break;
					

			default:
				break;
			}
			
		}
    	
    } 
    /**
     * 拦截返回键  弹出是否退出程序
     */
    @Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();

		ActionSheetExit.showSheet(MenuActivity.this, MenuActivity.this,
				MenuActivity.this);
	}
    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
            //Toast.makeText(mContext, "Menu is opened!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void closeMenu() {
            //Toast.makeText(mContext, "Menu is closed!", Toast.LENGTH_SHORT).show();
        }
    };

    private void changeFragment(Fragment targetFragment){
        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    // What good method is to access resideMenu？
    public ResideMenu getResideMenu(){
        return resideMenu;
    }

	@Override
	public void onCancel(DialogInterface dialog) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(int whichButton) {
		// TODO Auto-generated method stub
		switch (whichButton) {
		case 0:
			//ActivityStackControlUtil.finishProgram();
			HomeFragment.rootView=null;
			//LoginUtil.cancel(getApplication());
			finish();
			break;

		case 1:
			break;

		default:
			break;
		}
	}
	/**
	 * 定义菜单键  点击菜单键  弹出左侧菜单
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		
		//判断左侧菜单是否弹出，如果已经弹出就关闭，如果没有就弹出
		if (resideMenu.isOpened()) {
			resideMenu.closeMenu();
		}else {
			resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
		}
	
		return super.onPrepareOptionsMenu(menu);
	}
}
