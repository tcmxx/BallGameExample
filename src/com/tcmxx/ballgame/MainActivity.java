package com.tcmxx.ballgame;


import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import android.view.WindowManager;    
import android.view.Window; 
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);            
	        //隐去电池等图标和一切修饰部分（状态栏部分）    
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);    
	    String levelNum = getIntent().getExtras().getString("levelNum");
	    
	    //add and initialize movement View(graphic part)
	    MovementView movementView = new MovementView(this);
	    LinearLayout.LayoutParams moveParas = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
	    moveParas.weight = 9;
	    movementView.setLayoutParams(moveParas);
	    setContentView(R.layout.activity_main);
	    ((LinearLayout)findViewById(R.id.mainLayout)).addView(movementView);
	    
	    //the information view
		LayoutInflater inflater =getLayoutInflater();
		View infLayout =  inflater.inflate(R.layout.inflayout, null);
		LinearLayout.LayoutParams infParas = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		((LinearLayout)findViewById(R.id.mainLayout)).addView(infLayout,infParas);
		
		//
	    movementView.timeView =(TextView)this.findViewById(R.id.textTime);
	    movementView.lifeView = (TextView)this.findViewById(R.id.textLife);
	    movementView.startButton = (Button)this.findViewById(R.id.startButton);
	    movementView.initializeLevel("level"+levelNum+".xml");

	}



}
