package com.zz.studentsignin;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.zz.fuzhu.MyApplication;
import com.zz.fuzhu.dizhi;

public class welcomeActivity extends Activity {
	MyApplication app;
	String sname,sclass,user;
	Button wlc_return,wlc_sure;
	TextView wlc_name,wlc_number,wlc_class;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		wlc_name = (TextView) findViewById(R.id.wlc_name);
		wlc_number = (TextView) findViewById(R.id.wlc_number);
		wlc_class = (TextView) findViewById(R.id.wlc_class);
		wlc_return = (Button) findViewById(R.id.wlc_return);
		wlc_sure = (Button) findViewById(R.id.wlc_sure);
		app = (MyApplication) getApplication();
		StringRequest stringRequest = new StringRequest(Method.POST, dizhi.welcome,
				new Listener<String>() {

			@Override
			public void onResponse(String response) {
				try {
					JSONObject json = new JSONObject(response.trim());
					sname = json.getString("name");
					sclass = json.getString("classname");
					wlc_name.setText("欢迎"+json.getString("name")+"同学：");
					wlc_class.setText("班级："+json.getString("classname"));
					wlc_number.setText("学号："+json.getString("number"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				
				
				Log.d("T", response);
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e("T", error.toString());
			}
		}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Intent intent=getIntent();
				user=intent.getStringExtra("user");
				Map<String, String> params =new HashMap<String, String>();
				params.put("number", user);
				return params;
			}
		};
		app.getRequestQueue().add(stringRequest);
		wlc_return.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(welcomeActivity.this,MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
		wlc_sure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(welcomeActivity.this,loginActivity.class);
				intent.putExtra("sname", sname);
				intent.putExtra("user", user);
				intent.putExtra("sclass", sclass);
				startActivity(intent);
				finish();
			}
		});
	}
}
