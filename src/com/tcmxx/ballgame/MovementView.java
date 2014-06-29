package com.tcmxx.ballgame;


import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.tcmxx.ballgame.GameObjects.BallObject;
import com.tcmxx.ballgame.GameObjects.BrickObject;
import com.tcmxx.ballgame.GameObjects.Ejection;
import com.tcmxx.ballgame.GameObjects.GameObject;
import com.tcmxx.ballgame.GameObjects.HeadObject;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;  
import android.graphics.*;     
import android.view.SurfaceHolder;   
import android.widget.Button;
import android.widget.TextView;


public class MovementView extends SurfaceView implements SurfaceHolder.Callback, KeyEvent.Callback{

	
	//the standard dimension that brick data basing on
	 static final private int STANDARD_HEIGHT = 1280;
	 static final private int STANDARD_WIDTH = 720;
	 //the real dimension
	 private int width;   
	 private int height; 
	 private float widthRatio;			//real/standard
	 private float heightRatio;
	 //double click detection variables
	 static final private long DOUBLE_CLICK_TIME = 200;
	 static final private float DOUBLE_CLICK_DISTANCE = 50;
	 long lastClickTime = 0;
	 float lastClickX = 0;
	 float lastClickY = 0;
	 //other parameter
	 private boolean whetherWin = false;
	 // data
	 private Resources res;
	 GameObject winPic; 
	 HeadObject head;
	 HeadObject oldHead;
	 BallObject ball;
	 public ComponentGroup brickGroup;		//group that stores bricks
	 ArrayList<Ejection> ejectionGroup;
	 
	 boolean newPath=false;
	 //Thread
	 UpdateGraphThread updateThread; 
	 //widget object
	 Context mContext;
	 public TextView timeView;
	 public TextView lifeView;
	 public Button startButton;


	public MovementView(Context context) {
		super(context);
		getHolder().addCallback(this);   

		mContext = context;

		//get,create and initial the data 
	    res = this.getResources();  
	    
	    head = new HeadObject(360, 1000,40);
	    head.getPaint().setColor(Color.BLUE);
	    head.getPaint().setAntiAlias(true);
	    head.getCollisionModel().setCollisionRadius(100);
	    head.loadBitmap(BitmapFactory.decodeResource(res, R.drawable.head2));
	    try{
	    	oldHead = (HeadObject)head.clone();
	    }catch(Exception e){
	    }finally{	
	    }
	    ejectionGroup = new ArrayList<Ejection>();
	    this.setKeepScreenOn(true);
	    setFocusable(true);// for key control
	    
	    
	    
	}
	
	//////////////////////////////////////////////////////
	//this will load and initialize a new level while keeping the head the as input
	//if fail, return false
	protected boolean initializeLevel(String level){

		startButton.setText(R.string.start);
		
	    ball = new BallObject(0,0,20);
	    ball.getPaint().setColor(Color.BLUE);
	    ball.setPosition(500, 400);
	    
	    winPic = new GameObject();
	    winPic.setPosition(80, 500);
	    winPic.loadBitmap(BitmapFactory.decodeResource(res, R.drawable.you_win));
	    brickGroup = new ComponentGroup();
	    
	    //read file data to build bricks
	    //String data = readFromAssets(level);
	    SceneParser parser = new SceneParser();
    	try{ 
    		parser.parse(getResources().getAssets().open(level), this, res);
    	}catch (Exception e){
			e.printStackTrace();
		}
	    //
	    //System.out.println(buildBrickGroup(data));
		return true;
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

        	for(int i = 0;i<ejectionGroup.size();i++){
        		ejectionGroup.get(i).draw(canvas);
        	}
            
   
    }  
    
    ////////////////////////////////////////////////////////
    //fresh the frame///////////////////////
    //this is called by system every cycle
    public void updateFrame(){
    	
    	head.updateFrame(updateThread.getFPS());
    	for(int i = 0;i<ejectionGroup.size();i++){
    		if(ejectionGroup.get(i).update(brickGroup, ball)==-1){
    			ejectionGroup.remove(i);
    			i--;
    		}
    	}
    	if(brickGroup.getObjectNum()==0){
    		whetherWin = true;
    	}
    	
	    updateBall();
	    headComponentsEffect();
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
			long cTime = System.currentTimeMillis();
			float tmpX = event.getX()/widthRatio;
			float tmpY = event.getY()/heightRatio;
			//if the click interval is small and the distance between
			//each click is close
			if(cTime-lastClickTime<DOUBLE_CLICK_TIME&&
					(tmpX-lastClickX)*(tmpX-lastClickX)+
					(tmpY-lastClickY)*(tmpY-lastClickY)<=
					DOUBLE_CLICK_DISTANCE*DOUBLE_CLICK_DISTANCE){
					head.getPath().clearPath();
					head.getPath().addPoint(tmpX,tmpY);
					head.delay=0;
					
			}
			else{
				
				CollisionModel mPoint = new CollisionModel();
				mPoint.addPoint(new PointF(0,0), null);
				mPoint.setNodePosition(event.getX()/widthRatio, event.getY()/heightRatio);
				//if the touch point is in the head, create new path
				if(!mPoint.inClosure(head.getCollisionModel()).isEmpty()){
					newPath=true;
					head.getPath().clearPath();
					head.delay=0;
				}else{
					//if not in the head, calculate ejection
				}
			}
			//update the time and position record
			lastClickX = tmpX;
			lastClickY = tmpY;
			lastClickTime = cTime;
			break;
		case MotionEvent.ACTION_MOVE:
			if(newPath){
				head.getPath().addPoint(event.getX()/widthRatio, event.getY()/heightRatio);
			}
			
			//lastClickX = event.getX();
			//lastClickY =event.getY();
			break;
		case MotionEvent.ACTION_UP:
			float upX = event.getX()/widthRatio;
			float upY = event.getY()/heightRatio;
			float moveDist = (float) Math.sqrt((upX-lastClickX)*(upX-lastClickX)+
					(upY-lastClickY)*(upY-lastClickY));
			if(!newPath&&moveDist>5){
				//create ejection
				
				long moveTime = System.currentTimeMillis()-lastClickTime;
				VectorAttr moveVel = new VectorAttr((upX-lastClickX)/moveTime*300,(upY-lastClickY)/moveTime*300);
				ejectionGroup.add(new Ejection(new PointF(head.getX(), head.getY()),moveVel,(int) moveDist/50));
				ejectionGroup.get(ejectionGroup.size()-1).start(brickGroup,head);
				lastClickTime = 0;
			} 
			newPath=false;
			break;
		default:
			break;
		}
		return true;
	}

	//////////////////////////////////////////////////////
	//Build the bricksGroup from the data string// 
	//not used now
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
							BrickObject tmpBrick = new BrickObject(tmpLocation[0],tmpLocation[1],tmpLocation[2],
									tmpLocation[3]);
							tmpBrick.loadBitmap(BitmapFactory.decodeResource(res, R.drawable.wood_brick));
							brickGroup.addObject(tmpBrick, "brick"+String.valueOf(brickNum), true);
							
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
		int FPS = updateThread.getFPS();
		for(int i = 0; i<brickGroup.getObjectNum();i++){
			if(brickGroup.getGameObjectByIndex(i).effectBall(ball, FPS)==-1){
				brickGroup.removeObjectByIndex(i);
			}
		}
	}
	/////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////
	//detect head and bricks collision in bricks group///
	private void headComponentsEffect(){
		int FPS = updateThread.getFPS();
		for(int i = 0; i<brickGroup.getObjectNum();i++){
			if(brickGroup.getGameObjectByIndex(i).effectHead(head, FPS)==-1){
				brickGroup.removeObjectByIndex(i);
			}
		}
	}
	/////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////
	
	
	//update the ball according to collision
	private int updateBall(){
		int FPS = updateThread.getFPS();
		VectorAttr ballMotion = ball.getMotion();
		//get ready for update

		/////Check head collision and react
		if(head.effectBall(ball, FPS)==-1){
			//position adjust if collision
			//head.setPosition(head.getX()+head.getMotion().getX()/FPS, head.getY()+head.getMotion().getY()/FPS);
			//ball.setPosition(ball.getX()+ballMotion.getX()/FPS,
					//ball.getY()+ballMotion.getY()/FPS);
		}
		ball.setPosition(ball.getX()+ballMotion.getX()/(float)FPS, 
				ball.getY()+ballMotion.getY()/(float)FPS);
		/////Check bricks collision and react

		ballBricksCollision();
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
