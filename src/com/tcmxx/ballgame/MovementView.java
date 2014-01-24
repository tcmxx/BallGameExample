package com.tcmxx.ballgame;


import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import com.tcmxx.ballgame.GameObjects.BallObject;
import com.tcmxx.ballgame.GameObjects.BrickObject;
import com.tcmxx.ballgame.GameObjects.GameObject;
import com.tcmxx.ballgame.GameObjects.HeadObject;

import android.content.Context;
import android.content.res.Resources;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;  
import android.graphics.*;     
import android.view.SurfaceHolder;   


public class MovementView extends SurfaceView implements SurfaceHolder.Callback, KeyEvent.Callback{
	
	//the standard dimension that brick data basing on
	 static final private int STANDARD_HEIGHT = 1280;
	 static final private int STANDARD_WIDTH = 720;
	 //the real dimension
	 private int width;   
	 private int height; 
	 private float widthRatio;			//real/standard
	 private float heightRatio;
	 //Value for head control, initial for standard dimension
  
	 //other parameter
	 private boolean whetherWin = false;
	 // data
	 private Resources res;
	 GameObject winPic; 
	 HeadObject head;
	 BallObject ball;
	 ComponentGroup brickGroup;		//group that stores bricks
	 ComponentGroup accessaryGroup;
	 
	 //Thread
	 UpdateGraphThread updateThread; 

	 
	public MovementView(Context context, String level) {
		super(context);
		// TODO Auto-generated constructor stub
		getHolder().addCallback(this);   
		//Get the dimension parameters


		//get,create and initial the data 
	    res = this.getResources();  
	    
	    winPic = new GameObject();
	    winPic.setPosition(80, 500);
	    
	    head = new HeadObject(360, 1000,40);

	    ball = new BallObject(0,0,20);

	    head.getPaint().setColor(Color.BLUE);
	    head.getPaint().setAntiAlias(true);

	    head.getCollisionModel().setCollisionRadius(100);
	    ball.getPaint().setColor(Color.BLUE);
	    ball.setPosition(500, 400);
	    head.loadBitmap(BitmapFactory.decodeResource(res, R.drawable.head2));
	    winPic.loadBitmap(BitmapFactory.decodeResource(res, R.drawable.you_win));
	    brickGroup = new ComponentGroup();
	    
	    this.setKeepScreenOn(true);
	    setFocusable(true);// for key control
	    
	    //test for the file data to build bricks
	    String data = readFromAssets(level);
	    System.out.println(buildBrickGroup(data));
	    
	}

	/////////////////////////////////////////////
	//After update Frame , call it to draw the new canvas
    protected void drawFrame(Canvas canvas) {   


            canvas.scale(widthRatio, heightRatio);
            canvas.drawColor(Color.WHITE);   
            if(whetherWin) winPic.draw(canvas);
            head.draw(canvas);
            ball.draw(canvas);
            brickGroup.drawAll(canvas);
            
            
   
    }  
    
    ////////////////////////////////////////////////////////
    //fresh the frame///////////////////////
    public void updateFrame(){

	    
    	if(brickGroup.getObjectNum()==0){
    		whetherWin = true;
    	}
    	head.updateFrame(updateThread.getFPS());
	    updateBall();
    }
    
    ///////////////////////////////////////////////////////////
    //Read the asset file to String
    public String readFromAssets(String name){

    	StringBuffer sb = new StringBuffer();
		String line = null;
		BufferedReader reader = null;
    	try{ 

    		reader = new BufferedReader(new InputStreamReader(getResources().getAssets().open(name)));
    		while((line = reader.readLine())!=null){
				sb.append(line);
			}
    	}catch (Exception e){
			e.printStackTrace();
		}finally{
			try{
				reader.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
    	return sb.toString();
    }
    
    /////////////////////////////////////////////////////////////
    //write the message to file/////////////////
    public void writeFileData(String fileName,String message){ 

        try{ 

         FileOutputStream fout =this.getContext().openFileOutput(fileName, Context.MODE_PRIVATE);

         byte [] bytes = message.getBytes(); 

         fout.write(bytes); 

          fout.close(); 

         } 

        catch(Exception e){ 

         e.printStackTrace(); 

        } 

    }    
   
    /////////////////////////////////////////////////////////
    //Based on touchEvent to update the position
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		// TODO Auto-generated method stub
		int act = event.getActionMasked();
		switch(act){
		case MotionEvent.ACTION_DOWN:
			//updateThread.setRunning(true);
			head.getDestionation().x=event.getX()/widthRatio-40;
			head.getDestionation().y=event.getY()/heightRatio-40;
			break;
		case MotionEvent.ACTION_MOVE:
			head.getDestionation().x=event.getX()/widthRatio-40;
			head.getDestionation().y=event.getY()/heightRatio-40;
			break;
		case MotionEvent.ACTION_UP:
			break;
		default:
			break;
		}
		return true;
	}

	//////////////////////////////////////////////////////
	//Build the bricksGroup from the data string// 
	public boolean buildBrickGroup(String data){
		int brickNum = 0;
		char tmp;
		int pointer = 0;
		int tmpLocation[] = {0,0,0,0};
		int location = 0;
		while(true){
			if(data.charAt(pointer)=='b'){
				pointer++;
				if(data.charAt(pointer)==':'){
					while(true){
						pointer++;
						tmp = data.charAt(pointer);
						tmpLocation[location] = 0;
						while(48<=tmp&&tmp<=57){
							tmpLocation[location] = tmpLocation[location]*10+tmp-48;
							pointer++;
							tmp = data.charAt(pointer);

						}
						if(tmp ==','){
							location++;
							if(location>=4){
								return false;
							}
							continue;
						}
						else if(tmp == ';'){
							brickGroup.addObject(new BrickObject(tmpLocation[0],tmpLocation[1],tmpLocation[2],
									tmpLocation[3]), "brick"+String.valueOf(brickNum), true);
							brickNum++;
							location = 0;
							break;
						}
						else{
							return false;	
						}
					}
				}
				else{
					return false;
				}
			}
			else if(data.charAt(pointer) == '/'){
				return true;
			}
			else{
				return false;
			}
			pointer++;
		}

	}

	/////////////////////////////////////////////////////
	//detect ball and bricks collision in bricks group///
	private void ballBricksCollision(){
		for(int i = 0; i<brickGroup.getObjectNum();i++){
			if(brickGroup.getGameObjectByIndex(i).effectBall(ball)==-1){
				brickGroup.removeObjectByIndex(i);
			}
		}
	}
	/////////////////////////////////////////////////////////
	private PointF ballWallCollision(){
		if(ball.getX()+ball.getRadius()>=width/widthRatio){
			return new PointF(ball.getX()+ball.getRadius(), ball.getY());
		}
		else if(ball.getX()-ball.getRadius()<=0){
			return new PointF(ball.getX()-ball.getRadius(), ball.getY());
		}
		else if(ball.getY()-ball.getRadius()<=0){
			return new PointF(ball.getX(), ball.getY()-ball.getRadius());
		}
		else if(ball.getY()+ball.getRadius()>=height/heightRatio){
			return new PointF(ball.getX(), ball.getY()+ball.getRadius());
		}
		else{
			return null;
		}
	}
	///////////////////////////////////////////////////////////

	
	
	//update the ball according to collision
	private int updateBall(){
		//////reduce v by time
		VectorAttr ballMotion = ball.getMotion();
		if(ballMotion.getValue()>0) ballMotion.setValue(ballMotion.getValue()-1, ballMotion.getAngle());
		else ballMotion.setValue(0, 0);
		//get ready for update

		
		ball.setPosition(ball.getX()+ballMotion.getX()/(float)updateThread.getFPS(), 
				ball.getY()+ballMotion.getY()/(float)updateThread.getFPS());
		/////Check bricks collision and react
		PointF wallPoint;

		ballBricksCollision();
		/////Check walls collision and react
		if((wallPoint = ballWallCollision())!=null){
			VectorAttr normal = new VectorAttr((wallPoint.x-ball.getX()), (wallPoint.y-ball.getY()));
			ballMotion.set(VectorAttr.reflectVector(ballMotion, normal));
			ball.setPosition(ball.getX()+ballMotion.getX()/(float)updateThread.getFPS(), 
					ball.getY()+ballMotion.getY()/(float)updateThread.getFPS());

		}
		/////Check head collision and react
		head.effectBall(ball);
		return 0;
		
	}
	

	
	
	/////surface functions
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		Rect surfaceFrame = arg0.getSurfaceFrame();   
        width = surfaceFrame.width();   
        height = surfaceFrame.height();   
		widthRatio = (float)width/(float)STANDARD_WIDTH;
		heightRatio = (float)height/(float)STANDARD_HEIGHT;
		

		
        updateThread = new UpdateGraphThread(this);   
        updateThread.setRunning(true);   
        
        updateThread.start();   
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
	     boolean retry = true;   
	     
	     updateThread.setRunning(false);   
	        while (retry) {   
	            try {   
	                updateThread.join();   
	                retry = false;   
	            } catch (InterruptedException e) {   
	  
	            }   
	        }   
	}
	/////////////////////////////////////////////////////////

}
