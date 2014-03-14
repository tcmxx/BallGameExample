package com.tcmxx.ballgame.GameObjects;

import com.tcmxx.ballgame.CollisionModel;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

//all objects should be a game object!
public class GameObject {
	
	
	protected float xPos;			
	protected float yPos;			//these are the middle of each obeject.
	protected float orientation;
	Matrix T;				//the transition matrix of the center corner of the object
							//However, the transition value of it is the ul of the bitmap
							//when call draw, we create a new matrix for the bitmap and draw
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
			Matrix tmpM = new Matrix(T);
			//let the lu corner of bitmap be the translation of the T.
			tmpM.postTranslate(-width/2, -height/2);
			if(width>0 && height>0){
				//scale the bitmap to the desired height width 
				tmpM.preScale(width/bmp.getWidth(), height/bmp.getHeight());
			}
			canvas.drawBitmap(bmp, tmpM, paint);
			//canvas.drawBitmap(bmp, xPos, yPos, paint);
		}
		return true;
	}
	
	
	
	public boolean setPosition(float x, float y){

		collisionModel.setNodePosition(x, y);
		//T.setRotate(orientation);
		T.postTranslate(x-xPos, y-yPos);
		xPos = x;
		yPos = y;
		return true;
	}
	public void rotate(float degree){
		
		T.preRotate(degree, width/2, height/2);		//the last 2 parameters are the rotation center
													//wrt the bitmap local coordinate
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
