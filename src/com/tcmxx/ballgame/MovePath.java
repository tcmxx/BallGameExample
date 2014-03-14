package com.tcmxx.ballgame;


import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.PointF;
//This class is used to store the path which the head will follow. 
//It include the path to draw and a array of points for the head to follow
//based on time tick.

public class MovePath{
	
	protected Paint mPaint;
	protected ArrayList<PointF> pointsList; //list of points on the path. 
	protected ArrayList<Long> intervalList;//list of the time interval between the 
											//previous and this point. The first value is 0
	long lastAddTime; 							//the system time last point is added
	long lastPollTime;						//the system time last point is polled
	int maxLength;
	public int length;						//the length of this path, which will not
											//be reduced when points are polled.
	boolean ifAdd = true;					//Whether add new path when calling addPoint
											
	
	public MovePath(int l){
		pointsList=new ArrayList<PointF>();
		intervalList = new ArrayList<Long>();
		mPaint = new Paint();
		mPaint.setColor(Color.BLACK);
		mPaint.setAntiAlias(true);  
		/**���ʵ�����**/  
		mPaint.setStyle(Paint.Style.STROKE);  
		/**���û��ʱ�ΪԲ��״**/  
		mPaint.setStrokeCap(Paint.Cap.ROUND);  
		/**�����ߵĿ��**/  
		mPaint.setStrokeWidth(5);
		maxLength = l;
		lastAddTime=0;
		lastPollTime=0;
	}
	
	
	//add a new point to the path
	public boolean addPoint(float x, float y){
		if(length >= maxLength&&ifAdd){
			ifAdd = false;
			return false;
		}
		else{
			long cTime = System.currentTimeMillis();   
			if(pointsList.isEmpty()){
				lastAddTime = cTime;
			}
			pointsList.add(new PointF(x,y));
			intervalList.add(cTime-lastAddTime);
			lastAddTime = cTime;
			length = pointsList.size();
			return true;
		}
	}
	
	//remove and return the first point
	public PointF pollPoint(){
		if(pointsList.size()<= 0){
			return null;
		}
		else{
			PointF tmp = pointsList.remove(0);
			intervalList.remove(0);
			lastPollTime=System.currentTimeMillis();  
			length = pointsList.size();
			//length = pointsList.size();
			return tmp;
		}
		
	}
	//get the time interval from next point to the point that is already polled out.
	public long getNextInterval(){
		if (intervalList.isEmpty()){
			return Long.MAX_VALUE;
		}
		return intervalList.get(0);
	}
	//get the time interval from last point polled out. If no point polled, arbitary value
	public long getTimeFromLastPoll(){
		long cTime = System.currentTimeMillis();   
		long interval = cTime-lastPollTime;
		return interval;
	}
	//clear the path
	public void clearPath(){
		pointsList.clear();
		length = pointsList.size();
		intervalList.clear();
		ifAdd=true;
	}
	//draw the path
	public boolean draw(Canvas canvas){
		if(pointsList.size()>0){
			//draw the path
			for(int i = 0;i<pointsList.size()-1;i++){
				canvas.drawLine(pointsList.get(i).x, pointsList.get(i).y,
						pointsList.get(i+1).x, pointsList.get(i+1).y, mPaint);
			}
			//draw the beginning and end point with red color
			Paint tmpPaint = new Paint();
			tmpPaint.setColor(Color.RED);
			canvas.drawCircle(pointsList.get(0).x, pointsList.get(0).y, 
					10, tmpPaint);
			canvas.drawCircle(pointsList.get(pointsList.size()-1).x, pointsList.get(pointsList.size()-1).y, 
					10, tmpPaint);
		}
		return true;
	}

}
