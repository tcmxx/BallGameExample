package com.tcmxx.ballgame.GameObjects;

import com.tcmxx.ballgame.CollisionModel;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class GameObject {
	
	
	protected float xPos;
	protected float yPos;
	protected float orientation;
	Matrix T;				//the transition matrix of the LU corner of the bitmap
	protected float width;
	protected float height;
	protected Bitmap bmp;  

	protected Paint paint;
	protected CollisionModel collisionModel;
	protected float collisionRadius;
	
	
	public GameObject(){
		paint = new Paint();
		collisionModel = new CollisionModel();
		xPos=0;
		yPos=0;
		orientation=0;
		T=new Matrix();
	}
	public boolean loadBitmap(Bitmap arg){
		bmp = arg;
		return true;
	}
	
	public boolean draw(Canvas canvas){
		if(bmp!=null){
			canvas.drawBitmap(bmp, T, paint);
		}
		return true;
	}
	
	
	
	public boolean setPosition(float x, float y){
		xPos = x;
		yPos = y;
		collisionModel.setNodePosition(x, y);
		T.setRotate(orientation);
		T.postTranslate(xPos-width/2, yPos-height/2);
		return true;
	}
	public void rotate(float degree){
		T.preRotate(degree, xPos, yPos);
		orientation+=degree;
		collisionModel.rotate(degree);
	}
	
	public Paint getPaint(){
		return paint;
	}
	
	public float getX(){
		return xPos;
	}
	public float getY(){
		return yPos;
	}
	
	public CollisionModel getCollisionModel(){
		return collisionModel;
	}
	public void updateFrame(int FPS){
	}
}
