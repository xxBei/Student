package com.zz.fuzhu;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import android.app.Application;

public class MyApplication extends Application{
	
	RequestQueue requestQueue;
	
	
	

	@Override
	public void onCreate() {
		super.onCreate();
	}

	
	public RequestQueue getRequestQueue() {
		if (requestQueue == null) {
			requestQueue = Volley.newRequestQueue(getApplicationContext());
		}
		return requestQueue;
	}	
}
