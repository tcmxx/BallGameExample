package com.tcmxx.ballgame.GameObjects;

import java.util.ArrayList;


import android.graphics.Canvas;

import android.graphics.Color;
import android.graphics.Matrix;

import android.graphics.Path;
import android.graphics.PointF;

import com.tcmxx.ballgame.VectorAttr;

public class BrickObject extends Component {

	protected int color = Color.BLACK;
	protected float[] corners = new float[8];
	protected Path mPath;
	public int lives;				//how many time the brick can be hit
	public float stiffCoef=1;		//energy loss of ball when the ball hit it 
	
	public BrickObject(){
		super();
		corners = new float[8];
		mPath = new Path();
	}
	public BrickObject(float w, float h){
		super();
		corners = new float[8];
		mPath = new Path();
		width = w;
		height = h;
		collisionRadius = (float)Math.sqrt(width*width+height*height)/2;
		corners[0] = xPos-width/2; corners[1] = yPos-height/2; 
		corners[2] = xPos-width/2; corners[3] = yPos+height/2; 
		corners[4] = xPos+width/2; corners[5] = yPos+height/2; 
		corners[6] = xPos+width/2; corners[7] = yPos-height/2; 
		mPath.reset();
		mPath.moveTo(corners[0], corners[1]);
		mPath.lineTo(corners[2], corners[3]);
		mPath.lineTo(corners[4], corners[5]);
		mPath.lineTo(corners[6], corners[7]);
		mPath.lineTo(corners[0], corners[1]);
		paint.setColor(color);
		collisionModel.addLine(new PointF(-width/2, -height/2), new PointF(-width/2, +height/2), "");
		collisionModel.addLine(new PointF(-width/2, +height/2), new PointF(+width/2, +height/2), "");
		collisionModel.addLine(new PointF(+width/2, +height/2), new PointF(+width/2, -height/2), "");
		collisionModel.addLine(new PointF(+width/2, -height/2), new PointF(-width/2, -height/2), "");
		collisionModel.setCollisionRadius((float)Math.sqrt(width*width+height*height)/2);
		collisionModel.setNodePosition(xPos, yPos);
		
		//test for bmt load and draw
		

	}
	public BrickObject(float l, float t,float r, float b){
		this(r-l,b-t);
		setPosition((r+l)/2,(b+t)/2);
		
	}
	
	////////////////////////////////////////////////////////
	//////////////////change the geometry of the rect
	public void setGeometry(float w, float h){
		width = w;
		height = h;
		corners[0] = -width/2; corners[1] = -height/2; 
		corners[2] = -width/2; corners[3] = +height/2; 
		corners[4] = +width/2; corners[5] = +height/2; 
		corners[6] = +width/2; corners[7] = -height/2; 
		T.mapPoints(corners);
		mPath.reset();
		mPath.moveTo(corners[0], corners[1]);
		mPath.lineTo(corners[2], corners[3]);
		mPath.lineTo(corners[4], corners[5]);
		mPath.lineTo(corners[6], corners[7]);
		mPath.lineTo(corners[0], corners[1]);
		collisionRadius = (float)Math.sqrt(width*width+height*height)/2;
		collisionModel.setLineRelatedStartByIndex(0, corners[0], corners[1]);
		collisionModel.setLineRelatedEndByIndex(0, corners[2], corners[3]);
		collisionModel.setLineRelatedStartByIndex(1, corners[2], corners[3]);
		collisionModel.setLineRelatedEndByIndex(1, corners[4], corners[5]);		
		collisionModel.setLineRelatedStartByIndex(2, corners[4], corners[5]);
		collisionModel.setLineRelatedEndByIndex(2, corners[6], corners[7]);		
		collisionModel.setLineRelatedStartByIndex(3, corners[6], corners[7]);
		collisionModel.setLineRelatedEndByIndex(3, corners[0], corners[1]);
		collisionModel.setCollisionRadius((float)Math.sqrt(width*width+height*height)/2);
	
	}
	
	
	@Override
	public boolean setPosition(float x, float y) {
		float tmpx = xPos;
		float tmpy = yPos;
		super.setPosition(x, y);
		Matrix tmpM = new Matrix();
		tmpM.setTranslate(x-tmpx, y-tmpy);
		tmpM.mapPoints(corners); 
		mPath.reset();
		mPath.moveTo(corners[0], corners[1]);
		mPath.lineTo(corners[2], corners[3]);
		mPath.lineTo(corners[4], corners[5]);
		mPath.lineTo(corners[6], corners[7]);
		mPath.lineTo(corners[0], corners[1]);
		return true;
	}
	@Override
	public void rotate(float degree) {
		
		super.rotate(degree);
		Matrix r = new Matrix();
		r.setRotate(degree,xPos,yPos);
		r.mapPoints(corners);
		mPath.reset();
		mPath.moveTo(corners[0], corners[1]);
		mPath.lineTo(corners[2], corners[3]);
		mPath.lineTo(corners[4], corners[5]);
		mPath.lineTo(corners[6], corners[7]);
		mPath.lineTo(corners[0], corners[1]);
	}
	public void setColor(int c){
		color = c;
		paint.setColor(color);
	}
	public boolean draw(Canvas canvas) {
		if(bmp!=null)
			super.draw(canvas);
		else
			canvas.drawPath(mPath, paint);
		return true;
	}
	public float getWidth(){
		return width;
	}
	public float getHeight(){
		return height;
	}
	
	//called every frame for each brick
	public int effectBall(BallObject ball){
		ArrayList<PointF> collisionPoints=ball.getCollisionModel().detectCollision(collisionModel);
		if(collisionPoints.size()==0){
			return 0;
		}
		else{
			PointF point = new PointF(0,0);
			for(int i = 0;i<collisionPoints.size();i++){
				point.x+=collisionPoints.get(i).x;
				point.y+=collisionPoints.get(i).y;
				
			}
			point.x = point.x/collisionPoints.size();
			point.y = point.y/collisionPoints.size();
			VectorAttr v = ball.getMotion();
			VectorAttr normal = new VectorAttr((point.x-ball.getX()), (point.y-ball.getY()));
			v.set(VectorAttr.reflectVector(v, normal));
			v.setValue(v.getValue()*stiffCoef, v.getAngle());
			return -1;		//hit the ball
		}
		
	}
	public int effectHead(HeadObject head){return 1;}
}
