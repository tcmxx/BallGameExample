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
	long totalTime;							//the time length of the path
	long maxTime;							//the max total time of the path that can be stored
	public int length;						//the length of this path, which will not
											
	boolean ifAdd = true;					//Whether add new path when calling addPoint
											
	
	public MovePath(int l){
		pointsList=new ArrayList<PointF>();
		intervalList = new ArrayList<Long>();
		mPaint = new Paint();
		mPaint.setColor(Color.BLACK);
		mPaint.setAntiAlias(true);  
		mPaint.setStyle(Paint.Style.STROKE);   
		mPaint.setStrokeCap(Paint.Cap.ROUND);  
		mPaint.setStrokeWidth(5);
		maxTime = 0;
		totalTime = 0;
		lastAddTime=0;
		lastPollTime=0;
	}
	
	
	//add a new point to the path
	public boolean addPoint(float x, float y){
		if((totalTime >= maxTime&&ifAdd&&maxTime>0)||!ifAdd){
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
			totalTime += cTime-lastAddTime;
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
			totalTime -=intervalList.remove(0);
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
