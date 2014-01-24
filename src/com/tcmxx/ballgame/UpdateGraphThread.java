package com.tcmxx.ballgame;


import android.graphics.Canvas;
import android.view.SurfaceHolder;


public class UpdateGraphThread extends Thread {
	
	private long time;   
    private final int FPS = 80;   
    private boolean toRun = false;   
    private MovementView movementView;   
    private SurfaceHolder surfaceHolder;   
    
	public UpdateGraphThread(MovementView view){
		movementView = view;   
        surfaceHolder = movementView.getHolder();   
	}
	public int getFPS(){
		return FPS;
	}
	public void setRunning(boolean run) {   
        toRun = run;   
    }   

    public void run() {   
      
            Canvas c;   
            while (toRun) {   
                long cTime = System.currentTimeMillis();   

                c = null;   
                try {   
                    c = surfaceHolder.lockCanvas();   
      
                    movementView.updateFrame();   
                    movementView.drawFrame(c);   
                } catch(Exception e){
                		e.printStackTrace();
                    	}
                finally{   
                    if (c != null) {   
                        surfaceHolder.unlockCanvasAndPost(c);   
                    }   
                }   
                //Sleep until, to fix the time per frame
                if(1000 / FPS-(cTime - time)>0)
                {
	                try {   
	                    Thread.sleep(1000 / FPS-(cTime - time));   
	                } catch (Exception e) {   
	                }   
                }
                time = cTime;
            }   
        }  
}
