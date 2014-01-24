package com.tcmxx.ballgame;



import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class BeginningActivity extends Activity {

	private Button buttonGo = null;
	private EditText level = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beginning);
		
		buttonGo = (Button)findViewById(R.id.buttonGo);
		level = (EditText)findViewById(R.id.editLevel);
		buttonGo.setOnClickListener(new ButtonGoListener());
	}


	//button go listener, press to go to the input input.
	class ButtonGoListener implements OnClickListener{
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			String levelNum = level.getText().toString();
			Intent intent = new Intent();
			intent.setClass(BeginningActivity.this, MainActivity.class);
			intent.putExtra("levelNum", levelNum);
			//try to find the level file, if it does not exist, nothing happens.
			try{
				getResources().getAssets().open("bricks"+levelNum+".dat");
				startActivity(intent);
				finish();//finish this beginning activity
			}catch (Exception e){
			}			
		}
		
	}


}
