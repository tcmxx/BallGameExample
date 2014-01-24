package com.tcmxx.ballgame.GameObjects;


import com.tcmxx.ballgame.VectorAttr;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

public class BallObject extends GameObject {

	protected int color = Color.BLACK;
	
	//inner physical features
	private float radius;
	public float mass;

	
	//outer physical features
	public float friction;
	public float gAcc;
	public float acc;
	protected VectorAttr motionAttr;
	
	public BallObject(float x, float y, float r){
		super();
		radius = r;
		collisionRadius = r;
		paint.setColor(color);
		super.setPosition(x, y);
		collisionModel.addCircle( new PointF(0,0), r, "");
		collisionModel.setCollisionRadius(r);
		collisionModel.addPoint(new PointF(0,0),"");
		width=2*r;
		height=2*r;
		motionAttr = new VectorAttr();
	}
	public BallObject(){
		this(0.0f,0.0f,0.0f);
	}
	
	public void setRadius(float r){
		radius = r;
		collisionModel.setCircleRadiusByIndex(0,r);
		collisionRadius = r;
		collisionModel.setCollisionRadius(r);
		width=2*r;
		height=2*r;
	}
	public float getRadius(){
		return radius;
	}
	public void setColor(int c){
		color = c;
		paint.setColor(color);
	}
	public VectorAttr getMotion(){
		return motionAttr;
	}
	
	
	
	@Override
	public boolean draw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.drawCircle(xPos, yPos, radius, paint);
		return true;
	}
	

	
	
}
