package com.tcmxx.ballgame;


import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import android.graphics.PointF;
//This class is used to store the path which the head will follow. 
//It include the path to draw and a array of points for the head to follow
//based on time tick.

public class MovePath{
	
	protected Paint mPaint;
	protected ArrayList<PointF> pointsList; //list of points on the path. x1,y2,x2,y2.....
	int maxLength;
	public MovePath(int l){
		pointsList=new ArrayList<PointF>();
		mPaint = new Paint();
		mPaint.setColor(Color.BLACK);
		mPaint.setAntiAlias(true);  
		/**画笔的类型**/  
		mPaint.setStyle(Paint.Style.STROKE);  
		/**设置画笔变为圆滑状**/  
		mPaint.setStrokeCap(Paint.Cap.ROUND);  
		/**设置线的宽度**/  
		mPaint.setStrokeWidth(5);
		maxLength = l;
	}
	
	
	//add a new point to the path
	public boolean addPoint(float x, float y){
		if(pointsList.size() >= maxLength){
			pointsList.add(new PointF(x,y));
			pointsList.remove(0);
			return false;
		}
		else{
			pointsList.add(new PointF(x,y));
			
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
			return tmp;
		}
		
	}
	//clear the path
	public void clearPath(){
		pointsList.clear();
		
	}
	//draw the path
	public boolean draw(Canvas canvas){
		if(pointsList.size()>0){
			for(int i = 0;i<pointsList.size()-1;i++){
				canvas.drawLine(pointsList.get(i).x, pointsList.get(i).y,
						pointsList.get(i+1).x, pointsList.get(i+1).y, mPaint);
			}
		}
		return true;
	}

}
