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
		mLocationClient = new LocationClient(getApplicationContext());     //����LocationClient��
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
		li_xinxi1.setText("ѧ����" + sname + " ");
		li_xinxi2.setText("�༶��" + sclass + " ");
		li_xinxi3.setText("���ң�" + classroom + " ");
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
        option.setLocationMode(LocationMode.Hight_Accuracy);//��ѡ��Ĭ�ϸ߾��ȣ����ö�λģʽ���߾��ȣ��͹��ģ����豸
        option.setCoorType("bd09ll");//��ѡ��Ĭ��gcj02�����÷��صĶ�λ�������ϵ
        int span=1000;
        option.setScanSpan(span);//��ѡ��Ĭ��0��������λһ�Σ����÷���λ����ļ����Ҫ���ڵ���1000ms������Ч��
        option.setIsNeedAddress(true);//��ѡ�������Ƿ���Ҫ��ַ��Ϣ��Ĭ�ϲ���Ҫ
        option.setOpenGps(true);//��ѡ��Ĭ��false,�����Ƿ�ʹ��gps
        option.setLocationNotify(true);//��ѡ��Ĭ��false�������Ƿ�GPS��Чʱ����1S/1��Ƶ�����GPS���
        option.setIsNeedLocationDescribe(true);//��ѡ��Ĭ��false�������Ƿ���Ҫλ�����廯�����������BDLocation.getLocationDescribe��õ�����������ڡ��ڱ����찲�Ÿ�����
        option.setIsNeedLocationPoiList(true);//��ѡ��Ĭ��false�������Ƿ���ҪPOI�����������BDLocation.getPoiList��õ�
        option.setIgnoreKillProcess(false);//��ѡ��Ĭ��true����λSDK�ڲ���һ��SERVICE�����ŵ��˶������̣������Ƿ���stop��ʱ��ɱ��������̣�Ĭ�ϲ�ɱ��  
        option.SetIgnoreCacheException(false);//��ѡ��Ĭ��false�������Ƿ��ռ�CRASH��Ϣ��Ĭ���ռ�
        option.setEnableSimulateGps(false);//��ѡ��Ĭ��false�������Ƿ���Ҫ����GPS��������Ĭ����Ҫ
        mLocationClient.setLocOption(option);
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case RESULT_CODE:
			if (resultCode == Activity.RESULT_OK) {
				li_chenggong.setText("ǩ���ɹ�");
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
					li_chenggong.setText("ǩ��ʧ��");
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
				li_chenggong.setText("ǩ��ʧ��");
				li_chenggong.setVisibility(View.VISIBLE);
			}
		}

	}
 
	public static String convertIconToString(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] appicon = baos.toByteArray();// תΪbyte����
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
	         if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS��λ���
	             sb.append("\nspeed : ");
	             sb.append(location.getSpeed());// ��λ������ÿСʱ
	             sb.append("\nsatellite : ");
	             sb.append(location.getSatelliteNumber());
	             sb.append("\nheight : ");
	             sb.append(location.getAltitude());// ��λ����
	             sb.append("\ndirection : ");
	             sb.append(location.getDirection());// ��λ��
	             sb.append("\naddr : ");
	             sb.append(location.getAddrStr());
	             sb.append("\ndescribe : ");
	             sb.append("gps��λ�ɹ�");

	         } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// ���綨λ���
	             sb.append("\naddr : ");
	             sb.append(location.getAddrStr());
	             //��Ӫ����Ϣ
	             sb.append("\noperationers : ");
	             sb.append(location.getOperators());
	             sb.append("\ndescribe : ");
	             sb.append("���綨λ�ɹ�");
	             mLocationClient.stop();
	         } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// ���߶�λ���
	             sb.append("\ndescribe : ");
	             sb.append("���߶�λ�ɹ������߶�λ���Ҳ����Ч��");
	             mLocationClient.stop();
	         } else if (location.getLocType() == BDLocation.TypeServerError) {
	             sb.append("\ndescribe : ");
	             sb.append("��������綨λʧ�ܣ����Է���IMEI�źʹ��嶨λʱ�䵽loc-bugs@baidu.com��������׷��ԭ��");
	         } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
	             sb.append("\ndescribe : ");
	             sb.append("���粻ͬ���¶�λʧ�ܣ����������Ƿ�ͨ��");
	         } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
	             sb.append("\ndescribe : ");
	             sb.append("�޷���ȡ��Ч��λ���ݵ��¶�λʧ�ܣ�һ���������ֻ���ԭ�򣬴��ڷ���ģʽ��һ���������ֽ�����������������ֻ�");
	         }
	         	sb.append("\nlocationdescribe : ");
	            sb.append(location.getLocationDescribe());// λ�����廯��Ϣ
	             List<Poi> list = location.getPoiList();// POI����
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