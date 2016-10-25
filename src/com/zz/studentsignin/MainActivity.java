package com.zz.studentsignin;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.zz.fuzhu.MyApplication;
import com.zz.fuzhu.dizhi;

public class MainActivity extends Activity {
	MyApplication app;
	EditText snumber,spassword;
	Button denglu;
	String user;
	String password;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		snumber = (EditText) findViewById(R.id.number);
		spassword = (EditText) findViewById(R.id.password);
		denglu = (Button) findViewById(R.id.denglu);
		
		denglu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				user = snumber.getText().toString();
				password = spassword.getText().toString();
				if (user.equals("")&&password.equals("")) {
					
					Toast.makeText(MainActivity.this,"用户名或密码为空", 1).show();
				}else{
				app = (MyApplication) getApplication();
				StringRequest stringrequest = new StringRequest(Method.POST, dizhi.shouye, new Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d("T", response);
						try {
							JSONObject json = new JSONObject(response.trim());
							if (json.getBoolean("login")) {
								Intent intent = new Intent(MainActivity.this,welcomeActivity.class);
								String users = json.getString("user");
								intent.putExtra("user", users);
								startActivity(intent);
								finish();
							}else{
								Log.d("E", "登录失败");
								Toast.makeText(MainActivity.this,"用户名或密码错误", 1).show();
							}
							
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("T", error.toString());
					}
				}){
					@Override
					protected Map<String, String> getParams() throws AuthFailureError {
						
						Map<String, String> params =new HashMap<String, String>();
						params.put("suser", user);
						params.put("spassword", password);
						return params;
					}
				};
				app.getRequestQueue().add(stringrequest);
				}
			}
		});
	}
}
	
		
	

		
