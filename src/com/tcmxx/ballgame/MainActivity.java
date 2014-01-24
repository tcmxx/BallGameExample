package com.tcmxx.ballgame;

import android.os.Bundle;
import android.app.Activity;
import android.view.WindowManager;    
import android.view.Window; 

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);            
	        //隐去电池等图标和一切修饰部分（状态栏部分）    
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);    
	    String levelNum = getIntent().getExtras().getString("levelNum");
		setContentView(new MovementView(this,"bricks"+levelNum+".dat"));
	}



}
