package com.zz.studentsignin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.location.LocationClientOption.LocationMode;
import com.zz.fuzhu.MyApplication;
import com.zz.fuzhu.dizhi;

public class loginActivity extends Activity {
	private static final int RESULT_CODE = 1;
	Button li_return, li_signin;
	TextView li_xinxi1, li_xinxi2, li_xinxi3, li_chenggong;
	private static String image;
	String sname, user, sclass ,classroom;
	MyApplication app;
	Bitmap bitmap;
	private Uri imageUri;
	static ImageView imageView;
	LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	String Latitude;
	String Longitude;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
	    mLocationClient.registerLocationListener( myListener ); 
		li_return = (Button) findViewById(R.id.li_return);
		li_signin = (Button) findViewById(R.id.li_signin);
		li_xinxi1 = (TextView) findViewById(R.id.li_xinxi1);
		li_xinxi2 = (TextView) findViewById(R.id.li_xinxi2);
		li_xinxi3 = (TextView) findViewById(R.id.li_xinxi3);
		imageView = (ImageView) findViewById(R.id.li_iv);
		li_chenggong = (TextView) findViewById(R.id.li_chenggong);
		Intent intent = getIntent();
		sname = intent.getStringExtra("sname");
		sclass = intent.getStringExtra("sclass");
		user = intent.getStringExtra("user");
		
		initLocation();
		app = (MyApplication) getApplication();
		StringRequest str = new StringRequest(Method.POST, dizhi.cho_classname, 
				new Listener<String>() {

			@Override
			public void onResponse(String response) {
				try {
					JSONObject json = new JSONObject(response.trim());
					classroom =json.getString("classes");
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e("T", error.toString());
			}
		});
		li_xinxi1.setText("学生：" + sname + " ");
		li_xinxi2.setText("班级：" + sclass + " ");
		li_xinxi3.setText("教室：" + classroom + " ");
		app.getRequestQueue().add(str);
		li_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(loginActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
		li_signin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mLocationClient.start();
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				File photo = new File("/sdcard/qiandao/pic.jpg");
				if (!photo.getParentFile().exists()) {
					photo.getParentFile().mkdirs();
				}
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
				imageUri = Uri.fromFile(photo);
				startActivityForResult(intent, RESULT_CODE);

			}
		});
		
	}
	private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死  
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case RESULT_CODE:
			if (resultCode == Activity.RESULT_OK) {
				li_chenggong.setText("签到成功");
				li_chenggong.setVisibility(View.VISIBLE);
				li_signin.setEnabled(false);
				
				Uri selectedImage = imageUri;
				getContentResolver().notifyChange(selectedImage, null);
				ContentResolver cr = getContentResolver();
				
				try {
					bitmap = android.provider.MediaStore.Images.Media
							.getBitmap(cr, selectedImage);
					bitmap = ThumbnailUtils.extractThumbnail(bitmap,bitmap.getWidth()/8, bitmap.getHeight()/8);
					imageView.setImageBitmap(bitmap);
					Toast.makeText(this, selectedImage.toString(),
							Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					li_chenggong.setText("签到失败");
					li_chenggong.setVisibility(View.VISIBLE);
					li_signin.setEnabled(true);
					Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
							.show();
					Log.e("Camera", e.toString());
				}
				convertIconToString(bitmap);
				app = (MyApplication) getApplication();
				StringRequest stringRequest = new StringRequest(Method.POST,
						dizhi.result, new Listener<String>() {

							@Override
							public void onResponse(String response) {
								
							}
						}, new ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {

							}
						}) {
					@Override
					protected Map<String, String> getParams()
							throws AuthFailureError {
						Map<String, String> params = new HashMap<String, String>();
						params.put("user", user);
						params.put("classname", sclass);
						params.put("name",sname);
						params.put("image", image);
						params.put("Latitude", Latitude);
						params.put("Longitude", Longitude);
						return params;
					}
				};
				app.getRequestQueue().add(stringRequest);
			} else {
				li_chenggong.setText("签到失败");
				li_chenggong.setVisibility(View.VISIBLE);
			}
		}

	}
 
	public static String convertIconToString(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] appicon = baos.toByteArray();// 转为byte数组
		image = Base64.encodeToString(appicon, Base64.DEFAULT);
		Log.d("T", image);
		Log.d("D---------", image.length()+"");
		
		return image;

	}
	public class MyLocationListener implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation location) {
			 StringBuffer sb = new StringBuffer(256);
	         sb.append("time : ");
	         sb.append(location.getTime());
	         sb.append("\nerror code : ");
	         sb.append(location.getLocType());
	         sb.append("\nlatitude : ");
	         Latitude = location.getLatitude()+"";
	         sb.append(location.getLatitude());
	         sb.append("\nlontitude : ");
	         Longitude = location.getLongitude()+"";
	         sb.append(location.getLongitude());
	         sb.append("\nradius : ");
	         sb.append(location.getRadius());
	         if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
	             sb.append("\nspeed : ");
	             sb.append(location.getSpeed());// 单位：公里每小时
	             sb.append("\nsatellite : ");
	             sb.append(location.getSatelliteNumber());
	             sb.append("\nheight : ");
	             sb.append(location.getAltitude());// 单位：米
	             sb.append("\ndirection : ");
	             sb.append(location.getDirection());// 单位度
	             sb.append("\naddr : ");
	             sb.append(location.getAddrStr());
	             sb.append("\ndescribe : ");
	             sb.append("gps定位成功");

	         } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
	             sb.append("\naddr : ");
	             sb.append(location.getAddrStr());
	             //运营商信息
	             sb.append("\noperationers : ");
	             sb.append(location.getOperators());
	             sb.append("\ndescribe : ");
	             sb.append("网络定位成功");
	             mLocationClient.stop();
	         } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
	             sb.append("\ndescribe : ");
	             sb.append("离线定位成功，离线定位结果也是有效的");
	             mLocationClient.stop();
	         } else if (location.getLocType() == BDLocation.TypeServerError) {
	             sb.append("\ndescribe : ");
	             sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
	         } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
	             sb.append("\ndescribe : ");
	             sb.append("网络不同导致定位失败，请检查网络是否通畅");
	         } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
	             sb.append("\ndescribe : ");
	             sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
	         }
	         	sb.append("\nlocationdescribe : ");
	            sb.append(location.getLocationDescribe());// 位置语义化信息
	             List<Poi> list = location.getPoiList();// POI数据
	             if (list != null) {
	                 sb.append("\npoilist size = : ");
	                 sb.append(list.size());
	                 for (Poi p : list) {
	                     sb.append("\npoi= : ");
	                     sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
	                 }
	             }
	         Log.i("BaiduLocationApiDem", sb.toString());
	         
	     }
			
		}
}