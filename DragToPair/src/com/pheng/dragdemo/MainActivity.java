package com.pheng.dragdemo;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ListView lv_drag;
	Bitmap bitmap;
	Matrix matrix = new Matrix();
	Matrix saveMatrix = new Matrix();
	float startX = 0;
	float startY = 0;
	WindowManager wm;
	WindowManager.LayoutParams wmParams;

	int po = -1;
	boolean isCanDrag = false;
	ImageView dragView;
	float dragOffsetX = -130;
	float dragOffsetY = -130;
	int[] startPoint = new int[2];
	int[] listPoint = new int[2];

	private Context context;

	ListView lv_tar;
	List<String> datas = new ArrayList<String>();

	int dragPosition = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		wm = getWindowManager();
		wmParams = new WindowManager.LayoutParams();
		context = this;
		initView();
	}

	@SuppressLint({ "RtlHardcoded", "ClickableViewAccessibility", "ShowToast" })
	private void initView() {
		datas.add("111");
		datas.add("222");
		datas.add("333");
		datas.add("444");
		datas.add("555");
		datas.add("666");
		datas.add("777");
		datas.add("888");
		datas.add("999");
		datas.add("010");
		datas.add("011");
		datas.add("012");
		datas.add("013");
		datas.add("014");
		datas.add("015");
		datas.add("016");
		datas.add("017");
		datas.add("018");
		lv_tar = (ListView) findViewById(R.id.ll_target);
		lv_tar.setAdapter(new ArrayAdapter<String>(context,
				R.layout.listitem_tar, datas));

		lv_drag = (ListView) findViewById(R.id.lv_drag);
		lv_drag.setAdapter(new ArrayAdapter<String>(context, R.layout.listitem,
				datas));

		lv_drag.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// ListView在界面中的位置，在界面绘制完成时确定，因为不知道界面什么绘制完成，所以放在这里了
				lv_tar.getLocationInWindow(listPoint);

				// 记录拖动的item的position
				dragPosition = position;

				// 获得拖动的item展示内容，以bitmap对象形式
				view.setDrawingCacheEnabled(true);
				bitmap = Bitmap.createBitmap(view.getDrawingCache());
				view.setDrawingCacheEnabled(false);

				// 根据手指按压位置，初始化拖拽的view的基本属性
				wmParams = new WindowManager.LayoutParams();
				wmParams.gravity = Gravity.TOP | Gravity.LEFT;
				wmParams.x = startPoint[0];
				wmParams.y = startPoint[1];
				wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
				wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

				wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE// 不需获取焦点
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE// 不需接受触摸事件
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;// 保持设备常开，并保持亮度不变。
				// | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;//
				// 窗口占满整个屏幕，忽略周围的装饰边框（例如状态栏）。此窗口需考虑到装饰边框的内容。

				wmParams.windowAnimations = 0;

				// 拖动的view对象
				dragView = new ImageView(context);
				dragView.setImageBitmap(bitmap);

				// 奖拖动对象加入当前视图界面
				wm.addView(dragView, wmParams);

				// 改变要拖动的item的背景，更直观的让用户知道拖动的是哪个item
				view.setBackgroundColor(Color.parseColor("#ffff9000"));

				// 设置move可以拖动了
				isCanDrag = true;
				return false;
			}
		});
		lv_drag.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// v.getLocationInWindow(startPoint);
					// dragOffsetX = - event.getX() + 3;
					// dragOffsetY = - event.getY() - 50;

					startPoint[0] = (int) (event.getRawX() + dragOffsetX);
					startPoint[1] = (int) (event.getRawY() + dragOffsetY);

					break;
				case MotionEvent.ACTION_MOVE:
					System.out.println("===================================");
					if (isCanDrag && dragView != null) {
						wmParams.x = (int) (event.getRawX() + dragOffsetX);
						wmParams.y = (int) (event.getRawY() + dragOffsetY);
						wm.updateViewLayout(dragView, wmParams);
						int po2 = lv_tar.pointToPosition(
								(int) (event.getRawX() - listPoint[0]),
								(int) (event.getRawY() - listPoint[1]));
						System.out.println("-----po2-     " + po2);
						int firstVisiblePosition = lv_tar
								.getFirstVisiblePosition();
						if (po2 >= 0 && po2 != po) {
							if (po >= 0) {
								lv_tar.getChildAt(po - firstVisiblePosition)
										.setBackgroundColor(
												android.graphics.Color.TRANSPARENT);
							}
							lv_tar.getChildAt(po2 - firstVisiblePosition)
									.setBackgroundColor(
											Color.parseColor("#ffff00af"));
							po = po2;
						}
						if (po2 < 0 && po >= 0) {
							lv_tar.getChildAt(po - firstVisiblePosition)
									.setBackgroundColor(
											android.graphics.Color.TRANSPARENT);
							po = po2;
						}
						return true;
					}
					System.out.println("===================================");
					break;
				case MotionEvent.ACTION_UP:
					if (isCanDrag && dragView != null) {
						isCanDrag = false;
						lv_drag.getChildAt(
								dragPosition
										- lv_drag.getFirstVisiblePosition())
								.setBackgroundColor(
										Color.parseColor("#ffff3000"));
						wm.removeView(dragView);
						dragView = null;
						// v.setBackgroundColor(android.graphics.Color.TRANSPARENT);
						if (po >= 0) {
							int firstVisiblePosition = lv_tar
									.getFirstVisiblePosition();
							lv_tar.getChildAt(po - firstVisiblePosition)
									.setBackgroundColor(
											android.graphics.Color.TRANSPARENT);
							Toast.makeText(
									context,
									"配对成功：" + "drag " + ++dragPosition
											+ "与target " + ++po, 0).show();
							po = -1;
						}
					}
					break;

				default:
					break;
				}
				return false;
			}
		});
	}
}
